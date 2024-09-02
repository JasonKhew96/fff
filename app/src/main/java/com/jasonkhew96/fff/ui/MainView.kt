package com.jasonkhew96.fff.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.EditText

class MainView(context: Context, attrs: AttributeSet? = null) : CustomViewGroup(context, attrs) {

    val buttonScope = Button(context).apply {
        layoutParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        this@MainView.addView(this)
    }

    val editText = EditText(context).apply {
        setSingleLine()
        layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            marginStart = 16.dp
            marginEnd = 16.dp
        }
        this@MainView.addView(this)
    }

    val buttonSave = Button(context).apply {
        layoutParams = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        this@MainView.addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        buttonScope.autoMeasure()
        editText.measure(
            editText.defaultWidthMeasureSpec(this) - editText.marginStart - editText.marginEnd,
            editText.defaultHeightMeasureSpec(this)
        )
        buttonSave.autoMeasure()

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val optimizeY =
            (measuredHeight / 2) - ((buttonScope.measuredHeightWithMargins + editText.measuredHeightWithMargins + buttonSave.measuredHeightWithMargins) / 2)

        buttonScope.autoLayout((measuredWidth / 2) - (buttonScope.measuredWidth / 2), optimizeY)
        editText.autoLayout(editText.marginStart, buttonScope.bottom)
        buttonSave.autoLayout((measuredWidth / 2) - (buttonSave.measuredWidth / 2), editText.bottom)
    }
}