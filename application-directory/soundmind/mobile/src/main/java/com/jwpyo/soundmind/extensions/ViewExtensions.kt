package com.jwpyo.soundmind.extensions

import android.graphics.Rect
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.lifecycle.MutableLiveData
import com.jwpyo.soundmind.utils.Constant.WIDTH_PER_HOUR
import org.threeten.bp.LocalTime
import kotlin.math.roundToInt

enum class StickyDirection { LEFT, RIGHT, CENTER }
data class TouchState(
    val isFocused: Boolean
)

fun View.setHorizontalDragListenerOnHolder(
    scrollView: HorizontalScrollView,
    lineView: View,
    stickyDirection: StickyDirection,
    step: Float,
    xLiveData: MutableLiveData<LocalTime>,
    touchStateLiveData: MutableLiveData<TouchState>,
) {
    val CLICK_DRAG_TOLERANCE = 10f

    var scrollViewScrollX = 0f
    var downRawX = 0f
    var downRawY = 0f
    var dX = 0f
    var dY = 0f

    setOnTouchListener { view, motionEvent ->
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        val action = motionEvent.action

        if (action == MotionEvent.ACTION_DOWN) {
            touchStateLiveData.value = TouchState(true)
            scrollViewScrollX = scrollView.scrollX.toFloat()
            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY
            dX = view.x - downRawX
            dY = view.y - downRawY

            true // Consumed
        } else if (action == MotionEvent.ACTION_MOVE) {
            val viewWidth = view.width
            val viewParent = view.parent as View
            val parentWidth = viewParent.width

            var newX = motionEvent.rawX + dX + scrollView.scrollX.toFloat() - scrollViewScrollX
            newX = Math.max(
                layoutParams.leftMargin.toFloat(),
                newX
            ) // Don't allow the FAB past the left hand side of the parent
            newX = Math.min(
                (parentWidth - viewWidth - layoutParams.rightMargin).toFloat(),
                newX
            ) // Don't allow the FAB past the right hand side of the parent

            newX = (newX / step).roundToInt() * step

            val minutes = (newX / WIDTH_PER_HOUR).roundToInt()
            xLiveData.postValue(LocalTime.of(minutes / 60, minutes % 60))

            when (stickyDirection) {
                StickyDirection.LEFT -> {
                    lineView.animate()
                        .x(newX)
                        .setDuration(0)
                        .start()
                }
                StickyDirection.RIGHT -> {
                    lineView.animate()
                        .x(newX + view.width)
                        .setDuration(0)
                        .start()
                }
                StickyDirection.CENTER -> {
                    lineView.animate()
                        .x(newX + view.width * 0.5f)
                        .setDuration(0)
                        .start()
                }
            }

            view.animate()
                .x(newX)
                .setDuration(0)
                .start()
            true // Consumed
        } else if (action == MotionEvent.ACTION_UP) {
            touchStateLiveData.value = TouchState(false)
            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY
            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY

            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                performClick()
            } else { // A drag
                true // Consumed
            }
        } else {
            // etc.
            false
        }
    }
}

fun View.enlargeTouchArea() {
    val parent = (this.parent as View)
    parent.post {
        val rect = Rect()
        getHitRect(rect)
        rect.top -= 100
        rect.left -= 100
        rect.bottom += 100
        rect.right += 100
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}