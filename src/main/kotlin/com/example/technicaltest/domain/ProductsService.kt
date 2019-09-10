package com.example.technicaltest.domain

import com.example.technicaltest.data.productsapi.ProductsApiClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductsService(
        @Autowired private val productsApiClient: ProductsApiClient,
        @Autowired private val productsApiResponseToProductsConverter: ProductsApiResponseToProductsConverter
) {

    fun getReducedProductsSortedByReductionDescending(): List<Product> {
        return productsApiResponseToProductsConverter.convert(productsApiClient.getProducts())
                .filter { it.price.hasBeenReduced() }
                .sortedByDescending { it.price.getReduction() }
    }
}