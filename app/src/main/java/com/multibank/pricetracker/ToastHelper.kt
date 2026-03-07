package com.multibank.pricetracker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object ToastHelper {

    private var toast: Toast? = null
    private val handler = Handler(Looper.getMainLooper())

    fun show(context: Context, message: String) {

        handler.post {

            toast?.cancel()

            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}