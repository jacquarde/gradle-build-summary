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


package org.eu.jacquarde.gradle.plugins.buildsummary


import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.eu.jacquarde.gradle.plugins.buildsummary.renderers.BuildSummaryRenderer


object Writer {

    private lateinit var renderer: BuildSummaryRenderer
    private lateinit var summaryFolder: Directory
    private lateinit var summaryFile: String

    fun setup(
            configuration: BuildSummaryConfiguration,
            folder: DirectoryProperty,
    ) {
        renderer = configuration.renderer.get()
        summaryFolder = folder.get()
        summaryFile = configuration.fileName.get()
    }

    fun write(buildSummary: BuildSummary) {
        val file = summaryFolder.ensureIsCreated().resolve(summaryFile)
        file
                .readLines()
                .add(buildSummary)
                .writeTo(file)
    }

    private fun List<String>.writeTo(file: Path) =
            Files.writeString(
                    file,
                    this.joinToString("\n"),
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE
            )

    private fun List<String>.add(summary: BuildSummary) =
            renderer.add(summary, to = this)
}

//region Private extensions

private fun Directory.ensureIsCreated(): Path =
        Files.createDirectories(this.asFile.toPath())

private fun Path.readLines(): List<String> =
        if (Files.exists(this))
            Files.readAllLines(this)
        else
            emptyList()

//endregion
