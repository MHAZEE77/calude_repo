package com.silkysys.warehouse.app.error_handle

import java.io.IOException

class InterceptorHandledException(
    val code: Int,
    message: String
) : IOException(message)