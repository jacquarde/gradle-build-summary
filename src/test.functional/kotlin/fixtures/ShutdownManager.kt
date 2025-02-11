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


import java.util.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer


object ShutdownManager {

	private var active = true

	fun <RECEIVER> onShutdown(
			receiver: RECEIVER,
			action: RECEIVER.()->Unit,
	) {
		if (active) addShutdownHook {
			receiver.action()
		}
	}

	fun <RECEIVER: Any> afterShutdown(
			receiver: RECEIVER,
			action: RECEIVER.()->Unit,
			serializer: KSerializer<RECEIVER>,
	) {
		if (active) addShutdownHook {
			AfterShutdownRunner<RECEIVER>()
					.start(receiver, action, serializer)
		}
	}


	private class AfterShutdownRunner<RECEIVER: Any>: ProcessRunner<RECEIVER>() {

		init {
			active = false
		}

		override fun run(
				argument: RECEIVER,
				lambda: RECEIVER.()->Unit,
		) {
			ProcessHandle.current().parent().waitFor()
			argument.lambda()
		}

		// TODO: move to shared utils package in another source set
		private fun Optional<ProcessHandle>.waitFor() = ifPresent {it.onExit().get()}
	}
}


inline fun <reified RECEIVER: Any> RECEIVER.afterShutdown(
		noinline action: RECEIVER.()->Unit,
): Unit = ShutdownManager.afterShutdown(this, action, serializer<RECEIVER>())


private fun addShutdownHook(action: ()->Unit) {
	Runtime.getRuntime().addShutdownHook(Thread(action))
}
