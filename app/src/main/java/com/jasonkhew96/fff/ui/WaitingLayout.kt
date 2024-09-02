package com.jasonkhew96.fff.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import com.jasonkhew96.fff.R

class WaitingLayout(context: Context, attrs: AttributeSet? = null) :
    CustomViewGroup(context, attrs) {
    init {
        setBackgroundColor(Color.argb(128, 0, 0, 0))
    }

    private val waitText = TextView(context).apply {
        setText(R.string.waiting)
        setTextColor(Color.WHITE)
        layoutParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        this@WaitingLayout.addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        waitText.autoMeasure()
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        waitText.autoLayout(
            (measuredWidth - waitText.measuredWidth) / 2,
            (measuredHeight - waitText.measuredHeight) / 2
        )
    }
}