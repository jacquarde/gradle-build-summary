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
import org.eu.jacquarde.gradle.plugins.writers.MarkdownRenderer


abstract class BuildSummaryPlugin @Inject constructor(
        private val flowScope: FlowScope,
        private val flowProviders: FlowProviders,
): Plugin<Gradle> {

    private val summaryFileName = "build-summary.md"

    public override fun apply(gradle: Gradle) {
        BuildSummaryCollector(gradle, flowScope, flowProviders) {buildSummary ->
            buildSummary
                    .render()
                    .writeTo(gradle.summaryFile)
        }
    }

    private val Gradle.summaryFile: Path get() =
        rootProject.layout.buildDirectory
                .ensureIsCreated()
                .resolve(summaryFileName)

}


private fun DirectoryProperty.ensureIsCreated() =
        Files.createDirectories(this.toPath)

private val DirectoryProperty.toPath: Path get() =
        get().asFile.toPath()

private fun BuildSummary.render(): String =
        MarkdownRenderer(this).render()

private fun String.writeTo(file: Path?) =
        Files.writeString(file, this, StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
