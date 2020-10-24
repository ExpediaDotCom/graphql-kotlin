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

package com.expediagroup.graphql.plugin.generator.extensions

import com.expediagroup.graphql.plugin.generator.exceptions.InvalidFragmentException
import graphql.language.Document
import graphql.language.FragmentDefinition

internal fun Document.findFragmentDefinition(targetFragment: String, targetType: String): FragmentDefinition =
    this.getDefinitionsOfType(FragmentDefinition::class.java)
        .find { it.name == targetFragment && it.typeCondition.name == targetType } ?: throw InvalidFragmentException(targetFragment, targetType)
