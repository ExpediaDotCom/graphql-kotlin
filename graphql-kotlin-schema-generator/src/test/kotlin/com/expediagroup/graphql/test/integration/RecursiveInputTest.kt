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

package com.expediagroup.graphql.test.integration

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.testSchemaConfig
import com.expediagroup.graphql.toSchema
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertNotNull

class RecursiveInputTest {

    @Test
    fun `Input type with a recursive argument should work`() {
        val queries = listOf(TopLevelObject(Queries()))
        val schema = toSchema(testSchemaConfig, queries)
        assertNotNull(schema)
    }
}

class RecursivePerson {
    val id: String = UUID.randomUUID().toString()
    val friend: RecursivePerson = RecursivePerson()
}

class Queries {
    fun getPerson() = RecursivePerson()
    fun addPerson(inputPerson: RecursivePerson): String = inputPerson.id
}
