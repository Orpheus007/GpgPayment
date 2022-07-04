package com.gpg.gpgpayment.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.gpg.gpgpayment.R
import com.gpg.gpgpayment.databinding.ActivityPaymentBinding
import com.gpg.gpgpayment.entities.PaymentParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentActivity : AppCompatActivity() {

    private lateinit var navHost: NavHostFragment
    private lateinit var navController: NavController

    private lateinit var binding: ActivityPaymentBinding

    private val model by viewModels<PaymentAcViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController
        val view = binding.root
        setContentView(view)

        intent.getParcelableExtra<PaymentParams>("paymentParams")?.let {
            model.savePaymentParams(it)
        }

        if (navController.currentDestination?.id == R.id.splashFragment) {
            model.viewModelScope.launch(Dispatchers.Main) {
                delay(1500)
                val bundle = bundleOf("paymentParams" to model.paymentParams)
                navController.navigate(R.id.paymentMethodListFragment, bundle)
            }
        }

    }
}