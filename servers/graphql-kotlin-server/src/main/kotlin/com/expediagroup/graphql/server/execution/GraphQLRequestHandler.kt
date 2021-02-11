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

package com.expediagroup.graphql.server.execution

import com.expediagroup.graphql.generator.execution.GraphQLContext
import com.expediagroup.graphql.server.exception.KotlinGraphQLError
import com.expediagroup.graphql.server.extensions.toExecutionInput
import com.expediagroup.graphql.server.extensions.toGraphQLKotlinType
import com.expediagroup.graphql.server.extensions.toGraphQLResponse
import com.expediagroup.graphql.types.GraphQLRequest
import com.expediagroup.graphql.types.GraphQLResponse
import graphql.GraphQL
import kotlinx.coroutines.future.await
import org.dataloader.DataLoaderRegistry

open class GraphQLRequestHandler(
    private val graphQL: GraphQL,
    private val dataLoaderRegistry: DataLoaderRegistry? = null
) {

    /**
     * Execute a GraphQL request in a non-blocking fashion.
     * This should only be used for queries and mutations.
     * Subscriptions require more specific server logic and will need to be handled separately.
     */
    open suspend fun executeRequest(request: GraphQLRequest, context: GraphQLContext? = null): GraphQLResponse<*> {
        val executionInput = request.toExecutionInput(context, dataLoaderRegistry)

        return try {
            graphQL.executeAsync(executionInput).await().toGraphQLResponse()
        } catch (exception: Exception) {
            val graphKotlinQLError = KotlinGraphQLError(exception)
            GraphQLResponse<Any?>(errors = listOf(graphKotlinQLError.toGraphQLKotlinType()))
        }
    }
}
