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

package com.expediagroup.graphql.federation

import com.expediagroup.graphql.federation.directives.EXTERNAL_DIRECTIVE_NAME
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.federation.directives.REQUIRES_DIRECTIVE_NAME
import graphql.schema.GraphQLDirective
import io.mockk.every
import io.mockk.mockk

internal fun getKeyDirective(diretiveValue: String): GraphQLDirective = mockk {
    every { name } returns KEY_DIRECTIVE_NAME
    every { getArgument(eq("fields")) } returns mockk {
        every { value } returns mockk<FieldSet> {
            every { value } returns diretiveValue
        }
    }
}

internal fun getRequiresDirective(directiveValue: String): GraphQLDirective = mockk {
    every { name } returns REQUIRES_DIRECTIVE_NAME
    every { getArgument(eq("fields")) } returns mockk {
        every { value } returns mockk<FieldSet> {
            every { value } returns directiveValue
        }
    }
}

internal val externalDirective = GraphQLDirective.newDirective().name(EXTERNAL_DIRECTIVE_NAME)
