/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:59 AM
 *
 */

package com.gpg.gpgpayment.payment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gpg.gpgpayment.R
import com.gpg.gpgpayment.databinding.FragmentPaymentBinding
import com.gpg.gpgpayment.entities.PaymentParamsFinal
import com.gpg.gpgpayment.util.addReturnButton
import java.net.URLEncoder
import java.security.MessageDigest

const val PAYMENT_RESULT = "PAYMENT_RESULT"

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var paymentParams: PaymentParamsFinal

    private val model: PaymentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().getParcelable<PaymentParamsFinal>("paymentParamsFinal")?.let {
            paymentParams = it
        }
        model.saveParams(paymentParams)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        setupBrowser()
        setupMenu()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when (val state = model.paymentState) {
            PaymentState.Success -> {
                val data = Intent()
                data.putExtra(PAYMENT_RESULT, resources.getString(R.string.payment_successful))
                requireActivity().setResult(Activity.RESULT_CANCELED, data)
                requireActivity().finish()
            }
            PaymentState.Error -> {
                val data = Intent()
                data.putExtra(PAYMENT_RESULT, model.error)
                requireActivity().setResult(Activity.RESULT_CANCELED, data)
                requireActivity().finish()
            }
            PaymentState.Pending -> {
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupBrowser() {
        model.params.apply {
            val encrypted = signature.sha1
            val encryptedPass = password.md5
            val paymentType = (if (paymentType) 1 else 0).toString()
            val url = "https://preprod.gpgcheckout.com/Paiement_test/Validation_paiementmobile.php"
            val postData = "NumSite=" + URLEncoder.encode(numSite, "UTF-8") +
                    "&TypeTransaction=" + URLEncoder.encode("Mobile", "UTF-8") +
                    "&signature=" + URLEncoder.encode(encrypted, "UTF-8") +
                    "&Password=" + URLEncoder.encode(encryptedPass, "UTF-8") +
                    "&orderID=" + URLEncoder.encode(orderId, "UTF-8") +
                    "&Amount=" + URLEncoder.encode(amount.toString(), "UTF-8") +
                    "&Currency=" + URLEncoder.encode(currency.name, "UTF-8") +
                    "&Language=" + URLEncoder.encode(language.name, "UTF-8") +
                    "&EMAIL=" + URLEncoder.encode(customerEmail, "UTF-8") +
                    "&CustLastName=" + URLEncoder.encode(customerLastName, "UTF-8") +
                    "&CustFirstName=" + URLEncoder.encode(customerFirstName, "UTF-8") +
                    "&CustAddress=" + URLEncoder.encode(customerAddress, "UTF-8") +
                    "&CustZIP=" + URLEncoder.encode(customerZip ?: "", "UTF-8") +
                    "&CustCity=" + URLEncoder.encode(customerCity ?: "", "UTF-8") +
                    "&CustCountry=" + URLEncoder.encode(customerCountry ?: "", "UTF-8") +
                    "&CustTel=" + URLEncoder.encode(customerTel, "UTF-8") +
                    "&PayementType=" + URLEncoder.encode(paymentType, "UTF-8") +
                    "&MerchandSession=" + URLEncoder.encode("", "UTF-8") +
                    "&orderProducts=" + URLEncoder.encode(orderProducts, "UTF-8") +
                    "&vad=" + URLEncoder.encode(vad, "UTF-8") +
                    "&Terminal=" + URLEncoder.encode(terminal, "UTF-8") +
                    "&TauxConversion=" + URLEncoder.encode(tauxConversion.toString(), "UTF-8") +
                    "&ChoixPaiement=" + URLEncoder.encode(paymentMethodChoice.name, "UTF-8") +
                    "&MerchantReference=" + URLEncoder.encode(merchantReference ?: "", "UTF-8") +
                    "&AmountSecond=" + URLEncoder.encode(amountSecond.toString(), "UTF-8") +
                    "&BatchNumber=" + URLEncoder.encode(batchNumber ?: "", "UTF-8") +
                    "&_user=" + URLEncoder.encode(merchantUserName, "UTF-8") +
                    "&_pass=" + URLEncoder.encode(merchantPassword, "UTF-8")

            Log.i("Pandora", "Post data => $postData")

            binding.webView.settings.javaScriptEnabled = true
            binding.webView.settings.loadsImagesAutomatically = true
            binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            binding.webView.webViewClient = MyBrowser(binding)
            binding.webView.postUrl(url, postData.toByteArray())

            //Call this after setting up your webview and enabling javascript
            binding.webView.addJavascriptInterface(WebAppInterface(successExiting = {
                model.savePaymentState(PaymentState.Success)
                val data = Intent()
                data.putExtra(PAYMENT_RESULT, resources.getString(R.string.payment_successful))
                requireActivity().setResult(Activity.RESULT_CANCELED, data)
                requireActivity().finish()
            }, errorExiting = {
                model.savePaymentState(PaymentState.Error)
                model.saveErrorString(it)
                val data = Intent()
                data.putExtra(PAYMENT_RESULT, it)
                requireActivity().setResult(Activity.RESULT_CANCELED, data)
                requireActivity().finish()
            }), "Android")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null
    }


    private class MyBrowser(val binding: FragmentPaymentBinding) : WebViewClient() {
        @SuppressLint("JavascriptInterface")
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            view.addJavascriptInterface(object : Any() {
                @JavascriptInterface
                @Throws(Exception::class)
                fun performClick() {
                }

            }, "login")
            return true
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            binding.recListLoading.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            binding.recListLoading.visibility = View.GONE
            super.onPageFinished(view, url)
        }

    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.menu_payment_method_list)
        binding.toolbar.title = resources.getString(R.string.payment)
        addReturnButton(binding.toolbar, {
            findNavController().popBackStack(R.id.paymentMethodListFragment, false)
        })
        binding.toolbar.menu.findItem(R.id.refresh).setOnMenuItemClickListener {
            binding.webView.loadUrl(binding.webView.originalUrl ?: "")
            return@setOnMenuItemClickListener true
        }
    }
}

/** Instantiate the interface and set the context  */
class WebAppInterface(
    private val successExiting: () -> Unit,
    private val errorExiting: (message: String) -> Unit
) {

    /** Await response and execute success  */
    @JavascriptInterface
    fun success(message: String) {
        successExiting()
    }

    /** Await response and execute error  */
    @JavascriptInterface
    fun error(message: String) {
        errorExiting(message)
    }
}

val String.sha1: String
    get() {
        val bytes = MessageDigest.getInstance("SHA-1").digest(this.toByteArray(charset("UTF-8")))
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

val String.md5: String
    get() {
        return MessageDigest.getInstance("MD5").digest(this.toByteArray(charset("UTF-8"))).joinToString("") {
            "%02x".format(it)
        }
    }