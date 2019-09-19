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

package com.expediagroup.graphql.exceptions

import graphql.schema.GraphQLType
import kotlin.reflect.KClass

/**
 * Thrown when the casting a GraphQLType to some parent type is invalid
 */
class CouldNotCastGraphQLType(type: GraphQLType, kClass: KClass<*>) :
    GraphQLKotlinException("Could not cast GraphQLType $type to $kClass")
