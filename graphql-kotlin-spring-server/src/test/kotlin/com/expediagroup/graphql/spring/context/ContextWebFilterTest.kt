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

package com.expediagroup.graphql.spring.context

import com.expediagroup.graphql.spring.GraphQLConfigurationProperties
import com.expediagroup.graphql.spring.execution.ContextWebFilter
import com.expediagroup.graphql.spring.execution.GRAPHQL_CONTEXT_KEY
import com.expediagroup.graphql.spring.execution.GraphQLContextFactory
import graphql.GraphQLContext
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ContextWebFilterTest {

    @Test
    @Suppress("ForbiddenVoid")
    fun `verify web filter populates context in the reactor subscriber context`() {
        var generatedContext: Context? = null
        val exchange: ServerWebExchange = mockk {
            every { request } returns mockk {
                every { uri.path } returns "/graphql"
            }
            every { response } returns mockk()
        }
        val chain: WebFilterChain = mockk {
            every { filter(any()) } returns Mono.subscriberContext().flatMap {
                generatedContext = it
                Mono.empty<Void>()
            }
        }

        val simpleFactory: GraphQLContextFactory<Any> = mockk {
            coEvery { generateContext(any(), any()) } returns GraphQLContext.newContext().build()
        }

        val contextFilter = ContextWebFilter(GraphQLConfigurationProperties(), simpleFactory)
        StepVerifier.create(contextFilter.filter(exchange, chain))
            .verifyComplete()

        assertNotNull(generatedContext)
        val graphQLContext = generatedContext?.getOrDefault<GraphQLContext>(GRAPHQL_CONTEXT_KEY, null)
        assertNotNull(graphQLContext)
    }

    @Test
    fun `verify web filter order`() {
        val contextFilter = ContextWebFilter(GraphQLConfigurationProperties(), mockk())
        assertEquals(expected = 0, actual = contextFilter.order)
    }

    @Test
    @Suppress("ForbiddenVoid")
    fun `verify web filter does not generate context on non graphql routes`() {
        var reactorContext: Context? = null
        val exchange: ServerWebExchange = mockk {
            every { request } returns mockk {
                every { uri.path } returns "/whatever"
            }
            every { response } returns mockk()
        }
        val chain: WebFilterChain = mockk {
            every { filter(any()) } returns Mono.subscriberContext().flatMap {
                reactorContext = it
                Mono.empty<Void>()
            }
        }

        val simpleFactory: GraphQLContextFactory<Any> = mockk {
            coEvery { generateContext(any(), any()) } returns GraphQLContext.newContext().build()
        }

        val contextFilter = ContextWebFilter(GraphQLConfigurationProperties(), simpleFactory)
        StepVerifier.create(contextFilter.filter(exchange, chain))
            .verifyComplete()

        assertNotNull(reactorContext)
        assertTrue(reactorContext?.isEmpty == true)
    }

    @Test
    fun `verify context web filter is applicable on default graphql routes`() {
        val contextFilter = ContextWebFilter(GraphQLConfigurationProperties(), mockk())
        for (path in listOf("/graphql", "/subscriptions")) {
            assertTrue(contextFilter.isApplicable(path))
        }
    }

    @Test
    fun `verify context web filter is applicable on non-default graphql routes`() {
        val graphQLRoute = "myGraphQL"
        val subscriptionRoute = "mySubscription"
        val props = GraphQLConfigurationProperties()
        props.endpoint = graphQLRoute
        props.subscriptions.endpoint = subscriptionRoute

        val contextFilter = ContextWebFilter(props, mockk())
        for (path in listOf("/${graphQLRoute.toLowerCase()}", "/${subscriptionRoute.toLowerCase()}")) {
            assertTrue(contextFilter.isApplicable(path))
        }
    }

    @Test
    fun `verify context web filter is not applicable on non graphql routes`() {
        val contextFilter = ContextWebFilter(GraphQLConfigurationProperties(), mockk())
        assertFalse(contextFilter.isApplicable("/whatever"))
    }
}
