/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:40 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 11:40 AM
 *  
 */

package com.gpg.gpgpayment.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethodParams(
    val numSite: String,
    val language: Language,
    val signature: String
) : Parcelable

enum class Language {
    Fr,
    En
}


inline fun <reified T : Enum<T>> valueOf(type: String, default: T): T {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: Exception) {
        default
    }
}