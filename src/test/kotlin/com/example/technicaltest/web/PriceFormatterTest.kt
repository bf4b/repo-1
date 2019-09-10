package com.example.technicaltest.web

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class PriceFormatterTest {

    private lateinit var priceFormatter: PriceFormatter

    @Before
    fun setUp() {
        priceFormatter = PriceFormatter()
    }

    @Test
    fun `should format large price`() {
        // When
        val formattedPrice = priceFormatter.format(BigDecimal("54.78"), "GBP")

        // Then
        assertThat(formattedPrice).isEqualTo("£54")
    }

    @Test
    fun `should format small price`() {
        // When
        val formattedPrice = priceFormatter.format(BigDecimal("4.78"), "GBP")

        // Then
        assertThat(formattedPrice).isEqualTo("£4.78")
    }

    @Test
    fun `should format non-GBP currency`() {
        // When
        val formattedPrice = priceFormatter.format(BigDecimal("6.90"), "EUR")

        // Then
        assertThat(formattedPrice).isEqualTo("€6.90")
    }

    @Test
    fun ` should handle unrecognised currency code`() {
        // When
        val formattedPrice = priceFormatter.format(BigDecimal("2.90"), "BLAH")

        // Then
        assertThat(formattedPrice).isEqualTo("2.90")
    }
}