package com.expedia.graphql

import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.hooks.SchemaGeneratorHooks

/**
 * Settings for generating the schema.
 */
data class SchemaGeneratorConfig(
    val supportedPackages: List<String>,
    val topLevelQueryName: String = "Query",
    val topLevelMutationName: String = "Mutation",
    val hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks(),
    val dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = KotlinDataFetcherFactoryProvider(hooks)
)
