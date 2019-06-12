package com.codingblocks.routerblocks;

import android.content.Context;
import android.util.Log;

import com.codingblocks.routerblocks.models.Route;
import com.codingblocks.routerblocks.router.HistoryItem;
import com.codingblocks.routerblocks.router.IActivityRouteTableInitializer;
import com.codingblocks.routerblocks.router.RouterInterface;

import java.util.Locale;
import java.util.Queue;

public class Router {


    private static final String TAG = "Router::class.java";

    public static synchronized void addRouter(RouterInterface router) {
        RouterManager.getSingleton().addRouter(router);
    }

    public static synchronized void initBrowserRouter(Context context) {
        RouterManager.getSingleton().initBrowserRouter(context);
    }


    public static synchronized void initActivityRouter(Context context) {
        RouterManager.getSingleton().initActivityRouter(context);
    }

    /**
     * @param context
     * @param scheme
     * @param initializer
     * @See
     */
    @Deprecated
    public static synchronized void initActivityRouter(Context context, String scheme, IActivityRouteTableInitializer initializer) {
        RouterManager.getSingleton().initActivityRouter(context, initializer, scheme);
    }


    public static synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer, String... scheme) {
        RouterManager.getSingleton().initActivityRouter(context, initializer, scheme);
    }

    public static synchronized void initActivityRouter(Context context, String... scheme) {
        RouterManager.getSingleton().initActivityRouter(context, scheme);
    }

    public static boolean open(String url, Object... params) {
        String temp = formatUrl(url, params);
        return RouterManager.getSingleton().open(temp);
    }

    public static boolean open(Context context, String url, Object... params) {
        String temp = formatUrl(url, params);
        return RouterManager.getSingleton().open(context, temp);
    }


    /**
     * the route of the url, if there is not router to process the url, return null
     *
     * @param url
     * @return
     */
    public static Route getRoute(String url, Object... params) {
        String temp = formatUrl(url, params);
        return RouterManager.getSingleton().getRoute(temp);
    }


    public static boolean openRoute(Route route) {
        return RouterManager.getSingleton().openRoute(route);
    }

    public static Queue<HistoryItem> getActivityChangedHistories() {
        return RouterManager.getSingleton().getActivityChangedHistories();
    }

    public static void setInterceptor(Interceptor interceptor) {
        RouterManager.getSingleton().setInterceptor(interceptor);
    }

    private static String formatUrl(String url, Object... params) {
        String formatted = url;
        try {
            formatted = String.format(Locale.ENGLISH, url, params);
        } catch (Exception e) {
            Log.e(TAG, "ops", e);
        }

        return formatted;

    }

}