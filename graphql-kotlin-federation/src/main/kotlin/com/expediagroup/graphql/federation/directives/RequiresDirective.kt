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

package com.expediagroup.graphql.federation.directives

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection

/**
 * ```graphql
 * directive @requires(fields: _FieldSet!) on FIELD_DEFINITON
 * ```
 *
 * The @requires directive is used to annotate the required input field set from a base type for a resolver. It is used to develop a query plan where the required fields may not be needed by the
 * client, but the service may need additional information from other services. Required fields specified in the directive field set should correspond to a valid field on the underlying GraphQL
 * interface/object and should be instrumented with @external directive. Since @requires directive specifies additional fields (besides the one specified in @key directive) that are required to
 * resolve federated type fields, this directive can only be specified on federated extended objects fields.
 *
 * NOTE: fields specified in the @requires directive will only be specified in the queries that reference those fields. This is problematic for Kotlin as the non nullable primitive properties have
 * to be initialized when they are declared. Simplest workaround for this problem is to initialize the underlying property to some dummy value that will be used if it is not specified. This approach
 * might become problematic though as it might be impossible to determine whether fields was initialized with the default value or the invalid/default value was provided by the federated query.
 * Another potential workaround is to rely on delegation to initialize the property after the object gets created. This will ensure that exception will be thrown if queries attempt to resolve fields
 * that reference the uninitialized property.
 *
 * Example:
 * Given
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * @ExtendsDirective
 * class Product(@property:ExternalDirective val id: String) {
 *
 *   @ExternalDirective
 *   var weight: Double by Delegates.notNull()
 *
 *   @RequiresDirective(FieldSet("weight"))
 *   fun shippingCost(): String { ... }
 *
 *   fun additionalInfo(): String { ... }
 * }
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Product @extends @key(fields : "id") {
 *   additionalInfo: String!
 *   id: String! @external
 *   shippingCost: String! @requires(fields : "weight")
 *   weight: Float! @external
 * }
 * ```
 *
 * @param fields field set that represents a set of additional external fields required to resolve target field
 *
 * @see FieldSet
 * @see com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
 * @see ExtendsDirective
 * @see ExternalDirective
 * @see kotlin.properties.Delegates.notNull
 */
@GraphQLDirective(
    name = "requires",
    description = "Specifies required input field set from the base type for a resolver",
    locations = [Introspection.DirectiveLocation.FIELD_DEFINITION]
)
annotation class RequiresDirective(val fields: FieldSet)
