package com.gpg.gpgpayment.util

import android.util.TypedValue
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.gpg.gpgpayment.R

fun Fragment.addReturnButton(toolBar: MaterialToolbar, listener: () -> Unit, customIcon: Int? = null) {
    val outValue = TypedValue()
    requireActivity().theme.resolveAttribute(android.R.attr.homeAsUpIndicator, outValue, true)
    toolBar.setNavigationIcon(customIcon ?: outValue.resourceId)
    toolBar.navigationIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.white))
    toolBar.setNavigationOnClickListener {
        listener()
    }
}


fun Fragment.changeStatusBarColor(color: Int) {
    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    activity?.window?.statusBarColor = color
}