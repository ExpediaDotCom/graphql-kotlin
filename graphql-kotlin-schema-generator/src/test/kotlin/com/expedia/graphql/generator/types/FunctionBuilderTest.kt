package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.annotations.GraphQLName
import com.expedia.graphql.execution.FunctionDataFetcher
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.DataFetchingEnvironment
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLNonNull
import io.reactivex.Flowable
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class FunctionBuilderTest : TypeTestHelper() {

    private lateinit var builder: FunctionBuilder

    override fun beforeTest() {
        builder = FunctionBuilder(generator)
    }

    internal interface MyInterface {
        fun printMessage(message: String): String
    }

    internal class MyImplementation : MyInterface {
        override fun printMessage(message: String): String = "message=$message"
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
    internal annotation class FunctionDirective(val arg: String)

    @GraphQLDirective
    internal annotation class ArgumentDirective(val arg: String)

    private class Happy {

        @GraphQLDescription("By bob")
        @FunctionDirective("happy")
        fun littleTrees() = UUID.randomUUID().toString()

        fun paint(@GraphQLDescription("brush color") @ArgumentDirective("red") color: String) = color.reversed()

        @Deprecated("Should paint instead")
        fun sketch(tree: String) = tree

        @GraphQLName("renamedFunction")
        fun originalName(input: String) = input

        @Deprecated("No saw, just paint", replaceWith = ReplaceWith("paint"))
        fun saw(tree: String) = tree

        fun context(@GraphQLContext context: String, string: String) = "$context and $string"

        fun ignoredParameter(color: String, @GraphQLIgnore ignoreMe: String) = "$color and $ignoreMe"

        fun publisher(num: Int): Publisher<Int> = Flowable.just(num)

        fun flowable(num: Int): Flowable<Int> = Flowable.just(num)

        fun completableFuture(num: Int): CompletableFuture<Int> = CompletableFuture.completedFuture(num)

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment): String = environment.field.name
    }

    @Test
    fun `test description`() {
        val kFunction = Happy::littleTrees
        val result = builder.function(kFunction, "Query")
        assertEquals("By bob", result.description)
    }

    @Test
    fun `test description on argument`() {
        val kFunction = Happy::paint
        val result = builder.function(kFunction, "Query").arguments[0]
        assertEquals("brush color", result.description)
    }

    @Test
    fun `test deprecation`() {
        val kFunction = Happy::sketch
        val result = builder.function(kFunction, "Query")
        assertTrue(result.isDeprecated)
        assertEquals("Should paint instead", result.deprecationReason)

        val fieldDirectives = result.directives
        assertEquals(1, fieldDirectives.size)
        assertEquals("deprecated", fieldDirectives.first().name)
    }

    @Test
    fun `test changing the name of a function`() {
        val kFunction = Happy::originalName
        val result = builder.function(kFunction, "Query")
        assertEquals("renamedFunction", result.name)
    }

    @Test
    fun `test deprecation with replacement`() {
        val kFunction = Happy::saw
        val result = builder.function(kFunction, "Query")
        assertTrue(result.isDeprecated)
        assertEquals("No saw, just paint, replace with paint", result.deprecationReason)
    }

    @Test
    fun `test custom directive on function`() {
        val kFunction = Happy::littleTrees
        val result = builder.function(kFunction, "Query")

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("functionDirective", directive.name)
        assertEquals("happy", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.FIELD_DEFINITION)
        )
    }

    @Test
    fun `test custom directive on function argument`() {
        val kFunction = Happy::paint
        val result = builder.function(kFunction, "Query").arguments[0]

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("argumentDirective", directive.name)
        assertEquals("red", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
    }

    @Test
    fun `test context on argument`() {
        val kFunction = Happy::context
        val result = builder.function(kFunction, "Query")

        assertTrue(result.directives.isEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)
        val arg = result.arguments.firstOrNull()
        assertEquals(expected = "string", actual = arg?.name)
    }

    @Test
    fun `test ignored parameter`() {
        val kFunction = Happy::ignoredParameter
        val result = builder.function(kFunction, "Query")

        assertTrue(result.directives.isEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)
        val arg = result.arguments.firstOrNull()
        assertEquals(expected = "color", actual = arg?.name)
    }

    @Test
    fun `non-abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertTrue(generator.codeRegistry.getDataFetcher(FieldCoordinates.coordinates("Query", kFunction.name), result) is FunctionDataFetcher)
    }

    @Test
    fun `abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
    }

    @Test
    fun `abstract function with target`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, parentName = "Query", target = MyImplementation(), abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
    }

    @Test
    fun `publisher return type is valid`() {
        val kFunction = Happy::publisher
        val result = builder.function(fn = kFunction, parentName = "Query")

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals("Int", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `a return type that implements Publisher is valid`() {
        val kFunction = Happy::flowable
        val result = builder.function(fn = kFunction, parentName = "Query")

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals("Int", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `completable future return type is valid`() {
        val kFunction = Happy::completableFuture
        val result = builder.function(fn = kFunction, parentName = "Query")

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals("Int", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `DataFetchingEnvironment argument type is ignored`() {
        val kFunction = Happy::dataFetchingEnvironment
        val result = builder.function(fn = kFunction, parentName = "Query")

        assertEquals(expected = 0, actual = result.arguments.size)
        assertEquals("String", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }
}
