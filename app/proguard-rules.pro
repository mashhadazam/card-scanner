# Keep BouncyCastle provider classes used for the TLS client identity.
-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
# Play Billing
-keep class com.android.billingclient.api.** { *; }
