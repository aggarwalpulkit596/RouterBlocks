package com.codingblocks.routerblocks.builders

import android.net.Uri

abstract class BaseRouteRuleBuilder : RouteRuleBuilder {

    private var builder = Uri.Builder()

    override fun setScheme(scheme: String): RouteRuleBuilder {
        builder.scheme(scheme)
        return this
    }

    override fun setHost(host: String): RouteRuleBuilder {
        builder.authority(host)
        return this
    }


    override fun setPath(path: String): RouteRuleBuilder {
        builder.path(path)
        return this
    }


    override fun addPathSegment(seg: String): RouteRuleBuilder {
        builder.appendPath(seg)
        return this
    }


    override fun addQueryParameter(key: String, value: String): RouteRuleBuilder {
        builder.appendQueryParameter(key, value)
        return this
    }


    fun build(): String {
        return builder.build().toString()
    }
}
