package com.mde.nestedrecyclerview

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.IdRes

fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
    return lazy { findViewById<T>(res) }
}

fun Context.convertDipsToPixel(dips: Int): Int {
    return (dips * resources.displayMetrics.density + 0.5f).toInt()
}
