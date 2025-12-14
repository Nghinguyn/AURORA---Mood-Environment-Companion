package com.example.aurora.network

data class ApiResponse(
    val success: Boolean,      // Indicates whether the request was successful
    val message: String        // Message from the backend, e.g., "Journal submitted successfully"
)
