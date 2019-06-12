package com.codingblocks.routerblocks.router;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.codingblocks.routerblocks.Utils;
import com.codingblocks.routerblocks.models.BrowserRoute;
import com.codingblocks.routerblocks.models.Route;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class BrowserRouter extends BaseRouter {
    private static final Set<String> SCHEMES_CAN_OPEN = new LinkedHashSet<>();

    static BrowserRouter mBrowserRouter = new BrowserRouter();


    static {
        SCHEMES_CAN_OPEN.add("https");
        SCHEMES_CAN_OPEN.add("http");
    }

    public static BrowserRouter getInstance(){
        return mBrowserRouter;
    }

    protected boolean open(Context context, Route route){
        if(doOnInterceptor(context, route.getUrl())){
            return true;
        }
        Uri uri = Uri.parse(route.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean open(@NotNull Route route) {
        return open(getMBaseContext(), route);
    }

    @Override
    public boolean open(@NotNull String url) {
        open(getRoute(url));
        return true;
    }

    @Override
    public boolean open(@NotNull Context context, @NotNull String url) {
        return open(context, getRoute(url));
    }

    @Override
    public BrowserRoute getRoute(@NotNull String url) {
        return new BrowserRoute.Builder(this)
                .setUrl(url)
                .build();
    }

    @Override
    public boolean canOpenTheRoute(@NotNull Route route) {
        return getCanOpenRoute().equals(route.getClass());
    }

    @Override
    public boolean canOpenTheUrl(@NotNull String url) {
        return SCHEMES_CAN_OPEN.contains(Utils.Companion.getScheme(url));
    }

    @Override
    public Class<? extends Route> getCanOpenRoute() {
        return BrowserRoute.class;
    }



    private boolean doOnInterceptor(Context context, String url){
        if(getInterceptor() != null){
            return getInterceptor().intercept(context, url);
        }
        return false;
    }
}
