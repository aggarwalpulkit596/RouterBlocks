package com.codingblocks.routerblocks.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.codingblocks.routerblocks.BuildConfig
import com.codingblocks.routerblocks.Utils
import com.codingblocks.routerblocks.Utils.Companion.getHost
import com.codingblocks.routerblocks.Utils.Companion.getPathSegments
import com.codingblocks.routerblocks.Utils.Companion.getScheme
import com.codingblocks.routerblocks.builders.ActivityRouteRuleBuilder
import com.codingblocks.routerblocks.exceptions.InvalidRoutePathException
import com.codingblocks.routerblocks.exceptions.InvalidValueTypeException
import com.codingblocks.routerblocks.exceptions.RouteNotFoundException
import com.codingblocks.routerblocks.models.ActivityRoute
import com.codingblocks.routerblocks.models.Route
import org.apache.commons.collections4.queue.CircularFifoQueue
import java.util.*

class ActivityRouter : BaseRouter() {

    private val mRouteTable = HashMap<String, Class<out Activity>>()
    private val mHistoryCaches = CircularFifoQueue<HistoryItem>(HISTORY_CACHE_SIZE)

    /**
     * It support multi schemes now
     * @see .getMatchSchemes
     * @return
     */
    var matchScheme: String
        @Deprecated("")
        get() = MATCH_SCHEMES[0]
        set(scheme) {
            MATCH_SCHEMES.clear()
            MATCH_SCHEMES.add(scheme)
        }

    val matchSchemes: List<String>
        get() = MATCH_SCHEMES

    override val canOpenRoute: Class<out Route>
        get() = CAN_OPEN_ROUTE!!

    val routeHistories: Queue<HistoryItem>
        get() = mHistoryCaches

    fun init(appContext: Context, initializer: IActivityRouteTableInitializer?) {
        super.init(appContext)
        initActivityRouterTable(initializer)
    }

    override fun init(appContext: Context) {
        init(appContext, null)
    }

    fun initActivityRouterTable(initializer: IActivityRouteTableInitializer?) {
        initializer?.initRouterTable(mRouteTable)
        for (pathRule in mRouteTable.keys) {
            val isValid = ActivityRouteRuleBuilder.isActivityRuleValid(pathRule)
            if (!isValid) {
                Log.e(TAG,"",InvalidRoutePathException(pathRule))
                mRouteTable.remove(pathRule)
            }
        }
    }


    override fun getRoute(url: String): ActivityRoute {
        return ActivityRoute.Builder(this)
            .setUrl(url)
            .build()
    }

    override fun canOpenTheRoute(route: Route): Boolean {
        return CAN_OPEN_ROUTE?.equals(route::class.java) ?: false
    }

    override fun canOpenTheUrl(url: String): Boolean {
        for (scheme in MATCH_SCHEMES) {
            if (TextUtils.equals(scheme, getScheme(url))) {
                return true
            }
        }
        return false
    }

    fun setMatchSchemes(vararg schemes: String) {
        MATCH_SCHEMES.clear()
        val list = Arrays.asList(*schemes)
        list.remove("")
        list.remove(null)
        MATCH_SCHEMES.addAll(list)
    }

    fun addMatchSchemes(scheme: String) {
        MATCH_SCHEMES.add(scheme)
    }

