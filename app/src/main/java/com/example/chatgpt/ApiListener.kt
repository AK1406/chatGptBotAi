package com.example.chatgpt

internal interface APIListener {
    fun showProgress()
    fun hideProgress()
    fun networkError()
}