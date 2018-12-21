package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.directives
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidFunctions
import com.expedia.graphql.generator.extensions.getValidProperties
import com.expedia.graphql.generator.extensions.isInterface
import com.expedia.graphql.generator.extensions.isUnion
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.superclasses

internal class ObjectTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun objectType(kClass: KClass<*>, interfaceType: GraphQLInterfaceType? = null): GraphQLType {
        return state.cache.buildIfNotUnderConstruction(kClass) { _ ->
            val builder = GraphQLObjectType.newObject()

            builder.name(kClass.getSimpleName())
            builder.description(kClass.getGraphQLDescription())

            kClass.directives(generator).forEach {
                builder.withDirective(it)
                state.directives.add(it)
            }

            if (interfaceType != null) {
                builder.withInterface(interfaceType)
            } else {
                kClass.superclasses
                    .asSequence()
                    .filter { it.isInterface() && !it.isUnion() }
                    .map { objectFromReflection(it.createType(), false) as? GraphQLInterfaceType }
                    .forEach { builder.withInterface(it) }
            }

            kClass.getValidProperties(config.hooks)
                .forEach { builder.field(generator.property(it, kClass)) }

            kClass.getValidFunctions(config.hooks)
                .forEach { builder.field(generator.function(it)) }

            builder.build()
        }
    }
}
