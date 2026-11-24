package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class SaveRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) : SaveDataSource {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Debes iniciar sesión con Google antes de guardar en la nube.")
    }

    override fun observeSaveSlots(userId: String): Flow<List<SaveSlot>> = flow {
        require(userId.isNotBlank()) { "El identificador de usuario no puede estar vacío." }
        val path = "users/$userId/saveSlots"
        emitAll(
            db.collection("users").document(userId).collection("saveSlots")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        SaveSlot::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    ).sortedBy { it.slotNumber }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

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
    ): Result<SaveSlot> {
        val slotId = "slot_$slotNumber"
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/saveSlots/$slotId"
        return try {
            val uid = requireUserId()
            val safeSlotNum = slotNumber.coerceIn(1, 3)
            val safeName = slotName.trim().ifBlank { "Historia de Isabella — Ranura $safeSlotNum" }.take(60)
            val safeChapter = currentChapter.coerceIn(1, 5)
            val safeScene = currentSceneIndex.coerceIn(0, 20)
            val safeBond = bondScore.coerceIn(0, 100)
            val safeCourage = couragePoints.coerceIn(0, 100)
            val safeTenderness = tendernessPoints.coerceIn(0, 100)
            val safeWit = witPoints.coerceIn(0, 100)
            val safeMemories = unlockedMemories.distinct().take(20)
            val safeChoices = choiceHistory.distinct().take(30)
            val safeTime = playTimeSeconds.coerceIn(0L, 3_600_000L)
            val safeTitle = lastChapterTitle.take(80)

            val docRef = db.collection("users").document(uid)
                .collection("saveSlots").document(slotId)
            docPath = docRef.path

            val existingSnapshot = docRef.get().await()
            if (existingSnapshot.exists()) {
                val updatePayload = mapOf(
                    "slotName" to safeName,
                    "currentChapter" to safeChapter.toLong(),
                    "currentSceneIndex" to safeScene.toLong(),
                    "bondScore" to safeBond.toLong(),
                    "couragePoints" to safeCourage.toLong(),
                    "tendernessPoints" to safeTenderness.toLong(),
                    "witPoints" to safeWit.toLong(),
                    "unlockedMemories" to safeMemories,
                    "choiceHistory" to safeChoices,
                    "playTimeSeconds" to safeTime,
                    "finished" to finished,
                    "lastChapterTitle" to safeTitle,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            } else {
                val createPayload = mapOf(
                    "userId" to uid,
                    "slotNumber" to safeSlotNum.toLong(),
                    "slotName" to safeName,
                    "currentChapter" to safeChapter.toLong(),
                    "currentSceneIndex" to safeScene.toLong(),
                    "bondScore" to safeBond.toLong(),
                    "couragePoints" to safeCourage.toLong(),
                    "tendernessPoints" to safeTenderness.toLong(),
                    "witPoints" to safeWit.toLong(),
                    "unlockedMemories" to safeMemories,
                    "choiceHistory" to safeChoices,
                    "playTimeSeconds" to safeTime,
                    "finished" to finished,
                    "lastChapterTitle" to safeTitle,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            }

            val savedSlot = SaveSlot(
                id = slotId,
                userId = uid,
                slotNumber = safeSlotNum.toLong(),
                slotName = safeName,
                currentChapter = safeChapter.toLong(),
                currentSceneIndex = safeScene.toLong(),
                bondScore = safeBond.toLong(),
                couragePoints = safeCourage.toLong(),
                tendernessPoints = safeTenderness.toLong(),
                witPoints = safeWit.toLong(),
                unlockedMemories = safeMemories,
                choiceHistory = safeChoices,
                playTimeSeconds = safeTime,
                finished = finished,
                lastChapterTitle = safeTitle
            )
            Result.success(savedSlot)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun saveProgress(slot: SaveSlot): Result<Unit> {
        val slotId = slot.id.ifBlank { "slot_${slot.slotNumber.coerceIn(1L, 3L)}" }
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/saveSlots/$slotId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
                .collection("saveSlots").document(slotId)
            docPath = docRef.path

            val safeName = slot.slotName.trim().ifBlank { "Historia de Isabella — Ranura ${slot.slotNumber}" }.take(60)
            val safeChapter = slot.currentChapter.coerceIn(1L, 5L)
            val safeScene = slot.currentSceneIndex.coerceIn(0L, 20L)
            val safeBond = slot.bondScore.coerceIn(0L, 100L)
            val safeCourage = slot.couragePoints.coerceIn(0L, 100L)
            val safeTenderness = slot.tendernessPoints.coerceIn(0L, 100L)
            val safeWit = slot.witPoints.coerceIn(0L, 100L)
            val safeMemories = slot.unlockedMemories.distinct().take(20)
            val safeChoices = slot.choiceHistory.distinct().take(30)
            val safeTime = slot.playTimeSeconds.coerceIn(0L, 3_600_000L)
            val safeTitle = slot.lastChapterTitle.take(80)

            val existingSnapshot = docRef.get().await()
            if (existingSnapshot.exists()) {
                docRef.update(
                    mapOf(
                        "slotName" to safeName,
                        "currentChapter" to safeChapter,
                        "currentSceneIndex" to safeScene,
                        "bondScore" to safeBond,
                        "couragePoints" to safeCourage,
                        "tendernessPoints" to safeTenderness,
                        "witPoints" to safeWit,
                        "unlockedMemories" to safeMemories,
                        "choiceHistory" to safeChoices,
                        "playTimeSeconds" to safeTime,
                        "finished" to slot.finished,
                        "lastChapterTitle" to safeTitle,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "slotNumber" to slot.slotNumber.coerceIn(1L, 3L),
                        "slotName" to safeName,
                        "currentChapter" to safeChapter,
                        "currentSceneIndex" to safeScene,
                        "bondScore" to safeBond,
                        "couragePoints" to safeCourage,
                        "tendernessPoints" to safeTenderness,
                        "witPoints" to safeWit,
                        "unlockedMemories" to safeMemories,
                        "choiceHistory" to safeChoices,
                        "playTimeSeconds" to safeTime,
                        "finished" to slot.finished,
                        "lastChapterTitle" to safeTitle,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    override suspend fun deleteSlot(slotId: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/saveSlots/$slotId"
        return try {
            val uid = requireUserId()
            require(slotId.isNotBlank()) { "El ID de ranura no puede estar vacío." }
            val docRef = db.collection("users").document(uid)
                .collection("saveSlots").document(slotId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }
}
