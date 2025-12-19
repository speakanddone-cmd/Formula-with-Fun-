package com.jbs.formulawithfun

data class Formula(
    val title: String,
    val explanation: String,
    var isFavorite: Boolean = false // add this!
)
