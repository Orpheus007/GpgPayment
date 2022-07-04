/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 12:34 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 12:34 PM
 *
 */

package com.gpg.gpgpayment.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

open class BaseViewModel : ViewModel() {
    val coroutineContext = viewModelScope.coroutineContext
    val scope = viewModelScope
}