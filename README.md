# 🐱 SipKitty: Cute Kitten Drink Water Reminder

A lovely, aesthetic native Android water reminder app built for your bestie! Featuring an interactive kitten companion that reacts to hydration levels, pastel kawaii visuals, smart reminders, and an in-app GitHub auto-updater that preserves 100% of drink history and streaks across updates.

---

## 🌸 Key Features

- **🐾 Interactive Kitten Mascot**:
  - **Thirsty (< 35%)**: Kitten is sleepy beside an empty bowl `(◡_◡)`
  - **Happy (35% - 99%)**: Kitten sits up smiling with paws out `( ^ • ﻌ • ^ )`
  - **Goal Crushed (100%+)**: Kitten wears a royal golden crown with floating hearts and sparkles!
  - **Interactive**: Tap the kitten anytime to see it bounce and purr!
- **🌊 Animated Water Reservoir**:
  - Real-time sine-wave animated liquid with rising translucent bubbles and daily goal progress.
- **🥛 Quick Sip Logging**:
  - 1-tap logging for 150ml (Cup), 250ml (Glass), 500ml (Bottle), and 350ml (Boba / Special).
  - Custom amount slider for logging any quantity.
- **🏆 Sticker Scrapbook & Streaks**:
  - Track consecutive daily streaks and unlock adorable kitten sticker badges.
- **🔔 Bestie Reminders**:
  - Gentle, humorous, and affectionate notification nudges throughout the day (e.g. *"Purr... Bestie, your kitten says it's water time! 🐱💧"*).
- **🚀 In-App GitHub Auto-Updater (Zero Data Loss)**:
  - Daily background check for new releases on `https://api.github.com/repos/Sarty-085/Cute_Reminder/releases/latest`.
  - Manual "Check for Updates" button in Settings.
  - Downloads the new APK and installs seamlessly via Android `FileProvider`.
  - **100% Data Preservation**: All Room database tables (drinks, history, streaks, preferences) remain untouched across app updates!

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose (Material 3)
- **Local Database**: Android Room Database (Offline-first, persistent)
- **Settings**: Jetpack DataStore Preferences
- **Background Tasks**: Android WorkManager (Daily 24h periodic update worker)
- **Reminders**: Android AlarmManager + BroadcastReceiver
- **Networking**: OkHttp 4 + Gson
- **CI/CD**: GitHub Actions (`.github/workflows/release.yml`)

---

## 📦 How Releasing Updates Works

Whenever you want to release a new version of the app to your bestie:

1. Update `versionCode` and `versionName` in `app/build.gradle.kts` (e.g., `versionName = "1.0.1"`).
2. Commit and push a git tag:
   ```bash
   git tag v1.0.1
   git push origin v1.0.1
   ```
3. GitHub Actions will automatically compile the APK and create a GitHub Release attaching `SipKitty.apk`.
4. Your bestie's app will detect the update during its daily background check (or when she taps "Check for Updates" in Settings), download it, and update in-place without losing any of her water logs or streak!

---

## 📱 Building Locally

```bash
# Set JDK 17+ (e.g. Android Studio JBR)
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"

# Build debug APK
.\gradlew.bat assembleDebug
```
The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`
