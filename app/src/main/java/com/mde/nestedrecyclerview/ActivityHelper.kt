package com.mde.nestedrecyclerview

import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.view.View

fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
    return lazy { findViewById<T>(res) }
}

fun Context.convertDipsToPixel(dips: Int): Int {
    return (dips * resources.displayMetrics.density + 0.5f).toInt()
}