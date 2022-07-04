/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 9:21 AM
 *
 */

package com.gpg.gpgpaymentexample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gpg.gpgpayment.activity.PaymentActivity
import com.gpg.gpgpayment.entities.Currency
import com.gpg.gpgpayment.entities.Language
import com.gpg.gpgpayment.entities.PaymentParams
import com.gpg.gpgpayment.payment.PAYMENT_RESULT
import com.gpg.gpgpaymentexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val paymentResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        when (it.resultCode) {
            RESULT_OK -> {
                //Here the payment is finished successfully.
                Toast.makeText(this, it.data?.getStringExtra(PAYMENT_RESULT) ?: "Payment Successful!", Toast.LENGTH_LONG).show()
            }
            RESULT_CANCELED -> {
                //The user cancels the payment
                Toast.makeText(this, it.data?.getStringExtra(PAYMENT_RESULT) ?: "Payment Failed", Toast.LENGTH_LONG).show()
            }
            else -> {
                //An unknown error has occurred
                Toast.makeText(this, "Unknown Error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.click.setOnClickListener {
            val orderId = System.currentTimeMillis().toString()
            val amount = 10000
            val numSite = "March222"
            val pass = "tk#qvD70"
            val lang = "Fr"
            val typeTransaction = "1"
            val signature = "${numSite}${pass}${orderId}${amount}TND"

            val paymentParams = PaymentParams(
                numSite = "",
                password = "",
                orderId = orderId,
                customerEmail = "saber.jerda@gmail.com",
                customerLastName = "haithem",
                customerFirstName = "ben ali",
                customerAddress = "rue marsa",
                customerZip = "8075",
                customerCity = "TN",
                customerCountry = "TN",
                customerTel = "46655",
                language = Language.Fr,
                amount = 10000,
                amountSecond = 10000,
                currency = Currency.TND,
                paymentType = true,
                orderProducts = "Test App Mobile",
                signature = signature,
                vad = "",
                terminal = "",
                tauxConversion = null,
                batchNumber = null,
                merchantReference = null,
                merchantUserName = "",
                merchantPassword = ""
            )

            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("paymentParams", paymentParams)
            paymentResult.launch(intent)
        }
    }

}