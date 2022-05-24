package com.esaudev.memorygame.model

import com.esaudev.memorygame.StringUtils
import java.util.*

data class CR7Card(
    var id: String = StringUtils.randomID(),
    val cardIndicator: String = StringUtils.randomID(),
    val image: Int,
    var founded: Boolean
)
