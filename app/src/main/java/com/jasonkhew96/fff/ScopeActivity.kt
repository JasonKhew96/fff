package com.jasonkhew96.fff

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

class ScopeActivity : AppCompatActivity() {

    var isBlocked = false
    lateinit var waitingLayout: WaitingLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        waitingLayout = WaitingLayout(this)

        onBackPressedDispatcher.addCallback(this) {
            if (isBlocked) {
                return@addCallback
            }
            super.onBackPressed()
        }

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
        scopeView.recyclerView.layoutManager = LinearLayoutManager(this)
        scopeView.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        setContentView(scopeView)
    }
}