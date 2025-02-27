/*
import io.kotest.core.spec.style.StringSpec
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
import org.eu.jacquarde.stubs.GradleBuildScanServer


class `Generating build summary`: StringSpec({

	autoClose(GradleBuildScanServer())

	"for a simple gradle task with successful result and no develocity plugin" {
		GradleBuild().apply {
			gradleVersion = "8.12.1"
			initScript append """
					apply<org.eu.jacquarde.gradle.plugins.BuildSummaryPlugin>()
				"""
		}.build(task = "build")
	}

})
