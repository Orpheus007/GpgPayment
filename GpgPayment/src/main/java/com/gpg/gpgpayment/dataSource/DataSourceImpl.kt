/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 11:05 AM
 *
 */

package com.gpg.gpgpayment.dataSource

import com.gpg.gpgpayment.entities.PaymentMethod
import com.gpg.gpgpayment.entities.PaymentMethodParams
import com.gpg.gpgpayment.util.Janus
import com.gpg.gpgpayment.util.Ok
import com.gpg.gpgpayment.util.apiWrapper

class DataSourceImpl(
    private val api: Api
) : DataSource {
    override suspend fun getPaymentMethods(
        params: PaymentMethodParams
    ): Janus<List<PaymentMethod>, Exception> {
        //delay(2000)
        return apiWrapper {
            val result = api.getPaymentMethods(
                params
            )
            Ok(result)
        }
        /*  return Ok(
              listOf(
                  PaymentMethod(
                      0, "Credit Card", "https://ma.visamiddleeast" +
                              ".com/dam/VCOM/regional/ap/taiwan/global-elements/images/tw-visa-gold-card-498x280.png", PaymentMethodType.CB
                  ),
                  PaymentMethod(
                      0, "Post", "https://cdn.pixabay.com/photo/2015/10/07/12/17/post-976115_960_720.png", PaymentMethodType.PT
                  ),
                  PaymentMethod(
                      0, "GpPay", "https://www.idee.com.tn/wp-content/uploads/2018/07/gpg-1.png", PaymentMethodType.GP
                  ),
              )
          ) */
    }
}