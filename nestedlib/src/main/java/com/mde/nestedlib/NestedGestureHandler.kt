package com.mde.nestedlib

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * The main method is [shouldIntercept]. It returns true if the parent should intercept th touch event
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

    var mLastEvent: MotionEvent? = null

    fun shouldIntercept(e: MotionEvent): Boolean {
        var intercepted: Boolean = true //default state = true
        val action = e.getAction()

        if (action == MotionEvent.ACTION_DOWN) {
            mLastEvent = MotionEvent.obtain(e)
        }

        val lastEvent: MotionEvent? = mLastEvent

        if (action == MotionEvent.ACTION_MOVE && lastEvent != null) {

            val dx = Math.round(e.x - lastEvent.x)
            val dy = Math.round(e.y - lastEvent.y)

            val slop = ViewConfiguration.get(context).scaledTouchSlop
            if (isForChild(dx.toFloat(), dy.toFloat(), slop)) {
                intercepted = false //do not intercept
            }
        }

        return intercepted
    }

    /**
     * @param distanceX : delta X scrolled
     * @param distanceY : delta Y scrolled
     * @param slop      : Distance in pixels a touch can wander before we think the user is scrolling
     * @return : true if the Y scroll distance is more than the slop threshold
     *           and the scroll angle is between +-pi/4 and +-3*pi/4
     */
    private fun isForChild(distanceX: Float, distanceY: Float, slop: Int): Boolean {
        return if (orientation == LinearLayoutManager.VERTICAL) Math.abs(distanceX) >= Math.abs(distanceY) && Math.abs(distanceX) > slop
        else Math.abs(distanceY) >= Math.abs(distanceX) && Math.abs(distanceY) > slop
    }

    /**
     * Dispatch the current event to the child view
     * @return true if the event has been correctly dispatched to the view.
     * If true, the parent should consider to not intercept this event, by returning false to
     * its onInterceptTouchEvent method.
     */
    fun dispatchToChild(recyclerView: RecyclerView, e: MotionEvent): Boolean {
        val view: View = recyclerView.findChildViewUnder(e.x, e.y)
        return (view is ScrollableChild && view.canChildScroll())
    }
}