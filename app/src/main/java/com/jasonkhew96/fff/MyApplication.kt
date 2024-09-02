package com.jasonkhew96.fff

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.jasonkhew96.fff.Constant.PREF_NAME
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.Locale

lateinit var app: MyApplication

class MyApplication : Application() {

    var xposedService: XposedService? = null
    var prefs: SharedPreferences? = null
    var scopeList = mutableListOf<String>()
    var appList = listOf<AppInfo>()

    private val globalScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        app = this
        globalScope.launch {
            fetchAppList()
        }
        globalScope.launch {
            XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    xposedService = service
                    prefs = service.getRemotePreferences(PREF_NAME)
                    scopeList = service.scope
                }

                override fun onServiceDied(service: XposedService) {
                    xposedService = null
                    prefs = null
                    scopeList.clear()
                }
            })
        }
    }

    data class AppInfo(val packageName: String, val label: String)

    private suspend fun fetchAppList() {
        withContext(Dispatchers.IO) {
            val pm = app.packageManager
            val collection = mutableListOf<AppInfo>()

            pm.getInstalledApplications(PackageManager.GET_META_DATA).forEach {
                val label = pm.getApplicationLabel(it)
                if (it.flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) != 0) return@forEach
                if (pm.getLaunchIntentForPackage(it.packageName) == null) return@forEach
                collection.add(AppInfo(it.packageName, label.toString()))
            }
            collection.sortWith(
                compareBy(
                    Collator.getInstance(Locale.getDefault()), AppInfo::label
                ),
            )
            appList = collection
        }
    }
}