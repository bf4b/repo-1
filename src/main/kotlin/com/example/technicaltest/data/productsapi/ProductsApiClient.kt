package com.example.technicaltest.data.productsapi

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations

@Component
class ProductsApiClient(
        @Autowired private val restOperations: RestOperations,
        @Value("\${jl.products.api.url}") private val productsApiUrl: String
) {

    fun getProducts(): ProductsApiResponse {
        return restOperations.getForObject(productsApiUrl, ProductsApiResponse::class.java)
                ?: ProductsApiResponse(emptyList())
    }
}