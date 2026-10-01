package com.silkysys.warehouse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
