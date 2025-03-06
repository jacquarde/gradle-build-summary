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


import fixtures.GradleBuild
import fixtures.append
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import java.nio.file.Path
import kotlin.io.path.readText
import org.eu.jacquarde.stubs.GradleBuildScanServer


class `Generating simple-project build summary`: StringSpec({

	val develocityServer = GradleBuildScanServer()

	"for a single gradle task with successful result without develocity plugin." {

		val givenGradleBuild = GradleBuild().apply {
			gradleVersion = "8.12.1"
			initScript append """
				apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
			"""
			settingsScript append """
				rootProject.name = "root-project" 
			"""
		}

		givenGradleBuild.build(":tasks")

		givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			✔ **root-project** `:tasks` ┃ _Gradle 8.12.1_  
		""".trimIndent()
	}

	"for several gradle tasks with successful result with develocity plugin." {

		develocityServer.scanUrl = "test://scan.url"
		val givenGradleBuild = GradleBuild().apply {
			gradleVersion = "8.12.1"
			initScript append """
				apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
			"""
			settingsScript append """
				rootProject.name = "an-project"
				plugins {
    				id("com.gradle.develocity") version("3.19.2")
				}
				develocity {
					server = "${develocityServer.url}"
				}
			"""
		}

		givenGradleBuild.build("tasks", "projects")

		givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			✔ **an-project** `tasks projects` ┃ _Gradle 8.12.1 [BuildScan](test://scan.url)_  
		""".trimIndent()
	}

	"for a single gradle task with successful result with failing develocity server." {

		develocityServer.responseMode = GradleBuildScanServer.ResponseMode.Error
		val givenGradleBuild = GradleBuild().apply {
			gradleVersion = "8.12.1"
			initScript append """
				apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
			"""
			settingsScript append """
				rootProject.name = "root-project"
				plugins {
    				id("com.gradle.develocity") version("3.19.2")
				}
				develocity {
					server = "${develocityServer.url}"
				}
			"""
		}

		givenGradleBuild.build(":tasks")

		givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			✔ **root-project** `:tasks` ┃ _Gradle 8.12.1 ~~BuildScan~~_  
		""".trimIndent()
	}

	"for a several builds with successful result" {

		val givenGradleBuild = GradleBuild().apply {
			gradleVersion = "8.12.1"
			initScript append """
				apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
			"""
			settingsScript append """
				rootProject.name = "root-project"
			"""
		}

		givenGradleBuild.build(":tasks")
		givenGradleBuild.build(":projects")

		givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			✔ **root-project** `:tasks` ┃ _Gradle 8.12.1_  ✔ **root-project** `:projects` ┃ _Gradle 8.12.1_  
		""".trimIndent()
	}

})


private infix fun Path.shouldContain(text: String) =
		readText().shouldBeEqual(text)
