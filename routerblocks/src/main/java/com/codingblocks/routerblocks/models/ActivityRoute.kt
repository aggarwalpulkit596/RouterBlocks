package com.codingblocks.routerblocks.models

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable

import androidx.fragment.app.Fragment

import com.codingblocks.routerblocks.router.RouterInterface

import java.io.Serializable
import java.lang.ref.WeakReference

class ActivityRoute private constructor(router: RouterInterface, url: String) :
    BaseRoute(router, url) {
    var extras: Bundle? = null
        private set //some option params or not base type params. for example you want to add a parcelable parameter
    var inAnimation = -1
        private set  //the animation the next activity in
    var outAnimation = -1
        private set  //the animation the last activity out
    private var mARef: WeakReference<Activity>? =
        null //if you want to set the animation, you must give last activity
    var openType = 0
        private set
    private var mSupportFRef: WeakReference<Fragment>? =
        null //if you want to use fragment.startActivityForResutl
    var requestCode = 0
        private set  // request code to start activity for result
    private var mFRef: WeakReference<android.app.Fragment>? =
        null //if you want to use android.app.fragment to startActivityForResult, you should set this
    var flags = 0
        private set


    val isAnimationValid: Boolean
        get() = inAnimation != -1 && outAnimation != -1 && mARef != null && mARef!!.get() != null

    val activity: Activity?
        get() = if (mARef != null && mARef!!.get() != null) {
            mARef!!.get()
        } else {
            null
        }

    val supportFragment: Fragment?
        get() = if (mSupportFRef != null) mSupportFRef!!.get() else null

    val fragment: android.app.Fragment?
        get() = if (mFRef != null) mFRef!!.get() else null

    fun addExtras(extras: Bundle): ActivityRoute {
        this.extras!!.putAll(extras)
        return this
    }

    fun setAnimation(activity: Activity, inAnimation: Int, outAnimation: Int): ActivityRoute {
        mARef = WeakReference(activity)
        this.inAnimation = inAnimation
        this.outAnimation = outAnimation
        return this
    }


    /**
     * Set the way to open the page as startActivity, which is also the default way to open
     */
    fun withOpenMethodStart(activity: Activity?): ActivityRoute {
        openType = START
        mARef = WeakReference<Activity>(activity)
        return this
    }

    fun withOpenMethodStartForResult(activity: Activity?, requestCode: Int): ActivityRoute {
        this.requestCode = requestCode
        openType = FOR_RESULT_ACTIVITY
        mARef = WeakReference<Activity>(activity)
        return this
    }

    fun withOpenMethodStartForResult(fragment: Fragment?, requestCode: Int): ActivityRoute {
        this.requestCode = requestCode
        openType = FOR_RESULT_SUPPORT_FRAGMENT
        mSupportFRef = WeakReference<Fragment>(fragment)
        return this
    }

    fun withOpenMethodStartForResult(
        fragment: android.app.Fragment?,
        requestCode: Int
    ): ActivityRoute {
        this.requestCode = requestCode
        openType = FOR_RESULT_FRAGMENT
        mFRef = WeakReference<android.app.Fragment>(fragment)
        return this
    }

    fun withParams(key: String, value: Serializable): ActivityRoute {
        extras!!.putSerializable(key, value)
        return this
    }

    fun withParams(key: String, value: Parcelable): ActivityRoute {
        extras!!.putParcelable(key, value)
        return this
    }

    fun withParams(key: String, value: Int): ActivityRoute {
        extras!!.putInt(key, value)
        return this
    }

    fun withParams(key: String, value: Double): ActivityRoute {
        extras!!.putDouble(key, value)
        return this
    }

    fun withParams(key: String, value: Float): ActivityRoute {
        extras!!.putFloat(key, value)
        return this
    }

    fun withParams(key: String, value: Char): ActivityRoute {
        extras!!.putChar(key, value)
        return this
    }

    fun withParams(key: String, value: CharSequence): ActivityRoute {
        extras!!.putCharSequence(key, value)
        return this
    }

    fun withParams(key: String, value: String): ActivityRoute {
        extras!!.putString(key, value)
        return this
    }

    fun withParams(key: String, value: Long): ActivityRoute {
        extras!!.putLong(key, value)
        return this
    }

    fun withFlags(flags: Int): ActivityRoute {
        this.flags = flags
        return this
    }


    class Builder(internal var mRouter: RouterInterface) {
        private var mUrl: String = ""
        private var mBundle: Bundle = Bundle()
        private var mInAnimation: Int = 0
        private var mOutAnimation: Int = 0
        private var mAct: Activity? = null
        private var mOpenType = 0
        private var mSupportFra: Fragment? =
            null //if you want to use fragment.startActivityForResutl
        private var mRequestCode = 0  // request code to start activity for result
        private var mFra: android.app.Fragment? =
            null //if you want to use android.app.fragment to startActivityForResult, you should set this
        private var mFlags = 0


        fun setUrl(url: String): Builder {
            mUrl = url
            return this
        }

        fun withParams(key: String, value: Parcelable): Builder {
            mBundle.putParcelable(key, value)
            return this
        }

        fun withParams(key: String, value: Int): Builder {
            mBundle.putInt(key, value)
            return this
        }

        fun withParams(key: String, value: Double): Builder {
            mBundle.putDouble(key, value)
            return this
        }

        fun withParams(key: String, value: Float): Builder {
            mBundle.putFloat(key, value)
            return this
        }

        fun withParams(key: String, value: Char): Builder {
            mBundle.putChar(key, value)
            return this
        }

        fun withParams(key: String, value: CharSequence): Builder {
            mBundle.putCharSequence(key, value)
            return this
        }

        fun withParams(key: String, value: String): Builder {
            mBundle.putString(key, value)
            return this
        }

        fun withParams(key: String, value: Long): Builder {
            mBundle.putLong(key, value)
            return this
        }

        /**
         * Set the way to open the page as startActivity, which is also the default way to open
         */
        fun withOpenMethodStart(activity: Activity): Builder {
            mOpenType = START
            mAct = activity
            return this
        }

        fun withOpenMethodStartForResult(activity: Activity, requestCode: Int): Builder {
            mRequestCode = requestCode
            mOpenType = FOR_RESULT_ACTIVITY
            mAct = activity
            return this
        }

        fun withOpenMethodStartForResult(fragment: Fragment, requestCode: Int): Builder {
            mRequestCode = requestCode
            mOpenType = FOR_RESULT_SUPPORT_FRAGMENT
            mSupportFra = fragment
            return this
        }

        fun withOpenMethodStartForResult(
            fragment: android.app.Fragment,
            requestCode: Int
        ): Builder {
            mRequestCode = requestCode
            mOpenType = FOR_RESULT_FRAGMENT
            mFra = fragment
            return this
        }

        fun withFlags(flags: Int): Builder {
            mFlags = flags
            return this
        }

        /**
         * @param extra
         * @return
         */
        fun putAllParams(extra: Bundle): Builder {
            mBundle.putAll(extra)
            return this
        }

        /**
         * set the animation of activity transform
         *
         * @param inAnimation
         * @param outAnimation
         * @return
         */
        fun withAnimation(activity: Activity, inAnimation: Int, outAnimation: Int): Builder {
            mAct = activity
            mInAnimation = inAnimation
            mOutAnimation = outAnimation
            return this
        }


        fun build(): ActivityRoute {
            val route = ActivityRoute(mRouter, mUrl)
            if (mAct != null && mInAnimation != -1 && mOutAnimation != -1) {
                route.setAnimation(mAct as Activity, mInAnimation, mOutAnimation)
            }
            route.withOpenMethodStart(mAct)
            when (mOpenType) {
                1 -> route.withOpenMethodStartForResult(mAct, mRequestCode)
                2 -> route.withOpenMethodStartForResult(mSupportFra, mRequestCode)
                3 -> route.withOpenMethodStartForResult(mFra, mRequestCode)
            }
            route.extras = mBundle
            route.withFlags(mFlags)
            return route
        }
    }

    companion object {
        val START = 0
        val FOR_RESULT_ACTIVITY = 1
        val FOR_RESULT_SUPPORT_FRAGMENT = 2
        val FOR_RESULT_FRAGMENT = 3
    }


}