package com.codingblocks.routerblocks.builders

import android.util.Log
import com.codingblocks.routerblocks.Utils
import java.util.*
import java.util.regex.Pattern

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

        @Deprecated("")
        fun isActivityRuleValid(url: String): Boolean {
            val pattern = ":[iflds]?\\{[a-zA-Z0-9]+\\}"
            val p = Pattern.compile(pattern)
            val pathSegs = Utils.getPathSegments(url)
            val checkedSegs = ArrayList<String>()
            for (seg in pathSegs) {
                if (seg.startsWith(":")) {
                    val matcher = p.matcher(seg)
                    if (!matcher.matches()) {
                        Log.w(TAG, "The key format not match : $seg")
                        return false
                    }
                    if (checkedSegs.contains(seg)) {
                        Log.w(TAG, "The key is duplicated : $seg")
                        return false
                    }
                    checkedSegs.add(seg)

                }
            }
            return true
        }
    }
}
