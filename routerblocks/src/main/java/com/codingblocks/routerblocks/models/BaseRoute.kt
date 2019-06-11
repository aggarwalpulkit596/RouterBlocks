package com.codingblocks.routerblocks.models

import com.codingblocks.routerblocks.Utils
import com.codingblocks.routerblocks.router.RouterInterface

abstract class BaseRoute(router: RouterInterface, url: String) : Route {

    private var mRouter: RouterInterface = router
    private var mUrl: String = url
    private var mScheme: String = ""
    private var mHost: String = ""
    private var mPort: Int = 0
    private var mPath: List<String>
    private var mQueryParameters: Map<String, String>

    init {
        mScheme = Utils.getScheme(url)
        mHost = Utils.getHost(url)
        mPort = Utils.getPort(url)
        mPath = Utils.getPathSegments(url)
        mQueryParameters = Utils.getParameters(url)
    }

    override val router: RouterInterface
        get() = mRouter
    override val url: String
        get() = mUrl
    override val scheme: String
        get() = mScheme
    override val host: String
        get() = mHost
    override val port: Int
        get() = mPort
    override val path: List<String>
        get() = mPath
    override val parameters: Map<String, String>
        get() = mQueryParameters

    override fun open(): Boolean {
        return mRouter.open(this)
    }

}