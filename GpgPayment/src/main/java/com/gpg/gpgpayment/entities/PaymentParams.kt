package com.gpg.gpgpayment.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentParams(
    val numSite: String,
    val password: String,
    val orderId: String,
    val customerEmail: String,
    val customerLastName: String,
    val customerFirstName: String,
    val customerAddress: String,
    val customerZip: String? = null,
    val customerCity: String? = null,
    val customerCountry: String? = null,
    val customerTel: String,
    val language: Language,
    val amount: Long,
    val amountSecond: Long? = null,
    val currency: Currency,
    val paymentType: Boolean,
    val orderProducts: String,
    val signature: String,
    val vad: String,
    val terminal: String,
    val tauxConversion: Float?,
    val batchNumber: String? = null,
    val merchantReference: String? = null,
    val merchantUserName: String,
    val merchantPassword: String
) : Parcelable

@Parcelize
data class PaymentParamsFinal(
    val numSite: String,
    val password: String,
    val orderId: String,
    val customerEmail: String,
    val customerLastName: String,
    val customerFirstName: String,
    val customerAddress: String,
    val customerZip: String? = null,
    val customerCity: String? = null,
    val customerCountry: String? = null,
    val customerTel: String,
    val language: Language,
    val amount: Long,
    val amountSecond: Long? = null,
    val currency: Currency,
    val paymentType: Boolean,
    val orderProducts: String,
    val signature: String,
    val vad: String,
    val terminal: String,
    val tauxConversion: Float?,
    val batchNumber: String? = null,
    val merchantReference: String? = null,
    val paymentMethodChoice: PaymentMethodType,
    val merchantUserName: String,
    val merchantPassword: String
) : Parcelable

enum class PaymentMethodType {
    CB,
    PT,
    GP
}


enum class Currency {
    TND,
    EUR,
    USD
}