package com.codingblocks.routerblocks.routerBuilder

import android.util.Log
import com.codingblocks.routerblocks.Utils
import java.util.regex.Pattern

class ActivityRouteUrlBuilder
/**
 * @param matchPath
 */
    (private var mPath: String) {
    private val mMatchPath: String = mPath


    fun withKeyValue(key: String, value: Int): ActivityRouteUrlBuilder {
        mPath = mPath.replace(String.format(":i{%s}", key), Integer.toString(value))
        return this
    }

    fun withKeyValue(key: String, value: Float): ActivityRouteUrlBuilder {
        mPath = mPath.replace(String.format(":f{%s}", key), java.lang.Float.toString(value))
        return this
    }

    fun withKeyValue(key: String, value: Long): ActivityRouteUrlBuilder {
        mPath = mPath.replace(String.format(":l{%s}", key), java.lang.Long.toString(value))
        return this
    }

    fun withKeyValue(key: String, value: Double): ActivityRouteUrlBuilder {
        mPath = mPath.replace(String.format(":d{%s}", key), java.lang.Double.toString(value))
        return this
    }


    fun withKeyValue(key: String, value: String): ActivityRouteUrlBuilder {
        mPath = mPath.replace(String.format(":{%s}", key), value)
        mPath = mPath.replace(String.format(":s{%s}", key), value)
        return this
    }

    fun withKeyValue(key: String, value: Char): ActivityRouteUrlBuilder {
        mPath = mPath.replace(String.format(":c{%s}", key), Character.toString(value))
        return this
    }

    fun withQueryParameter(key: String, value: String): ActivityRouteUrlBuilder {
        mPath = Utils.addQueryParameters(mPath, key, value)
        return this
    }

    fun build(): String? {
        val matcher = Pattern.compile(":[i, f, l, d, s, c]?\\{[a-zA-Z0-9]+?}").matcher(mPath)
        if (matcher.find()) {
            Log.w(TAG, "Not all the key settled")
        }
        return mPath
    }

    companion object {
        private val TAG = "RoutePathBuilder"
    }

}
