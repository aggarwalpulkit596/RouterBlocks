package com.codingblocks.routerblocks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.codingblocks.routerblocks.models.Route;
import com.codingblocks.routerblocks.router.ActivityRouter;
import com.codingblocks.routerblocks.router.BrowserRouter;
import com.codingblocks.routerblocks.router.HistoryItem;
import com.codingblocks.routerblocks.router.IActivityRouteTableInitializer;
import com.codingblocks.routerblocks.router.RouterInterface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RouterManager {

    private static final RouterManager singleton = new RouterManager();

    //Note that this is a list is ordered, so the priority in the front will be higher
    static List<RouterInterface> mRouters = new LinkedList<>();

    private RouterManager() {
    }

    static RouterManager getSingleton() {
        return singleton;
    }

    public synchronized void addRouter(RouterInterface router) {
        if (router != null) {
            //first remove all the duplicate routers
            List<RouterInterface> duplicateRouters = new ArrayList<>();
            for (RouterInterface r : mRouters) {
                if (r.getClass().equals(router.getClass())) {
                    duplicateRouters.add(r);
                }
            }
            mRouters.removeAll(duplicateRouters);
            mRouters.add(router);
        } else {
            Log.e("Router Manager", "The Router is null");
        }
    }

    public void setInterceptor(Interceptor interceptor) {
        for (RouterInterface router : mRouters) {
            router.setInterceptor(interceptor);
        }
    }

    public synchronized void initBrowserRouter(Context context) {
        BrowserRouter browserRouter = BrowserRouter.getInstance();
        browserRouter.init(context);
        addRouter(browserRouter);
    }


    public synchronized void initActivityRouter(Context context) {
        ActivityRouter activityRouter = ActivityRouter.Companion.getInstance();
        activityRouter.init(context);
        addRouter(activityRouter);
    }

    public synchronized void initActivityRouter(Context context, String... schemes) {
        initActivityRouter(context, null, schemes);
    }

    public synchronized void initActivityRouter(Context context, IActivityRouteTableInitializer initializer, String... schemes) {
        ActivityRouter router = ActivityRouter.Companion.getInstance();
        if (initializer == null) {
            router.init(context);
        } else {
            router.init(context, initializer);
        }
        if (schemes != null && schemes.length > 0) {
            router.setMatchSchemes(schemes);
        }
        addRouter(router);
    }

    public List<RouterInterface> getRouters() {
        return mRouters;
    }


    public boolean open(String url) {
        for (RouterInterface router : mRouters) {
            if (router.canOpenTheUrl(url)) {
                return router.open(url);
            }
        }
        return false;
    }

    /**
     * the route of the url, if there is not router to process the url, return null
     *
     * @param url
     * @return
     */
    @Nullable
    public Route getRoute(String url) {
        for (RouterInterface router : mRouters) {
            if (router.canOpenTheUrl(url)) {
                return router.getRoute(url);
            }
        }
        return null;
    }

    public boolean open(Context context, String url) {
        for (RouterInterface router : mRouters) {
            if (router.canOpenTheUrl(url)) {
                return router.open(context, url);
            }
        }
        return false;
    }


    public boolean openRoute(Route route) {
        for (RouterInterface router : mRouters) {
            if (router.canOpenTheRoute(route)) {
                return router.open(route);
            }
        }
        return false;
    }

    public Queue<HistoryItem> getActivityChangedHistories() {
        ActivityRouter aRouter = null;
        for (RouterInterface router : mRouters) {
            if (router instanceof ActivityRouter) {
                aRouter = (ActivityRouter) router;
                break;
            }
        }
        return aRouter != null ? aRouter.getRouteHistories() : null;
    }


}

