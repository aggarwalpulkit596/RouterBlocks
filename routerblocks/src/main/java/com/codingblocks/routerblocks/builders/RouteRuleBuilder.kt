package com.codingblocks.routerblocks.builders

interface RouteRuleBuilder {

    fun setScheme(scheme: String): RouteRuleBuilder
    fun setHost(host: String): RouteRuleBuilder
    fun setPath(path: String): RouteRuleBuilder
    fun addPathSegment(seg: String): RouteRuleBuilder
    fun addQueryParameter(key: String, value: String): RouteRuleBuilder
}
