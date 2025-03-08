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


package fixtures


import java.io.File
import java.nio.file.Path
import kotlin.io.path.appendText
import org.gradle.internal.classpath.DefaultClassPath
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.internal.PluginUnderTestMetadataReading
import org.eu.jacquarde.utils.TemporalFolderManager
import org.eu.jacquarde.utils.createFile


internal open class GradleBuild {

	private val temporalFolderManager = TemporalFolderManager("test")

	val projectFolder = temporalFolderManager.createFolder("project")
	val gradleFolder  = temporalFolderManager.createFolder("gradle")

	val buildScript    = projectFolder.createFile("build.gradle.kts")
	val settingsScript = projectFolder.createFile("settings.gradle.kts")
	val propertiesFile = projectFolder.createFile("gradle.properties")
	val initScript     = gradleFolder.createFile("init.gradle.kts")

	var gradleVersion: String? = null

	init {
		initScript.appendPluginClasspath()
	}

	fun build(vararg task: String): BuildResult = runner.withArguments(*task).build()
	fun buildAndFail(vararg task: String): BuildResult = runner.withArguments(*task).buildAndFail()

	private val runner
		get() = GradleRunner
				.create()
				.withTestKitDir(gradleFolder.toFile())
				.withProjectDir(projectFolder.toFile())
				.withPluginClasspath()
				.withDebug(true)
				.apply {
					if (gradleVersion != null) withGradleVersion(gradleVersion)
				}

	private fun Path.appendPluginClasspath() =
			append("""
					initscript{
						dependencies{
							classpath(files($pluginClassPath))
						}
					}
			""")

	private val pluginClassPath
		get() = PluginUnderTestMetadataReading
				.readImplementationClasspath()
				.normalize()
				.joinToString {""""${it.path}""""}

	// TODO: move to shared utils package in another source set
	private fun List<File>.normalize() = DefaultClassPath.of(this).asURIs
}


public infix fun Path.append(text: String): Path =
		apply {
			appendText(System.lineSeparator())
			appendText(text.trimIndent()) }
