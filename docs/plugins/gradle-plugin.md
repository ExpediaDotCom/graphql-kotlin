---
id: gradle-plugin
title: Gradle Plugin
---

GraphQL Kotlin Gradle Plugin provides functionality to generate a lightweight GraphQL HTTP client and generate GraphQL
schema directly from your source code.

> NOTE: This plugin is dependent on Kotlin compiler plugin as it generates Kotlin source code that needs to be compiled.

## Usage

`graphql-kotlin-gradle-plugin` is published on Gradle [Plugin Portal](https://plugins.gradle.org/plugin/com.expediagroup.graphql).
In order to execute any of the provided tasks you need to first apply the plugin on your project.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

Using plugins DSL syntax

```kotlin
// build.gradle.kts
plugins {
    id("com.expediagroup.graphql") version $graphQLKotlinVersion
}
```

Or by using legacy plugin application

```kotlin
// build.gradle.kts
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("com.expediagroup:graphql-kotlin-gradle-plugin:$graphQLKotlinVersion")
  }
}

apply(plugin = "com.expediagroup.graphql")
```
<!--Groovy-->

Using plugins DSL syntax

```groovy
// build.gradle
plugins {
    id 'com.expediagroup.graphql' version $graphQLKotlinVersion
}
```

Or by using legacy plugin application

```groovy
// build.gradle
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "com.expediagroup:graphql-kotlin-gradle-plugin:$graphQLKotlinVersion"
  }
}

apply plugin: "com.expediagroup.graphql"
```
<!--END_DOCUSAURUS_CODE_TABS-->

## Extension

GraphQL Kotlin Gradle Plugin uses an extension on the project named `graphql` of type
[GraphQLPluginExtension](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/plugins/graphql-kotlin-gradle-plugin/src/main/kotlin/com/expediagroup/graphql/plugin/gradle/GraphQLPluginExtension.kt).
This extension can be used to configure global options instead of explicitly configuring individual tasks. Once extension
is configured, it will automatically download SDL/run introspection to generate GraphQL schema and subsequently generate
all GraphQL clients. GraphQL Extension should be used by default, except for cases where you need to only run individual
tasks.

<!--DOCUSAURUS_CODE_TABS-->
<!--Kotlin-->

```kotlin
// build.gradle.kts
import com.expediagroup.graphql.plugin.generator.GraphQLClientType
import com.expediagroup.graphql.plugin.gradle.graphql

graphql {
  client {
    // Boolean flag indicating whether or not selection of deprecated fields is allowed.
    allowDeprecatedFields = false
    // Type of GraphQL client implementation to generate.
    clientType = GraphQLClientType.DEFAULT
    // Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.
    converters = mapOf("UUID" to ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter"))
    // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint`.
    endpoint = "http://localhost:8080/graphql"
    // Optional HTTP headers to be specified on an introspection query or SDL request.
    headers = mapOf("X-Custom-Header" to "Custom-Header-Value")
    // Target package name to be used for generated classes.
    packageName = "com.example.generated"
    // Custom directory containing query files, defaults to src/main/resources
    queryFileDirectory = "${project.projectDir}/src/main/resources/queries"
    // Optional list of query files to be processed, takes precedence over queryFileDirectory
    queryFiles = listOf(file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql"))
    // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint`.
    sdlEndpoint = "http://localhost:8080/sdl"
    // Timeout configuration for introspection query/downloading SDL
    timeout {
        // Connect timeout in milliseconds
        connect = 5_000
        // Read timeout in milliseconds
        read = 15_000
    }
  }
  schema {
    // List of supported packages that can contain GraphQL schema type definitions
    packages = listOf("com.example")
    // Optional artifact name that contains SchemaGeneratorHooks service provider
    hooksProviderArtifact = "com.expediagroup:graphql-kotlin-federated-hooks-provider:$$graphQLKotlinVersion"
  }
}
```
<!--Groovy-->

```groovy
// build.gradle
graphql {
    client {
        // Boolean flag indicating whether or not selection of deprecated fields is allowed.
        allowDeprecatedFields = false
        // Type of GraphQL client implementation to generate.
        clientType = com.expediagroup.graphql.plugin.generator.GraphQLClientType.DEFAULT
        // Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values.
        converters = ["UUID" : new com.expediagroup.graphql.plugin.generator.ScalarConverterMapping("java.util.UUID", "com.example.UUIDScalarConverter")]
        // GraphQL server endpoint that will be used to for running introspection queries. Alternatively you can download schema directly from `sdlEndpoint`.
        endpoint = "http://localhost:8080/graphql"
        // Optional HTTP headers to be specified on an introspection query or SDL request.
        headers = ["X-Custom-Header" : "My-Custom-Header-Value"]
        // Target package name to be used for generated classes.
        packageName = "com.example.generated"
        // Custom directory containing query files, defaults to src/main/resources
        queryFileDirectory = "${project.projectDir}/src/main/resources/queries"
        // Optional list of query files to be processed, takes precedence over queryFileDirectory
        queryFiles = [file("${project.projectDir}/src/main/resources/queries/MyQuery.graphql")]
        // GraphQL server SDL endpoint that will be used to download schema. Alternatively you can run introspection query against `endpoint`.
        sdlEndpoint = "http://localhost:8080/sdl"
        // Timeout configuration for introspection query/downloading SDL
        timeout { t ->
            // Connect timeout in milliseconds
            t.connect = 5000
            t.read = 15000
        }
    }
    schema {
        packages = ["com.example"]
        hooksProviderArtifact = "com.expediagroup:graphql-kotlin-federated-hooks-provider:$graphQLKotlinVersion"
    }
}
```

<!--END_DOCUSAURUS_CODE_TABS-->

## Tasks

All `graphql-kotlin-gradle-plugin` tasks are grouped together under `GraphQL` task group and their names are prefixed with
`graphql`. You can find detailed information about GraphQL kotlin tasks by running Gradle `help --task <taskName>` task.

### graphqlDownloadSDL

Task that attempts to download GraphQL schema in SDL format from the specified `endpoint` and saves the underlying
schema file as `schema.graphql` under build directory. In general, this task provides limited functionality by itself
and could be used as an alternative to `graphqlIntrospectSchema` to generate input for the subsequent
`graphqlGenerateClient` task.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server SDL endpoint that will be used to download schema.<br/>**Command line property is**: `endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on a SDL request. |
| `outputFile` | File | | Target GraphQL schema file to be generated.<br/>**Default value is:** `${project.buildDir}/schema.graphql` |
| `timeoutConfig` | TimeoutConfig | | Timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.<br/>**Default value are:**<br/>connect timeout = 5_000<br/>read timeout = 15_000.<br/>|

