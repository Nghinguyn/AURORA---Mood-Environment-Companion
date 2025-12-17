package com.example.aurora.location

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.aurora.HomeActivity
import com.example.aurora.R
import com.example.aurora.data.db.AppDatabase
import com.example.aurora.data.db.LocationEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@AndroidEntryPoint
class LocationTrackingService : Service() {

    @Inject
    lateinit var database: AppDatabase

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var lastLatitude: Double? = null
    private var lastLongitude: Double? = null
    private var lastLocationTime: Long = 0

    companion object {
        const val CHANNEL_ID = "location_tracking_channel"
        const val JOURNAL_PROMPT_CHANNEL_ID = "journal_prompt_channel"
        const val NOTIFICATION_ID = 1
        const val JOURNAL_PROMPT_NOTIFICATION_ID = 2
        const val LOCATION_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
        const val MIN_DISTANCE_METERS = 100.0 // Minimum distance to consider a new location
        const val MIN_STAY_DURATION_MS = 10 * 60 * 1000L // 10 minutes to consider a "stay"
        const val ACTION_OPEN_JOURNAL = "com.example.aurora.ACTION_OPEN_JOURNAL"

        fun start(context: Context) {
            val intent = Intent(context, LocationTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, LocationTrackingService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        setupLocationCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val trackingChannel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks your location to help with journaling"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(trackingChannel)

            val promptChannel = NotificationChannel(
                JOURNAL_PROMPT_CHANNEL_ID,
                "Journal Prompts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Prompts you to write about places you've visited"
            }
            notificationManager.createNotificationChannel(promptChannel)
        }
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, HomeActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Aurora")
            .setContentText("Tracking your journey")
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    processLocation(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            LOCATION_INTERVAL_MS
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun processLocation(latitude: Double, longitude: Double) {
        val currentTime = System.currentTimeMillis()

        if (lastLatitude != null && lastLongitude != null) {
            val distance = calculateDistance(lastLatitude!!, lastLongitude!!, latitude, longitude)

            if (distance < MIN_DISTANCE_METERS) {
                return
            }

            val stayDuration = currentTime - lastLocationTime
            if (stayDuration >= MIN_STAY_DURATION_MS) {
                val prevLat = lastLatitude!!
                val prevLon = lastLongitude!!
                val durationMinutes = (stayDuration / 60000).toInt()
                serviceScope.launch {
                    saveLocationAndPrompt(prevLat, prevLon, durationMinutes, promptOnLeave = true)
                }
            }
        }

        lastLatitude = latitude
        lastLongitude = longitude
        lastLocationTime = currentTime

        serviceScope.launch {
            saveLocation(latitude, longitude, 0)
        }
    }

    private suspend fun saveLocation(latitude: Double, longitude: Double, durationMinutes: Int) {
        saveLocationAndPrompt(latitude, longitude, durationMinutes, promptOnLeave = false)
    }

    private suspend fun saveLocationAndPrompt(
        latitude: Double,
        longitude: Double,
        durationMinutes: Int,
        promptOnLeave: Boolean
    ) {
        val lastEntry = database.locationDao().getLastLocation()

        if (lastEntry != null &&
            calculateDistance(lastEntry.latitude, lastEntry.longitude, latitude, longitude) < MIN_DISTANCE_METERS
        ) {
            database.locationDao().update(
                lastEntry.copy(
                    durationMinutes = lastEntry.durationMinutes + durationMinutes,
                    timestamp = System.currentTimeMillis()
                )
            )
            if (promptOnLeave && (lastEntry.placeName != null || lastEntry.address != null)) {
                showJournalPromptNotification(lastEntry.placeName ?: lastEntry.address ?: "a place")
            }
            return
        }

        val (placeName, address) = reverseGeocode(latitude, longitude)

        val entry = LocationEntry(
            latitude = latitude,
            longitude = longitude,
            placeName = placeName,
            address = address,
            durationMinutes = durationMinutes
        )
        database.locationDao().insert(entry)

        if (promptOnLeave && (placeName != null || address != null)) {
            showJournalPromptNotification(placeName ?: address ?: "a place")
        }
    }

    private fun showJournalPromptNotification(placeName: String) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            action = ACTION_OPEN_JOURNAL
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            JOURNAL_PROMPT_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, JOURNAL_PROMPT_CHANNEL_ID)
            .setContentTitle("You visited $placeName")
            .setContentText("Tap to write about your experience")
            .setSmallIcon(R.drawable.ic_location)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(JOURNAL_PROMPT_NOTIFICATION_ID, notification)
    }

    private fun reverseGeocode(latitude: Double, longitude: Double): Pair<String?, String?> {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val placeName = addr.featureName ?: addr.thoroughfare
                val address = addr.getAddressLine(0)
                Pair(placeName, address)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            Pair(null, null)
        }
    }

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}
