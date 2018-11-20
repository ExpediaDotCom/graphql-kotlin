package com.expedia.graphql.sample.directives

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironmentBuilder
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiringEnvironment

class StringEvalDirectiveWiring : DirectiveWiring {
    private val dirName = getDirectiveName(StringEval::class)

    override fun isApplicable(environment: SchemaDirectiveWiringEnvironment<*>): Boolean =
            (environment.element as? GraphQLFieldDefinition)?.arguments?.any { it.getDirective(dirName) != null }
                    ?: false

    override fun onField(wiringEnv: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>): GraphQLFieldDefinition {
        val field = wiringEnv.element
        val originalDataFetcher: DataFetcher<Any> = field.dataFetcher

        val defaultValueFetcher = DataFetcher<Any> { dataEnv ->
            val newArguments = HashMap(dataEnv.arguments)
            wiringEnv.element.arguments.asSequence()
                    .map { gqlArg ->
                        val strArg: String? = dataEnv.getArgument(gqlArg.name) as String?
                        Pair(gqlArg, strArg)
                    }
                    .forEach { (gqlArg, value) ->
                        if (gqlArg.getDirective(dirName).getArgument(StringEval::lowerCase.name).value as Boolean) {
                            newArguments[gqlArg.name] = value?.toLowerCase()
                        }
                        if (value.isNullOrEmpty()) {
                            newArguments[gqlArg.name] = gqlArg.getDirective(dirName).getArgument(StringEval::default.name).value as String
                        }
                    }
            val newEnv = DataFetchingEnvironmentBuilder.newDataFetchingEnvironment(dataEnv)
                    .arguments(newArguments)
                    .build()
            originalDataFetcher.get(newEnv)
        }
        return field.transform { it.dataFetcher(defaultValueFetcher) }
    }
}