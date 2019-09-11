package com.expedia.graphql.generator.types

import com.expedia.graphql.directives.deprecatedDirectiveWithReason
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getPropertyDeprecationReason
import com.expedia.graphql.generator.extensions.getPropertyDescription
import com.expedia.graphql.generator.extensions.getPropertyName
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.isPropertyGraphQLID
import com.expedia.graphql.generator.extensions.safeCast
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLOutputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class PropertyBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun property(prop: KProperty<*>, parentClass: KClass<*>): GraphQLFieldDefinition {
        val propertyType = graphQLTypeOf(type = prop.returnType, annotatedAsID = prop.isPropertyGraphQLID(parentClass))
            .safeCast<GraphQLOutputType>()

        val fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
            .description(prop.getPropertyDescription(parentClass))
            .name(prop.getPropertyName(parentClass))
            .type(propertyType)

        prop.getPropertyDeprecationReason(parentClass)?.let {
            fieldBuilder.deprecate(it)
            fieldBuilder.withDirective(deprecatedDirectiveWithReason(it))
        }

        generator.directives(prop).forEach {
            fieldBuilder.withDirective(it)
        }

        val field = fieldBuilder.build()

        val parentType = parentClass.getSimpleName()
        val coordinates = FieldCoordinates.coordinates(parentType, prop.name)
        val dataFetcherFactory = config.dataFetcherFactoryProvider.propertyDataFetcherFactory(kClass = parentClass, kProperty = prop)
        generator.codeRegistry.dataFetcher(coordinates, dataFetcherFactory)

        return config.hooks.onRewireGraphQLType(field, coordinates, codeRegistry).safeCast()
    }
}
