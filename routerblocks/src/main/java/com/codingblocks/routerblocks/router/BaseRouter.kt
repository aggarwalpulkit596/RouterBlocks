package com.codingblocks.routerblocks.router

import android.content.Context

import com.codingblocks.routerblocks.Interceptor
import com.codingblocks.routerblocks.models.Route

abstract class BaseRouter : RouterInterface {

    var interceptor: Interceptor? = null


    protected var mBaseContext: Context? = null


    open fun init(context: Context) {
        mBaseContext = context
    }


    companion object {

        public var CAN_OPEN_ROUTE: Class<out Route>? = null
    }


}
