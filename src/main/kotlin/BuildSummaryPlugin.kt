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


// TODO: move package to 'org.eu.jacquarde.gradle.plugins.buildsummary
package org.eu.jacquarde.gradle.plugins


import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Property
import org.eu.jacquarde.gradle.plugins.renderers.BuildSummaryRenderer


abstract class BuildSummaryPlugin @Inject constructor(
        private val flowScope: FlowScope,
        private val flowProviders: FlowProviders,
): Plugin<Gradle> {

    public override fun apply(target: Gradle) {
        val configuration = BuildSummaryConfiguration
                .createExtensionIn(target)
                .createConvention()
        BuildSummaryCollector(target, flowScope, flowProviders) {buildSummary ->
            buildSummary
                    .renderWith(configuration.renderer)
                    .writeTo(target.buildDirectory(configuration.fileName))
        }
    }

    private fun BuildSummary.renderWith(renderer: Property<BuildSummaryRenderer>): String =
            renderer.get().render(this)

    private fun Gradle.buildDirectory(fileName: Property<String>): Path =
            rootProject.layout.buildDirectory
                    .ensureIsCreated()
                    .resolve(fileName.get())

    private fun String.writeTo(file: Path?) =
            Files.writeString(file, this, StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
}


private fun DirectoryProperty.ensureIsCreated() =
        Files.createDirectories(this.toPath)

private val DirectoryProperty.toPath: Path
    get() =
        get().asFile.toPath()
