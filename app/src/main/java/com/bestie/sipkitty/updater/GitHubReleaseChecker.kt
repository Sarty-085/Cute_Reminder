package com.bestie.sipkitty.updater

import android.content.Context
import android.content.pm.PackageManager
import com.bestie.sipkitty.BuildConfig
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class ReleaseAsset(
    @SerializedName("name") val name: String,
    @SerializedName("browser_download_url") val downloadUrl: String,
    @SerializedName("size") val size: Long
)

data class GitHubReleaseResponse(
    @SerializedName("tag_name") val tagName: String,
    @SerializedName("name") val title: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("assets") val assets: List<ReleaseAsset>?
)

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val fileName: String
)

class GitHubReleaseChecker(private val context: Context) {

    companion object {
        const val REPO_OWNER = "Sarty-085"
        const val REPO_NAME = "Cute_Reminder"
        private const val API_URL =
            "https://api.github.com/repos/$REPO_OWNER/$REPO_NAME/releases/latest"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun checkForUpdates(): Result<UpdateInfo> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(API_URL)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "SipKitty-Android-App")
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("GitHub API error: ${response.code} ${response.message}"))
            }

            val bodyString = response.body?.string()
                ?: return@withContext Result.failure(Exception("Empty response body"))

            val release = gson.fromJson(bodyString, GitHubReleaseResponse::class.java)

            // Find APK asset
            val apkAsset = release.assets?.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
                ?: return@withContext Result.failure(Exception("No APK found in the latest release assets"))

            val currentVersion = getCurrentVersionName()
            val latestVersion = release.tagName.removePrefix("v").trim()

            val isNewer = isVersionNewer(latest = latestVersion, current = currentVersion)

            Result.success(
                UpdateInfo(
                    isUpdateAvailable = isNewer,
                    latestVersion = release.tagName,
                    currentVersion = currentVersion,
                    releaseTitle = release.title ?: "Release ${release.tagName}",
                    releaseNotes = release.body ?: "No release notes provided.",
                    downloadUrl = apkAsset.downloadUrl,
                    fileName = apkAsset.name
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCurrentVersionName(): String {
        return try {
            val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            packageInfo.versionName ?: BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            BuildConfig.VERSION_NAME
        }
    }

    /**
     * Compares semantic versions (e.g. 1.0.1 vs 1.0.0). Returns true if latest > current.
     */
    private fun isVersionNewer(latest: String, current: String): Boolean {
        val cleanLatest = latest.split("-")[0].split("+")[0]
        val cleanCurrent = current.split("-")[0].split("+")[0]

        val latestParts = cleanLatest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = cleanCurrent.split(".").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(latestParts.size, currentParts.size)
        for (i in 0 until maxLen) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}
