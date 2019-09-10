package com.example.technicaltest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.core.io.Resource
import org.springframework.test.context.junit4.SpringRunner
import java.net.URI

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 9191)
class ComponentTests {

	@Autowired
	private lateinit var restTemplate: TestRestTemplate
	@Autowired
	private lateinit var objectMapper: ObjectMapper
	@Value("classpath:example-products-api-response.json")
	private lateinit var exampleProductsApiResponse: Resource
	@Value("classpath:expected-response.json")
	private lateinit var expectedResponse: Resource

	@LocalServerPort
	private lateinit var port: String

	@Test
	fun `should convert products API response into list of reduced products`() {
		// Given
		val productsApiResponseJson = objectMapper.readTree(exampleProductsApiResponse.file)
		val productsApiResponseJsonString = objectMapper.writeValueAsString(productsApiResponseJson)
		stubFor(get(anyUrl()).willReturn(okJson(productsApiResponseJsonString)))

		// When
		val response = restTemplate.getForObject(URI(
				"http://localhost:${port}/reducedProducts?labelType=ShowWasThenNow"), String::class.java)

		// Then
		val actualResponseJsonNode = objectMapper.readTree(response)
		val expectedResponseJsonNode = objectMapper.readTree(expectedResponse.file)
		assertThat(actualResponseJsonNode).isEqualTo(expectedResponseJsonNode)
	}
}
