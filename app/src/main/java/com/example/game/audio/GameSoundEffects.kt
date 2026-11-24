package com.example.game.audio

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameSoundEffects {
    private var toneGen: ToneGenerator? = null

    private fun ensureGenerator(): ToneGenerator? {
        if (toneGen == null) {
            toneGen = try {
                ToneGenerator(AudioManager.STREAM_MUSIC, 42)
            } catch (_: Exception) {
                null
            }
        }
        return toneGen
    }

    fun playWebThwip(muted: Boolean, scope: CoroutineScope) {
        if (muted) return
        scope.launch(Dispatchers.Default) {
            try {
                ensureGenerator()?.startTone(ToneGenerator.TONE_PROP_BEEP2, 70)
            } catch (_: Exception) {
            }
        }
    }

    fun playRomanticChime(muted: Boolean, scope: CoroutineScope) {
        if (muted) return
        scope.launch(Dispatchers.Default) {
            try {
                ensureGenerator()?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
            } catch (_: Exception) {
            }
        }
    }

    fun playChapterComplete(muted: Boolean, scope: CoroutineScope) {
        if (muted) return
        scope.launch(Dispatchers.Default) {
            try {
                ensureGenerator()?.startTone(ToneGenerator.TONE_PROP_PROMPT, 220)
            } catch (_: Exception) {
            }
        }
    }

    fun release() {
        try {
            toneGen?.release()
        } catch (_: Exception) {
        } finally {
            toneGen = null
        }
    }
}
