package com.mde.nestedrecylcer

import android.app.Activity
import android.support.annotation.IdRes
import android.view.View

fun <T : View> Activity.bind(@IdRes res: Int): Lazy<T> {
    return lazy { findViewById<T>(res) }
}
