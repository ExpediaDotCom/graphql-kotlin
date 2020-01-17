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

package com.expediagroup.graphql.extensions

import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import java.util.function.Predicate

/**
 * Prints out SDL representation of a target schema.
 *
 * @param includeIntrospectionTypes boolean flag indicating whether SDL should include introspection types
 * @param includeScalarTypes boolean flag indicating whether SDL should include custom schema scalars
 * @param includeExtendedScalarTypes boolean flag indicating whether SDL should include extended scalars (e.g. Long)
 *   supported by graphql-java, if set will automatically also set the includeScalarTypes flag
 * @param includeDefaultSchemaDefinition boolean flag indicating whether SDL should include schema definition if using
 *   default root type names
 * @param includeDirectives boolean flag indicating whether SDL should include directive information
 * @param includeDirectivesFilter Predicate to filter out specifc directives. Defaults to filter all directives by the value of [includeDirectives]
 * @param descriptionsAsHashComments boolean flag indicating whether SDL print the description with # instead of ""
 */
fun GraphQLSchema.print(
    includeIntrospectionTypes: Boolean = false,
    includeScalarTypes: Boolean = true,
    includeExtendedScalarTypes: Boolean = true,
    includeDefaultSchemaDefinition: Boolean = true,
    includeDirectives: Boolean = true,
    includeDirectivesFilter: Predicate<GraphQLDirective> = Predicate { includeDirectives },
    descriptionsAsHashComments: Boolean = false
): String {
    val schemaPrinter = SchemaPrinter(
        SchemaPrinter.Options.defaultOptions()
            .includeIntrospectionTypes(includeIntrospectionTypes)
            .includeScalarTypes(includeScalarTypes || includeExtendedScalarTypes)
            .includeExtendedScalarTypes(includeExtendedScalarTypes)
            .includeSchemaDefinition(includeDefaultSchemaDefinition)
            .includeDirectives(includeDirectives)
            .includeDirectives(includeDirectivesFilter)
            .descriptionsAsHashComments(descriptionsAsHashComments)
    )

    return schemaPrinter.print(this)
}
