package com.jasonkhew96.fff.activity

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.jasonkhew96.fff.CustomAdapter
import com.jasonkhew96.fff.ui.ScopeView
import com.jasonkhew96.fff.ui.WaitingLayout

class ScopeActivity : Activity() {

    var isBlocked = false
    lateinit var waitingLayout: WaitingLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        waitingLayout = WaitingLayout(this)

        val mListener = object : CustomAdapter.OnRequestListener {
            override fun onRequest() {
                isBlocked = true
                (window.decorView as ViewGroup).addView(waitingLayout)
            }

            override fun onFinish() {
                isBlocked = false
                (window.decorView as ViewGroup).removeView(waitingLayout)
            }
        }

        val scopeView = ScopeView(this)
        scopeView.recyclerView.adapter = CustomAdapter(mListener)
        setContentView(scopeView)
    }

    override fun onBackPressed() {
        if (isBlocked) {
            return
        }
        finish()
    }
}