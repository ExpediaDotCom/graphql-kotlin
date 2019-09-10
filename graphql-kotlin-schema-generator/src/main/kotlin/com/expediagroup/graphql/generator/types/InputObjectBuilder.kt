/*
 * Copyright 2019 Expedia, Inc
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

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.extensions.getValidProperties
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInputObjectType
import kotlin.reflect.KClass

internal class InputObjectBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun inputObjectType(kClass: KClass<*>): GraphQLInputObjectType {
        val builder = GraphQLInputObjectType.newInputObject()

        builder.name(kClass.getSimpleName(isInputClass = true))
        builder.description(kClass.getGraphQLDescription())

        generator.directives(kClass).forEach {
            builder.withDirective(it)
        }

        // It does not make sense to run functions against the input types so we only process the properties
        kClass.getValidProperties(config.hooks).forEach {
            builder.field(generator.inputProperty(it, kClass))
        }

        return config.hooks.onRewireGraphQLType(builder.build()).safeCast()
    }
}
