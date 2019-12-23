# GraphQL Kotlin

[![Build Status](https://travis-ci.org/ExpediaGroup/graphql-kotlin.svg?branch=master)](https://travis-ci.org/ExpediaGroup/graphql-kotlin)
[![codecov](https://codecov.io/gh/ExpediaGroup/graphql-kotlin/branch/master/graph/badge.svg)](https://codecov.io/gh/ExpediaGroup/graphql-kotlin)
[![Docs](https://github.com/ExpediaGroup/graphql-kotlin/workflows/Publish%20Docs/badge.svg)](https://expediagroup.github.io/graphql-kotlin)
[![Slack](https://img.shields.io/badge/Slack-%23graphql--kotlin-ECB22E.svg?logo=data:image/svg+xml;base64,PHN2ZyB2aWV3Qm94PSIwIDAgNTQgNTQiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBkPSJNMTkuNzEyLjEzM2E1LjM4MSA1LjM4MSAwIDAgMC01LjM3NiA1LjM4NyA1LjM4MSA1LjM4MSAwIDAgMCA1LjM3NiA1LjM4Nmg1LjM3NlY1LjUyQTUuMzgxIDUuMzgxIDAgMCAwIDE5LjcxMi4xMzNtMCAxNC4zNjVINS4zNzZBNS4zODEgNS4zODEgMCAwIDAgMCAxOS44ODRhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYgNS4zODdoMTQuMzM2YTUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2LTUuMzg3IDUuMzgxIDUuMzgxIDAgMCAwLTUuMzc2LTUuMzg2IiBmaWxsPSIjMzZDNUYwIi8+PHBhdGggZD0iTTUzLjc2IDE5Ljg4NGE1LjM4MSA1LjM4MSAwIDAgMC01LjM3Ni01LjM4NiA1LjM4MSA1LjM4MSAwIDAgMC01LjM3NiA1LjM4NnY1LjM4N2g1LjM3NmE1LjM4MSA1LjM4MSAwIDAgMCA1LjM3Ni01LjM4N20tMTQuMzM2IDBWNS41MkE1LjM4MSA1LjM4MSAwIDAgMCAzNC4wNDguMTMzYTUuMzgxIDUuMzgxIDAgMCAwLTUuMzc2IDUuMzg3djE0LjM2NGE1LjM4MSA1LjM4MSAwIDAgMCA1LjM3NiA1LjM4NyA1LjM4MSA1LjM4MSAwIDAgMCA1LjM3Ni01LjM4NyIgZmlsbD0iIzJFQjY3RCIvPjxwYXRoIGQ9Ik0zNC4wNDggNTRhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYtNS4zODcgNS4zODEgNS4zODEgMCAwIDAtNS4zNzYtNS4zODZoLTUuMzc2djUuMzg2QTUuMzgxIDUuMzgxIDAgMCAwIDM0LjA0OCA1NG0wLTE0LjM2NWgxNC4zMzZhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYtNS4zODYgNS4zODEgNS4zODEgMCAwIDAtNS4zNzYtNS4zODdIMzQuMDQ4YTUuMzgxIDUuMzgxIDAgMCAwLTUuMzc2IDUuMzg3IDUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2IDUuMzg2IiBmaWxsPSIjRUNCMjJFIi8+PHBhdGggZD0iTTAgMzQuMjQ5YTUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2IDUuMzg2IDUuMzgxIDUuMzgxIDAgMCAwIDUuMzc2LTUuMzg2di01LjM4N0g1LjM3NkE1LjM4MSA1LjM4MSAwIDAgMCAwIDM0LjI1bTE0LjMzNi0uMDAxdjE0LjM2NEE1LjM4MSA1LjM4MSAwIDAgMCAxOS43MTIgNTRhNS4zODEgNS4zODEgMCAwIDAgNS4zNzYtNS4zODdWMzQuMjVhNS4zODEgNS4zODEgMCAwIDAtNS4zNzYtNS4zODcgNS4zODEgNS4zODEgMCAwIDAtNS4zNzYgNS4zODciIGZpbGw9IiNFMDFFNUEiLz48L2c+PC9zdmc+&labelColor=611f69)](https://kotlinlang.slack.com/messages/graphql-kotlin/)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

GraphQL Kotlin consists of number of libraries that aim to simplify running a GraphQL server in Kotlin.

## 📦 Modules

* [graphql-kotlin-schema-generator](/graphql-kotlin-schema-generator) - Code only GraphQL schema generation for Kotlin
* [graphql-kotlin-federation](/graphql-kotlin-federation) - Schema generator extension to build federated GraphQL schemas
* [graphql-kotlin-spring-server](/graphql-kotlin-spring-server) - Spring Boot auto-configuration library to create GraphQL web app
* [examples](/examples) - Example apps that use graphql-kotlin libraries to test and demonstrate usages

## ⌨️ Usage

Below is a basic example of how `graphql-kotlin-schema-generator` converts your Kotlin code into a GraphQL schema. For more details, see our documentation below or in the individual module READMEs

```kotlin
// Your existing Kotlin code

data class Widget(val id: Int, val value: String)

class WidgetService {
  fun widgetById(id: Int): Widget? {
    // grabs widget from a data source, might return null
  }
}

// Generate the schema

val config = SchemaGeneratorConfig(supportedPackages = listOf("org.example"))
val queries = listOf(TopLevelObject(WidgetService()))

toSchema(config, queries)
```

will generate

```graphql
type Query {
  widgetById(id: Int!): Widget
}

type Widget {
  id: Int!
  value: String!
}
```

## 📋 Documentation

Examples and documentation are available on our [documentation](https://expediagroup.github.io/graphql-kotlin), or in each module README file.

If you have a question about something you can not find in our documentation, the indivdual modules, or javadocs, feel free to [create an issue](https://github.com/ExpediaGroup/graphql-kotlin/issues) and tag it with the question label.

## 👥 Contact

This project is part of Expedia Group Open Source but also maintained by a dedicated team

* Expedia Group
  * https://expediagroup.github.io
  * oss@expediagroup.com
  
* `@ExpediaGroup/graphql-kotlin-committers`
  * Tag us in an issue on Github
  * We also have a public channel, ([#graphql-kotlin](https://app.slack.com/client/T09229ZC6/CQLNT7B29)), open on the Kotlin Slack instance. See the info [here on how to join this slack instance](https://slack.kotlinlang.org/)

## ✏️ Contributing

To get started, please fork the repo and checkout a new branch. You can then build the library locally with Maven

```shell script
mvn clean install
```


See more info in [CONTRIBUTING.md](CONTRIBUTING.md)

## ⚖️ License
This library is licensed under the [Apache License, Version 2.0](LICENSE)
