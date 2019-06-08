package com.codingblocks.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RouterMap(vararg val value: String)