package com.zhakki.quizapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data structure for session token retrieve from https://opentdb.com/api_token.php?command=request
 */
data class TokenResponse(
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("response_message")
    val responseMessage: String,
    val token: String
)
