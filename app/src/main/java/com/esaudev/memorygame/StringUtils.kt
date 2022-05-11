package com.esaudev.memorygame

object StringUtils {
    fun randomID(): String = List(16) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")
}