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


import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet


val Project.sourceSets: SourceSetContainer
	get() =
		(this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer

val Project.shared: NamedDomainObjectProvider<KotlinSourceSet>
	get() =
		sourceSets.maybeCreate("shared").let {kotlinExtension.sourceSets.named("shared")}

val Project.testFunctional: NamedDomainObjectProvider<KotlinSourceSet>
	get() =
		sourceSets.maybeCreate("testFunctional").let {kotlinExtension.sourceSets.named("testFunctional")}

val SourceSetContainer.shared: SourceSet
	get() =
		named("shared").get()

val SourceSetContainer.testFunctional: SourceSet
	get() =
		named("testFunctional").get()
