# Add project specific ProGuard rules here.
-keep class com.spacegame.** { *; }
-keepclassmembers class com.spacegame.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Supabase
-keep class io.github.jan.supabase.** { *; }
-keep class kotlinx.serialization.** { *; }

# Kotlin Serialization
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
