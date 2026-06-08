# Optimizaciones generales y ofuscación agresiva
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Proteger clases de Modelos (Data Classes) para que GSON/Retrofit puedan mapearlas
-keepclassmembers class com.example.swo.model.** { *; }

# Hilt/Dagger
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class androidx.room.paging.** { *; }

# Retrofit / OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }

# Seguridad: Evitar que se eliminen atributos de seguridad
-keepattributes *Annotation*
-keep class androidx.security.crypto.** { *; }
-keep class androidx.biometric.** { *; }
