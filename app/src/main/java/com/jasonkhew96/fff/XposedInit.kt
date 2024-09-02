package com.jasonkhew96.fff

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.TextView.BufferType
import com.jasonkhew96.fff.Constant.TAG
import com.jasonkhew96.fff.XposedHelper.getHostClassLoader
import com.jasonkhew96.fff.XposedHelper.hookBefore
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class XposedInit(
    base: XposedInterface, param: XposedModuleInterface.ModuleLoadedParam
) : XposedModule(base, param) {

    @SuppressLint("DiscouragedPrivateApi")
    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        if (!param.isFirstPackage) return
        XposedHelper.init(this, param.classLoader)

        val prefs = getRemotePreferences("fff")
        val replaceText = prefs.getString("replace_text", "FFF") ?: "FFF"

        val attachMethod = Application::class.java.getDeclaredMethod("attach", Context::class.java)
        hookBefore(attachMethod, object : XposedHelper.BeforeCallback {
            override fun before(callback: XposedInterface.BeforeHookCallback) {
                val setTextMethod = TextView::class.java.getDeclaredMethod(
                    "setText",
                    CharSequence::class.java,
                    BufferType::class.java,
                    Boolean::class.java,
                    Int::class.java
                )
                val setHintMethod =
                    TextView::class.java.getDeclaredMethod("setHint", CharSequence::class.java)

                val textViewCallback = object : XposedHelper.BeforeCallback {
                    override fun before(callback: XposedInterface.BeforeHookCallback) {
                        val text = callback.args[0] as CharSequence?
                        text ?: return
                        callback.args[0] = text.toString().replace(Regex("\\w+"), replaceText)
                    }
                }

                hookBefore(setTextMethod, textViewCallback)
                hookBefore(setHintMethod, textViewCallback)

                try {
                    val textStringSimpleElementClass =
                        getHostClassLoader().loadClass("androidx.compose.foundation.text.modifiers.TextStringSimpleElement")
                    val textStringSimpleElementCtor =
                        textStringSimpleElementClass.declaredConstructors.first { ctor -> ctor.parameterTypes.size == 8 }

                    hookBefore(textStringSimpleElementCtor, textViewCallback)
                } catch (e: Throwable) {
                    Log.d(TAG, "textStringSimpleElementClass not found")
                }

                try {
                    val textStringSimpleNodeClass =
                        getHostClassLoader().loadClass("androidx.compose.foundation.text.modifiers.TextStringSimpleNode")
                    val textStringSimpleNodeCtor =
                        textStringSimpleNodeClass.declaredConstructors.minBy { ctor -> ctor.parameterTypes.size }
                    val updateTextMethod =
                        textStringSimpleNodeClass.declaredMethods.first { m -> m.name == "updateText" }

                    hookBefore(textStringSimpleNodeCtor, textViewCallback)
                    hookBefore(updateTextMethod, textViewCallback)
                } catch (e: Throwable) {
                    Log.d(TAG, "textStringSimpleNodeClass not found")
                }

                try {
                    val textAnnotatedStringNodeClass =
                        getHostClassLoader().loadClass("androidx.compose.foundation.text.modifiers.TextAnnotatedStringNode")
                    val textAnnotatedStringNodeCtor =
                        textAnnotatedStringNodeClass.declaredConstructors.minBy { ctor -> ctor.parameterTypes.size }
                    val annotatedStringClass =
                        getHostClassLoader().loadClass("androidx.compose.ui.text.AnnotatedString")
                    val updateTextMethod =
                        textAnnotatedStringNodeClass.declaredMethods.first { m -> m.name == "updateText" }

                    val annotatedStringCallback = object : XposedHelper.BeforeCallback {
                        override fun before(callback: XposedInterface.BeforeHookCallback) {
                            val text = callback.args[0]
                            text ?: return
                            val beforeText = annotatedStringClass.getDeclaredMethod("getText")
                                .invoke(text) as String
                            val textField =
                                annotatedStringClass.declaredFields.first { f -> f.name == "text" }
                            textField.isAccessible = true
                            textField.set(
                                callback.args[0], beforeText.replace(Regex("\\w+"), replaceText)
                            )
                        }
                    }

                    hookBefore(textAnnotatedStringNodeCtor, annotatedStringCallback)
                    hookBefore(updateTextMethod, annotatedStringCallback)
                } catch (e: Throwable) {
                    Log.d(TAG, "textAnnotatedStringNodeClass not found")
                }
            }
        })
    }
}