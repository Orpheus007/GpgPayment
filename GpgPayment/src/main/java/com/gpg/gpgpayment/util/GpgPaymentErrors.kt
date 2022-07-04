/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:08 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 11:08 AM
 *
 */

package com.gpg.gpgpayment.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed class GpgPaymentErrors(message: String) : Exception(message) {
    object LocalIOException : GpgPaymentErrors("Database related problem")
    object UserNotLoggedIn : GpgPaymentErrors("The user is not logged in. There is no user >:(")
    object RowNotFound : GpgPaymentErrors("Row Does not exist in table")

    //---------------------- Web Errors -------------------------------
    data class JsonError(private val exception: String) : GpgPaymentErrors("Json Error =>  $exception")
    object NoConnectivity : GpgPaymentErrors("Not Internet")
    data class HttpException(private val exception: String) : GpgPaymentErrors("Http Exception => $exception")
    object SocketTimeOut : GpgPaymentErrors("Socket TimeOut")
    data class UnknownException(private val exception: String) : GpgPaymentErrors("Unknown Exception => $exception")
    data class ApiFailed(private val apiMessage: String) : GpgPaymentErrors(apiMessage)
    //---------------------- ----------- -------------------------------
}

class NoConnectivityException : IOException()
interface ConnectivityInterceptor : Interceptor

class ConnectivityInterceptorImpl(context: Context) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline())
            throw NoConnectivityException()
        return try {
            chain.proceed(chain.request())
        } catch (e: SocketTimeoutException) {
            throw e
        } catch (e: HttpException) {
            throw e
        } catch (e: Throwable) {
            throw e
        }
    }

    private fun isOnline(): Boolean {
        var result = false
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            true
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            true
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            true
                        }
                        else -> false
                    }
                }
            }
        } else {
            @Suppress("DEPRECATION")
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = true
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = true
                    }
                }
            }
        }
        return result
    }
}