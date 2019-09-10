package com.example.technicaltest.web

import com.example.technicaltest.domain.LabelType
import com.example.technicaltest.domain.ProductsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("reducedProducts")
class ProductsController(
        @Autowired private val productsService: ProductsService,
        @Autowired private val productsConverter: ProductsConverter
) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getProductsReduced(@RequestParam("labelType", required = false, defaultValue = "ShowWasNow") labelType: String)
            : ResponseEntity<ReducedProductsResponse> {

        val labelTypeEnum = when (labelType) {
            "ShowWasNow" -> LabelType.SHOW_WAS_NOW
            "ShowWasThenNow" -> LabelType.SHOW_WAS_THEN_NOW
            "ShowPercDscount" -> LabelType.SHOW_PERCENTAGE_DISCOUNT
            else -> return ResponseEntity.badRequest().build()
        }

        val reducedProducts = productsService.getReducedProductsSortedByReductionDescending()
        val reducedProductsResponse = productsConverter.convert(reducedProducts, labelTypeEnum)

        return ResponseEntity.ok(reducedProductsResponse)
    }
}