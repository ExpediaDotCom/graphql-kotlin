---
id: examples
title: Examples
---

## Spring Server Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). A sample Spring
Boot app that uses [Spring
Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with
`graphql-kotlin-schema-generator` and [graphql-playground](https://github.com/prisma/graphql-playground) is provided as
a [examples/spring](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/spring). All the examples used
in this documentation should be available in the sample app.

In order to run it you can run
[Application.kt](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/spring/src/main/kotlin/com/expediagroup/graphql/examples/Application.kt)
directly from your IDE. Alternatively you can also use the Spring Boot maven plugin by running `mvn spring-boot:run`
from the command line. Once the app has started you can explore the example schema by opening Playground endpoint at
[http://localhost:8080/playground](http://localhost:8080/playground).

## Federation Example

There is also an example of [Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/) with two Spring Boot apps using `graphql-kotlin-federation` and an Apollo Gateway app in Nodejs that exposes a single federated schema in [examples/federation](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/federation)
project. Please refer to the README files for details on how to run each application.
