package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.LocalGuestSaveRepository
import com.example.data.SaveRepository
import com.example.data.SaveSlot
import com.example.data.authStateFlow
import com.example.data.signOut
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.EpilogueScreen
import com.example.ui.screens.GameStageScreen
import com.example.ui.screens.HubScreen
import com.example.ui.screens.MemoriesAlbumScreen
import com.example.ui.theme.SpiderLoveTheme
import com.example.ui.viewmodel.GameViewModel
import com.example.ui.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

const val ROUTE_HUB = "hub"
const val ROUTE_GAME = "game"
const val ROUTE_MEMORIES = "memories"
const val ROUTE_EPILOGUE = "epilogue"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpiderLoveTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = auth.currentUser)
    var isGuestMode by rememberSaveable { mutableStateOf(false) }

    val user = currentUser
    when {
        user != null -> {
            val uid = user.uid
            val displayName = user.displayName ?: "Isabella"
            key(uid) {
                AuthenticatedSpiderLoveApp(
                    currentUserId = uid,
                    userDisplayName = displayName,
                    isGuestMode = false,
                    onExitGuestMode = { isGuestMode = false }
                )
            }
        }
        isGuestMode -> {
            key("guest_local") {
                AuthenticatedSpiderLoveApp(
                    currentUserId = "guest_local",
                    userDisplayName = "Isabella (Invitada)",
                    isGuestMode = true,
                    onExitGuestMode = { isGuestMode = false }
                )
            }
        }
        else -> {
            AuthScreen(
                onAuthSuccess = { isGuestMode = false },
                onGuestSignIn = { isGuestMode = true }
            )
        }
    }
}

@Composable
private fun AuthenticatedSpiderLoveApp(
    currentUserId: String,
    userDisplayName: String,
    isGuestMode: Boolean,
    onExitGuestMode: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val navController = rememberNavController()

    val viewModel: GameViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val dataSource = if (isGuestMode) {
                    LocalGuestSaveRepository(app)
                } else {
                    val databaseId = app.getString(R.string.firestore_database_id)
                    val db = FirebaseFirestore.getInstance(databaseId)
                    SaveRepository(db)
                }
                GameViewModel(
                    repository = dataSource,
                    currentUserId = currentUserId,
                    isGuestMode = isGuestMode
                )
            }
        }
    )

    val slotsState by viewModel.slotsState.collectAsStateWithLifecycle()
    val playState by viewModel.playState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = ROUTE_HUB
    ) {
        composable(ROUTE_HUB) {
            HubScreen(
                userName = userDisplayName,
                isGuestMode = isGuestMode,
                slotsState = slotsState,
                isSaving = playState.isSaving,
                errorBanner = playState.errorBanner,
                onStartNewGame = { slotNum, name ->
                    viewModel.startNewGameInSlot(slotNum, name) {
                        navController.navigate(ROUTE_GAME)
                    }
                },
                onContinueSlot = { slot, chapterOverride ->
                    viewModel.loadSlotAndPlay(slot, chapterOverride) {
                        navController.navigate(ROUTE_GAME)
                    }
                },
                onDeleteSlot = { slotId -> viewModel.deleteSlot(slotId) },
                onOpenMemories = { navController.navigate(ROUTE_MEMORIES) },
                onSignOut = {
                    viewModel.stopPlayTimer()
                    if (isGuestMode) {
                        onExitGuestMode()
                    } else {
                        signOut(credentialManager, scope)
                    }
                },
                onDismissError = { viewModel.dismissError() }
            )
        }

        composable(ROUTE_GAME) {
            GameStageScreen(
                playState = playState,
                onMovePlayer = { dx, dy, dt -> viewModel.movePlayer(dx, dy, dt) },
                onWebAction = {
                    if (playState.dialogueActive) {
                        viewModel.advanceDialogue { isEnd ->
                            if (isEnd) {
                                viewModel.stopPlayTimer()
                                navController.navigate(ROUTE_EPILOGUE) {
                                    popUpTo(ROUTE_HUB)
                                }
                            }
                        }
                    } else {
                        viewModel.triggerWebAction()
                    }
                },
                onHotspotTap = { hotspotId -> viewModel.tapHotspotDirectly(hotspotId) },
                onAdvanceDialogue = {
                    viewModel.advanceDialogue { isEnd ->
                        if (isEnd) {
                            viewModel.stopPlayTimer()
                            navController.navigate(ROUTE_EPILOGUE) {
                                popUpTo(ROUTE_HUB)
                            }
                        }
                    }
                },
                onSelectChoice = { idx ->
                    val currentBeat = playState.chapter.dialogueBeats.getOrNull(playState.dialogueBeatIndex)
                    val choice = currentBeat?.choices?.getOrNull(idx)
                    if (choice != null) {
                        viewModel.selectChoice(choice)
                    }
                },
                onManualSave = { targetSlot -> viewModel.manualSaveToCloud(targetSlot) },
                onSetControlMode = { mode -> viewModel.setControlMode(mode) },
                onToggleMute = { viewModel.toggleMute() },
                onDismissToast = { viewModel.dismissToast() },
                onExitToHub = {
                    viewModel.stopPlayTimer()
                    viewModel.manualSaveToCloud(null)
                    navController.popBackStack(ROUTE_HUB, inclusive = false)
                }
            )
        }

        composable(ROUTE_MEMORIES) {
            val slotMemories = (slotsState as? UiState.Success)
                ?.data
                ?.flatMap { it.unlockedMemories }
                ?.toSet()
                ?: emptySet()
            val activeMemories = playState.activeSlot?.unlockedMemories?.toSet() ?: emptySet()
            MemoriesAlbumScreen(
                unlockedMemoryIds = slotMemories + activeMemories,
                onBack = { navController.popBackStack() }
            )
        }

        composable(ROUTE_EPILOGUE) {
            val active = playState.activeSlot
                ?: (slotsState as? UiState.Success)?.data?.firstOrNull()
                ?: SaveSlot(slotNumber = 1L, slotName = "Historia de Isabella", bondScore = 85L)
            EpilogueScreen(
                slot = active,
                onOpenMemories = { navController.navigate(ROUTE_MEMORIES) },
                onReturnToHub = {
                    navController.popBackStack(ROUTE_HUB, inclusive = false)
                }
            )
        }
    }
}
