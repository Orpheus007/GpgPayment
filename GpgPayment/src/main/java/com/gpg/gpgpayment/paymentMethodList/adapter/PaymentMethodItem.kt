/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:05 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:59 AM
 *
 */

package com.gpg.gpgpayment.paymentMethodList.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gpg.gpgpayment.R
import com.gpg.gpgpayment.databinding.ItemPaymentMethodBinding
import com.gpg.gpgpayment.entities.PaymentMethod
import com.gpg.gpgpayment.entities.PaymentMethodType
import com.gpg.gpgpayment.paymentMethodList.PaymentMethodListViewModel
import com.gpg.gpgpayment.util.setOneClickListener
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

fun ImageView.load(url: String, context: Context) {
    if (url.isNotBlank()) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_image_placeholder)
            .error(R.drawable.ic_image_placeholder)
            .resize(200, 200)
            .centerInside()
            .into(this)
    } else {
        this.setImageDrawable(context.drawables(R.drawable.ic_image_placeholder))
    }
}

class PaymentMethodItem(
    private val context: Context,
    private val model: PaymentMethodListViewModel,
    private val onItemClicked: (value: PaymentMethodType) -> Unit
) : RecyclerView.Adapter<PaymentMethodItem.Holder>(), AutoUpdatableAdapter {

    var items: List<PaymentMethod> by Delegates.observable(emptyList()) { prop, old, new ->
        autoNotify(old, new) { o, n -> o.id == n.id }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemPaymentMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val content = items[position]
        holder.binding.apply {
            methodIcon.load(content.img, context)
            isSelected.isChecked = content.value == model.selectedPaymentMethod.value
            card.setOneClickListener {
                notifyDataSetChanged()
                onItemClicked(content.value)
            }

            methodName.text = content.name
        }

    }

    inner class Holder(val binding: ItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root)
}

interface AutoUpdatableAdapter {

    fun <T> RecyclerView.Adapter<*>.autoNotify(old: List<T>, new: List<T>, compare: (T, T) -> Boolean) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(old[oldItemPosition], new[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return old[oldItemPosition] == new[newItemPosition]
            }

            override fun getOldListSize() = old.size

            override fun getNewListSize() = new.size
        })

        diff.dispatchUpdatesTo(this)
    }
}

fun Context.drawables(id: Int): Drawable {
    return ContextCompat.getDrawable(this, id)!!
}