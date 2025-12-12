package com.example.aurora

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.aurora.MoodEntry

class MoodUpdate(
    private val context: Context,
    private val moodEntries: List<MoodEntry>
) : BaseAdapter() {

    override fun getCount(): Int = moodEntries.size

    override fun getItem(position: Int): Any = moodEntries[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.mood_list, parent, false)
            holder = ViewHolder(
                view.findViewById(R.id.dateTextView),
                view.findViewById(R.id.moodTextView),
                view.findViewById(R.id.descriptionTextView),
                view.findViewById(R.id.expandLayout)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val entry = moodEntries[position]

        // Format the date using DateTimeFormatter for LocalDate
        val formattedDate = formatDate(entry.date)

        // Set data to TextViews
        holder.dateTextView.text = formattedDate
        holder.moodTextView.text = entry.mood
        holder.descriptionTextView.text = entry.description

        // Initially hide description
        holder.expandLayout.visibility = View.GONE

        // Toggle expand/collapse on item click
        view.setOnClickListener {
            holder.expandLayout.visibility =
                if (holder.expandLayout.visibility == View.GONE) View.VISIBLE
                else View.GONE
        }

        return view
    }


    private fun formatDate(date: LocalDate?): String {
        // Return empty string if the date is null to prevent crashes
        return if (date != null) {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
                date.format(formatter) // Format LocalDate to String
            } catch (e: Exception) {
                // In case of formatting errors, return a fallback string
                "Invalid Date"
            }
        } else {
            "No Date" // Fallback if date is null
        }
    }

    // ViewHolder class to hold references to the views
    private data class ViewHolder(
        val dateTextView: TextView,
        val moodTextView: TextView,
        val descriptionTextView: TextView,
        val expandLayout: LinearLayout
    )
}
