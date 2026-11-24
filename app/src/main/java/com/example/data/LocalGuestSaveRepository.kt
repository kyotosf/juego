package com.example.data

import android.content.Context
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LocalGuestSaveRepository(context: Context) : SaveDataSource {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val slotsFlow = MutableStateFlow(readSlotsFromPrefs())

    override fun observeSaveSlots(userId: String): Flow<List<SaveSlot>> = slotsFlow.asStateFlow()

    override suspend fun createOrOverwriteSlot(
        slotNumber: Int,
        slotName: String,
        currentChapter: Int,
        currentSceneIndex: Int,
        bondScore: Int,
        couragePoints: Int,
        tendernessPoints: Int,
        witPoints: Int,
        unlockedMemories: List<String>,
        choiceHistory: List<String>,
        playTimeSeconds: Long,
        finished: Boolean,
        lastChapterTitle: String
    ): Result<SaveSlot> = withContext(Dispatchers.IO) {
        try {
            val safeSlotNum = slotNumber.coerceIn(1, 3)
            val slotId = "slot_$safeSlotNum"
            val existing = slotsFlow.value.firstOrNull { it.id == slotId }
            val now = Timestamp.now()

            val newSlot = SaveSlot(
                id = slotId,
                userId = GUEST_UID,
                slotNumber = safeSlotNum.toLong(),
                slotName = slotName.trim().ifBlank { "Historia de Isabella — Ranura $safeSlotNum" }.take(60),
                currentChapter = currentChapter.coerceIn(1, 5).toLong(),
                currentSceneIndex = currentSceneIndex.coerceIn(0, 20).toLong(),
                bondScore = bondScore.coerceIn(0, 100).toLong(),
                couragePoints = couragePoints.coerceIn(0, 100).toLong(),
                tendernessPoints = tendernessPoints.coerceIn(0, 100).toLong(),
                witPoints = witPoints.coerceIn(0, 100).toLong(),
                unlockedMemories = unlockedMemories.distinct().take(20),
                choiceHistory = choiceHistory.distinct().take(30),
                playTimeSeconds = playTimeSeconds.coerceIn(0L, 3_600_000L),
                finished = finished,
                lastChapterTitle = lastChapterTitle.take(80),
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )

            val updatedList = (slotsFlow.value.filterNot { it.id == slotId } + newSlot)
                .sortedBy { it.slotNumber }
            writeSlotsToPrefs(updatedList)
            slotsFlow.value = updatedList
            Result.success(newSlot)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveProgress(slot: SaveSlot): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val safeSlotNum = slot.slotNumber.coerceIn(1L, 3L)
            val slotId = slot.id.ifBlank { "slot_$safeSlotNum" }
            val existing = slotsFlow.value.firstOrNull { it.id == slotId }
            val now = Timestamp.now()

            val updatedSlot = slot.copy(
                id = slotId,
                userId = GUEST_UID,
                slotNumber = safeSlotNum,
                slotName = slot.slotName.trim().ifBlank { "Historia de Isabella — Ranura $safeSlotNum" }.take(60),
                currentChapter = slot.currentChapter.coerceIn(1L, 5L),
                currentSceneIndex = slot.currentSceneIndex.coerceIn(0L, 20L),
                bondScore = slot.bondScore.coerceIn(0L, 100L),
                couragePoints = slot.couragePoints.coerceIn(0L, 100L),
                tendernessPoints = slot.tendernessPoints.coerceIn(0L, 100L),
                witPoints = slot.witPoints.coerceIn(0L, 100L),
                unlockedMemories = slot.unlockedMemories.distinct().take(20),
                choiceHistory = slot.choiceHistory.distinct().take(30),
                playTimeSeconds = slot.playTimeSeconds.coerceIn(0L, 3_600_000L),
                lastChapterTitle = slot.lastChapterTitle.take(80),
                createdAt = existing?.createdAt ?: slot.createdAt ?: now,
                updatedAt = now
            )

            val updatedList = (slotsFlow.value.filterNot { it.id == slotId } + updatedSlot)
                .sortedBy { it.slotNumber }
            writeSlotsToPrefs(updatedList)
            slotsFlow.value = updatedList
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSlot(slotId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val updatedList = slotsFlow.value.filterNot { it.id == slotId }
            writeSlotsToPrefs(updatedList)
            slotsFlow.value = updatedList
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun readSlotsFromPrefs(): List<SaveSlot> {
        val raw = prefs.getString(KEY_SLOTS_JSON, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = ArrayList<SaveSlot>(array.length())
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val memArr = obj.optJSONArray("unlockedMemories") ?: JSONArray()
                val memories = (0 until memArr.length()).map { memArr.getString(it) }
                val chArr = obj.optJSONArray("choiceHistory") ?: JSONArray()
                val choices = (0 until chArr.length()).map { chArr.getString(it) }
                val updatedSec = obj.optLong("updatedAtSec", System.currentTimeMillis() / 1000L)
                val createdSec = obj.optLong("createdAtSec", updatedSec)
                list.add(
                    SaveSlot(
                        id = obj.optString("id", "slot_1"),
                        userId = GUEST_UID,
                        slotNumber = obj.optLong("slotNumber", 1L),
                        slotName = obj.optString("slotName", "Historia de Isabella"),
                        currentChapter = obj.optLong("currentChapter", 1L),
                        currentSceneIndex = obj.optLong("currentSceneIndex", 0L),
                        bondScore = obj.optLong("bondScore", 50L),
                        couragePoints = obj.optLong("couragePoints", 0L),
                        tendernessPoints = obj.optLong("tendernessPoints", 0L),
                        witPoints = obj.optLong("witPoints", 0L),
                        unlockedMemories = memories,
                        choiceHistory = choices,
                        playTimeSeconds = obj.optLong("playTimeSeconds", 0L),
                        finished = obj.optBoolean("finished", false),
                        lastChapterTitle = obj.optString("lastChapterTitle", "Cap. 1: Atardecer en Queens"),
                        createdAt = Timestamp(createdSec, 0),
                        updatedAt = Timestamp(updatedSec, 0)
                    )
                )
            }
            list.sortedBy { it.slotNumber }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun writeSlotsToPrefs(slots: List<SaveSlot>) {
        val array = JSONArray()
        for (slot in slots) {
            val obj = JSONObject().apply {
                put("id", slot.id)
                put("slotNumber", slot.slotNumber)
                put("slotName", slot.slotName)
                put("currentChapter", slot.currentChapter)
                put("currentSceneIndex", slot.currentSceneIndex)
                put("bondScore", slot.bondScore)
                put("couragePoints", slot.couragePoints)
                put("tendernessPoints", slot.tendernessPoints)
                put("witPoints", slot.witPoints)
                put("unlockedMemories", JSONArray(slot.unlockedMemories))
                put("choiceHistory", JSONArray(slot.choiceHistory))
                put("playTimeSeconds", slot.playTimeSeconds)
                put("finished", slot.finished)
                put("lastChapterTitle", slot.lastChapterTitle)
                put("createdAtSec", slot.createdAt?.seconds ?: (System.currentTimeMillis() / 1000L))
                put("updatedAtSec", slot.updatedAt?.seconds ?: (System.currentTimeMillis() / 1000L))
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_SLOTS_JSON, array.toString()).apply()
    }

    private companion object {
        const val PREFS_NAME = "spider_love_guest_saves"
        const val KEY_SLOTS_JSON = "guest_save_slots"
        const val GUEST_UID = "guest_local"
    }
}
