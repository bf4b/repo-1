package com.example.technicaltest.domain

import com.example.technicaltest.data.productsapi.ColorSwatchApiResponse
import com.example.technicaltest.data.productsapi.PriceApiResponse
import com.example.technicaltest.data.productsapi.ProductApiResponse
import com.example.technicaltest.data.productsapi.ProductsApiResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ProductsApiResponseToProductsConverterTest {

    private lateinit var converter: ProductsApiResponseToProductsConverter

    @Before
    fun setUp() {
        converter = ProductsApiResponseToProductsConverter(ColorNameToHexConverter())
    }

    @Test
    fun `should handle no products`() {
        // When
        val products = converter.convert(ProductsApiResponse(null))

        // Then
        assertThat(products).isEmpty()
    }

    @Test
    fun `should handle null product`() {
        // When
        val product = converter.convert(ProductApiResponse(null, null, null, null))

        // Then
        assertThat(product).isEqualTo(Product("", "", Companion.EMPTY_PRICE, emptyList()))
    }

    @Test
    fun `should convert product`() {
        // Given
        val productId = "my product id"
        val title = "my title"
        val productApiResponse = ProductApiResponse(
                productId,
                title,
                null,
                null
        )

        // When
        val product = converter.convert(productApiResponse)

        // Then
        val expectedProduct = Product(
                productId,
                title,
                Companion.EMPTY_PRICE,
                emptyList()
        )
        assertThat(product).isEqualTo(expectedProduct)
    }

    @Test
    fun `should convert price - no then1, no then2`() {
        // Given
        val priceApiResponse = PriceApiResponse("12.20", null, null, "9.49", "GBP")

        // When
        val price = converter.convert(priceApiResponse)

        // Then
        assertThat(price).isEqualTo(Price(BigDecimal("12.20"), null, BigDecimal("9.49"), "GBP"))
    }

    @Test
    fun `should convert price - with then1, no then2`() {
        // Given
        val priceApiResponse = PriceApiResponse("12.20", "10", null, "9.49", "GBP")

        // When
        val price = converter.convert(priceApiResponse)

        // Then
        assertThat(price).isEqualTo(Price(BigDecimal("12.20"), BigDecimal("10.00"), BigDecimal("9.49"), "GBP"))
    }

    @Test
    fun `should convert price - no then1, with then2`() {
        // Given
        val priceApiResponse = PriceApiResponse("12.20", null, "10", "9.49", "GBP")

        // When
        val price = converter.convert(priceApiResponse)

        // Then
        assertThat(price).isEqualTo(Price(BigDecimal("12.20"), BigDecimal("10.00"), BigDecimal("9.49"), "GBP"))
    }

    @Test
    fun `should convert price - with then1, with then2`() {
        // Given
        val priceApiResponse = PriceApiResponse("12.20", "11.1", "10", "9.49", "GBP")

        // When
        val price = converter.convert(priceApiResponse)

        // Then
        assertThat(price).isEqualTo(Price(BigDecimal("12.20"), BigDecimal("10.00"), BigDecimal("9.49"), "GBP"))
    }


    @Test
    fun `should convert price that cannot be formatted as a price`() {
        // Given
        val priceApiResponse = PriceApiResponse("12.A", null, null, null, null)

        // When
        val price = converter.convert(priceApiResponse)

        // Then
        assertThat(price).isEqualTo(EMPTY_PRICE)
    }

    @Test
    fun `should convert color swatch`() {
        // Given
        val color = "shocking pink"
        val skuId = "my sku"
        val colorSwatchApiResponse = ColorSwatchApiResponse(color, "ReD", skuId)

        // When
        val colorSwatch = converter.convert(colorSwatchApiResponse)

        // Then
        assertThat(colorSwatch).isEqualTo(ColorSwatch(color, "FF0000", skuId))
    }

    @Test
    fun `should convert null color swatch`() {
        // Given
        val colorSwatchApiResponse = ColorSwatchApiResponse(null, null, null)

        // When
        val colorSwatch = converter.convert(colorSwatchApiResponse)

        // Then
        assertThat(colorSwatch).isEqualTo(ColorSwatch("", "", ""))
    }

    @Test
    fun `should convert color swatch with unrecognised basicColor`() {
        // Given
        val color = "shocking pink"
        val skuId = "my sku"
        val colorSwatchApiResponse = ColorSwatchApiResponse(color, "this isn't a colour", skuId)

        // When
        val colorSwatch = converter.convert(colorSwatchApiResponse)

        // Then
        assertThat(colorSwatch).isEqualTo(ColorSwatch(color, "", skuId))
    }

    companion object {
        private val EMPTY_PRICE = Price(BigDecimal.ZERO, null, BigDecimal.ZERO, "")
    }
}