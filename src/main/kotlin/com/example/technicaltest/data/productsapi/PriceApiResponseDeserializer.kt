package com.example.technicaltest.data.productsapi

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

/**
 * If `price.now` is an object, use `price.now.from`. Otherwise assume `price.now` is a String.
 */
class PriceApiResponseDeserializer: JsonDeserializer<PriceApiResponse> {

    constructor() : super()

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): PriceApiResponse {
        val priceNode = p!!.codec.readTree<JsonNode>(p)
        val now = if (priceNode.has("now") && priceNode.get("now").isObject) {
            priceNode.get("now").get("from").asText()
        } else {
            priceNode.get("now").asText()
        }

        return PriceApiResponse(
                priceNode.get("was").asText(),
                priceNode.get("then1").asText(),
                priceNode.get("then2").asText(),
                now,
                priceNode.get("currency").asText()
        )
    }
}