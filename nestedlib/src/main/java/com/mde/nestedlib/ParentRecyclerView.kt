package com.mde.nestedlib

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

class ParentRecyclerView(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView(context, attrs, defStyle) {

    var gestureHandler = NestedGestureHandler(context, LinearLayoutManager.VERTICAL)

    val isScrollHandled: Boolean by lazy {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NestedRecycler, defStyle, 0)
        val isScrollHandle = a.getBoolean(R.styleable.NestedRecycler_nestedEnabled, false)
        a.recycle()
        isScrollHandle
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (isScrollHandled && gestureHandler.shouldIntercept(e)) {
            // If the event has been correctly dispatched to the child view, return false, as the parent
            // should not intercept the event anymore
            return !gestureHandler.dispatchToChild(this@ParentRecyclerView, e)
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        if (layout is LinearLayoutManager) {
            gestureHandler = NestedGestureHandler(context, layout.orientation )
        }
    }
}

