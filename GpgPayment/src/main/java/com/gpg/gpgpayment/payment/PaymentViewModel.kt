/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:25 AM
 *
 */

package com.gpg.gpgpayment.payment

import androidx.lifecycle.SavedStateHandle
import com.gpg.gpgpayment.entities.PaymentParamsFinal
import com.gpg.gpgpayment.util.BaseViewModel

class PaymentViewModel(private val savedStateHandle: SavedStateHandle) : BaseViewModel() {
    var isThisTheFirstTime: Boolean = true
    lateinit var params: PaymentParamsFinal
    var paymentState: PaymentState = PaymentState.Pending
    var error: String = ""

    init {
        savedStateHandle.get<Boolean>("isThisTheFirstTime")?.let {
            isThisTheFirstTime = it
        }

        savedStateHandle.get<PaymentParamsFinal>("params")?.let {
            params = it
        }

        savedStateHandle.get<PaymentState>("paymentState")?.let {
            paymentState = it
        }
        savedStateHandle.get<String>("errorString")?.let {
            error = it
        }
    }

    fun saveParams(paymentParams: PaymentParamsFinal) {
        params = paymentParams
        savedStateHandle.set("params", paymentParams)
    }

    fun savePaymentState(state: PaymentState) {
        paymentState = state
        savedStateHandle.set("paymentState", state)
    }

    fun saveErrorString(string: String) {
        error = string
        savedStateHandle.set("errorString",string)
    }
}