### graphqlGenerateClient

Task that generates GraphQL Kotlin client and corresponding data classes based on the provided GraphQL queries that are
evaluated against target Graphql schema. Individual clients with their specific data models are generated for each query
file and are placed under specified `packageName`. When this task is added to the project, either through explicit configuration
or through the `graphql` extension, it will automatically configure itself as a dependency of a `compileKotlin` task and
resulting generated code will be automatically added to the project main source set.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `allowDeprecatedFields` | Boolean | | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br/>**Default value is:** `false`.<br/>**Command line property is**: `allowDeprecatedFields`. |
| `clientType` | GraphQLClientType | | Enum value that specifies target GraphQL client type implementation.<br/>**Default value is:** `GraphQLClientType.DEFAULT`. |
| `converters` | Map<String, ScalarConverter> | | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `packageName` | String | yes | Target package name for generated code.<br/>**Command line property is**: `packageName`. |
| `queryFiles` | FileCollection | | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property. |
| `queryFileDirectory` | String | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/main/resources`.<br/>**Command line property is**: `queryFileDirectory`. |
| `schemaFile` | File | `schemaFileName` or `schemaFile` has to be provided | GraphQL schema file that will be used to generate client code. |
| `schemaFileName` | String | `schemaFileName` or `schemaFile` has to be provided | Path to GraphQL schema file that will be used to generate client code.<br/>**Command line property is**: `schemaFileName`. |

### graphqlGenerateSDL

Task that generates GraphQL schema in SDL format from your source code using reflections. Utilizes `graphql-kotlin-schema-generator`
to generate the schema from classes implementing `graphql-kotlin-types` marker `Query`, `Mutation` and `Subscription` interfaces.
In order to limit the amount of packages to scan, this task requires users to provide a list of `packages` that can contain
GraphQL types.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `hooksProvider` | String | | Optional fully qualified artifact name that contains SchemaGeneratorHooks service provider. **Default hooks:** `NoopSchemaGeneratorHooks` |
| `packages` | List<String> | yes | List of supported packages that can be scanned to generate SDL. |
| `schemaFile` | File | | Target GraphQL schema file to be generated.<br/>**Default value is:** `${project.buildDir}/schema.graphql` |

### graphqlGenerateTestClient

Task that generates GraphQL Kotlin test client and corresponding data classes based on the provided GraphQL queries that are
evaluated against target Graphql schema. Individual test clients with their specific data models are generated for each query
file and are placed under specified `packageName`. When this task is added to the project it will automatically configure
itself as a dependency of a `compileTestKotlin` task and resulting generated code will be automatically added to the project
test source set.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `allowDeprecatedFields` | Boolean | | Boolean flag indicating whether selection of deprecated fields is allowed or not.<br/>**Default value is:** `false`.<br/>**Command line property is**: `allowDeprecatedFields`. |
| `clientType` | GraphQLClientType | | Enum value that specifies target GraphQL client type implementation.<br/>**Default value is:** `GraphQLClientType.DEFAULT`. |
| `converters` | Map<String, ScalarConverter> | | Custom GraphQL scalar to converter mapping containing information about corresponding Java type and converter that should be used to serialize/deserialize values. |
| `packageName` | String | yes | Target package name for generated code.<br/>**Command line property is**: `packageName`. |
| `queryFiles` | FileCollection | | List of query files to be processed. Instead of a list of files to be processed you can specify `queryFileDirectory` directory instead. If this property is specified it will take precedence over the corresponding directory property. |
| `queryFileDirectory` | String | | Directory file containing GraphQL queries. Instead of specifying a directory you can also specify list of query file by using `queryFiles` property instead.<br/>**Default value is:** `src/test/resources`.<br/>**Command line property is**: `queryFileDirectory`. |
| `schemaFile` | File | `schemaFileName` or `schemaFile` has to be provided | GraphQL schema file that will be used to generate client code. |
| `schemaFileName` | String | `schemaFileName` or `schemaFile` has to be provided | Path to GraphQL schema file that will be used to generate client code.<br/>**Command line property is**: `schemaFileName`. |

### graphqlIntrospectSchema

Task that executes GraphQL introspection query against specified `endpoint` and saves the underlying schema file as
`schema.graphql` under build directory. In general, this task provides limited functionality by itself and instead
should be used to generate input for the subsequent `graphqlGenerateClient` task.

**Properties**

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `endpoint` | String | yes | Target GraphQL server endpoint that will be used to execute introspection queries.<br/>**Command line property is**: `endpoint`. |
| `headers` | Map<String, Any> | | Optional HTTP headers to be specified on an introspection query. |
| `outputFile` | File | | Target GraphQL schema file to be generated.<br/>**Default value is:** `${project.buildDir}/schema.graphql` |
| `timeoutConfig` | TimeoutConfig | | Timeout configuration(in milliseconds) to download schema from SDL endpoint before we cancel the request.<br/>**Default value are:**<br/>connect timeout = 5_000</br>read timeout = 15_000.<br/>|
