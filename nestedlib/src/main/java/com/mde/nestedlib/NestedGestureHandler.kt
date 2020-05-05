package com.mde.nestedlib

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * It returns true if the parent should intercept the touch event for itself or false
 * if it must dispatch the event to the child view. It that case, the parent
 * should return false in its onInterceptTouchEvent.
 * @param context : Current context
 * @param orientation : Orientation of the parent. Should be one of [LinearLayoutManager.VERTICAL] or [LinearLayoutManager.HORIZONTAL]
 *
 */
class NestedGestureHandler(val context: Context, val orientation: Int) {

    /**
     * max amount of scroll angle required to dispatch event to child
     */
    var mAngle: Double = Math.PI / 4

    private var mLastEvent: MotionEvent? = null

    /**
     * This methods first calculate the scroll angle
     *
     */
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
}
