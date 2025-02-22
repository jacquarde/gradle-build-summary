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


package org.eu.jacquarde.utils


import arrow.resilience.Schedule
import arrow.resilience.retryRaise
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.eu.jacquarde.extensions.wait


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

		private val retryPolicy = Schedule.recurs<Unit>(5) and Schedule.fibonacci(50.milliseconds)

		override fun run(
				receiver: RECEIVER,
				lambda: RECEIVER.()->Unit,
		) {
			ProcessHandle.current().parent().wait()
			runBlocking {
				retryPolicy.retryRaise {
					receiver.lambda()
				}
			}
		}
	}
}


inline fun <reified RECEIVER: Any> RECEIVER.afterShutdown(
		noinline action: RECEIVER.()->Unit,
): Unit =
		ShutdownManager.afterShutdown(this, action, serializer<RECEIVER>())


private fun addShutdownHook(
		action: ()->Unit,
): Unit =
		Runtime.getRuntime().addShutdownHook(Thread(action))
