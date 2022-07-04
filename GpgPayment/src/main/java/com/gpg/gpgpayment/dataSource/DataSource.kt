/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 11:02 AM
 *
 */

package com.gpg.gpgpayment.dataSource

import com.gpg.gpgpayment.entities.PaymentMethod
import com.gpg.gpgpayment.entities.PaymentMethodParams
import com.gpg.gpgpayment.util.Janus

interface DataSource {
    suspend fun getPaymentMethods(
        params: PaymentMethodParams
    ): Janus<List<PaymentMethod>, Exception>
}