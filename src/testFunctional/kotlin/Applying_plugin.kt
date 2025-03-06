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
import org.gradle.testkit.runner.TaskOutcome


class `Applying plugin`: StringSpec({

	// SEE: https://gradle.org/releases/
	with("8.12.1", "8.12", "8.11.1", "8.10.2") {version ->
		"to an init script in version $version" {

			val givenTask   = ":buildEnvironment"
			val givenGradle = GradleBuild().apply {
				gradleVersion = version
				initScript append """
					apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
				"""
			}

			val actualResult = givenGradle.build(givenTask)

			actualResult.task(givenTask)!!.outcome shouldBeEqual TaskOutcome.SUCCESS
		}
	}
})


// TODO: move this to utils and check kotest dataset dependency
fun <DATA> with(vararg data: DATA, testBlock: (DATA)->Unit) =
		data.forEach {testBlock(it)}
