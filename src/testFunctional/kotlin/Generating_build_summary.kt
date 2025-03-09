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
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText
import org.eu.jacquarde.stubs.GradleBuildScanServer
import org.eu.jacquarde.utils.createFile


// TODO: generate tests for when using the develocity in foreground
// CHECK: the use of develocity in background on CI
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

    "for a several builds with successful result." {

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

    "for a single gradle task with successful result configuring the renderer." {

        val givenGradleBuild = GradleBuild().apply {
            gradleVersion = "8.12.1"
            initScript append """
				import org.eu.jacquarde.gradle.plugins.*
				import org.eu.jacquarde.gradle.plugins.renderers.*
				apply<BuildSummaryPlugin>()
				configure<BuildSummaryConfiguration> {
					renderer = MarkdownBadgeRenderer()
				}
			"""
            settingsScript append """
				rootProject.name = "root-project" 
			"""
        }

        givenGradleBuild.build(":tasks")

        givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			![](https://img.shields.io/badge/✔_root--project_-:tasks-0A0?&style=flat-square)
			![](https://img.shields.io/badge/8.12.1-555?&style=flat-square&logo=Gradle)  
		""".trimIndent()
    }

    "for a single gradle task with successful result with inline renderer." {

        val givenGradleBuild = GradleBuild().apply {
            gradleVersion = "8.12.1"
            initScript append """
				import org.eu.jacquarde.gradle.plugins.*
				import org.eu.jacquarde.gradle.plugins.renderers.*
				apply<BuildSummaryPlugin>()
				configure<BuildSummaryConfiguration> {
					renderer.set({it.toString()})
				}
			"""
            settingsScript append """
				rootProject.name = "root-project" 
			"""
        }

        givenGradleBuild.build(":tasks")

        givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			BuildSummary(rootProject=root-project, tasks=[:tasks], gradleVersion=8.12.1, hasBuildFailed=false, buildScanUrl=, hasPublishFailed=false)
		""".trimIndent()
    }


    "for a single gradle task with successful result renaming summary fie." {

        val givenGradleBuild = GradleBuild().apply {
            gradleVersion = "8.12.1"
            initScript append """
				import org.eu.jacquarde.gradle.plugins.*
				apply<BuildSummaryPlugin>()
				configure<BuildSummaryConfiguration> {
					fileName = "summary.markdown"
				}
			"""
            settingsScript append """
				rootProject.name = "root-project" 
			"""
        }

        givenGradleBuild.build(":tasks")

        givenGradleBuild.projectFolder.resolve("build/summary.markdown") shouldContain """
			✔ **root-project** `:tasks` ┃ _Gradle 8.12.1_  
		""".trimIndent()
    }

    "for a several builds with build failed." {

        val givenGradleBuild = GradleBuild().apply {
            gradleVersion = "8.12.1"
            initScript append """
				apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
			"""
            settingsScript append """
				rootProject.name = "test-project"
			"""
            buildScript append """
				plugins {
    				kotlin("jvm") version "2.1.10"
				}
				repositories {
				    mavenCentral()
				}
			"""
            projectFolder
                    .createFolders("src", "main", "kotlin")
                    .createFile("test.kt") append """
			        	val a = 2
			        	a = 3
			        """
        }

        givenGradleBuild.buildAndFail(":build")

        givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			✖ **test-project** `:build` ┃ _Gradle 8.12.1_  
		""".trimIndent()
    }

    "for a several builds with build cached." {

        val givenGradleBuild = GradleBuild().apply {
            gradleVersion = "8.12.1"
            initScript append """
				apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
			"""
            settingsScript append """
				rootProject.name = "test-project"
			"""
            buildScript append """
				plugins {
    				kotlin("jvm") version "2.1.10"
				}
				repositories {
				    mavenCentral()
				}
			"""
            projectFolder
                    .createFolders("src", "main", "kotlin")
                    .createFile("test.kt") append """
			        	class Test
			        """
        }

        givenGradleBuild.build("--build-cache", ":build")
        givenGradleBuild.build("--build-cache", ":build")

        givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			✔ **test-project** `:build` ┃ _Gradle 8.12.1_  ✔ **test-project** `:build` ┃ _Gradle 8.12.1_  
		""".trimIndent()
    }

})


private fun Path.createFolders(vararg subFolders: String): Path =
        Files.createDirectories(
                subFolders.fold(this, Path::resolve)
        )


private infix fun Path.shouldContain(text: String) =
        readText().shouldBe(text)
