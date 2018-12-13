package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.generator.functionFilters
import com.expedia.graphql.schema.generator.propertyFilters
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

internal fun KClass<*>.getValidProperties(hooks: SchemaGeneratorHooks): List<KProperty<*>> =
    this.declaredMemberProperties
        .filter { hooks.isValidProperty(it) }
        .filter { prop -> propertyFilters.all { it.invoke(prop, this) } }

internal fun KClass<*>.getValidFunctions(hooks: SchemaGeneratorHooks): List<KFunction<*>> =
    this.declaredMemberFunctions
        .filter { hooks.isValidFunction(it) }
        .filter { func -> functionFilters.all { it.invoke(func) } }

internal fun KClass<*>.findConstructorParamter(name: String): KParameter? =
    this.primaryConstructor
        ?.parameters
        ?.find { it.name == name }

internal fun KClass<*>.isGraphQLInterface(): Boolean = this.java.isInterface

internal fun KClass<*>.isGraphQLUnion(): Boolean =
    this.isGraphQLInterface() && this.declaredMemberProperties.isEmpty() && this.declaredMemberFunctions.isEmpty()

internal fun KClass<*>.isEnum(): Boolean = this.isSubclassOf(Enum::class)

internal fun KClass<*>.isList(): Boolean = this.isSubclassOf(List::class)

internal fun KClass<*>.isArray(): Boolean = this.java.isArray
