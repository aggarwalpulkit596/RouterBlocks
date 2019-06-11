package com.codingblocks.routerblocks.router

import android.content.Context

import com.codingblocks.routerblocks.Interceptor
import com.codingblocks.routerblocks.models.Route

abstract class BaseRouter(protected var mBaseContext: Context) : RouterInterface {

    private var interceptor: Interceptor? = null


    fun init(context: Context) {
        mBaseContext = context
    }

    override fun setInterceptor(interceptor: Interceptor) {
        this.interceptor = interceptor
    }

    companion object {


        protected var CAN_OPEN_ROUTE: Class<out Route>? = null
    }


}
