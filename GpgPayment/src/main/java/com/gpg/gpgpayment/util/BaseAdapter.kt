/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:04 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:19 AM
 *
 */

package com.gpg.gpgpayment.util

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.gpg.gpgpayment.R

class BaseAdapter<Item, Binding : ViewBinding>(private val inflate: () -> Binding) : RecyclerView.Adapter<BaseAdapter.BindingHolder<Binding>>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return conditionAreItemsTheSame.invoke(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return conditionAreContentsTheSame.invoke(oldItem, newItem)
        }
    })

    fun publish(collection: Collection<Item>) {
        differ.submitList(collection.toMutableList())
    }

    private lateinit var conditionAreItemsTheSame: (old: Item, new: Item) -> Boolean
    private lateinit var conditionAreContentsTheSame: (old: Item, new: Item) -> Boolean
    private lateinit var filterCondition: (query: String, item: Item) -> Boolean
    private lateinit var unFilteredData: () -> List<Item>

    private var bindView: (Binding.(item: Item, position: Int) -> Unit)? = null
    private var error: (Binding.(e: Exception) -> Unit)? = null
    private var clickListener: (Binding.(item: Item) -> Unit)? = null
    private var longClickListener: (Binding.(item: Item) -> Unit)? = null
    private lateinit var parent: ViewGroup


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<Binding> {
        this.parent = parent
        val binding = inflate()
        return BindingHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: BindingHolder<Binding>, position: Int) {
        holder.itemView.setTag(
            R
                .id.adapterPosition, position
        )
        try {
            holder.binding.run { bindView?.invoke(this, differ.currentList.elementAt(position), position) }
        } catch (exp: Exception) {
            error?.invoke(holder.binding, exp)
        }
        clickListener?.let { listener ->
            holder.binding.root.setOneClickListener {
                listener.invoke(
                    holder.binding,
                    differ.currentList.elementAt(position)
                )
            }
        }
        longClickListener?.let { listener ->
            holder.binding.root.setOnLongClickListener {
                listener.invoke(
                    holder.binding,
                    differ.currentList.elementAt(position)
                );true
            }
        }
    }

    class BindingHolder<Binding : ViewBinding>(val binding: Binding) : RecyclerView.ViewHolder(binding.root)

    fun setOnItemClickListener(listener: Binding.(item: Item) -> Unit): BaseAdapter<Item, Binding> {
        this.clickListener = listener
        return this
    }

    fun setOnItemLongClickListener(listener: Binding.(item: Item) -> Unit): BaseAdapter<Item, Binding> {
        this.longClickListener = listener
        return this
    }

    fun bindView(listener: Binding.(item: Item, position: Int) -> Unit): BaseAdapter<Item, Binding> {
        this.bindView = listener
        return this
    }

    fun mapViewError(listener: Binding.(e: Exception) -> Unit): BaseAdapter<Item, Binding> {
        this.error = listener
        return this
    }

    fun unFilterList(listener: () -> List<Item>): BaseAdapter<Item, Binding> {
        this.unFilteredData = listener
        return this
    }

    fun filterList(listener: (query: String, item: Item) -> Boolean): BaseAdapter<Item, Binding> {
        this.filterCondition = listener
        return this
    }

    fun conditionAreItemsTheSame(
        listener: (old: Item, new: Item) -> Boolean
    ): BaseAdapter<Item, Binding> {
        this.conditionAreItemsTheSame = listener
        return this
    }

    fun conditionAreContentsTheSame(
        listener: (old: Item, new: Item) -> Boolean
    ): BaseAdapter<Item, Binding> {
        this.conditionAreContentsTheSame = listener
        return this
    }

    fun setUpSearchView(searchView: SearchView, recyclerView: RecyclerView): BaseAdapter<Item, Binding> {
        //----------------SEARCH VIEW-----------
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query.isNotBlank()) {
                    recyclerView.scrollToPosition(0)
                    filterByQuery(query)
                } else {
                    differ.submitList(unFilteredData.invoke())
                }
                return false
            }
        })
        //---------------------------------------
        return this
    }

    private fun filterByQuery(query: String) {
        val filter = ArrayList<Item>()
        for (item in unFilteredData.invoke()) {
            if (filterCondition.invoke(query, item)) {
                filter.add(item)
            }
        }
        differ.submitList(filter)
    }
}

class OneClickListener(private val interval: Long = 300L, private val listenerBlock: (View) -> Unit) : View.OnClickListener {
    private var lastClickTime = 0L

    override fun onClick(v: View) {
        val time = System.currentTimeMillis()
        if (time - lastClickTime >= interval) {
            lastClickTime = time
            listenerBlock(v)
        }
    }
}

fun View.setOneClickListener(debounceInterval: Long = 300L, listenerBlock: (View) -> Unit) =
    setOnClickListener(OneClickListener(debounceInterval, listenerBlock))