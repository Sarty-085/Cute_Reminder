# Proguard rules for SipKitty
-keepclassmembers class * {
    @androidx.room.Entity *;
    @androidx.room.Dao *;
}
