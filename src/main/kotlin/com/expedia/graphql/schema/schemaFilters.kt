package com.expedia.graphql.schema

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility

private val blackListFunctions: List<String> = listOf("annotationType", "toString", "copy", "equals", "hashCode")
private val componentFunctionRegex = Regex("component([0-9]+)")

private typealias CallableFilter = (KCallable<*>) -> Boolean
private typealias AnnotatedElementFilter = (KAnnotatedElement) -> Boolean

internal typealias PropertyFilter = (KProperty<*>) -> Boolean
internal typealias FunctionFilter = (KFunction<*>) -> Boolean

internal val isNotBlackListed: FunctionFilter = { (blackListFunctions.contains(it.name) || it.name.matches(componentFunctionRegex)).not() }

private val isPublic: CallableFilter = { it.visibility == KVisibility.PUBLIC }

private val isNotGraphQLIgnored: AnnotatedElementFilter = { it.isGraphQLIgnored().not() }

internal val propertyFilters: List<PropertyFilter> = listOf(isPublic, isNotGraphQLIgnored)
internal val functionFilters: List<FunctionFilter> = listOf(isPublic, isNotGraphQLIgnored, isNotBlackListed)
