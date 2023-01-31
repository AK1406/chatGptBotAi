package com.example.chatgpt

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val request: Request = original.newBuilder()
            //  .method(original.method, original.body)
            .header("Authorization", "Bearer API KEY")
            .header("Content-Type", "application/json")
            .build()
        return chain.proceed(request)
    }
}
