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


package utils


import kotlinx.serialization.KSerializer


internal abstract class ProcessRunner<ARGUMENT: Any> {

	abstract fun run(
			receiver: ARGUMENT,
			lambda: ARGUMENT.()->Unit,
	): Unit

	fun start(
			receiver: ARGUMENT,
			lambda: ARGUMENT.()->Unit = {},
			serializer: KSerializer<ARGUMENT>,
	): Unit =
			JvmProcess(
					mainClass = this::class.java.name,
					Arguments(this, lambda, serializer, receiver).toStringList()
			).start()

	private companion object {
		@JvmStatic
		fun main(args: Array<String>) {
			with(Arguments<Any>(args)) {
				runner.run(receiver, action)
			}
		}
	}
}
