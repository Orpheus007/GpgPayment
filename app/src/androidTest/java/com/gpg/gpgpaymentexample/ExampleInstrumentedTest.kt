/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:04 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 9:21 AM
 *
 */

package com.gpg.gpgpaymentexample

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.gpg.gpgpaymentexample", appContext.packageName)
    }
}