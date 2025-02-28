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


class `Generating simple-project build summary`: StringSpec({

	"for a single gradle task with successful result and no develocity plugin." {

		val givenVersion         = "8.12.1"
		val givenTask            = ":build"
		val givenRootProject     = """root-project"""
		val givenGradleBuild     = GradleBuild().apply {
			gradleVersion = givenVersion
			initScript append """
				apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
			"""
			settingsScript append """
				rootProject.name = "$givenRootProject" 
			"""
		}

		givenGradleBuild.build(task = givenTask)

		givenGradleBuild.projectFolder.resolve("build/build-summary.md") shouldContain """
			[![](https://img.shields.io/badge/$givenVersion-Build_Scan_not_published-06A0CE?&logo=Gradle)](https://scans.gradle.com)

			:white_check_mark: **$givenRootProject** :$givenTask
		""".trimIndent()
	}

})


private infix fun Path.shouldContain(text: String) =
		readText().shouldBeEqual(text)
