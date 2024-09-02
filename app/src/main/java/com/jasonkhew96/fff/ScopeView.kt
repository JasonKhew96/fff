package com.jasonkhew96.fff

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class ScopeView(context: Context, attrs: AttributeSet? = null) : CustomViewGroup(context, attrs) {

    val recyclerView = RecyclerView(context).apply {
        layoutParams = MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this@ScopeView.addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        recyclerView.autoMeasure()

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        recyclerView.autoLayout(0, 0)
    }
}