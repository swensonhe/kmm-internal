package com.swensonhe.strapikmm.model

data class OAuthProviderCredential(
    val providerId:  String,
    val idToken: String,
    val rawNonce: String?,
    val accessToken: String?
)