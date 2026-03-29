package com.wardenprotocol.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.wardenprotocol.game.R
import kotlin.random.Random

enum class MusicScene {
    HUB,
    SURFACE,
    EVENT,
    OUTCOME
}

enum class UiSound {
    PRIMARY,
    SECONDARY,
    NAV,
    TOGGLE,
    DANGER
}

class WardenAudioController(context: Context) {
    private val appContext = context.applicationContext
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIds = mutableMapOf<UiSound, Int>()
    private var musicPlayer: MediaPlayer? = null
    private var musicEnabled = true
    private var sfxEnabled = true
    private var currentScene: MusicScene? = null
    private var currentTrackRes: Int? = null
    private val lastTrackByScene = mutableMapOf<MusicScene, Int>()

    private val playlists = mapOf(
        MusicScene.HUB to listOf(
            R.raw.music_coordinates_of_tomorrow
        ),
        MusicScene.SURFACE to listOf(
            R.raw.music_twelve_levels_down,
            R.raw.ae_doomed,
            R.raw.ae_twists,
            R.raw.ae_warped
        ),
        MusicScene.EVENT to listOf(
            R.raw.ae_waking_the_devil,
            R.raw.ae_doomed,
            R.raw.ae_twists,
            R.raw.ae_spacetime
        ),
        MusicScene.OUTCOME to listOf(
            R.raw.music_last_consequence,
            R.raw.music_breathing_cold_air,
            R.raw.ae_spacetime
        )
    )

    init {
        soundIds[UiSound.PRIMARY] = soundPool.load(appContext, R.raw.sfx_button_primary, 1)
        soundIds[UiSound.SECONDARY] = soundPool.load(appContext, R.raw.sfx_button_secondary, 1)
        soundIds[UiSound.NAV] = soundPool.load(appContext, R.raw.sfx_button_nav, 1)
        soundIds[UiSound.TOGGLE] = soundPool.load(appContext, R.raw.sfx_button_toggle, 1)
        soundIds[UiSound.DANGER] = soundPool.load(appContext, R.raw.sfx_button_danger, 1)
    }

    fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        if (!enabled) {
            stopMusic()
            return
        }
        startSceneMusic(forceDifferentTrack = false)
    }

    fun setSfxEnabled(enabled: Boolean) {
        sfxEnabled = enabled
    }

    fun setScene(scene: MusicScene) {
        if (currentScene == scene) return
        currentScene = scene
        startSceneMusic(forceDifferentTrack = true)
    }

    fun play(sound: UiSound, force: Boolean = false) {
        if (!force && !sfxEnabled) return
        val soundId = soundIds[sound] ?: return
        soundPool.play(soundId, 0.85f, 0.85f, 1, 0, 1f)
    }

    fun release() {
        stopMusic()
        soundPool.release()
    }

    fun onPause() {
        musicPlayer?.pause()
    }

    fun onResume() {
        if (musicEnabled) {
            musicPlayer?.start() ?: startSceneMusic(forceDifferentTrack = false)
        }
    }

    private fun startSceneMusic(forceDifferentTrack: Boolean) {
        if (!musicEnabled) return
        val scene = currentScene ?: return
        val playlist = playlists[scene].orEmpty()
        if (playlist.isEmpty()) return

        val nextTrack = nextTrackFor(scene, playlist, forceDifferentTrack)
        if (currentTrackRes == nextTrack && musicPlayer?.isPlaying == true) return

        stopMusic()
        currentTrackRes = nextTrack
        lastTrackByScene[scene] = nextTrack
        musicPlayer = MediaPlayer.create(appContext, nextTrack)?.apply {
            isLooping = false
            setVolume(0.32f, 0.32f)
            setOnCompletionListener {
                currentTrackRes = null
                startSceneMusic(forceDifferentTrack = true)
            }
            start()
        }
    }

    private fun nextTrackFor(
        scene: MusicScene,
        playlist: List<Int>,
        forceDifferentTrack: Boolean
    ): Int {
        val lastTrack = lastTrackByScene[scene]
        if (playlist.size == 1) return playlist.first()
        if (!forceDifferentTrack && currentTrackRes != null) {
            return currentTrackRes ?: playlist.first()
        }
        val candidates = if (forceDifferentTrack && lastTrack != null) {
            playlist.filterNot { it == lastTrack }
        } else {
            playlist
        }
        return candidates.random(Random(System.nanoTime()))
    }

    private fun stopMusic() {
        musicPlayer?.runCatching {
            stop()
        }
        musicPlayer?.release()
        musicPlayer = null
        currentTrackRes = null
    }
}
