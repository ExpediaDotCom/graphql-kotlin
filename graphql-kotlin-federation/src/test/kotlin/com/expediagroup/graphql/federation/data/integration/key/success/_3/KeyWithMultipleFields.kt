/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.federation.data.integration.key.success._3

import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import io.mockk.mockk

/*
# example usage of a valid @key directive referencing multiple fields on a local type
type KeyWithMultipleFields @key(fields : "id type") {
  description: String!
  id: String!
  type: String!
}
 */
@KeyDirective(fields = FieldSet("id type"))
data class KeyWithMultipleFields(val id: String, val type: String, val description: String)

class KeyWithMultipleFieldsQuery {
    fun keyWithMultipleFields(): KeyWithMultipleFields = mockk()
}
