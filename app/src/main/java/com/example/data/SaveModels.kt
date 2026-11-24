package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class SaveSlot(
    @DocumentId val id: String = "",
    val userId: String = "",
    val slotNumber: Long = 1L,
    val slotName: String = "",
    val currentChapter: Long = 1L,
    val currentSceneIndex: Long = 0L,
    val bondScore: Long = 50L,
    val couragePoints: Long = 0L,
    val tendernessPoints: Long = 0L,
    val witPoints: Long = 0L,
    val unlockedMemories: List<String> = emptyList(),
    val choiceHistory: List<String> = emptyList(),
    val playTimeSeconds: Long = 0L,
    val finished: Boolean = false,
    val lastChapterTitle: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
