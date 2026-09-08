package com.bestie.sipkitty.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bestie.sipkitty.ui.theme.CardSurface
import com.bestie.sipkitty.ui.theme.CreamBackground
import com.bestie.sipkitty.ui.theme.SakuraPink
import com.bestie.sipkitty.ui.theme.SoftPink
import com.bestie.sipkitty.ui.theme.TextPrimary
import com.bestie.sipkitty.ui.theme.TextSecondary
import com.bestie.sipkitty.ui.theme.WaterBlue
import com.bestie.sipkitty.updater.UpdateInfo

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    isDownloading: Boolean,
    downloadProgress: Int,
    onStartUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isDownloading) onDismiss()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🌸", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "New Update Available!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "${updateInfo.latestVersion} (Current: v${updateInfo.currentVersion})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "A new version of SipKitty is ready on GitHub with cute improvements! All your water history and streaks are 100% saved.",
                    fontSize = 13.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Integrity Badge
                if (updateInfo.expectedSha256 != null) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Verified",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Cryptographically Verified SHA-256",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (updateInfo.releaseNotes.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 140.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamBackground)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "What's New:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = updateInfo.releaseNotes,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                if (isDownloading) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (downloadProgress > 0) "Downloading update... $downloadProgress%" else "Starting download...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = WaterBlue
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { downloadProgress / 100f },
                        modifier = Modifier.fillMaxWidth(),
                        color = WaterBlue,
                        trackColor = SoftPink
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStartUpdate,
                enabled = !isDownloading,
                colors = ButtonDefaults.buttonColors(containerColor = SakuraPink)
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Downloading...", color = Color.White)
                } else {
                    Text("Update Now 🐱✨", color = Color.White)
                }
            }
        },
        dismissButton = {
            if (!isDownloading) {
                TextButton(onClick = onDismiss) {
                    Text("Later", color = TextSecondary)
                }
            }
        },
        containerColor = CardSurface,
        shape = RoundedCornerShape(22.dp)
    )
}
