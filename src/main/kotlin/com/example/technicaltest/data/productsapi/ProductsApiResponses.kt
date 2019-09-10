package com.example.technicaltest.data.productsapi

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class ProductsApiResponse(val products: List<ProductApiResponse>?)

data class ProductApiResponse(val productId: String?,
                              val title: String?,
                              val price: PriceApiResponse?,
                              val colorSwatches: List<ColorSwatchApiResponse>?)

@JsonDeserialize(using = PriceApiResponseDeserializer::class)
data class PriceApiResponse(val was: String?,
                            val then1: String?,
                            val then2: String?,
                            val now: String?,
                            val currency: String?)

data class ColorSwatchApiResponse(val color: String?,
                                  val basicColor: String?,
                                  val skuId: String?)
