# Les modèles de sauvegarde sont sérialisés par kotlinx.serialization.
-keepclassmembers class com.felix.bestfelix.data.backup.** {
    *** Companion;
}
-keepclasseswithmembers class com.felix.bestfelix.data.backup.** {
    kotlinx.serialization.KSerializer serializer(...);
}
