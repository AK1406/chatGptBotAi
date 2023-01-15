package com.example.chatgpt

import java.io.Serializable
data class Choices(
    val text : String?,
    val index : Int?,
    val logprobs : Any? = null,
    val finish_reason : String?
):Serializable

/*

{
    "text": "\n\nMy name is Chelsea.",
    "index": 0,
    "logprobs": null,
    "finish_reason": "stop"
}*/
