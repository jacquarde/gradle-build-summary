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


import java.lang.management.ManagementFactory


class JvmProcess(
		private val mainClass: String,
		private val arguments: List<String>,
		private val inheritClasspath: Boolean = true,
		private val inheritIO: Boolean = true,
) {
	fun start() {
		ProcessBuilder().apply {
			command().add("java")
			if (inheritClasspath) {
				command().add("-cp")
				command().add(System.getProperty("java.class.path"))
			}
			if (inDebugging) {
				command().add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005")
			}
			command().add(mainClass)
			command().addAll(arguments)
			if (inheritIO) inheritIO()
		}.start()
	}

	private val inDebugging: Boolean =
			ManagementFactory.getRuntimeMXBean().inputArguments.any {it.contains("jdwp")}
}
