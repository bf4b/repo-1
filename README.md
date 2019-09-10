# Technical Test

## Usage

Build with `./gradlew build` and run with `./gradlew bootRun`

## Assumptions
The API has multiple pages, as indicated by the `results` and `pagesAvailable` fields. I only noticed this quite late, and I couldn't find any instructions on how to page through the API (I guessed    the `page` query param), so this solution only handles the first page of results. If it had to handle more, I would have implemented it quite differently – probably by having a scheduled task that paged through and cached all products in a local database for later querying.  

For some products on the API response, `price.now` is an object with `from` and `to` fields, rather than a string. In this case, I used a custom `JsonDeserializer` to extract `price.now.from`, as that is the lower price for the products returned.

In the specification, the "now" price is formatted with three decimal places – "...now £y.yyy" and "...now £z.zzz". I've assumed you don't want the price formatted to include fractions of a penny, so I've used two decimal places.

## Implementation notes
I've created three layers, currently grouped into packages but could be split into modules as the project grows:
* `web` depends on `domain`, which depends on `data`. 
* `data` is responsible for obtaining the source data. In this case we're using an HTTP API, but any database repositories would also go here. Here is where we handle `null` values by replacing them with zero or empty, as specified.
* `domain` is where the business logic resides. Here we convert source data into our internal representation. For example, we convert String prices to BigDecimal.
* `web` is where input and output via HTTP are handled (not withstanding Spring MVC libraries) and our internal data representations are converted into a format for presentation, depending on what the user of the service requests via URL query parameter.

This project contains unit tests under `src/test` and component tests under `src/componentTest`, using the definitions given in [this resource on microservice testing](https://martinfowler.com/articles/microservice-testing/#conclusion-summary). The component test can be thought of as an "end-to-end" test for this service. The application runs in its entirety (using `@SpringBootTest`) and external dependencies are mocked (using Wiremock).
  
## Further work
Here are further improvements I could make to this solution, given more time and depending on performance requirements:

* I haven't completely covered all unit test cases – I simply ran out of time. Most business logic is in the converters, so I focused on those. I've added some TODOs as I went along.
* Use resilience4j to create a circuit breaker and define the fallback behaviour for this service when the API is unresponsive. At the moment, long-running calls to the API will propagate to this service when they could instead, for example, return an empty list or return 503. I've not personally used resilience4j, but I've used Hystrix and I understand resilience4j offers similar functionality. 
* Define a Consumer Driven Contract between this service and its API, to reduce the likelihood of the API making breaking changes.
* Use Swagger (Spring Fox) to document the endpoints.
* Could cache the API results
* Make the service more observable, with logging probably being the most useful.
* Use data tables in unit tests
* Build script logic could be refactored
* Better handling of port used for Wiremock
