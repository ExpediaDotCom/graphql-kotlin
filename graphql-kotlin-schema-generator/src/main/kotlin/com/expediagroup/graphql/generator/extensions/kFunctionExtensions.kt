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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.exceptions.InvalidExtensionFunction
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction


internal fun KFunction<*>.getValidArguments(): List<KParameter> =
    this.valueParameters
        .filterNot { it.isGraphQLContext() }
        .filterNot { it.isGraphQLIgnored() }
        .filterNot { it.isDataFetchingEnvironment() }

internal fun Method.isStatic() = Modifier.isStatic(this.modifiers)

@Throws(InvalidExtensionFunction::class)
internal fun Method.asExtensionFunction() = when{
    this.isStatic().not() -> throw InvalidExtensionFunction(this)
    this.parameters.firstOrNull() == null -> throw InvalidExtensionFunction(this)
    else -> this.kotlinFunction ?: throw InvalidExtensionFunction(this)
}

