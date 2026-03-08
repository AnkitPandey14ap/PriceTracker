package com.multibank.pricetracker.data.core

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AppHttpInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val response = chain.proceed(chain.request())
            val ok = response.isSuccessful || response.code == 101 // 101 = WebSocket upgrade
            if (!ok) {
                throw AppException.HttpException(
                    code = response.code,
                    message = response.message.ifBlank { "Unknown HTTP error" }
                )
            }
            response
        } catch (e: AppException) {
            throw e
        } catch (e: IOException) {
            throw AppException.NetworkException(
                message = e.message ?: "Network I/O error",
                cause = e
            )
        }
    }
}
