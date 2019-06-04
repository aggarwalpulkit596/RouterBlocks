package com.codingblocks.routerblocks

import kotlin.annotation.Target
import kotlin.annotation.Retention

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RouterMap(vararg val value: String)