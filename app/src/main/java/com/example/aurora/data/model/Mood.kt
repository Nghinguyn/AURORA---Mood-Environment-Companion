package com.example.aurora.data.model

import com.example.aurora.R

enum class Mood(val emoji: String, val label: String, val labelResId: Int) {
    HAPPY("😊", "Happy", R.string.mood_happy),
    SAD("😢", "Sad", R.string.mood_sad),
    ANXIOUS("😰", "Anxious", R.string.mood_anxious),
    CALM("😌", "Calm", R.string.mood_calm),
    ANGRY("😠", "Angry", R.string.mood_angry),
    EXCITED("🎉", "Excited", R.string.mood_excited),
    TIRED("😴", "Tired", R.string.mood_tired),
    GRATEFUL("🙏", "Grateful", R.string.mood_grateful),
    CONFUSED("😕", "Confused", R.string.mood_confused),
    HOPEFUL("🌟", "Hopeful", R.string.mood_hopeful)
}
