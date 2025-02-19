/*
 * Copyright 2025 jacquarde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalContracts::class)


package org.eu.jacquarde.extensions


import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * Calls the specified [block] lambda with no arguments.
 *
 * Allows the use of the standard scope function [also][kotlin.also] with callable references that have no parameters.
 */
inline fun Unit.also(block: ()->Unit) {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}
	block()
}

/**
 * Calls the specified [block] lambda with no arguments and returns its result.
 *
 * Allows the clean use of standard scope function [let][kotlin.let] with callable references that have no parameters.
 */
inline fun <RESULT> Unit.let(block: ()->RESULT): RESULT {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}
	return block()
}
