package com.jasonkhew96.fff.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class ScopeView(context: Context, attrs: AttributeSet? = null) : CustomViewGroup(context, attrs) {

    val listView = ListView(context).apply {
        layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this@ScopeView.addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        listView.autoMeasure()

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        listView.autoLayout(0, 0)
    }
}