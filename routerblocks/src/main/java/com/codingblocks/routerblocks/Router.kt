package com.codingblocks.routerblocks

import android.content.Context
import android.util.Log
import com.codingblocks.routerblocks.models.Route
import com.codingblocks.routerblocks.router.HistoryItem
import com.codingblocks.routerblocks.router.IActivityRouteTableInitializer
import com.codingblocks.routerblocks.router.RouterInterface
import java.util.*

object Router {


    private val TAG = "Router::class.java"

    val activityChangedHistories: Queue<HistoryItem>?
        get() = RouterManager.singleton.activityChangedHistories

    @Synchronized
    fun addRouter(router: RouterInterface) {
        RouterManager.singleton.addRouter(router)
    }

    @Synchronized
    fun initBrowserRouter(context: Context) {
        RouterManager.singleton.initBrowserRouter(context)
    }


    @Synchronized
    fun initActivityRouter(context: Context) {
        RouterManager.singleton.initActivityRouter(context)
    }

    /**
     * @param context
     * @param scheme
     * @param initializer
     * @See
     */
    @Deprecated(
        "",
        ReplaceWith("RouterManager.getSingleton().initActivityRouter(context, initializer, scheme)")
    )
    @Synchronized
    fun initActivityRouter(
        context: Context,
        scheme: String,
        initializer: IActivityRouteTableInitializer
    ) {
        RouterManager.singleton.initActivityRouter(context, initializer, scheme)
    }


    @Synchronized
    fun initActivityRouter(
        context: Context,
        initializer: IActivityRouteTableInitializer,
        vararg scheme: String
    ) {
        RouterManager.singleton.initActivityRouter(
            context = context,
            initializer = initializer,
            schemes = *scheme
        )
    }

    @Synchronized
    fun initActivityRouter(context: Context, vararg scheme: String) {
        RouterManager.singleton.initActivityRouter(context = context, schemes = *scheme)
    }

    fun open(url: String, vararg params: Any): Boolean {
        val temp = formatUrl(url, *params)
        return RouterManager.singleton.open(temp)
    }

    fun open(context: Context, url: String, vararg params: Any): Boolean {
        val temp = formatUrl(url, *params)
        return RouterManager.singleton.open(context, temp)
    }


    /**
     * the route of the url, if there is not router to process the url, return null
     *
     * @param url
     * @return
     */
    fun getRoute(url: String, vararg params: Any): Route? {
        val temp = formatUrl(url, *params)
        return RouterManager.singleton.getRoute(temp)
    }


    fun openRoute(route: Route): Boolean {
        return RouterManager.singleton.openRoute(route)
    }

    fun setRouteInterceptor(interceptor: Interceptor) {
        RouterManager.singleton.setInterceptor(interceptor)
    }

    private fun formatUrl(url: String, vararg params: Any): String {
        var formatted = url
        try {
            formatted = String.format(Locale.ENGLISH, url, *params)
        } catch (e: Exception) {
            Log.e(TAG, "ops", e)
        }

        return formatted

    }

}