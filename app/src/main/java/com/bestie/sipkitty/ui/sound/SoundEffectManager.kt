package com.bestie.sipkitty.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundEffectManager {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    /**
     * Synthesizes a cute, bubbly rising pop chirp.
     */
    fun playBubblePop(context: Context, soundEnabled: Boolean = true) {
        playHapticClick(context)
        if (!soundEnabled) return

        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 90
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val samples = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Rising frequency from 750Hz to 1350Hz
                    val progress = i.toDouble() / numSamples
                    val freq = 750.0 + (600.0 * progress * progress)
                    val envelope = exp(-progress * 4.5) // Smooth natural decay
                    val wave = sin(2.0 * PI * freq * t) * envelope
                    samples[i] = (wave * Short.MAX_VALUE * 0.7).toInt().toShort()
                }

                playPcm(samples, sampleRate)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Synthesizes a pleasant melodic water-pour chord (C5 - E5 - G5).
     */
    fun playWaterPour(context: Context, soundEnabled: Boolean = true) {
        playHapticSuccess(context)
        if (!soundEnabled) return

        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 180
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val samples = ShortArray(numSamples)

                val freqs = doubleArrayOf(523.25, 659.25, 783.99) // C5, E5, G5 major chord

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val envelope = sin(PI * progress) * exp(-progress * 2.0)
                    var waveSum = 0.0
                    for (f in freqs) {
                        waveSum += sin(2.0 * PI * f * t)
                    }
                    val wave = (waveSum / freqs.size) * envelope
                    samples[i] = (wave * Short.MAX_VALUE * 0.65).toInt().toShort()
                }

                playPcm(samples, sampleRate)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Synthesizes a soothing cat purr chord with gentle low-frequency flutter.
     */
    fun playPurr(context: Context, soundEnabled: Boolean = true) {
        playHapticPurr(context)
        if (!soundEnabled) return

        audioScope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 280
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val samples = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / numSamples
                    val envelope = sin(PI * progress)
                    val flutter = 0.5 * (1.0 + sin(2.0 * PI * 26.0 * t)) // 26Hz purr flutter
                    val fundamental = sin(2.0 * PI * 130.0 * t) + 0.4 * sin(2.0 * PI * 260.0 * t)
                    val wave = fundamental * flutter * envelope
                    samples[i] = (wave * Short.MAX_VALUE * 0.6).toInt().toShort()
                }

                playPcm(samples, sampleRate)
            } catch (_: Exception) {
            }
        }
    }

    private fun playPcm(samples: ShortArray, sampleRate: Int) {
        val bufferSize = samples.size * 2
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.play()
        // Wait and release
        Thread.sleep(300)
        audioTrack.release()
    }

    // --- Haptics ---

    fun playHapticClick(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25)
            }
        } catch (_: Exception) {
        }
    }

    fun playHapticSuccess(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 30, 60, 45),
                        intArrayOf(0, 140, 0, 200),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 30, 60, 45), -1)
            }
        } catch (_: Exception) {
        }
    }

    fun playHapticPurr(context: Context) {
        try {
            val vibrator = getVibrator(context) ?: return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 50, 40, 50, 40, 60),
                        intArrayOf(0, 90, 0, 110, 0, 130),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 50, 40, 50, 40, 60), -1)
            }
        } catch (_: Exception) {
        }
    }

    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
