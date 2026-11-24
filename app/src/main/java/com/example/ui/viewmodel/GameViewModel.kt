package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SaveDataSource
import com.example.data.SaveSlot
import com.example.game.audio.GameSoundEffects
import com.example.game.model.CanonicalMemory
import com.example.game.model.ChapterSpec
import com.example.game.model.ControlMode
import com.example.game.model.Speaker
import com.example.game.model.StoryCatalog
import com.example.game.model.StoryChoice
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.hypot

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class GamePlayState(
    val activeSlot: SaveSlot? = null,
    val chapter: ChapterSpec = StoryCatalog.chapterByNumber(1),
    val playerX: Float = 0.16f,
    val playerY: Float = 0.68f,
    val facingRight: Boolean = true,
    val isMoving: Boolean = false,
    val webActive: Boolean = false,
    val webTargetX: Float = 0.5f,
    val webTargetY: Float = 0.3f,
    val completedHotspotIds: Set<String> = emptySet(),
    val reactionBanner: String? = null,
    val unlockedMemoryToast: CanonicalMemory? = null,
    val dialogueActive: Boolean = false,
    val dialogueBeatIndex: Int = 0,
    val pendingChoiceResponse: Pair<Speaker, String>? = null,
    val controlMode: ControlMode = ControlMode.AUTO,
    val isMuted: Boolean = false,
    val isSaving: Boolean = false,
    val saveBanner: String? = null,
    val errorBanner: String? = null
)

