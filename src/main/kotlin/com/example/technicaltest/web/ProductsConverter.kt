package com.example.technicaltest.web

import com.example.technicaltest.domain.ColorSwatch
import com.example.technicaltest.domain.LabelType
import com.example.technicaltest.domain.Price
import com.example.technicaltest.domain.Product
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

@Component
class ProductsConverter(
        @Autowired private val priceToPriceLabelConverter: PriceToPriceLabelConverter,
        @Autowired private val priceFormatter: PriceFormatter
) {

    fun convert(products: List<Product>,
                labelType: LabelType): ReducedProductsResponse {

        return ReducedProductsResponse(products.map { convert(it, labelType) })
    }

    fun convert(product: Product, labelType: LabelType): ReducedProductResponse {
        return ReducedProductResponse(
                product.productId,
                product.title,
                product.colorSwatches.map { convert(it) },
                priceFormatter.format(product.price.now, product.price.currency),
                priceToPriceLabelConverter.convertToPriceLabel(product.price, labelType)
        )
    }

    fun convert(colorSwatch: ColorSwatch): ColorSwatchResponse {
        return ColorSwatchResponse(
                colorSwatch.color,
                colorSwatch.rgbColor,
                colorSwatch.skuid
        )
    }
}

@Component
class PriceFormatter {
    fun format(price: BigDecimal, currencyCode: String): String {
        val symbol = getCurrencySymbol(currencyCode)
        val decimalFormat = DecimalFormat()
        decimalFormat.roundingMode = RoundingMode.DOWN
        if (price >= BigDecimal(10)) {
            decimalFormat.minimumFractionDigits = 0
            decimalFormat.maximumFractionDigits = 0
        } else {
            decimalFormat.minimumFractionDigits = 2
            decimalFormat.maximumFractionDigits = 2
        }
        return "$symbol${decimalFormat.format(price)}"
    }

    private fun getCurrencySymbol(currencyCode: String): String {
        return try {
            Currency.getInstance(currencyCode).symbol
        } catch (iae: IllegalArgumentException) {
            return ""
        }
    }
}

@Component
class PriceToPriceLabelConverter(
        @Autowired private val priceFormatter: PriceFormatter
) {
    fun convertToPriceLabel(price: Price, labelType: LabelType): String {
        val was = price.was
        val now = price.now
        val currency = price.currency
        return when (labelType) {
            LabelType.SHOW_WAS_NOW -> {
                "Was ${priceFormatter.format(was, currency)}, now ${priceFormatter.format(now, currency)}"
            }
            LabelType.SHOW_WAS_THEN_NOW -> {
                val then = if (price.then == null) "" else ", then ${priceFormatter.format(price.then, currency)}"
                "Was ${priceFormatter.format(was, currency)}$then, now ${priceFormatter.format(now, currency)}"
            }
            LabelType.SHOW_PERCENTAGE_DISCOUNT -> {
                val percentageDiscountFormat = DecimalFormat()
                percentageDiscountFormat.maximumFractionDigits = 0
                val formattedDiscountPercentage = percentageDiscountFormat.format(price.getDiscountPercentage())
                "$formattedDiscountPercentage% off - now ${priceFormatter.format(now, currency)}"
            }
        }
    }

}