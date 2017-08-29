package com.mde.nestedlib

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

class ParentRecyclerView : RecyclerView {


    var gestureHandler = NestedGestureHandler(context, LinearLayoutManager.VERTICAL)

    val isScrollHandled: Boolean by lazy {
        true
//        val a = context.obtainStyledAttributes(attrs, R.styleable.NestedRecycler, defStyle, 0)
//        val isScrollHandle = a.getBoolean(R.styleable.NestedRecycler_nestedEnabled, false)
//        a.recycle()
//        isScrollHandle
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (isScrollHandled && gestureHandler.isForChild(e)) {
            // If the event has been correctly dispatched to the child view, return false, as the parent
            // should not intercept the event anymore
            if (gestureHandler.dispatchToChild(this@ParentRecyclerView, e)) {
                return false
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        if (layout is LinearLayoutManager) {
            gestureHandler = NestedGestureHandler(context, layout.orientation)
        }
    }
}

