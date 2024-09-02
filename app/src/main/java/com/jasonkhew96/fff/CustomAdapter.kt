package com.jasonkhew96.fff

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomAdapter(private val mListener: OnRequestListener) : BaseAdapter() {

    interface OnRequestListener {
        fun onRequest()
        fun onFinish()
    }

    override fun getCount(): Int {
        return app.appList.size
    }

    override fun getItem(position: Int): Any {
        return app.appList[position]
    }

    override fun getItemId(position: Int): Long {
        return app.appList[position].hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = AppLabelView(parent!!.context)
        view.labelView.text = app.appList[position].label
        view.packageNameView.text = app.appList[position].packageName
        view.switch.setOnCheckedChangeListener { button, isChecked ->
            if (button.isPressed.not()) return@setOnCheckedChangeListener
            if (isChecked) {
                mListener.onRequest()
                app.xposedService?.requestScope(
                    app.appList[position].packageName,
                    object : XposedService.OnScopeEventListener {
                        override fun onScopeRequestApproved(packageName: String?) {
                            super.onScopeRequestApproved(packageName)
                            app.scopeList.add(packageName!!)
                            mListener.onFinish()
                        }

                        override fun onScopeRequestDenied(packageName: String?) {
                            super.onScopeRequestDenied(packageName)
                            CoroutineScope(Dispatchers.Main).launch {
                                button.isChecked = false
                                app.scopeList.remove(packageName!!)
                                mListener.onFinish()
                            }
                        }

                        override fun onScopeRequestTimeout(packageName: String?) {
                            super.onScopeRequestTimeout(packageName)
                            CoroutineScope(Dispatchers.Main).launch {
                                button.isChecked = false
                                app.scopeList.remove(packageName!!)
                                mListener.onFinish()
                            }
                        }

                        override fun onScopeRequestFailed(
                            packageName: String?, message: String?
                        ) {
                            super.onScopeRequestFailed(packageName, message)
                            CoroutineScope(Dispatchers.Main).launch {
                                button.isChecked = false
                                app.scopeList.remove(packageName!!)
                                mListener.onFinish()
                            }
                        }
                    })
            } else {
                app.xposedService?.removeScope(app.appList[position].packageName)
            }
        }
        return view
    }
}