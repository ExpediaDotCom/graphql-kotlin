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

package com.expediagroup.graphql.federation.types

import graphql.AssertException
import graphql.language.ArrayValue
import graphql.language.BooleanValue
import graphql.language.EnumValue
import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.language.NullValue
import graphql.language.ObjectField
import graphql.language.ObjectValue
import graphql.language.StringValue
import graphql.language.Value
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class AnyTest {

    @Test
    fun `_Any scalar should allow all types`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertNull(coercing.parseLiteral(NullValue.Null))
        assertEquals(expected = BigDecimal.ONE, actual = coercing.parseLiteral(FloatValue(BigDecimal.ONE)))
        assertEquals(expected = "hello", actual = coercing.parseLiteral(StringValue("hello")))
        assertEquals(expected = BigInteger.ONE, actual = coercing.parseLiteral(IntValue(BigInteger.ONE)))
        assertEquals(expected = true, actual = coercing.parseLiteral(BooleanValue(true)))
        assertEquals(expected = "MyEnum", actual = coercing.parseLiteral(EnumValue("MyEnum")))

        val listValues = listOf<Value<IntValue>>(IntValue(BigInteger.TEN))
        assertEquals(expected = listOf(BigInteger.TEN), actual = coercing.parseLiteral(ArrayValue(listValues)))

        val objectValue = ObjectValue(listOf(ObjectField("myName", IntValue(BigInteger.ONE))))
        assertEquals(expected = mapOf("myName" to BigInteger.ONE), actual = coercing.parseLiteral(objectValue))
    }

    @Test
    fun `_Any scalar should throw exception on invalid graphql value`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertFailsWith(AssertException::class) {
            coercing.parseLiteral(1)
        }
    }

    @Test
    fun `_Any scalar serialize should just return`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertEquals(expected = 1, actual = coercing.serialize(1))
    }

    @Test
    fun `_Any scalar parseValue should just return`() {
        val coercing = ANY_SCALAR_TYPE.coercing

        assertEquals(expected = 1, actual = coercing.parseValue(1))
    }
}
