# Keep Kotlin serialization metadata used by the API DTOs.
-keepattributes *Annotation*,InnerClasses,EnclosingMethod,Signature
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclasseswithmembers,includedescriptorclasses class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor and OkHttp ship consumer rules. Keep model names only where reflection is used.
-keep class com.silkysys.warehouse.sharedcode.data.** { *; }
-keep class com.silkysys.warehouse.sharedcode.domain.** { *; }

# Firebase installation and messaging are initialized from manifest metadata.
-keep class com.google.firebase.** { *; }
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**


#-dontwarn androidx.navigation.**
#-dontwarn org.slf4j.impl.StaticLoggerBinder
