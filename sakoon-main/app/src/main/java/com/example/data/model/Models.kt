package com.example.data.model

data class UserProfile(
    val displayName: String = "প্রিয় মানুষ",
    val partnerName: String = "Shohan"
)

data class HabitItem(
    val id: String,
    val title: String,
    val subtitle: String
)

data class StudyTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class MealCategory(
    val title: String,
    val subtitle: String,
    val icon: String,
    val examples: List<String>,
    val tips: String
)

data class MovementOption(
    val id: String,
    val name: String,
    val icon: String
)

data class MoodOption(
    val id: String,
    val label: String,
    val emoji: String,
    val message: String
)

data class DhikrPhrase(
    val id: String,
    val arabic: String,
    val transliteration: String,
    val meaning: String,
    val virtue: String = "",
    val defaultGoal: Int = 33
)
