package com.mde.nestedlib

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * The main method is [isForChild]. It returns true if the parent should intercept the touch event
 * for itself or false if it must dispatch the event to the child view. It that case, the parent
 * should return false in its onInterceptTouchEvent override method.
 * @param context : Current context
 * @param orientation : Orientation of the parent. Should be one of [LinearLayoutManager.VERTICAL] or [LinearLayoutManager.HORIZONTAL]
 *
 */
class NestedGestureHandler(val context: Context, val orientation: Int) {

    interface ScrollableChild {
        /**
         * This method must be override by child views that would like to scroll
         */
        fun canChildScroll(): Boolean
    }

    /**
     * max amount of scroll angle required to dispatch event to child
     */
    var mAngle: Double = Math.PI / 4

    private var mLastEvent: MotionEvent? = null

    fun isForChild(e: MotionEvent): Boolean {
        var forChild = false
        val action = e.getAction()

        if (action == MotionEvent.ACTION_DOWN) {
            mLastEvent = MotionEvent.obtain(e)
        }

        mLastEvent?.let {
            if (action == MotionEvent.ACTION_MOVE) {
                val dx: Double = Math.abs(Math.round(e.x - it.x)).toDouble()
                val dy: Double = Math.abs(Math.round(e.y - it.y)).toDouble()
                val slopVector = Math.sqrt(dx * dx + dy * dy)

                /**
                 * forChild is true if mAngle performed by touch vector is <= nested mAngle
                 */
                forChild = slopVector > ViewConfiguration.get(context).scaledTouchSlop &&
                        if (orientation == LinearLayoutManager.VERTICAL) Math.atan2(dy, dx) <= mAngle
                        else Math.atan2(dy, dx) >= mAngle

            }
        }
        return forChild
    }

    /**
     * Dispatch the current event to the child view
     * @return true if the event has been correctly dispatched to the view.
     * If true, the parent should consider to not intercept this event, by returning false to
     * its onInterceptTouchEvent method.
     */
    fun dispatchToChild(recyclerView: RecyclerView, e: MotionEvent): Boolean {
        val view: View? = recyclerView.findChildViewUnder(e.x, e.y)
        return (view is ScrollableChild && view.canChildScroll())
    }
}