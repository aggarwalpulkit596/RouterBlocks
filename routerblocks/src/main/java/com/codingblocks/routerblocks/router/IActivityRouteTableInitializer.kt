package com.codingblocks.routerblocks.router

import android.app.Activity

interface IActivityRouteTableInitializer {
    /**
     * init the router table
     * @param router the router map to
     */
    fun initRouterTable(router: Map<String, Class<out Activity>>)


}
