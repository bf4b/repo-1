package com.example.technicaltest.web

data class ReducedProductsResponse(val products: List<ReducedProductResponse>)

data class ReducedProductResponse(val productId: String,
                                  val title: String,
                                  val colorSwatches: List<ColorSwatchResponse>,
                                  val nowPrice: String,
                                  val priceLabel: String)

data class ColorSwatchResponse(val color: String,
                               val rgbColor: String,
                               val skuid: String)