    override fun open(route: Route): Boolean {
        var ret = false
        if (route is ActivityRoute) {
            try {
                when (route.openType) {
                    ActivityRoute.START -> {
                        if (doOnInterceptor(route.activity, route.url)) {
                            return true
                        }
                        open(route, route.activity)
                        ret = true
                    }
                    ActivityRoute.FOR_RESULT_ACTIVITY -> {
                        if (doOnInterceptor(route.activity, route.url)) {
                            return true
                        }
                        openForResult(route, route.activity, route.requestCode)
                        ret = true
                    }
                    ActivityRoute.FOR_RESULT_SUPPORT_FRAGMENT -> {
                        if (doOnInterceptor(
                                route.fragment?.activity,
                                route.url
                            )
                        ) {
                            return true
                        }
                        openForResult(route, route.supportFragment, route.requestCode)
                        ret = true
                    }
                    ActivityRoute.FOR_RESULT_FRAGMENT -> {
                        if (doOnInterceptor(route.fragment?.activity, route.url)) {
                            return true
                        }
                        openForResult(route, route.fragment, route.requestCode)
                        ret = true
                    }
                    else -> {
                        Log.e(TAG, "Error Open Type")
                        ret = false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Url route not specified: ${route.url}", e)
                ret = false
            }

        }
        return ret

    }

    override fun open(url: String): Boolean {
        return open(null, url)
    }
    override fun open(context: Context?, url: String): Boolean {
        if (doOnInterceptor(context, url)) {
            return true
        }
        val route = getRoute(url)
        if (route is ActivityRoute) {
            try {
                open(route, context)
                return true
            } catch (e: Exception) {
                Log.e(TAG, "Url route not specified: ${route.url}",e)
            }

        }
        return false
    }
    private fun doOnInterceptor(context: Context?, url: String): Boolean {
        return interceptor?.intercept(context ?: mBaseContext!!, url) ?: false
    }


    @Throws(RouteNotFoundException::class)
    protected fun open(route: ActivityRoute, context: Context?) {
        val fromClazz = context?.javaClass ?: mBaseContext?.javaClass
        val intent =
            match(fromClazz!!, route) ?: throw RouteNotFoundException(routePath = route.url)
        if (context == null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or route.flags
            mBaseContext?.startActivity(intent)
        } else {
            intent.flags = route.flags
            context.startActivity(intent)
        }

        if (route.inAnimation !== -1 && route.outAnimation !== -1 && route.activity != null) {
            route.activity!!
                .overridePendingTransition(route.inAnimation, route.outAnimation)
        }

    }

    @Throws(RouteNotFoundException::class)
    protected fun openForResult(route: ActivityRoute, activity: Activity?, requestCode: Int) {


        val intent =
            match(activity!!::class.java, route) ?: throw RouteNotFoundException(route.url)
        intent.flags = route.flags
        if (route.inAnimation !== -1 && route.outAnimation !== -1 && route.activity != null) {
            route.activity!!
                .overridePendingTransition(route.inAnimation, route.outAnimation)
        }
        activity.startActivityForResult(intent, requestCode)

    }

    @Throws(RouteNotFoundException::class)
    protected fun openForResult(route: ActivityRoute, fragment: Fragment?, requestCode: Int) {

        val intent =
            match(fragment!!::class.java, route) ?: throw RouteNotFoundException(route.url)
        intent.flags = route.flags
        if (route.inAnimation !== -1 && route.outAnimation !== -1 && route.activity != null) {
            route.activity!!
                .overridePendingTransition(route.inAnimation, route.outAnimation)
        }
        fragment.startActivityForResult(intent, requestCode)

    }

    @Throws(RouteNotFoundException::class)
    protected fun openForResult(
        route: ActivityRoute,
        fragment: android.app.Fragment?,
        requestCode: Int
    ) {

        val intent =
            match(fragment!!::class.java, route) ?: throw RouteNotFoundException(route.url)
        intent.flags = route.flags
        if (route.inAnimation !== -1 && route.outAnimation !== -1 && route.activity != null) {
            route.activity!!
                .overridePendingTransition(route.inAnimation, route.outAnimation)
        }
        fragment.startActivityForResult(intent, requestCode)

    }


    /**
     * host Matching with path

     *
     * @param route
     * @return String the match routePath
     */
    @Nullable
    private fun findMatchedRoute(route: ActivityRoute): String? {
        val givenPathSegs = route.path
        OutLoop@ for (routeUrl in mRouteTable.keys) {
            val routePathSegs = getPathSegments(routeUrl)
            if (!TextUtils.equals(getHost(routeUrl), route.host)) {
                continue
            }
            if (givenPathSegs.size != routePathSegs.size) {
                continue
            }
            for (i in routePathSegs.indices) {
                if (!routePathSegs[i].startsWith(":") && !TextUtils.equals(
                        routePathSegs[i],
                        givenPathSegs[i]
                    )
                ) {
                    continue@OutLoop
                }
            }
            //find the match route
            return routeUrl
        }

        return null
    }

    /**
     * find the key value in the path and set them in the intent
     *
     * @param routeUrl the matched route path
     * @param givenUrl the given path
     * @param intent   the intent
     * @return the intent
     */
    private fun setKeyValueInThePath(routeUrl: String, givenUrl: String, intent: Intent): Intent {
        val routePathSegs = getPathSegments(routeUrl)
        val givenPathSegs = getPathSegments(givenUrl)
        for (i in routePathSegs.indices) {
            val seg = routePathSegs[i]
            if (seg.startsWith(":")) {
                val indexOfLeft = seg.indexOf("{")
                val indexOfRight = seg.indexOf("}")
                val key = seg.substring(indexOfLeft + 1, indexOfRight)
                val typeChar = seg[1]
                when (typeChar) {
                    //integer type
                    'i' -> try {
                        val value = Integer.parseInt(givenPathSegs[i])
                        intent.putExtra(key, value)
                    } catch (e: Exception) {
                        Log.e(TAG, "Parsing the shaping type failed " + givenPathSegs[i], e)
                        if (BuildConfig.DEBUG) {
                            throw InvalidValueTypeException(givenUrl, givenPathSegs[i])
                        } else {
                            //If it is in the case of release, give a default value
                            intent.putExtra(key, 0)
                        }
                    }

                    'f' ->
                        //float type
                        try {
                            val value = java.lang.Float.parseFloat(givenPathSegs[i])
                            intent.putExtra(key, value)
                        } catch (e: Exception) {
                            Log.e(TAG, "Parsing floating point type failed " + givenPathSegs[i], e)
                            if (BuildConfig.DEBUG) {
                                throw InvalidValueTypeException(givenUrl, givenPathSegs[i])
                            } else {
                                intent.putExtra(key, 0f)
                            }
                        }

                    'l' ->
                        //long type
                        try {
                            val value = java.lang.Long.parseLong(givenPathSegs[i])
                            intent.putExtra(key, value)
                        } catch (e: Exception) {
                            Log.e(TAG, "Parsing long integer failure " + givenPathSegs[i], e)
                            if (BuildConfig.DEBUG) {
                                throw InvalidValueTypeException(givenUrl, givenPathSegs[i])
                            } else {
                                intent.putExtra(key, 0L)
                            }
                        }

                    'd' -> try {
                        val value = java.lang.Double.parseDouble(givenPathSegs[i])
                        intent.putExtra(key, value)
                    } catch (e: Exception) {
                        Log.e(TAG, "Parsing double type failed " + givenPathSegs[i], e)
                        if (BuildConfig.DEBUG) {
                            throw InvalidValueTypeException(givenUrl, givenPathSegs[i])
                        } else {
                            intent.putExtra(key, 0.0)
                        }
                    }

                    'c' -> try {
                        val value = givenPathSegs[i][0]
                    } catch (e: Exception) {
                        Log.e(TAG, "Parsing the Character type failed" + givenPathSegs[i], e)
                        if (BuildConfig.DEBUG) {
                            throw InvalidValueTypeException(givenUrl, givenPathSegs[i])
                        } else {
                            intent.putExtra(key, ' ')
                        }
                    }

                    's' -> intent.putExtra(key, givenPathSegs[i])
                    else -> intent.putExtra(key, givenPathSegs[i])
                }
            }

        }
        return intent
    }

    private fun setOptionParams(url: String, intent: Intent): Intent {
        val queryParams = Utils.getParameters(url)
        for (key in queryParams.keys) {
            intent.putExtra(key, queryParams[key])
        }

        return intent
    }

    private fun setExtras(bundle: Bundle?, intent: Intent): Intent {
        intent.putExtras(bundle)
        return intent
    }

    @Nullable
    private fun match(from: Class<*>, route: ActivityRoute): Intent? {
        val matchedRoute = findMatchedRoute(route) ?: return null
        val matchedActivity = mRouteTable[matchedRoute]
        var intent = Intent(mBaseContext, matchedActivity)
        mHistoryCaches.add(HistoryItem(from, matchedActivity))
        //find the key value in the path
        intent = setKeyValueInThePath(matchedRoute, route.url, intent)
        intent = setOptionParams(route.url, intent)
        intent = setExtras(route.extras, intent)
        intent.putExtra(keyUrl, route.url)
        return intent
    }

    companion object {
        private const val TAG = "Router"
        private val MATCH_SCHEMES = ArrayList<String>()
        private const val DEFAULT_SCHEME = "activity"
        private const val HISTORY_CACHE_SIZE = 20

        val instance = ActivityRouter()   //Activity

        val keyUrl = "key_and_activity_router_url"

        init {
            CAN_OPEN_ROUTE = ActivityRoute::class.java
            MATCH_SCHEMES.add(DEFAULT_SCHEME)

            try {
                val constructor =
                    Class.forName("com.codingblocks.routerblocks.router.AnnotatedRouterTableInitializer")
                        .getConstructor()
                val initializer = constructor.newInstance() as IActivityRouteTableInitializer
                instance.initActivityRouterTable(initializer)

            } catch (e: Exception) {
                //do nothing
            }

        }

    }

}
