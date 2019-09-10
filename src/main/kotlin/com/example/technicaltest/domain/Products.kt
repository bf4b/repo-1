package com.example.technicaltest.domain

import java.math.BigDecimal
import java.math.RoundingMode

data class Product(val productId: String,
                   val title: String,
                   val price: Price,
                   val colorSwatches: List<ColorSwatch>)

data class Price(val was: BigDecimal,
                 val then: BigDecimal?,
                 val now: BigDecimal,
                 val currency: String) {

    fun hasBeenReduced(): Boolean = was > now
    fun getReduction(): BigDecimal = if (hasBeenReduced()) was - now else BigDecimal.ZERO
    fun getDiscountPercentage(): BigDecimal = ((getReduction().divide(was, 2, RoundingMode.DOWN)) * BigDecimal(100))
}

data class ColorSwatch(val color: String,
                       val rgbColor: String,
                       val skuid: String)

enum class LabelType {
    SHOW_WAS_NOW,
    SHOW_WAS_THEN_NOW,
    SHOW_PERCENTAGE_DISCOUNT
}