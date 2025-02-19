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


import kotlin.reflect.KProperty
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet


@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
annotation class GradleDsl

/**
 * Retrieves the [sourceSets][org.gradle.api.tasks.SourceSetContainer] extension.
 */
val Project.`sourceSets`: org.gradle.api.tasks.SourceSetContainer get() =
		(this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as org.gradle.api.tasks.SourceSetContainer


interface PropertyDelegate<OWNER: Any?, PROPERTY> {
	operator fun provideDelegate(propertyOWNER: OWNER, property: KProperty<*>): PropertyDelegate<OWNER, PROPERTY>
	operator fun getValue(propertyOwner: OWNER, property: KProperty<*>): PROPERTY
	operator fun setValue(propertyOwner: OWNER, property: KProperty<*>, value: PROPERTY) = {}
}

fun Project.modules(configuration: GradleModules.()->Unit) {
	GradleModules(project = this).configuration()
}

@GradleDsl
class GradleModules(private val project: Project) {
	fun registering(configuration: GradleModule.()->Unit) =
			object: PropertyDelegate<GradleModules?, SourceSet> {
				override fun provideDelegate(
						propertyOWNER: GradleModules?,
						property: KProperty<*>,
				): PropertyDelegate<GradleModules?, SourceSet> {
					println("~~~~~ SourceSet: ${property.name}")
					project.sourceSets.register(property.name)
					GradleModule(project, module = property.name ).configuration()
					return this
				}

				override fun getValue(propertyOwner: GradleModules?, property: KProperty<*>): SourceSet {
					return project.sourceSets.getByName(property.name)
				}
			}
	fun existing(configuration: GradleModule.()->Unit) =
			object: PropertyDelegate<GradleModules?, SourceSet> {
				override fun provideDelegate(
						propertyOWNER: GradleModules?,
						property: KProperty<*>,
				): PropertyDelegate<GradleModules?, SourceSet> {
					println("~~~~~ SourceSet: ${property.name}")
					GradleModule(project, module = property.name ).configuration()
					return this
				}

				override fun getValue(propertyOwner: GradleModules?, property: KProperty<*>): SourceSet {
					return project.sourceSets.getByName(property.name)
				}
			}
}
@GradleDsl
class GradleModule(private val project: Project, private val module: String) {
	fun implementation(dependency: Any) {
		println("~~~~~ Dependency: $module < $dependency")
		if (module=="main") {
			project.dependencies.add("implementation", dependency)
		} else {
			project.dependencies.add("${module}Implementation",dependency)
		}
	}
	fun runtimeOnly(dependency: Any) {
		println("~~~~~ Dependency: $module < $dependency")
		if (module=="main") {
			project.dependencies.add("runtimeOnly", dependency)
		} else {
			project.dependencies.add("${module}RuntimeOnly",dependency)
		}
	}
}
