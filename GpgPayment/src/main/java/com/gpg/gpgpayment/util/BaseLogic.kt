/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 12:22 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 12:22 PM
 *
 */

package com.gpg.gpgpayment.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

abstract class BaseLogic(private val viewModelContext: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = viewModelContext + Dispatchers.Default
}