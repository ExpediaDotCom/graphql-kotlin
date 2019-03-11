package com.expedia.graphql.sample

import com.expedia.graphql.sample.context.MyGraphQLContext
import graphql.ExecutionInput
import graphql.GraphQL
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class QueryHandler(private val graphql: GraphQL) {

    fun executeQuery(request: GraphQLRequest): Mono<GraphQLResponse> = Mono.subscriberContext()
        .flatMap { ctx ->
            val graphQLContext: MyGraphQLContext = ctx.get("graphQLContext")
            val input = ExecutionInput.newExecutionInput()
                .query(request.query)
                .operationName(request.operationName)
                .variables(request.variables)
                .context(graphQLContext)
                .build()

            Mono.fromFuture(graphql.executeAsync(input))
                .map { executionResult -> executionResult.toGraphQLResponse() }
        }
}