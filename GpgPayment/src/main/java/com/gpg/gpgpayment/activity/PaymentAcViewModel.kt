package com.gpg.gpgpayment.activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gpg.gpgpayment.entities.PaymentParams

class PaymentAcViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    lateinit var paymentParams: PaymentParams

    init {
        savedStateHandle.get<PaymentParams>("paymentParams")?.let {
            paymentParams = it
        }
    }

    fun savePaymentParams(paymentParams: PaymentParams) {
        this.paymentParams = paymentParams
        savedStateHandle.set("paymentParams", paymentParams)
    }
}