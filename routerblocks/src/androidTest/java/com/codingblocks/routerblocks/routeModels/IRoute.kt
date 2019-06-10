package com.codingblocks.routerblocks.routeModels

import com.codingblocks.routerblocks.router.RouterInterface

interface IRoute {

    /**
     * get the Router to process the Route
     *
     * @return
     */
    val router: RouterInterface

    val url: String

    val scheme: String

    val host: String

    val port: Int

    val path: List<String>

    val parameters: Map<String, String>

    //Route can open itself

    /**
     *
     * @return true: open success, false : open fail
     */
    fun open(): Boolean

}
