/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:07 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 11:07 AM
 *
 */

package com.gpg.gpgpayment.util

import org.json.JSONException
import retrofit2.HttpException
import java.net.SocketTimeoutException

suspend fun <V> apiWrapper(function: suspend () -> Janus<V, Exception>): Janus<V, Exception> =
    try {
        function.invoke()
    } catch (e: Exception) {
        when (e) {
            is JSONException -> {
                Err(GpgPaymentErrors.JsonError(e.localizedMessage ?: "Json Exception"))
            }
            is NoConnectivityException -> {
                Err(GpgPaymentErrors.NoConnectivity)
            }
            is SocketTimeoutException -> {
                Err(GpgPaymentErrors.SocketTimeOut)
            }
            is HttpException -> {
                Err(GpgPaymentErrors.HttpException(e.localizedMessage ?: "Http Exception"))
            }
            else -> {
                Err(GpgPaymentErrors.UnknownException(e.localizedMessage ?: "Unknown Exception"))
            }
        }
    }