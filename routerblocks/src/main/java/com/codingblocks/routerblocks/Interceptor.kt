package com.codingblocks.routerblocks

import android.content.Context

interface Interceptor {

    /**
     *
     * @param url
     * @return if intercept the request
     */
    fun intercept(context: Context, url: String): Boolean

}
