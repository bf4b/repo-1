package com.example.technicaltest.web

import com.example.technicaltest.domain.LabelType
import com.example.technicaltest.domain.Price
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class PriceToPriceLabelConverterTest {

    @Mock private lateinit var priceFormatter: PriceFormatter

    private lateinit var converter: PriceToPriceLabelConverter

    @Before
    fun setUp() {
        converter = PriceToPriceLabelConverter(priceFormatter)
        doReturn("£10").whenever(priceFormatter).format(NOW, CURRENCY)
        doReturn("£20").whenever(priceFormatter).format(THEN, CURRENCY)
        doReturn("£30").whenever(priceFormatter).format(WAS, CURRENCY)
    }

    @Test
    fun `should format "Was X, now Y" price`() {
        // When
        val priceLabel = converter.convertToPriceLabel(Price(WAS, THEN, NOW, CURRENCY), LabelType.SHOW_WAS_NOW)

        // Then
        assertThat(priceLabel).isEqualTo("Was £30, now £10")
    }

    @Test
    fun `should format "Was X, then Y, now Z" price`() {
        // When
        val priceLabel = converter.convertToPriceLabel(Price(WAS, THEN, NOW, CURRENCY), LabelType.SHOW_WAS_THEN_NOW)

        // Then
        assertThat(priceLabel).isEqualTo("Was £30, then £20, now £10")
    }

    @Test
    fun `should format "Was X, then Y, now Z" price - no then price`() {
        // When
        val priceLabel = converter.convertToPriceLabel(Price(WAS, null, NOW, CURRENCY), LabelType.SHOW_WAS_THEN_NOW)

        // Then
        assertThat(priceLabel).isEqualTo("Was £30, now £10")
    }

    @Test
    fun `should format "X% off - now Y" price`() {
        // When
        val priceLabel = converter.convertToPriceLabel(Price(WAS, THEN, NOW, CURRENCY), LabelType.SHOW_PERCENTAGE_DISCOUNT)

        // Then
        assertThat(priceLabel).isEqualTo("66% off - now £10")
    }

    companion object {
        val NOW = BigDecimal(10.00)
        val THEN = BigDecimal(20.00)
        val WAS = BigDecimal(30.00)
        val CURRENCY = "GBP"
    }
}