/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.exceptions.InvalidMutationTypeException
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getValidFunctions
import com.expediagroup.graphql.generator.extensions.isNotPublic
import graphql.schema.GraphQLObjectType

internal class MutationBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    fun getMutationObject(mutations: List<TopLevelObject>): GraphQLObjectType? {

        if (mutations.isEmpty()) {
            return null
        }

        val mutationBuilder = GraphQLObjectType.Builder()
        mutationBuilder.name(config.topLevelNames.mutation)

        for (mutation in mutations) {
            if (mutation.kClass.isNotPublic()) {
                throw InvalidMutationTypeException(mutation.kClass)
            }

            generator.directives(mutation.kClass).forEach {
                mutationBuilder.withDirective(it)
            }

            mutation.kClass.getValidFunctions(config.hooks)
                .forEach {
                    val function = generator.function(it, config.topLevelNames.mutation, mutation.obj)
                    val functionFromHook = config.hooks.didGenerateMutationType(it, function)
                    mutationBuilder.field(functionFromHook)
                }
        }

        return mutationBuilder.build()
    }
}
