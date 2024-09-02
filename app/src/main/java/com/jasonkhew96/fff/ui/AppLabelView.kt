package com.jasonkhew96.fff.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import android.widget.TextView

class AppLabelView(context: Context, attrs: AttributeSet? = null) :
    CustomViewGroup(context, attrs) {

    val labelView = TextView(context).apply {
        layoutParams =
            MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                topMargin = 8.dp
                leftMargin = 16.dp
                bottomMargin = 4.dp
            }
        this@AppLabelView.addView(this)
    }

    val packageNameView = TextView(context).apply {
        layoutParams =
            MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftMargin = 16.dp
                bottomMargin = 8.dp
            }
        this@AppLabelView.addView(this)
    }

    val switch = Switch(context).apply {
        layoutParams =
            MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                rightMargin = 16.dp
            }
        this@AppLabelView.addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        switch.autoMeasure()
        val width = measuredWidth - switch.measuredWidthWithMargins
        labelView.measure(
            width, labelView.defaultHeightMeasureSpec(this)
        )
        packageNameView.measure(
            width, labelView.defaultHeightMeasureSpec(this)
        )

        val height = labelView.measuredHeightWithMargins + packageNameView.measuredHeightWithMargins

        setMeasuredDimension(measuredWidth, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        labelView.autoLayout(labelView.marginStart, labelView.marginTop)
        packageNameView.autoLayout(packageNameView.marginStart, labelView.bottom)
        switch.autoLayout(
            measuredWidth - switch.measuredWidth - switch.marginEnd,
            measuredHeight / 2 - switch.measuredHeight / 2
        )
    }
}