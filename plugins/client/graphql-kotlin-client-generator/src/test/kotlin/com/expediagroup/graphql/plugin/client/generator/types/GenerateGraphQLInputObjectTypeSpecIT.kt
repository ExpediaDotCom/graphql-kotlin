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

import com.expediagroup.graphql.plugin.client.generator.verifyGeneratedFileSpecContents
import org.junit.jupiter.api.Test

class GenerateGraphQLInputObjectTypeSpecIT {

    @Test
    fun `verify we can generate valid input object type spec`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.GraphQLClientRequest
                import com.expediagroup.graphql.types.GraphQLResponse
                import java.lang.Class
                import kotlin.Boolean
                import kotlin.String

                const val INPUT_OBJECT_TEST_QUERY: String =
                    "query InputObjectTestQuery {\n  inputObjectQuery(criteria: { min: 1.0, max: 5.0 } )\n}"

                class InputObjectTestQuery : GraphQLClientRequest(INPUT_OBJECT_TEST_QUERY, "InputObjectTestQuery") {
                  override fun responseType(): Class<InputObjectTestQuery.Result> =
                      InputObjectTestQuery.Result::class.java

                  data class Result(
                    /**
                     * Query that accepts some input arguments
                     */
                    val inputObjectQuery: Boolean
                  )
                }

                suspend fun GraphQLClient<*>.executeInputObjectTestQuery(request: InputObjectTestQuery):
                    GraphQLResponse<InputObjectTestQuery.Result> = execute(request)
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
    fun `verify generation of self referencing input object`() {
        val expected =
            """
                package com.expediagroup.graphql.plugin.generator.integration

                import com.expediagroup.graphql.client.GraphQLClient
                import com.expediagroup.graphql.client.GraphQLClientRequest
                import com.expediagroup.graphql.types.GraphQLResponse
                import java.lang.Class
                import kotlin.Boolean
                import kotlin.Float
                import kotlin.String

                const val INPUT_OBJECT_TEST_QUERY: String =
                    "query InputObjectTestQuery(${'$'}{'${'$'}'}input: ComplexArgumentInput) {\n  complexInputObjectQuery(criteria: ${'$'}{'${'$'}'}input)\n}"

                class InputObjectTestQuery(
                  variables: InputObjectTestQuery.Variables
                ) : GraphQLClientRequest(INPUT_OBJECT_TEST_QUERY, "InputObjectTestQuery", variables) {
                  override fun responseType(): Class<InputObjectTestQuery.Result> =
                      InputObjectTestQuery.Result::class.java

                  data class Variables(
                    val input: InputObjectTestQuery.ComplexArgumentInput? = null
                  )

                  /**
                   * Self referencing input object
                   */
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

                  data class Result(
                    /**
                     * Query that accepts self referencing input object
                     */
                    val complexInputObjectQuery: Boolean
                  )
                }

                suspend fun GraphQLClient<*>.executeInputObjectTestQuery(request: InputObjectTestQuery):
                    GraphQLResponse<InputObjectTestQuery.Result> = execute(request)
            """.trimIndent()

        val query =
            """
            query InputObjectTestQuery(${'$'}input: ComplexArgumentInput) {
              complexInputObjectQuery(criteria: ${'$'}input)
            }
            """.trimIndent()
        verifyGeneratedFileSpecContents(query, expected)
    }
}