class GameViewModel(
    private val repository: SaveDataSource,
    private val currentUserId: String,
    private val isGuestMode: Boolean = false
) : ViewModel() {

    private val soundEffects = GameSoundEffects()

    val slotsState: StateFlow<UiState<List<SaveSlot>>> = repository.observeSaveSlots(currentUserId)
        .map<List<SaveSlot>, UiState<List<SaveSlot>>> { UiState.Success(it) }
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(TAG, "Error observing save slots", rootError)
            emit(UiState.Error(rootError.message ?: "Error al sincronizar ranuras de guardado."))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private val _playState = MutableStateFlow(GamePlayState())
    val playState: StateFlow<GamePlayState> = _playState.asStateFlow()

    private var playTimerJob: Job? = null
    private var webResetJob: Job? = null

    fun startNewGameInSlot(
        slotNumber: Int,
        slotName: String,
        onReady: () -> Unit
    ) {
        viewModelScope.launch {
            _playState.update { it.copy(isSaving = true, errorBanner = null) }
            val result = repository.createOrOverwriteSlot(
                slotNumber = slotNumber,
                slotName = slotName,
                currentChapter = 1,
                currentSceneIndex = 0,
                bondScore = 50,
                couragePoints = 0,
                tendernessPoints = 0,
                witPoints = 0,
                unlockedMemories = emptyList(),
                choiceHistory = emptyList(),
                playTimeSeconds = 0L,
                finished = false,
                lastChapterTitle = "Cap. 1: ${StoryCatalog.chapterByNumber(1).title}"
            )
            result.fold(
                onSuccess = { createdSlot ->
                    setupChapter(createdSlot, chapterNum = 1)
                    _playState.update {
                        it.copy(
                            isSaving = false,
                            saveBanner = if (isGuestMode) {
                                "Partida guardada localmente (Ranura ${createdSlot.slotNumber})"
                            } else {
                                "Partida guardada en la nube (Ranura ${createdSlot.slotNumber})"
                            }
                        )
                    }
                    startPlayTimer()
                    onReady()
                },
                onFailure = { error ->
                    _playState.update {
                        it.copy(
                            isSaving = false,
                            errorBanner = error.localizedMessage ?: "No se pudo crear la ranura."
                        )
                    }
                }
            )
        }
    }

    fun loadSlotAndPlay(
        slot: SaveSlot,
        chapterOverride: Int? = null,
        onReady: () -> Unit
    ) {
        val targetChapter = (chapterOverride ?: slot.currentChapter.toInt()).coerceIn(1, 5)
        setupChapter(slot, chapterNum = targetChapter)
        startPlayTimer()
        onReady()
    }

    fun deleteSlot(slotId: String) {
        viewModelScope.launch {
            val result = repository.deleteSlot(slotId)
            result.onFailure { error ->
                _playState.update {
                    it.copy(errorBanner = error.localizedMessage ?: "No se pudo borrar la ranura.")
                }
            }
        }
    }

    private fun setupChapter(slot: SaveSlot, chapterNum: Int) {
        val spec = StoryCatalog.chapterByNumber(chapterNum)
        _playState.update {
            it.copy(
                activeSlot = slot.copy(
                    currentChapter = chapterNum.toLong(),
                    lastChapterTitle = "Cap. $chapterNum: ${spec.title}"
                ),
                chapter = spec,
                playerX = 0.16f,
                playerY = 0.68f,
                facingRight = true,
                isMoving = false,
                webActive = false,
                completedHotspotIds = emptySet(),
                reactionBanner = spec.objectiveLabel,
                unlockedMemoryToast = null,
                dialogueActive = false,
                dialogueBeatIndex = 0,
                pendingChoiceResponse = null,
                errorBanner = null
            )
        }
    }

    private fun startPlayTimer() {
        playTimerJob?.cancel()
        playTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _playState.update { state ->
                    val current = state.activeSlot ?: return@update state
                    state.copy(
                        activeSlot = current.copy(playTimeSeconds = current.playTimeSeconds + 1L)
                    )
                }
            }
        }
    }

    fun stopPlayTimer() {
        playTimerJob?.cancel()
        playTimerJob = null
    }

    fun movePlayer(inputX: Float, inputY: Float, dt: Float) {
        val current = _playState.value
        if (current.dialogueActive) return
        if (inputX == 0f && inputY == 0f) {
            if (current.isMoving) {
                _playState.update { it.copy(isMoving = false) }
            }
            return
        }
        val speed = 0.48f * dt.coerceIn(0.001f, 0.05f)
        val nextX = (current.playerX + inputX * speed).coerceIn(0.08f, 0.92f)
        val nextY = (current.playerY + inputY * speed).coerceIn(0.18f, 0.84f)
        val nextFacing = if (inputX != 0f) inputX > 0f else current.facingRight
        _playState.update {
            it.copy(
                playerX = nextX,
                playerY = nextY,
                facingRight = nextFacing,
                isMoving = true
            )
        }
    }

    fun triggerWebAction() {
        val state = _playState.value
        if (state.dialogueActive) {
            if (state.pendingChoiceResponse != null ||
                state.chapter.dialogueBeats.getOrNull(state.dialogueBeatIndex)?.choices.isNullOrEmpty()
            ) {
                advanceDialogue()
            }
            return
        }

        val remaining = state.chapter.hotspots.filterNot { it.id in state.completedHotspotIds }
        if (remaining.isEmpty()) {
            openChapterDialogue()
            return
        }

        val nearest = remaining.minByOrNull {
            hypot((it.xFraction - state.playerX).toDouble(), (it.yFraction - state.playerY).toDouble())
        } ?: return

        val dist = hypot(
            (nearest.xFraction - state.playerX).toDouble(),
            (nearest.yFraction - state.playerY).toDouble()
        ).toFloat()

        soundEffects.playWebThwip(state.isMuted, viewModelScope)

        if (dist <= 0.42f) {
            activateHotspot(nearest.id, nearest.xFraction, nearest.yFraction)
        } else {
            // Pull Spider-Man halfway toward the target with a dynamic web swing
            val targetX = state.playerX + (nearest.xFraction - state.playerX) * 0.55f
            val targetY = state.playerY + (nearest.yFraction - state.playerY) * 0.55f
            fireWebVisual(nearest.xFraction, nearest.yFraction)
            _playState.update {
                it.copy(
                    playerX = targetX.coerceIn(0.08f, 0.92f),
                    playerY = targetY.coerceIn(0.18f, 0.84f),
                    facingRight = nearest.xFraction >= state.playerX
                )
            }
        }
    }

    fun tapHotspotDirectly(hotspotId: String) {
        val state = _playState.value
        if (state.dialogueActive) return
        val hotspot = state.chapter.hotspots.firstOrNull { it.id == hotspotId } ?: return
        if (hotspot.id in state.completedHotspotIds) return
        soundEffects.playWebThwip(state.isMuted, viewModelScope)
        activateHotspot(hotspot.id, hotspot.xFraction, hotspot.yFraction)
    }

    private fun fireWebVisual(targetX: Float, targetY: Float) {
        webResetJob?.cancel()
        _playState.update {
            it.copy(webActive = true, webTargetX = targetX, webTargetY = targetY)
        }
        webResetJob = viewModelScope.launch {
            delay(260L)
            _playState.update { it.copy(webActive = false) }
        }
    }

    private fun activateHotspot(hotspotId: String, targetX: Float, targetY: Float) {
        val state = _playState.value
        val hotspot = state.chapter.hotspots.firstOrNull { it.id == hotspotId } ?: return
        fireWebVisual(targetX, targetY)

        val updatedCompleted = state.completedHotspotIds + hotspot.id
        val unlockedMem = hotspot.unlocksMemoryId?.let { StoryCatalog.memoryById(it) }
        var updatedSlot = state.activeSlot

        if (unlockedMem != null && updatedSlot != null && unlockedMem.id !in updatedSlot.unlockedMemories) {
            updatedSlot = updatedSlot.copy(
                unlockedMemories = (updatedSlot.unlockedMemories + unlockedMem.id).distinct().take(20)
            )
            soundEffects.playRomanticChime(state.isMuted, viewModelScope)
        }

        val allDone = updatedCompleted.size >= state.chapter.hotspots.size
        _playState.update {
            it.copy(
                activeSlot = updatedSlot,
                playerX = (targetX * 0.85f + it.playerX * 0.15f).coerceIn(0.08f, 0.92f),
                playerY = (targetY * 0.85f + it.playerY * 0.15f).coerceIn(0.18f, 0.84f),
                facingRight = targetX >= it.playerX,
                completedHotspotIds = updatedCompleted,
                reactionBanner = hotspot.reactionText,
                unlockedMemoryToast = unlockedMem ?: it.unlockedMemoryToast
            )
        }

        if (allDone) {
            viewModelScope.launch {
                autoSaveCurrentSlot()
                delay(850L)
                openChapterDialogue()
            }
        }
    }

    fun openChapterDialogue() {
        _playState.update {
            it.copy(
                dialogueActive = true,
                dialogueBeatIndex = 0,
                pendingChoiceResponse = null
            )
        }
    }

    fun advanceDialogue(onChapterFinished: (Boolean) -> Unit = {}) {
        val state = _playState.value
        if (!state.dialogueActive) return

        if (state.pendingChoiceResponse != null) {
            completeCurrentChapter(onChapterFinished)
            return
        }

        val beats = state.chapter.dialogueBeats
        val currentBeat = beats.getOrNull(state.dialogueBeatIndex)
        if (currentBeat != null && currentBeat.choices.isNotEmpty()) {
            // Wait for user to select a choice
            return
        }

        val nextIndex = state.dialogueBeatIndex + 1
        if (nextIndex < beats.size) {
            _playState.update { it.copy(dialogueBeatIndex = nextIndex) }
        } else {
            completeCurrentChapter(onChapterFinished)
        }
    }

    fun selectChoice(choice: StoryChoice) {
        val state = _playState.value
        val slot = state.activeSlot ?: return

        soundEffects.playRomanticChime(state.isMuted, viewModelScope)

        val nextBond = (slot.bondScore + choice.bondDelta).coerceIn(0L, 100L)
        val nextCourage = (slot.couragePoints + choice.courageDelta).coerceIn(0L, 100L)
        val nextTenderness = (slot.tendernessPoints + choice.tendernessDelta).coerceIn(0L, 100L)
        val nextWit = (slot.witPoints + choice.witDelta).coerceIn(0L, 100L)
        val nextChoices = (slot.choiceHistory + choice.id).distinct().take(30)
        val extraMemory = choice.unlocksMemoryId
        val nextMemories = if (extraMemory != null) {
            (slot.unlockedMemories + extraMemory).distinct().take(20)
        } else {
            slot.unlockedMemories
        }

        val updatedSlot = slot.copy(
            bondScore = nextBond,
            couragePoints = nextCourage,
            tendernessPoints = nextTenderness,
            witPoints = nextWit,
            choiceHistory = nextChoices,
            unlockedMemories = nextMemories
        )

        _playState.update {
            it.copy(
                activeSlot = updatedSlot,
                pendingChoiceResponse = choice.responseSpeaker to choice.responseText
            )
        }
        viewModelScope.launch {
            autoSaveCurrentSlot()
        }
    }

    private fun completeCurrentChapter(onChapterFinished: (Boolean) -> Unit) {
        val state = _playState.value
        val slot = state.activeSlot ?: return
        val currentNum = state.chapter.number
        val isStoryEnd = currentNum >= 5
        val nextChapterNum = if (isStoryEnd) 5 else currentNum + 1
        val nextSpec = StoryCatalog.chapterByNumber(nextChapterNum)

        val updatedMemories = (slot.unlockedMemories + state.chapter.completionMemoryId)
            .distinct()
            .take(20)

        val updatedSlot = slot.copy(
            currentChapter = nextChapterNum.toLong(),
            currentSceneIndex = 0L,
            unlockedMemories = updatedMemories,
            finished = slot.finished || isStoryEnd,
            lastChapterTitle = if (isStoryEnd) {
                "Completado: ${nextSpec.title}"
            } else {
                "Cap. $nextChapterNum: ${nextSpec.title}"
            }
        )

        soundEffects.playChapterComplete(state.isMuted, viewModelScope)

        _playState.update {
            it.copy(
                activeSlot = updatedSlot,
                dialogueActive = false,
                pendingChoiceResponse = null
            )
        }

        viewModelScope.launch {
            repository.saveProgress(updatedSlot)
            if (!isStoryEnd) {
                setupChapter(updatedSlot, nextChapterNum)
            }
            onChapterFinished(isStoryEnd)
        }
    }

    fun manualSaveToCloud(targetSlotNumber: Int? = null) {
        val state = _playState.value
        val slot = state.activeSlot ?: return
        viewModelScope.launch {
            _playState.update { it.copy(isSaving = true, errorBanner = null) }
            val result = if (targetSlotNumber != null && targetSlotNumber.toLong() != slot.slotNumber) {
                repository.createOrOverwriteSlot(
                    slotNumber = targetSlotNumber,
                    slotName = "Historia de Isabella — Ranura $targetSlotNumber",
                    currentChapter = slot.currentChapter.toInt(),
                    currentSceneIndex = slot.currentSceneIndex.toInt(),
                    bondScore = slot.bondScore.toInt(),
                    couragePoints = slot.couragePoints.toInt(),
                    tendernessPoints = slot.tendernessPoints.toInt(),
                    witPoints = slot.witPoints.toInt(),
                    unlockedMemories = slot.unlockedMemories,
                    choiceHistory = slot.choiceHistory,
                    playTimeSeconds = slot.playTimeSeconds,
                    finished = slot.finished,
                    lastChapterTitle = slot.lastChapterTitle
                )
            } else {
                repository.saveProgress(slot).map { slot }
            }

            result.fold(
                onSuccess = { saved ->
                    _playState.update {
                        it.copy(
                            activeSlot = saved,
                            isSaving = false,
                            saveBanner = if (isGuestMode) {
                                "Progreso guardado en este dispositivo (Ranura ${saved.slotNumber})"
                            } else {
                                "Progreso guardado en la nube (Ranura ${saved.slotNumber})"
                            }
                        )
                    }
                },
                onFailure = { error ->
                    _playState.update {
                        it.copy(
                            isSaving = false,
                            errorBanner = error.localizedMessage ?: "No se pudo guardar en la nube."
                        )
                    }
                }
            )
        }
    }

    private suspend fun autoSaveCurrentSlot() {
        val slot = _playState.value.activeSlot ?: return
        repository.saveProgress(slot)
    }

    fun setControlMode(mode: ControlMode) {
        _playState.update { it.copy(controlMode = mode) }
    }

    fun toggleMute() {
        _playState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun dismissToast() {
        _playState.update { it.copy(unlockedMemoryToast = null, saveBanner = null) }
    }

    fun dismissError() {
        _playState.update { it.copy(errorBanner = null) }
    }

    override fun onCleared() {
        super.onCleared()
        playTimerJob?.cancel()
        webResetJob?.cancel()
        soundEffects.release()
    }

    private companion object {
        const val TAG = "GameViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
