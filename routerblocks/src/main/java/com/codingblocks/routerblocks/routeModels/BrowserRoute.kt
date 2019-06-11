package com.codingblocks.routerblocks.routeModels

import com.codingblocks.routerblocks.router.RouterInterface

class BrowserRoute(router: RouterInterface, url: String) : BaseRoute(router, url) {


    class Builder(private var mRouter: RouterInterface) {
        var mUrl: String = ""

        fun setUrl(url: String): Builder {
            mUrl = url
            return this
        }

        fun build(): BrowserRoute {
            return BrowserRoute(mRouter, mUrl)
        }


    }
}
