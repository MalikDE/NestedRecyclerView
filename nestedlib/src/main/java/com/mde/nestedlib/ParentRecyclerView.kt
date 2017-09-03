package com.mde.nestedlib

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * A custom RecyclerView that uses a [NestedGestureHandler] to dispatch event to other nested recycler view.
 * Use [setLayoutManager] methods to define a scroll angle.
 *
 * Using xml, set [R.styleable.NestedRecycler_nestedEnabled] to true in order to allow nested scroll
 *
 */
class ParentRecyclerView : RecyclerView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initAttributes(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initAttributes(attrs, 0)
    }

    var mGestureHandler = NestedGestureHandler(context, LinearLayoutManager.VERTICAL)
    var mIsScrollHandled: Boolean = false

    fun initAttributes(attrs: AttributeSet, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NestedRecycler, defStyle, 0)
        mIsScrollHandled = a.getBoolean(R.styleable.NestedRecycler_nestedEnabled, false)
        a.recycle()
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (mIsScrollHandled && mGestureHandler.isForChild(e)) {
            // If the event has been correctly dispatched to the child view, return false, as the parent
            // should not intercept the event anymore
            if (mGestureHandler.dispatchToChild(this@ParentRecyclerView, e)) {
                return false
            }
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        if (layout is LinearLayoutManager) {
            mGestureHandler = NestedGestureHandler(context, layout.orientation)
        }
    }

    fun setLayoutManager(layout: LayoutManager?, angle: Double) {
        setLayoutManager(layout)
        mGestureHandler.mAngle = angle
    }
}

