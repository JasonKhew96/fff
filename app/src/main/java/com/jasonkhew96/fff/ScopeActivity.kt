package com.jasonkhew96.fff

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup

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