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


package io.github.jacquarde.gradle.plugins.fixtures


import kotlin.reflect.jvm.jvmName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString


abstract class ProcessRunner<ARGUMENT: Any> {

	abstract fun run(argument: ARGUMENT, lambda: ARGUMENT.()->Unit): Unit

	fun start(
			argument: ARGUMENT,
			action: ARGUMENT.()->Unit = {},
			serializer: KSerializer<ARGUMENT>,
	): Unit =
			JvmProcess(
					mainClass = this::class.jvmName,
					arguments = listOf(
							argument.encodeWith(serializer),
							action::class.jvmName,
							this::class.jvmName,
							serializer::class.jvmName
					)
			).start()


	companion object {

		@JvmStatic
		@Suppress("UNCHECKED_CAST")
		fun main(args: Array<String>) {
			val runner = args[2].createInstance() as ProcessRunner<Any>
			val lambda = args[1].createInstance() as Any.()->Unit
			val serializer = args[3].createInstance() as KSerializer<*>
			val argument = args[0].decodeWith(serializer) as Any
			runner.run(argument, lambda)
		}

		private fun String.createInstance() =
				Class.forName(this)
						.declaredConstructors
						.first()
						.also {it.isAccessible = true}
						.newInstance()
	}
}


@OptIn(ExperimentalSerializationApi::class)
private fun <TYPE> String.decodeWith(serializer: KSerializer<TYPE>) =
		Cbor.decodeFromHexString(serializer, this)


@OptIn(ExperimentalSerializationApi::class)
private fun <TYPE: Any> TYPE.encodeWith(serializer: KSerializer<TYPE>): String {
	return Cbor.encodeToHexString(serializer, this)
}
