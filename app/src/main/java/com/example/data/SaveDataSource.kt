package com.example.data

import kotlinx.coroutines.flow.Flow

interface SaveDataSource {
    fun observeSaveSlots(userId: String): Flow<List<SaveSlot>>

    suspend fun createOrOverwriteSlot(
        slotNumber: Int,
        slotName: String,
        currentChapter: Int = 1,
        currentSceneIndex: Int = 0,
        bondScore: Int = 50,
        couragePoints: Int = 0,
        tendernessPoints: Int = 0,
        witPoints: Int = 0,
        unlockedMemories: List<String> = emptyList(),
        choiceHistory: List<String> = emptyList(),
        playTimeSeconds: Long = 0L,
        finished: Boolean = false,
        lastChapterTitle: String = "Cap. 1: Atardecer en Queens"
    ): Result<SaveSlot>

    suspend fun saveProgress(slot: SaveSlot): Result<Unit>

    suspend fun deleteSlot(slotId: String): Result<Unit>
}
