/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 1:24 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 1:24 PM
 *
 */

package com.gpg.gpgpayment.util

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    //... constructor

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
        }
    }
}
