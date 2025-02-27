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


import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.TaskContainerScope
import org.gradle.kotlin.dsl.maybeCreate
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension


private object SourceSets {
	const val shared           = "shared"
	const val testFunctional   = "testFunctional"
}

val Project.shared
	get() = register(SourceSets.shared)

val SourceSetContainer.shared
	get() = named(SourceSets.shared).get()

val Project.testFunctional
	get() = register(SourceSets.testFunctional)

val SourceSetContainer.testFunctional
	get() = named(SourceSets.testFunctional).get()

val TaskContainerScope.testFunctional
	get() = maybeCreate(SourceSets.testFunctional, Test::class)
			.apply {
				group           = "verification"
				testClassesDirs = project.sourceSets.testFunctional.output.classesDirs
				classpath       = project.sourceSets.testFunctional.runtimeClasspath
			}
			.let {named(SourceSets.testFunctional, Test::class)}

private val Project.sourceSets
	get() = (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer

private fun Project.register(name: String) =
		sourceSets.maybeCreate(name)
				.let {kotlinExtension.sourceSets.named(name)}
