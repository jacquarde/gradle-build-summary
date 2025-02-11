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


import java.io.File
import kotlin.io.path.appendText
import org.gradle.internal.classpath.DefaultClassPath
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading


internal open class GradleBuild {

	private val temporalFolderManager = TemporalFolderManager("test")

	private val projectFolder	= temporalFolderManager.createFolder("project")
	private val gradleFolder	= temporalFolderManager.createFolder("gradle")

	val buildScript		= projectFolder.createFile("build.gradle.kts")
	val settingsScript	= projectFolder.createFile("settings.gradle.kts")
	val propertiesFile	= projectFolder.createFile("gradle.properties")
	var initScript		= gradleFolder.createFile("init.gradle.kts")

	init {
		initScript.appendText(
				"""
					initscript{
						dependencies{
							classpath(files($pluginClassPath))
						}
					}
				"""
		)
	}

	fun build(task: String): BuildResult =
			runner.withArguments(task).build()

	private val runner = GradleRunner.create()
			.withTestKitDir(gradleFolder.toFile())
			.withProjectDir(projectFolder.toFile())
			.withPluginClasspath()
			.withDebug(true)

	private val pluginClassPath
		get() =
			PluginUnderTestMetadataReading
					.readImplementationClasspath()
					.normalize()
					.joinToString {""""${it.path}""""}

	// TODO: move to shared utils package in another source set
	private fun List<File>.normalize() =
			DefaultClassPath.of(this)
					.asURIs
}
