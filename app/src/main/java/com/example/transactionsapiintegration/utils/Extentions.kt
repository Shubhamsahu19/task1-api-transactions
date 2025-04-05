package com.dictatenow.androidapp.utils

import android.content.Context
import android.view.View
import android.widget.Toast

var toast: Toast? = null
fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.inVisible() {
    this.visibility = View.INVISIBLE
}

fun Context.makeToast(msg: String) {
    toast?.cancel()
    toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
    toast!!.show()
}