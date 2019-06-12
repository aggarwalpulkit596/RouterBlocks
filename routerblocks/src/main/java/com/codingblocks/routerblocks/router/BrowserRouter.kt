package com.codingblocks.routerblocks.router

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.codingblocks.routerblocks.Interceptor
import com.codingblocks.routerblocks.Utils
import com.codingblocks.routerblocks.models.BrowserRoute
import com.codingblocks.routerblocks.models.Route
import java.util.*

class BrowserRouter : BaseRouter() {
    override fun setRouteInterceptor(interceptor: Interceptor) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val canOpenRoute: Class<out Route>
        get() = BrowserRoute::class.java

    protected fun open(context: Context?, route: Route): Boolean {
        if (doOnInterceptor(context, route.url)) {
            return true
        }
        val uri = Uri.parse(route.url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(intent)
        return true
    }

    override fun open(route: Route): Boolean {
        return open(mBaseContext, route)
    }

    override fun open(url: String): Boolean {
        open(getRoute(url))
        return true
    }

    override fun open(context: Context?, url: String): Boolean {
        return open(context, getRoute(url))
    }

    override fun getRoute(url: String): BrowserRoute {
        return BrowserRoute.Builder(this)
            .setUrl(url)
            .build()
    }

    override fun canOpenTheRoute(route: Route): Boolean {
        return canOpenRoute == route.javaClass
    }

    override fun canOpenTheUrl(url: String): Boolean {
        return SCHEMES_CAN_OPEN.contains(Utils.getScheme(url))
    }


    private fun doOnInterceptor(context: Context?, url: String): Boolean {
        return if (interceptor != null) {
            interceptor!!.intercept(context!!, url)
        } else false
    }

    companion object {
        private val SCHEMES_CAN_OPEN = LinkedHashSet<String>()

        var instance = BrowserRouter()
            internal set


        init {
            SCHEMES_CAN_OPEN.add("https")
            SCHEMES_CAN_OPEN.add("http")
        }
    }
}
