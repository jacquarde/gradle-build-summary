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


import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString


@OptIn(ExperimentalSerializationApi::class)
internal class Arguments<RECEIVER: Any>(
        val runner: ProcessRunner<RECEIVER>,
        val action: RECEIVER.()->Unit,
        private val serializer: KSerializer<RECEIVER>,
        val receiver: RECEIVER,
) {
	@Suppress("UNCHECKED_CAST")
	constructor(args: Array<String>): this(
			args[0].createInstance() as ProcessRunner<RECEIVER>,
			args[1].createInstance() as RECEIVER.()->Unit,
			args[2].createInstance() as KSerializer<RECEIVER>,
			Cbor.decodeFromHexString(args[2].createInstance() as KSerializer<RECEIVER>, args[3])
	)

	fun toStringList(): List<String> =
			listOf(
					runner::class.java.name,
					action::class.java.name,
					serializer::class.java.name,
					Cbor.encodeToHexString(serializer, receiver),
			)
}


private fun String.createInstance() =
		Class.forName(this)
				.declaredConstructors
				.first()
				.also {it.isAccessible = true}
				.newInstance()
