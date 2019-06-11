package com.codingblocks.routerblocks.routerBuilder

import android.util.Log
import java.util.*

class ActivityRouteRuleBuilder : BaseRouteRuleBuilder() {

    private var mKeys: MutableList<String> = ArrayList()


    override fun setHost(host: String): ActivityRouteRuleBuilder {
        super.setHost(host)
        return this
    }

    override fun setScheme(scheme: String): ActivityRouteRuleBuilder {
        super.setScheme(scheme)
        return this
    }

    override fun addPathSegment(seg: String): ActivityRouteRuleBuilder {
        super.addPathSegment(seg)
        return this
    }

    override fun addQueryParameter(key: String, value: String): ActivityRouteRuleBuilder {
        super.addQueryParameter(key, value)
        return this
    }

    override fun setPath(path: String): ActivityRouteRuleBuilder {
        super.setPath(path)
        return this
    }

    /**
     * Add a definition of the value in the path, including the data type
     *
     * @param key
     * @param type
     * @return
     */
    fun addKeyValueDefine(key: String, type: Class<*>): ActivityRouteRuleBuilder {
        val typeChar = if (type == Int::class.java) {
            "i"
        } else if (type == Float::class.java) {
            "f"
        } else if (type == Long::class.java) {
            "l"
        } else if (type == Double::class.java) {
            "d"
        } else if (type == String::class.java || type == CharSequence::class.java) {
            "s"
        } else {
            "s"
        }
        val keyFormat = String.format(":%s{%s}", typeChar, key)
        if (mKeys.contains(keyFormat)) {
            Log.e(TAG, "", KeyDuplicateException(keyFormat))
        } else {
            addPathSegment(keyFormat)
            mKeys.add(keyFormat)
        }
        return this
    }

    class KeyDuplicateException(key: String) : Exception("The key is duplicated: $key")

    companion object {
        private const val TAG = "ActivityRouteUrlBuilder"
    }
}
