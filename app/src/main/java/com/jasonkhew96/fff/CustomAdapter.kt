package com.jasonkhew96.fff

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomAdapter(private val mListener: OnRequestListener) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    interface OnRequestListener {
        fun onRequest()
        fun onFinish()
    }

    class ViewHolder(
        view: AppLabelView
    ) : RecyclerView.ViewHolder(view) {
        private val labelView = view.labelView
        private val packageNameView = view.packageNameView
        private val switch = view.switch

        fun bind(appInfo: MyApplication.AppInfo, mListener: OnRequestListener) {
            labelView.text = appInfo.label
            packageNameView.text = appInfo.packageName
            switch.setOnCheckedChangeListener { button, isChecked ->
                if (button.isPressed.not()) return@setOnCheckedChangeListener
                if (isChecked) {
                    mListener.onRequest()
                    app.xposedService?.requestScope(appInfo.packageName,
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
                    app.xposedService?.removeScope(appInfo.packageName)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = AppLabelView(parent.context)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(app.appList[position], mListener)
    }

    override fun getItemCount(): Int {
        return app.appList.size
    }
}