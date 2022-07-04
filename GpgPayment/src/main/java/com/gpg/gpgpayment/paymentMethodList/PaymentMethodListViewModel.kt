/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:22 AM
 *
 */

package com.gpg.gpgpayment.paymentMethodList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.gpg.gpgpayment.dataSource.DataSource
import com.gpg.gpgpayment.entities.PaymentMethod
import com.gpg.gpgpayment.entities.PaymentMethodParams
import com.gpg.gpgpayment.entities.PaymentMethodType
import com.gpg.gpgpayment.entities.PaymentParams
import com.gpg.gpgpayment.util.BaseViewModel
import com.gpg.gpgpayment.util.Err
import com.gpg.gpgpayment.util.Ok
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentMethodListViewModel(private val savedStateHandle: SavedStateHandle) : BaseViewModel() {
    private var isThisTheFirstTime: Boolean
    lateinit var params: PaymentParams
    var listState: MutableLiveData<ListState> = MutableLiveData(ListState.Loading)
    var paymentMethodsList: List<PaymentMethod> = emptyList()
    var selectedPaymentMethod: MutableLiveData<PaymentMethodType?> = MutableLiveData(null)

    init {

        isThisTheFirstTime = true

        savedStateHandle.get<PaymentParams>("paramsMVM")?.let {
            Log.i("Pandora", "SavedStateCalled?")
            params = it
        }

        savedStateHandle.get<List<PaymentMethod>>("listMVM")?.let {
            paymentMethodsList = it
        }

        savedStateHandle.getLiveData<ListState>("listStateMVM")

        savedStateHandle.get<Boolean>("isThisTheFirstTimeMVM")?.let {
            isThisTheFirstTime = it
        }

        savedStateHandle.getLiveData<PaymentMethodType>("selectedPaymentMethod")
    }

    fun saveSelectedPaymentMethod(paymentMethodType: PaymentMethodType) {
        this.selectedPaymentMethod.value = paymentMethodType
        savedStateHandle.set("selectedPaymentMethod", paymentMethodType)
    }

    fun saveParams(params2: PaymentParams) {
        this.params = params2
        savedStateHandle.set("paramsMVM", params2)
    }

    private fun saveList(list: List<PaymentMethod>) {
        this@PaymentMethodListViewModel.paymentMethodsList = list
        savedStateHandle.set("listMVM", list)
    }

    private fun saveListState(state: ListState) {
        listState.value = state
        savedStateHandle.set("listStateMVM", state)
    }

    private suspend fun saveIsThisTheFirstTime(bool: Boolean) {
        isThisTheFirstTime = bool
        savedStateHandle.set("isThisTheFirstTimeMVM", bool)
    }

    fun onStart(
        dataSource: DataSource,
        listError: (error: String) -> Unit,
        publishList: (data: List<PaymentMethod>) -> Unit
    ) = scope.launch(Dispatchers.Default) {
        if (isThisTheFirstTime) {
            Log.i("Pandora", "ThisIsTheFirstTime => $isThisTheFirstTime")
            val paymentMethod = PaymentMethodParams(params.numSite, params.language, params.signature)
            when (val result = dataSource.getPaymentMethods(paymentMethod)) {
                is Ok -> {
                    saveList(result.value)
                    if (result.value.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            publishList(result.value)
                            saveListState(ListState.Shown)
                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            saveListState(ListState.Empty)
                        }
                    }
                }
                is Err -> {
                    withContext(Dispatchers.Main) {
                        listError(result.error.localizedMessage ?: "Unknown Error")
                        saveListState(ListState.Error)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                saveIsThisTheFirstTime(false)
            }
        } else {
            withContext(Dispatchers.Main) {
                Log.w("Pandora", "NOT The First Time => $isThisTheFirstTime")
                if (paymentMethodsList.isEmpty()) {
                    saveListState(ListState.Empty)
                } else {
                    publishList(paymentMethodsList)
                }
            }

        }
    }

    fun fetchList(
        dataSource: DataSource,
        listError: (error: String) -> Unit,
        publishList: (data: List<PaymentMethod>) -> Unit
    ) = scope.launch(Dispatchers.Default) {
        withContext(Dispatchers.Main) {
            saveListState(ListState.Loading)
        }
        val paymentMethod = PaymentMethodParams(params.numSite, params.language, params.signature)
        when (val result = dataSource.getPaymentMethods(paymentMethod)) {
            is Ok -> {
                saveList(result.value)
                if (result.value.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        publishList(result.value)
                        saveListState(ListState.Shown)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        saveListState(ListState.Empty)
                    }
                }
            }
            is Err -> {
                withContext(Dispatchers.Main) {
                    listError(result.error.localizedMessage ?: "Unknown Error")
                    saveListState(ListState.Error)
                }

            }
        }
    }
}