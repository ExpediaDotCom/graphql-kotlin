/*
 * Copyright 2020 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.isEnum
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.isListType
import com.expediagroup.graphql.generator.extensions.isUnion
import com.expediagroup.graphql.generator.extensions.wrapInNonNull
import com.expediagroup.graphql.generator.state.TypesCacheKey
import com.expediagroup.graphql.generator.types.generateEnum
import com.expediagroup.graphql.generator.types.generateInputObject
import com.expediagroup.graphql.generator.types.generateInterface
import com.expediagroup.graphql.generator.types.generateList
import com.expediagroup.graphql.generator.types.generateObject
import com.expediagroup.graphql.generator.types.generateScalar
import com.expediagroup.graphql.generator.types.generateUnion
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLTypeUtil
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Internal use only. Please use [com.expediagroup.graphql.toSchema] instead.
 *
 * Return a basic GraphQL type given all the information about the kotlin type.
 */
internal fun generateGraphQLType(generator: SchemaGenerator, type: KType, inputType: Boolean = false, annotatedAsID: Boolean = false): GraphQLType {
    val hookGraphQLType = generator.config.hooks.willGenerateGraphQLType(type)
    val graphQLType = hookGraphQLType
        ?: generateScalar(generator, type, annotatedAsID)
        ?: objectFromReflection(generator, type, inputType)

    // Do not call the hook on GraphQLTypeReference as we have not generated the type yet
    val unwrappedType = GraphQLTypeUtil.unwrapType(graphQLType).lastElement()
    val typeWithNullability = graphQLType.wrapInNonNull(type)
    if (unwrappedType !is GraphQLTypeReference) {
        return generator.config.hooks.didGenerateGraphQLType(type, typeWithNullability)
    }

    return typeWithNullability
}

private fun objectFromReflection(generator: SchemaGenerator, type: KType, inputType: Boolean): GraphQLType {
    val cacheKey = TypesCacheKey(type, inputType)
    val cachedType = generator.cache.get(cacheKey)

    if (cachedType != null) {
        return cachedType
    }

    val kClass = type.getKClass()
    val graphQLType = generator.cache.buildIfNotUnderConstruction(kClass, inputType) { getGraphQLType(generator, kClass, inputType, type) }

    return generator.config.hooks.willAddGraphQLTypeToSchema(type, graphQLType)
}

private fun getGraphQLType(generator: SchemaGenerator, kClass: KClass<*>, inputType: Boolean, type: KType): GraphQLType = when {
    kClass.isEnum() -> @Suppress("UNCHECKED_CAST") (generateEnum(generator, kClass as KClass<Enum<*>>))
    kClass.isListType() -> generateList(generator, type, inputType)
    kClass.isUnion() -> generateUnion(generator, kClass)
    kClass.isInterface() -> generateInterface(generator, kClass)
    inputType -> generateInputObject(generator, kClass)
    else -> generateObject(generator, kClass)
}
