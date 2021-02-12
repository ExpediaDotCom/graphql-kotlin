/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.plugin.client.generator.types

import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test

class GenerateGraphQLInputObjectTypeSpecIT {

    @Test
    fun `verify we can generate valid input object type spec for queries with hard-coded input`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Boolean
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.Serializable

                const val INPUT_OBJECT_TEST_QUERY: String =
                    "query InputObjectTestQuery {\n  inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n}"

                @Serializable
                class InputObjectTestQuery : GraphQLClientRequest<InputObjectTestQuery.Result> {
                  override val query: String = INPUT_OBJECT_TEST_QUERY

                  override val operationName: String = "InputObjectTestQuery"

                  override fun responseType(): KClass<InputObjectTestQuery.Result> =
                      InputObjectTestQuery.Result::class

                  @Serializable
                  data class Result(
                    /**
                     * Query that accepts some input arguments
                     */
                    val inputObjectQuery: Boolean
                  )
                }
            """.trimIndent()

        val query =
            """
            query InputObjectTestQuery {
              inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )
            }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify we can generate valid input object type spec using kotlinx-serialization`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Boolean
                import kotlin.Float
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.Serializable

                const val INPUT_OBJECT_TEST_QUERY: String =
                    "query InputObjectTestQuery(${'$'}{'${'$'}'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}{'${'$'}'}input )\n}"

                @Serializable
                class InputObjectTestQuery(
                  override val variables: InputObjectTestQuery.Variables
                ) : GraphQLClientRequest<InputObjectTestQuery.Result> {
                  override val query: String = INPUT_OBJECT_TEST_QUERY

                  override val operationName: String = "InputObjectTestQuery"

                  override fun responseType(): KClass<InputObjectTestQuery.Result> =
                      InputObjectTestQuery.Result::class

                  @Serializable
                  data class Variables(
                    val input: InputObjectTestQuery.SimpleArgumentInput? = null
                  )

                  /**
                   * Test input object
                   */
                  @Serializable
                  data class SimpleArgumentInput(
                    /**
                     * Maximum value for test criteria
                     */
                    val max: Float? = null,
                    /**
                     * Minimum value for test criteria
                     */
                    val min: Float? = null,
                    /**
                     * New value to be set
                     */
                    val newName: String? = null
                  )

                  @Serializable
                  data class Result(
                    /**
                     * Query that accepts some input arguments
                     */
                    val inputObjectQuery: Boolean
                  )
                }
            """.trimIndent()

        val query =
            """
            query InputObjectTestQuery(${'$'}input: SimpleArgumentInput) {
              inputObjectQuery(criteria: ${'$'}input )
            }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify generation of self referencing input object`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Boolean
                import kotlin.Float
                import kotlin.String
                import kotlin.reflect.KClass
                import kotlinx.serialization.Serializable

                const val INPUT_OBJECT_TEST_QUERY: String =
                    "query InputObjectTestQuery(${'$'}{'${'$'}'}input: ComplexArgumentInput) {\n  complexInputObjectQuery(criteria: ${'$'}{'${'$'}'}input)\n}"

                @Serializable
                class InputObjectTestQuery(
                  override val variables: InputObjectTestQuery.Variables
                ) : GraphQLClientRequest<InputObjectTestQuery.Result> {
                  override val query: String = INPUT_OBJECT_TEST_QUERY

                  override val operationName: String = "InputObjectTestQuery"

                  override fun responseType(): KClass<InputObjectTestQuery.Result> =
                      InputObjectTestQuery.Result::class

                  @Serializable
                  data class Variables(
                    val input: InputObjectTestQuery.ComplexArgumentInput? = null
                  )

                  /**
                   * Self referencing input object
                   */
                  @Serializable
                  data class ComplexArgumentInput(
                    /**
                     * Maximum value for test criteria
                     */
                    val max: Float? = null,
                    /**
                     * Minimum value for test criteria
                     */
                    val min: Float? = null,
                    /**
                     * Next criteria
                     */
                    val next: InputObjectTestQuery.ComplexArgumentInput? = null
                  )

                  @Serializable
                  data class Result(
                    /**
                     * Query that accepts self referencing input object
                     */
                    val complexInputObjectQuery: Boolean
                  )
                }
            """.trimIndent()

        val query =
            """
            query InputObjectTestQuery(${'$'}input: ComplexArgumentInput) {
              complexInputObjectQuery(criteria: ${'$'}input)
            }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }

    @Test
    fun `verify we can generate valid input object type spec using jackson`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.types.GraphQLClientRequest
                import kotlin.Boolean
                import kotlin.Float
                import kotlin.String
                import kotlin.reflect.KClass

                const val INPUT_OBJECT_TEST_QUERY: String =
                    "query InputObjectTestQuery(${'$'}{'${'$'}'}input: SimpleArgumentInput) {\n  inputObjectQuery(criteria: ${'$'}{'${'$'}'}input )\n}"

                class InputObjectTestQuery(
                  override val variables: InputObjectTestQuery.Variables
                ) : GraphQLClientRequest<InputObjectTestQuery.Result> {
                  override val query: String = INPUT_OBJECT_TEST_QUERY

                  override val operationName: String = "InputObjectTestQuery"

                  override fun responseType(): KClass<InputObjectTestQuery.Result> =
                      InputObjectTestQuery.Result::class

                  data class Variables(
                    val input: InputObjectTestQuery.SimpleArgumentInput? = null
                  )

                  /**
                   * Test input object
                   */
                  data class SimpleArgumentInput(
                    /**
                     * Maximum value for test criteria
                     */
                    val max: Float? = null,
                    /**
                     * Minimum value for test criteria
                     */
                    val min: Float? = null,
                    /**
                     * New value to be set
                     */
                    val newName: String? = null
                  )

                  data class Result(
                    /**
                     * Query that accepts some input arguments
                     */
                    val inputObjectQuery: Boolean
                  )
                }
            """.trimIndent()

        val query =
            """
            query InputObjectTestQuery(${'$'}input: SimpleArgumentInput) {
              inputObjectQuery(criteria: ${'$'}input )
            }
            """.trimIndent()
        verifyGeneratedFileSpecContents(
            query,
            expected,
            GraphQLClientGeneratorConfig(
                packageName = "com.expediagroup.graphql.plugin.generator.integration",
                serializer = GraphQLSerializer.JACKSON
            )
        )
    }
}
