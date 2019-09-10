package com.example.technicaltest.domain

import com.example.technicaltest.data.productsapi.ColorSwatchApiResponse
import com.example.technicaltest.data.productsapi.PriceApiResponse
import com.example.technicaltest.data.productsapi.ProductApiResponse
import com.example.technicaltest.data.productsapi.ProductsApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class ProductsApiResponseToProductsConverter(
        @Autowired private val colorNameToHexConverter: ColorNameToHexConverter
) {

    fun convert(productsApiResponse: ProductsApiResponse): List<Product> {
        if (productsApiResponse.products == null) return emptyList()
        return productsApiResponse.products.map { convert(it) }
    }

    //@VisibleForTesting
    internal fun convert(productApiResponse: ProductApiResponse): Product {
        return Product(
                productApiResponse.productId ?: "",
                productApiResponse.title ?: "",
                productApiResponse.price?.let { convert(it) } ?: emptyPrice(),
                productApiResponse.colorSwatches?.let { convert(it) } ?: emptyList()
        )
    }

    //@VisibleForTesting
    internal fun convert(priceApiResponse: PriceApiResponse): Price {
        return Price(
                convertPriceString(priceApiResponse.was),
                convertPriceThen(priceApiResponse),
                convertPriceString(priceApiResponse.now),
                priceApiResponse.currency ?: ""
        )
    }

    private fun convertPriceThen(priceApiResponse: PriceApiResponse): BigDecimal? {
        if (priceApiResponse.then2 != null && priceApiResponse.then2.isNotEmpty()) {
            return convertPriceString(priceApiResponse.then2)
        }
        if (priceApiResponse.then1 != null && priceApiResponse.then1.isNotEmpty()) {
            return convertPriceString(priceApiResponse.then1)
        }
        return null
    }

    private fun convertPriceString(priceString: String?): BigDecimal {
        if (priceString == null) return BigDecimal.ZERO
        return try {
            priceString.toBigDecimal().setScale(2)
        } catch (nfe: NumberFormatException) {
            BigDecimal.ZERO
        }
    }

    private fun convert(colorSwatchApiResponses: List<ColorSwatchApiResponse>): List<ColorSwatch> {
        return colorSwatchApiResponses.map{ convert(it) }
    }

    //@VisibleForTesting
    internal fun convert(colorSwatchApiResponse: ColorSwatchApiResponse): ColorSwatch {
        return ColorSwatch(
                colorSwatchApiResponse.color ?: "",
                colorSwatchApiResponse.basicColor?.let {
                    colorNameToHexConverter.convertToHex(colorSwatchApiResponse.basicColor)
                } ?: "",
                colorSwatchApiResponse.skuId ?: ""
        )
    }

    private fun emptyPrice(): Price {
        return Price(
                BigDecimal.ZERO,
                null,
                BigDecimal.ZERO,
                ""
        )
    }
}

@Component
class ColorNameToHexConverter(private val colorMap: MutableMap<String, String> = HashMap()) {

    init {
        colorMap["red"] = "FF0000"
        colorMap["green"] = "00FF00"
        colorMap["blue"] = "0000FF"
        colorMap["pink"] = "FFC0CB"
        colorMap["yellow"] = "FFFF00"
        colorMap["grey"] = "808080"
        colorMap["purple"] = "800080"
    }

    fun convertToHex(color: String): String {
        return colorMap[color.toLowerCase()] ?: ""
    }
}