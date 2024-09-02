package com.jasonkhew96.fff.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.jasonkhew96.fff.Constant
import com.jasonkhew96.fff.R
import com.jasonkhew96.fff.app
import com.jasonkhew96.fff.ui.MainView

class MainActivity : Activity() {
    private lateinit var mainView: MainView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = MainView(this)
        mainView.buttonScope.setText(R.string.button_scope)
        mainView.editText.setText(R.string.edit_text_hint)
        mainView.buttonSave.let {
            it.setText(R.string.button_save)
            it.setOnClickListener {
                app.prefs?.edit()
                    ?.putString(Constant.PREF_REPLACE_TEXT, mainView.editText.text.toString())
                    ?.apply()
            }
        }
        setIsEnabled(false)

        setContentView(mainView)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            if (app.xposedService != null) {
                setIsEnabled(true)
                mainView.buttonScope.setOnClickListener {
                    val intent = Intent(this, ScopeActivity::class.java)
                    startActivity(intent)
                }
                mainView.editText.setText(app.prefs?.getString(Constant.PREF_REPLACE_TEXT, "FFF"))
            }
        }, 500)
    }

    private fun setIsEnabled(isEnabled: Boolean) {
        mainView.buttonScope.isEnabled = isEnabled
        mainView.editText.isEnabled = isEnabled
        mainView.buttonSave.isEnabled = isEnabled
    }
}