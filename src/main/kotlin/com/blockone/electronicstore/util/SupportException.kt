package com.blockone.electronicstore.util

import java.lang.RuntimeException

open class SupportException(
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message, cause)
