package com.codingblocks.routerblocks

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Utils {

    private val TAG = "UrlUtils"

    companion object {
        /**
         * get the scheme of the url
         * @param url
         * @return
         */
        fun getScheme(url: String): String {
            return Uri.parse(url).scheme
        }

        /**
         * get the path segments
         * @param url
         * @return
         */
        fun getPathSegments(url: String): List<String> {
            return Uri.parse(url).pathSegments
        }

        /**
         * get the protocol of the url
         */
        fun getPort(url: String): Int {
            return Uri.parse(url).port
        }

        fun getHost(url: String): String {
            return Uri.parse(url).host
        }

        fun getParameters(url: String): HashMap<String, String> {
            val parameters = HashMap<String, String>()
            try {
                val uri = Uri.parse(url)
                val keys = uri.queryParameterNames

                for (key in keys) {
                    parameters[key] = uri.getQueryParameter(key)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString() + "")
            }

            return parameters
        }

        fun addQueryParameters(url: String, parameters: Map<String, String>): String {
            try {
                val uri = Uri.parse(url)
                val builder = uri.buildUpon()
                for (key in parameters.keys) {
                    builder.appendQueryParameter(key, parameters[key])
                }
                return builder.toString()
            } catch (e: Exception) {
                Log.e(TAG, e.toString() + "")
            }

            return url
        }

        fun addQueryParameters(url: String, key: String, value: String): String {
            try {
                val uri = Uri.parse(url)
                return uri.buildUpon().appendQueryParameter(key, value).build().toString()
            } catch (e: Exception) {
                Log.e(TAG, e.toString() + "")
            }

            return url
        }
    }
}
