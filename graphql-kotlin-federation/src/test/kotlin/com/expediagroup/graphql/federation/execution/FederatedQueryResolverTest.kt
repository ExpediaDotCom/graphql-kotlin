package com.expediagroup.graphql.federation.execution

import graphql.ExecutionInput
import graphql.GraphQL
import org.junit.jupiter.api.Test
import test.data.BookResolver
import test.data.UserResolver
import test.data.federatedTestSchema
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

const val FEDERATED_QUERY = """
query (${'$'}_representations: [_Any!]!) {
  _entities(representations: ${'$'}_representations) {
    ... on User {
      name
    }
    ... on Book {
      reviews {
        id
        body
      }
      shippingCost
    }
  }
}"""

class FederatedQueryResolverTest {

    @Test
    fun `verify can resolve federated entities`() {
        val schema = federatedTestSchema(mapOf("Book" to BookResolver(), "User" to UserResolver()))
        val userRepresentation = mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName")
        val book1Representation = mapOf<String, Any>("__typename" to "Book", "id" to 987, "weight" to 2.0)
        val book2Representation = mapOf<String, Any>("__typename" to "Book", "id" to 988, "weight" to 1.0)
        val unknownRepresentation = mapOf<String, Any>("id" to 124)
        val representations = listOf(userRepresentation, book1Representation, book2Representation, unknownRepresentation)
        val variables = mapOf<String, Any>("_representations" to representations)
        val executionInput = ExecutionInput.newExecutionInput()
            .query(FEDERATED_QUERY)
            .variables(variables)
            .build()
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.executeAsync(executionInput).get().toSpecification()

        assertNotNull(result["data"] as? Map<*, *>) { data ->
            assertNotNull(data["_entities"] as? List<*>) { entities ->
                assertFalse(entities.isEmpty())
                assertEquals(representations.size, entities.size)
                assertNotNull(entities[0] as? Map<*, *>) { user ->
                    assertEquals("testName", user["name"])
                }
                assertNotNull(entities[1] as? Map<*, *>) { book ->
                    assertEquals("$19.98", book["shippingCost"])
                    assertNotNull(book["reviews"] as? List<*>) { reviews ->
                        assertEquals(1, reviews.size)
                        assertNotNull(reviews.firstOrNull() as? Map<*, *>) { review ->
                            assertEquals("parent-987", review["id"])
                            assertEquals("Dummy Review 987", review["body"])
                        }
                    }
                }
                assertNotNull(entities[2] as? Map<*, *>) { book ->
                    assertEquals("$9.99", book["shippingCost"])
                    assertNotNull(book["reviews"] as? List<*>) { reviews ->
                        assertEquals(1, reviews.size)
                        assertNotNull(reviews.firstOrNull() as? Map<*, *>) { review ->
                            assertEquals("parent-988", review["id"])
                            assertEquals("Dummy Review 988", review["body"])
                        }
                    }
                }
                assertNull(entities[3])
            }
        }
        assertNotNull(result["errors"] as? List<*>) { errors ->
            assertEquals(1, errors.size)
            assertNotNull(errors.firstOrNull() as? Map<*, *>) { error ->
                assertEquals("Unable to resolve federated type, representation={id=124}", error["message"])
            }
        }
    }
}
