package com.codingblocks.routerblocks

import android.content.Context
import android.util.Log
import com.codingblocks.routerblocks.models.Route
import com.codingblocks.routerblocks.router.ActivityRouter
import com.codingblocks.routerblocks.router.BrowserRouter
import com.codingblocks.routerblocks.router.HistoryItem
import com.codingblocks.routerblocks.router.IActivityRouteTableInitializer
import com.codingblocks.routerblocks.router.RouterInterface
import java.util.*

class RouterManager private constructor() {

    val routers: List<RouterInterface>
        get() = mRouters

    val activityChangedHistories: Queue<HistoryItem>?
        get() {
            var aRouter: ActivityRouter? = null
            for (router in mRouters) {
                if (router is ActivityRouter) {
                    aRouter = router
                    break
                }
            }
            return aRouter?.routeHistories
        }

    @Synchronized
    fun addRouter(router: RouterInterface?) {
        if (router != null) {
            //first remove all the duplicate routers
            val duplicateRouters = ArrayList<RouterInterface>()
            for (r in mRouters) {
                if (r.javaClass == router.javaClass) {
                    duplicateRouters.add(r)
                }
            }
            mRouters.removeAll(duplicateRouters)
            mRouters.add(router)
        } else {
            Log.e("Router Manager", "The Router is null")
        }
    }

    fun setInterceptor(interceptor: Interceptor) {
        for (router in mRouters) {
            router.setInterceptor(interceptor)
        }
    }

    @Synchronized
    fun initBrowserRouter(context: Context) {
        val browserRouter = BrowserRouter.instance
        browserRouter.init(context)
        addRouter(browserRouter)
    }


    @Synchronized
    fun initActivityRouter(context: Context) {
        val activityRouter = ActivityRouter.instance
        activityRouter.init(context)
        addRouter(activityRouter)
    }

    @Synchronized
    fun initActivityRouter(context: Context, vararg schemes: String) {
        initActivityRouter(context, null, *schemes)
    }

    @Synchronized
    fun initActivityRouter(
        context: Context,
        initializer: IActivityRouteTableInitializer?,
        vararg schemes: String
    ) {
        val router = ActivityRouter.instance
        if (initializer == null) {
            router.init(context)
        } else {
            router.init(context, initializer)
        }
        if (schemes != null && schemes.size > 0) {
            router.setMatchSchemes(*schemes)
        }
        addRouter(router)
    }


    fun open(url: String): Boolean {
        for (router in mRouters) {
            if (router.canOpenTheUrl(url)) {
                return router.open(url)
            }
        }
        return false
    }

    /**
     * the route of the url, if there is not router to process the url, return null
     *
     * @param url
     * @return
     */
    fun getRoute(url: String): Route? {
        for (router in mRouters) {
            if (router.canOpenTheUrl(url)) {
                return router.getRoute(url)
            }
        }
        return null
    }

    fun open(context: Context, url: String): Boolean {
        for (router in mRouters) {
            if (router.canOpenTheUrl(url)) {
                return router.open(context, url)
            }
        }
        return false
    }


    fun openRoute(route: Route): Boolean {
        for (router in mRouters) {
            if (router.canOpenTheRoute(route)) {
                return router.open(route)
            }
        }
        return false
    }

    companion object {

        internal val singleton = RouterManager()

        //Note that this is a list is ordered, so the priority in the front will be higher
        internal var mRouters: MutableList<RouterInterface> = LinkedList()
    }


}

