package com.jasonkhew96.fff

import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.BeforeInvocation
import io.github.libxposed.api.annotations.XposedHooker
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

object XposedHelper {
    private lateinit var xposedModule: XposedModule
    private lateinit var hostClassLoader: ClassLoader
    private lateinit var moduleClassLoader: ClassLoader

    fun getModule(): XposedModule {
        return xposedModule
    }

    fun getHostClassLoader(): ClassLoader {
        return hostClassLoader
    }

    fun getModuleClassLoader(): ClassLoader {
        return moduleClassLoader
    }

    interface BeforeCallback {
        fun before(callback: XposedInterface.BeforeHookCallback)
    }

    interface AfterCallback {
        fun after(callback: XposedInterface.AfterHookCallback)
    }

    @XposedHooker
    internal object CustomHooker : XposedInterface.Hooker {
        var beforeCallbacks: ConcurrentHashMap<Member, BeforeCallback> = ConcurrentHashMap()
        var afterCallbacks: ConcurrentHashMap<Member, AfterCallback> = ConcurrentHashMap()

        @BeforeInvocation
        @JvmStatic
        fun before(callback: XposedInterface.BeforeHookCallback) {
            beforeCallbacks[callback.member]?.before(callback)
        }

        @AfterInvocation
        @JvmStatic
        fun after(callback: XposedInterface.AfterHookCallback) {
            afterCallbacks[callback.member]?.after(callback)
        }
    }

    fun init(xposedModule: XposedModule, hostClassLoader: ClassLoader) {
        this.xposedModule = xposedModule
        this.hostClassLoader = hostClassLoader
        this.moduleClassLoader = XposedHelper::class.java.classLoader!!
    }

    fun hookBefore(
        member: Member, callback: BeforeCallback
    ): XposedInterface.MethodUnhooker<out Executable> {
        CustomHooker.beforeCallbacks[member] = callback
        return when (member.javaClass) {
            Method::class.java -> getModule().hook(member as Method, CustomHooker::class.java)
            Constructor::class.java -> getModule().hook(
                member as Constructor<*>, CustomHooker::class.java
            )

            else -> throw IllegalArgumentException()
        }
    }

    fun hookAfter(
        member: Member, callback: AfterCallback
    ): XposedInterface.MethodUnhooker<out Executable> {
        CustomHooker.afterCallbacks[member] = callback
        return when (member.javaClass) {
            Method::class.java -> getModule().hook(member as Method, CustomHooker::class.java)
            Constructor::class.java -> getModule().hook(
                member as Constructor<*>, CustomHooker::class.java
            )

            else -> throw IllegalArgumentException()
        }
    }
}