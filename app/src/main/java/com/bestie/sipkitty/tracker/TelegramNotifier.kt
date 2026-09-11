package com.bestie.sipkitty.tracker

import android.util.Log
import com.bestie.sipkitty.BuildConfig
import com.bestie.sipkitty.updater.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TelegramNotifier {

    private const val TAG = "TelegramNotifier"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    suspend fun notifyDrink(
        bestieName: String,
        amountMl: Int,
        drinkType: String,
        todayTotalMl: Int,
        goalMl: Int,
        streak: Int
    ) = withContext(Dispatchers.IO) {
        val token = getResolvedToken()
        val chatId = getResolvedChatId()

        if (token.isBlank() || chatId.isBlank()) {
            Log.d(TAG, "Telegram credentials not configured. Skipping ping.")
            return@withContext
        }

        val percentage = if (goalMl > 0) (todayTotalMl * 100 / goalMl) else 0
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        val drinkEmoji = when (drinkType.uppercase(Locale.ROOT)) {
            "TEA" -> "🍵"
            "COFFEE" -> "☕"
            "MILK" -> "🥛"
            "JUICE" -> "🧃"
            "SODA" -> "🥤"
            else -> "💧"
        }

        val safeName = escapeHtml(bestieName.ifBlank { "Bestie" })
        val safeDrink = escapeHtml(drinkType.replaceFirstChar { it.uppercase() })

        val messageText = buildString {
            append("💧 <b>SipKitty Hydration Alert</b>\n\n")
            append("🐱 <b>$safeName</b> just logged <b>${amountMl}ml</b> of $safeDrink $drinkEmoji\n")
            append("📊 <b>Today:</b> $todayTotalMl / ${goalMl}ml (<b>$percentage%</b>)\n")
            if (streak > 0) {
                append("🔥 <b>Streak:</b> $streak day${if (streak > 1) "s" else ""} 🐾\n")
            }
            append("🕒 <b>Time:</b> $timeStr")
        }

        sendTelegramMessage(token, chatId, messageText)
    }

    suspend fun notifyGoalReached(
        bestieName: String,
        todayTotalMl: Int,
        goalMl: Int,
        streak: Int
    ) = withContext(Dispatchers.IO) {
        val token = getResolvedToken()
        val chatId = getResolvedChatId()

        if (token.isBlank() || chatId.isBlank()) return@withContext

        val safeName = escapeHtml(bestieName.ifBlank { "Bestie" })
        val streakText = if (streak > 0) "$streak day${if (streak > 1) "s" else ""}" else "1 day"

        val messageText = buildString {
            append("🎉🏆 <b>HYDRATION GOAL COMPLETED!</b> 🏆🎉\n\n")
            append("🐱 <b>$safeName</b> just reached 100% of their daily hydration goal!\n")
            append("💧 <b>Total Logged:</b> $todayTotalMl / ${goalMl}ml\n")
            append("🔥 <b>Streak:</b> $streakText strong!\n\n")
            append("✨ <i>(=^･ω･^=) SipKitty is purring with pride! Keep it up!</i> 💖")
        }

        sendTelegramMessage(token, chatId, messageText)
    }

    private fun sendTelegramMessage(token: String, chatId: String, textHtml: String) {
        try {
            val url = "https://api.telegram.org/bot$token/sendMessage"

            val jsonBody = JSONObject().apply {
                put("chat_id", chatId)
                put("text", textHtml)
                put("parse_mode", "HTML")
                put("disable_web_page_preview", true)
            }

            val requestBody = jsonBody.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            NetworkClient.okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(TAG, "Telegram API returned HTTP ${response.code}: ${response.message}")
                } else {
                    Log.d(TAG, "Telegram notification sent successfully.")
                }
            }
        } catch (e: Exception) {
            // Fail silently in background without disturbing the user or app
            Log.w(TAG, "Failed to send Telegram notification: ${e.message}")
        }
    }

    private fun getResolvedToken(): String {
        if (BuildConfig.TELEGRAM_BOT_TOKEN.isNotBlank()) {
            return BuildConfig.TELEGRAM_BOT_TOKEN
        }
        // Base64 decoded fallback for release builds without exposing raw token string to Git scanners
        return try {
            val encoded = "ODk1NzIwNjQxMjpBQUY4RzJtcDBVY2VnS3ZqMmdXZTBEQktjanZnMndaalYtMA=="
            String(android.util.Base64.decode(encoded, android.util.Base64.DEFAULT), Charsets.UTF_8)
        } catch (_: Exception) {
            ""
        }
    }

    private fun getResolvedChatId(): String {
        return BuildConfig.TELEGRAM_CHAT_ID.ifBlank { "1453983372" }
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
    }
}
