package com.example.chatgpt

import java.io.Serializable

data class GptRequest(
    val model : String?,
    val prompt : String?,
    val max_tokens : Int? = 4000,
    val temperature : Double? = 0.5
): Serializable