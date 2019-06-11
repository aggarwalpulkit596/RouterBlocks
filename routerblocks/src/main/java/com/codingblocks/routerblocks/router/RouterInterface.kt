package com.codingblocks.routerblocks.router

import android.content.Context
import com.codingblocks.routerblocks.Interceptor
import com.codingblocks.routerblocks.models.Route

interface RouterInterface {

    /**
     * return the list that the Route list this router can open
     * @return
     */
    val canOpenRoute: Class<out Route>


    /**
     *
     * @param route
     * @return true: open success, fail: open fail
     */
    fun open(route: Route): Boolean

    /**
     *
     * @param url
     * @return true: open success, fail: open fail
     */
    fun open(url: String): Boolean


    fun open(context: Context, url: String): Boolean

    /**
     * build the route according to the url, if not match, return null
     * @param url
     * @return
     */
    fun getRoute(url: String): Route

    /**
     * decide if the route can be opened
     * @param route
     * @return
     */
    fun canOpenTheRoute(route: Route): Boolean

    /**
     * decide if the url can be opened
     * @param url
     * @return
     */
    fun canOpenTheUrl(url: String): Boolean

    fun setInterceptor(interceptor: Interceptor)


}
