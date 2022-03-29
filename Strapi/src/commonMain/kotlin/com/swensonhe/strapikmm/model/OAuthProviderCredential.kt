package com.swensonhe.strapikmm.model

data class OAuthProviderCredential(
    val providerId: String,
    val accessToken: String?,
    val idToken: String?,
    val nonce: String
)