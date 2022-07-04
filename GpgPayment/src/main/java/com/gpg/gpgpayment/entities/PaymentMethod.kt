/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:56 AM
 *
 */

package com.gpg.gpgpayment.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethod(
    val id: Int,
    val name: String,
    val img: String,
    val value: PaymentMethodType
) : Parcelable
