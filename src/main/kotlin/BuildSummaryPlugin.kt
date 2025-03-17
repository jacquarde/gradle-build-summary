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
import javax.inject.Inject
import kotlin.reflect.jvm.jvmName
import org.gradle.api.Plugin
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.services.BuildServiceSpec
import org.eu.jacquarde.gradle.plugins.buildsummary.renderers.BuildSummaryRenderer


@Suppress("unused")
abstract class BuildSummaryPlugin @Inject constructor(
        private val providerFactory: ProviderFactory,
        private val flowScope: FlowScope,
        private val flowProviders: FlowProviders
): Plugin<Gradle> {

    public override fun apply(target: Gradle) {
        val lc = target.register<LifecycleService, _> {
            parameters.gradleVersion.convention("")
            parameters.tasks.convention(emptyList())
            parameters.hasBuildFailed.convention(false)
            parameters.rootProject.convention("")
            parameters.builsScanUrl.convention("")
            parameters.hasBuildScanaFailed.convention(false)
        }.get()
        flowScope.always(XXX::class.java){
            parameters.buildWorkResult.set(
                    flowProviders.buildWorkResult
            )
        }
        val c = BuildSummaryConfiguration
                .createExtensionIn(target)
                .createConvention()
        LifecycleRegistry.setup(target, lc)
        target.projectsEvaluated {
            Writer.setup(
                    c,
                    target.parent?.rootProject?.layout?.buildDirectory?.get() ?: target.rootProject.layout.buildDirectory.get()
            )

        }

    }

    private val Gradle.buildDirectory: Path
        get() =
            rootProject.layout.buildDirectory
                    .ensureIsCreated()
}


object Writer {

    private val logger = Logging.getLogger("BuildSummaryPlugin")
    private lateinit var renderer: BuildSummaryRenderer
    private lateinit var file: Path

    fun setup(configuration: BuildSummaryConfiguration, folder: Directory ) {
        renderer = configuration.renderer.get()
        file = folder.asFile.toPath().resolve(configuration.fileName.get())
    }

    fun write(buildSummary: BuildSummary) {
        file
                .readLines()
                .add(buildSummary)
                .writeTo(file)

    }

    private fun BuildSummary.renderWith(renderer: Property<BuildSummaryRenderer>): String =
            renderer.get().render(this)

    private fun List<String>.writeTo(file: Path?) =
            Files.writeString(
                    file,
                    this.joinToString("\n"),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE
            )

    private fun List<String>.add(summary: BuildSummary) =
            renderer.add(summary, to = this)
}


//region Private extensions

private fun DirectoryProperty.ensureIsCreated() =
        Files.createDirectories(this.toPath)

private fun Path.readLines(): List<String> =
        if (Files.exists(this))
            Files.readAllLines(this)
        else
            emptyList()

private val DirectoryProperty.toPath: Path
    get() =
        get().asFile.toPath()

private inline fun <reified SERVICE> Gradle.register() where
        SERVICE: BuildService<BuildServiceParameters.None> =
        sharedServices.registerIfAbsent(SERVICE::class.jvmName, SERVICE::class.java)

private inline fun <reified SERVICE, PARAMETERS> Gradle.register(
        noinline specification: BuildServiceSpec<PARAMETERS>.()->Unit,
) where PARAMETERS: BuildServiceParameters,
        SERVICE: BuildService<PARAMETERS> =
        sharedServices.registerIfAbsent(SERVICE::class.jvmName, SERVICE::class.java, specification)

//endregion
