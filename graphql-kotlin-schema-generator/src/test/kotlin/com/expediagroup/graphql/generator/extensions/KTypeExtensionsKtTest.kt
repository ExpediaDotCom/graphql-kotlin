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

import com.expediagroup.graphql.exceptions.CouldNotGetNameOfKClassException
import com.expediagroup.graphql.exceptions.InvalidListTypeException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnJre
import org.junit.jupiter.api.condition.JRE
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.starProjectedType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class KTypeExtensionsKtTest {

    internal class MyClass {
        fun listFun(list: List<String>) = list.joinToString(separator = ",") { it }

        fun arrayFun(array: Array<String>) = array.joinToString(separator = ",") { it }

        fun primitiveArrayFun(intArray: IntArray) = intArray.joinToString(separator = ",") { it.toString() }

        fun stringFun(string: String) = "hello $string"
    }

    internal interface SimpleInterface

    internal class SimpleClass(val id: String) : SimpleInterface

    @Test
    fun getTypeOfFirstArgument() {
        assertEquals(String::class.starProjectedType, MyClass::listFun.findParameterByName("list")?.type?.getTypeOfFirstArgument())

        assertEquals(String::class.starProjectedType, MyClass::arrayFun.findParameterByName("array")?.type?.getTypeOfFirstArgument())

        assertEquals(Int::class.starProjectedType, MyClass::primitiveArrayFun.findParameterByName("intArray")?.type?.getWrappedType())

        assertFailsWith(InvalidListTypeException::class) {
            MyClass::stringFun.findParameterByName("string")?.type?.getTypeOfFirstArgument()
        }

        assertFailsWith(InvalidListTypeException::class) {
            val mockType: KType = mockk()
            every { mockType.arguments } returns emptyList()
            mockType.getTypeOfFirstArgument()
        }

        assertFailsWith(InvalidListTypeException::class) {
            val mockArgument: KTypeProjection = mockk()
            every { mockArgument.type } returns null
            val mockType: KType = mockk()
            every { mockType.arguments } returns listOf(mockArgument)
            mockType.getTypeOfFirstArgument()
        }
    }

    @Test
    fun getKClass() {
        assertEquals(MyClass::class, MyClass::class.starProjectedType.getKClass())
    }

    @Test
    fun isSubclassOf() {
        assertTrue(MyClass::class.starProjectedType.isSubclassOf(MyClass::class))
        assertTrue(SimpleClass::class.starProjectedType.isSubclassOf(SimpleInterface::class))
        assertFalse(SimpleInterface::class.starProjectedType.isSubclassOf(SimpleClass::class))
        assertFalse(MyClass::class.starProjectedType.isSubclassOf(SimpleInterface::class))
    }

    @Test
    fun getArrayType() {
        assertEquals(Int::class.starProjectedType, IntArray::class.starProjectedType.getWrappedType())
        assertEquals(Long::class.starProjectedType, LongArray::class.starProjectedType.getWrappedType())
        assertEquals(Short::class.starProjectedType, ShortArray::class.starProjectedType.getWrappedType())
        assertEquals(Float::class.starProjectedType, FloatArray::class.starProjectedType.getWrappedType())
        assertEquals(Double::class.starProjectedType, DoubleArray::class.starProjectedType.getWrappedType())
        assertEquals(Char::class.starProjectedType, CharArray::class.starProjectedType.getWrappedType())
        assertEquals(Boolean::class.starProjectedType, BooleanArray::class.starProjectedType.getWrappedType())
        assertEquals(String::class.starProjectedType, MyClass::listFun.findParameterByName("list")?.type?.getWrappedType())

        assertFailsWith(InvalidListTypeException::class) {
            MyClass::stringFun.findParameterByName("string")?.type?.getWrappedType()
        }
    }

    // TODO remove JUnit condition once we only build artifacts using Java 11
    //  BLOCKED: in order to publish to Maven Central we need to generate javadoc jar, blocked until Dokka https://github.com/Kotlin/dokka/issues/294 is resolved
    //  ISSUE: with Kotlin 1.3.40+ there is bug on JRE 1.8 that results in incorrect simple name of the anonymous classes, see https://youtrack.jetbrains.com/issue/KT-23072
    @Test
    @EnabledOnJre(JRE.JAVA_11)
    fun getSimpleName() {
        assertEquals("MyClass", MyClass::class.starProjectedType.getSimpleName())
        assertEquals("MyClassInput", MyClass::class.starProjectedType.getSimpleName(isInputType = true))
        assertFailsWith(CouldNotGetNameOfKClassException::class) {
            object {}::class.starProjectedType.getSimpleName()
        }
    }

    // TODO remove this JUnit once we only build artifacts using Java 11
    //  BLOCKED: in order to publish to Maven Central we need to generate javadoc jar, blocked until Dokka https://github.com/Kotlin/dokka/issues/294 is resolved
    //  ISSUE: with Kotlin 1.3.40+ there is bug on JRE 1.8 that results in incorrect simple name of the anonymous classes, see https://youtrack.jetbrains.com/issue/KT-23072
    @Test
    @EnabledOnJre(JRE.JAVA_8)
    fun getSimpleNameJava8() {
        assertEquals("MyClass", MyClass::class.starProjectedType.getSimpleName())
        assertEquals("MyClassInput", MyClass::class.starProjectedType.getSimpleName(isInputType = true))
    }

    @Test
    fun qualifiedName() {
        assertEquals("com.expediagroup.graphql.generator.extensions.KTypeExtensionsKtTest.MyClass", MyClass::class.starProjectedType.qualifiedName)
        assertEquals("", object { }::class.starProjectedType.qualifiedName)
    }
}
