# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Solana Mobile Wallet Adapter & WebSocket Client Keep Rules
-keep class com.solanamobile.** { *; }
-keep class com.solana.mobilewalletadapter.** { *; }
-keep class com.neovisionaries.ws.client.** { *; }

# Preserve line numbers for stack trace debugging
-keepattributes SourceFile,LineNumberTable

