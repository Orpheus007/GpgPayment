/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:40 AM
 *
 */

package com.gpg.gpgpayment.paymentMethodList

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gpg.gpgpayment.R
import com.gpg.gpgpayment.dataSource.Api
import com.gpg.gpgpayment.dataSource.DataSourceImpl
import com.gpg.gpgpayment.databinding.FragmentPaymentMethodListBinding
import com.gpg.gpgpayment.entities.PaymentMethodType
import com.gpg.gpgpayment.entities.PaymentParams
import com.gpg.gpgpayment.entities.PaymentParamsFinal
import com.gpg.gpgpayment.paymentMethodList.adapter.PaymentMethodItem
import com.gpg.gpgpayment.util.*


class PaymentMethodListFragment : Fragment() {

    private var _binding: FragmentPaymentMethodListBinding? = null
    private val binding get() = _binding!!

    private lateinit var paymentParams: PaymentParams
    private val model by viewModels<PaymentMethodListViewModel>()

    private lateinit var adapter: PaymentMethodItem

    private val dataSource by lazy {
        DataSourceImpl(Api.invoke(ConnectivityInterceptorImpl(requireContext()), requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentMethodListBinding.inflate(inflater)
        setupAdapter()
        requireArguments().getParcelable<PaymentParams>("paymentParams")?.let {
            paymentParams = it
        }
        model.saveParams(paymentParams)
        setupMenu()
        model.listState.observe(viewLifecycleOwner) {
            binding.apply {
                when (it) {
                    ListState.Shown -> {
                        recList.visibility = View.VISIBLE
                        description.visibility = View.VISIBLE
                        emptyListHolder.visibility = View.GONE
                        errorHolder.visibility = View.GONE
                        recListLoading.visibility = View.GONE

                        toolbar.menu.findItem(R.id.refresh).isEnabled = true
                        payment.visibility = View.VISIBLE
                    }
                    ListState.Loading -> {
                        recList.visibility = View.GONE
                        description.visibility = View.GONE
                        emptyListHolder.visibility = View.GONE
                        errorHolder.visibility = View.GONE
                        recListLoading.visibility = View.VISIBLE

                        toolbar.menu.findItem(R.id.refresh).isEnabled = false
                        payment.visibility = View.GONE
                    }
                    ListState.Empty -> {
                        recList.visibility = View.GONE
                        description.visibility = View.GONE
                        emptyListHolder.visibility = View.VISIBLE
                        errorHolder.visibility = View.GONE
                        recListLoading.visibility = View.GONE

                        toolbar.menu.findItem(R.id.refresh).isEnabled = true
                        payment.visibility = View.GONE
                    }
                    ListState.Error -> {
                        recList.visibility = View.GONE
                        description.visibility = View.GONE
                        emptyListHolder.visibility = View.GONE
                        errorHolder.visibility = View.VISIBLE
                        recListLoading.visibility = View.GONE

                        toolbar.menu.findItem(R.id.refresh).isEnabled = false
                        payment.visibility = View.GONE
                    }
                    else -> {
                        recList.visibility = View.GONE
                        description.visibility = View.GONE
                        emptyListHolder.visibility = View.GONE
                        errorHolder.visibility = View.GONE
                        recListLoading.visibility = View.VISIBLE

                        toolbar.menu.findItem(R.id.refresh).isEnabled = false
                        payment.visibility = View.GONE
                    }
                }
            }
        }
        model.selectedPaymentMethod.observe(viewLifecycleOwner) {
            binding.payment.isEnabled = it != null
        }
        model.onStart(dataSource, {
            binding.errorText.text = it
        }, {
            adapter.items = it
        })
        binding.payment.setOneClickListener {
            model.selectedPaymentMethod.value?.let {
                startWebPayment(it)
            }
        }
        binding.errorButton.setOneClickListener {
            model.fetchList(dataSource, {
                binding.errorText.text = it
            }, {
                adapter.items = it
            })
        }
        return binding.root
    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.menu_payment_method_list)
        binding.toolbar.title = resources.getString(R.string.payment_methods)
        addReturnButton(binding.toolbar, {
            requireActivity().finish()
        })
        binding.toolbar.menu.findItem(R.id.refresh).setOnMenuItemClickListener {
            model.fetchList(dataSource, {
                binding.errorText.text = it
            }, {
                adapter.items = it
            })
            return@setOnMenuItemClickListener true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        adapter = PaymentMethodItem(requireContext(), model) {
            model.saveSelectedPaymentMethod(it)
        }
        binding.recList.layoutManager = WrapContentLinearLayoutManager(requireContext())
        binding.recList.adapter = adapter
    }

    private fun startWebPayment(paymentMethod: PaymentMethodType) {
        Log.i("Pandora", "Payment method => $paymentMethod")
        val paramsFinal = PaymentParamsFinal(
            model.params.numSite,
            model.params.password,
            model.params.orderId,
            model.params.customerEmail,
            model.params.customerLastName,
            model.params.customerFirstName,
            model.params.customerAddress,
            model.params.customerZip,
            model.params.customerCity,
            model.params.customerCountry,
            model.params.customerTel,
            model.params.language,
            model.params.amount,
            model.params.amountSecond,
            model.params.currency,
            model.params.paymentType,
            model.params.orderProducts,
            model.params.signature,
            model.params.vad,
            model.params.terminal,
            model.params.tauxConversion,
            model.params.batchNumber,
            model.params.merchantReference,
            paymentMethod,
            model.params.merchantUserName,
            model.params.merchantPassword
        )
        val bundle = bundleOf("paymentParamsFinal" to paramsFinal)
        findNavController().navigate(R.id.action_paymentMethodListFragment_to_paymentFragment, bundle)
    }
}