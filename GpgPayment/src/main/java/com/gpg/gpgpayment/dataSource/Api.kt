/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:52 AM
 *
 */

package com.gpg.gpgpayment.dataSource

import android.content.Context
import com.gpg.gpgpayment.BuildConfig
import com.gpg.gpgpayment.entities.PaymentMethod
import com.gpg.gpgpayment.entities.PaymentMethodParams
import com.gpg.gpgpayment.util.ConnectivityInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface Api {

    //TODO Check in the ktor test how to do a Query
    @POST("paymentMethodsList")
    suspend fun getPaymentMethods(
        @Body paymentMethodParams: PaymentMethodParams
    ): List<PaymentMethod>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor,
            context: Context
        ): Api {

            val okHttpClient = getUnsafeOkHttpClient().newBuilder()
                .addInterceptor(connectivityInterceptor)
                .readTimeout(50000, TimeUnit.MILLISECONDS)
                .connectTimeout(50000, TimeUnit.MILLISECONDS)
                .writeTimeout(50000, TimeUnit.MILLISECONDS)
                .initHttpLogging()
                .build()

            /* val okHttpClient = OkHttpClient.Builder()
                 .addInterceptor(connectivityInterceptor)
                 .readTimeout(50000, TimeUnit.MILLISECONDS)
                 .connectTimeout(50000, TimeUnit.MILLISECONDS)
                 .writeTimeout(50000, TimeUnit.MILLISECONDS)
                 .initSSL(context)
                 .initHttpLogging()
                 .build()
                 */

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://41.231.9.227:8081/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java)
        }
    }
}

//TODO: Remove this in final version
fun getUnsafeOkHttpClient(): OkHttpClient {
    try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }

        return builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }

}

fun OkHttpClient.Builder.initHttpLogging(): OkHttpClient.Builder {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    if (BuildConfig.DEBUG) this.addInterceptor(logging)
    return this
}