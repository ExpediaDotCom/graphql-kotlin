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

package com.expediagroup.graphql.federation.validation

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateFieldSelectionKtTest {

    @Test
    fun `empty list returns no errors`() {
        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = "",
            iterator = emptyList<String>().iterator(),
            fields = emptyMap(),
            extendedType = false,
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }

    /**
     * interface MyInterface {
     *   bar: String
     * }
     *
     * type Parent {
     *   foo: MyInterface @taco("foo { bar }")
     * }
     */
    @Test
    fun `GraphQLInterface type is unwrapped, and returns a single error`() {
        val interfaceField = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(Scalars.GraphQLString)
            .build()
        val interfaceDefinition = GraphQLInterfaceType.newInterface()
            .name("MyInterface")
            .field(interfaceField)
            .build()
        val fieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(interfaceDefinition)
            .build()
        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = "taco",
            iterator = listOf("foo", "{", "bar", "}").iterator(),
            fields = mapOf("foo" to fieldDefinition),
            extendedType = false,
            errors = errors
        )

        assertEquals(expected = 1, actual = errors.size)
        assertEquals(expected = "taco specifies invalid field set - field set references GraphQLInterfaceType, field=foo", actual = errors.first())
    }

    /**
     * type MyObject {
     *   bar: String
     * }
     *
     * type Parent {
     *   foo: MyObject @taco("foo { bar }")
     * }
     */
    @Test
    fun `GraphQLObjectType type is unwrapped, and returns a no errors on valid selection`() {
        val field = GraphQLFieldDefinition.newFieldDefinition()
            .name("bar")
            .type(Scalars.GraphQLString)
            .build()
        val objectDefinition = GraphQLObjectType.newObject()
            .name("MyObject")
            .field(field)
            .build()
        val fieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(objectDefinition)
            .build()
        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = "taco",
            iterator = listOf("foo", "{", "bar", "}").iterator(),
            fields = mapOf("foo" to fieldDefinition),
            extendedType = false,
            errors = errors
        )

        assertEquals(expected = 0, actual = errors.size)
    }

    /**
     * type Parent {
     *   foo: String @taco("foo")
     * }
     */
    @Test
    fun `A valid field definition returns no errors`() {
        val fieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(Scalars.GraphQLString)
            .build()
        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = "taco",
            iterator = listOf("foo").iterator(),
            fields = mapOf("foo" to fieldDefinition),
            extendedType = false,
            errors = errors
        )

        assertTrue(errors.isEmpty())
    }

    /**
     * type Parent {
     *   foo: String @taco("bar { foo }")
     * }
     */
    @Test
    fun `A valid field definition but with invalid sub fields returns two errors`() {
        val fieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("foo")
            .type(Scalars.GraphQLString)
            .build()

        val errors = mutableListOf<String>()
        validateFieldSelection(
            validatedDirective = "",
            iterator = listOf("bar", "{", "foo", "}").iterator(),
            fields = mapOf("foo" to fieldDefinition),
            extendedType = false,
            errors = errors
        )

        assertEquals(expected = 2, actual = errors.size)
    }
}
