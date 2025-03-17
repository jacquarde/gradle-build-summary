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


import javax.inject.Inject
import kotlin.reflect.jvm.jvmName
import org.gradle.api.Plugin
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.services.BuildServiceSpec
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf
import org.eu.jacquarde.gradle.plugins.buildsummary.internal.BuildFinishedAction
import org.eu.jacquarde.gradle.plugins.buildsummary.internal.LifecycleRegister
import org.eu.jacquarde.gradle.plugins.buildsummary.internal.LifecycleService


@Suppress("unused")
abstract class BuildSummaryPlugin @Inject constructor(
        private val providerFactory: ProviderFactory,
        private val flowScope      : FlowScope,
        private val flowProviders  : FlowProviders,
): Plugin<Gradle> {

    public override fun apply(target: Gradle) {
        val configuration    = registerExtension(target)
        target.beforeSettings {
            if (configuration.activeIf.get().invoke()) {
                doApply(target, configuration)
            }
        }
    }

    private fun doApply(
            target: Gradle,
            configuration: BuildSummaryConfiguration,
    ) {
        log(target, configuration)
        registerBuildFinishedAction()
        val lifecycleService = registerLifecycleService(target)
        LifecycleRegister.setup(target, lifecycleService)
        target.projectsEvaluated {
            Writer.setup(configuration, topBuildRectory(target))
        }
    }

    private fun log(target: Gradle, configuration: BuildSummaryConfiguration) {
        target.serviceOf<StyledTextOutputFactory>().create(this::class.jvmName, LogLevel.LIFECYCLE).apply {
            style(StyledTextOutput.Style.Success)
                    .println("Plugin <${BuildSummaryPlugin::class.jvmName}> applied${scriptLocation(configuration)}.")
        }
    }

    private fun scriptLocation(configuration: BuildSummaryConfiguration): String =
            if (configuration.script.isPresent)
                " in '${configuration.script.get().canonicalPath}'"
            else ""

    private fun topBuildRectory(target: Gradle): DirectoryProperty =
            target.parent?.rootBuildDirectory ?: target.rootBuildDirectory

    private fun registerBuildFinishedAction() =
            flowScope.always(BuildFinishedAction::class.java) {
                parameters.buildWorkResult.set(
                        flowProviders.buildWorkResult
                )
            }

    private fun registerExtension(target: Gradle): BuildSummaryConfiguration =
            BuildSummaryConfiguration
                    .createExtensionIn(target)
                    .createConvention()

    private fun registerLifecycleService(target: Gradle): LifecycleService =
            target.register<LifecycleService, _> {
                parameters.gradleVersion.convention("")
                parameters.tasks.convention(emptyList())
                parameters.hasBuildFailed.convention(false)
                parameters.rootProject.convention("")
                parameters.builsScanUrl.convention("")
                parameters.hasBuildScanaFailed.convention(false)
            }.get()

    private val Gradle.rootBuildDirectory: DirectoryProperty
        get() =
            rootProject.layout.buildDirectory
}


//region Private extensions

private inline fun <reified SERVICE> Gradle.register() where
        SERVICE: BuildService<BuildServiceParameters.None> =
        sharedServices.registerIfAbsent(SERVICE::class.jvmName, SERVICE::class.java)

private inline fun <reified SERVICE, PARAMETERS> Gradle.register(
        noinline specification: BuildServiceSpec<PARAMETERS>.()->Unit,
) where PARAMETERS: BuildServiceParameters,
        SERVICE: BuildService<PARAMETERS> =
        sharedServices.registerIfAbsent(SERVICE::class.jvmName, SERVICE::class.java, specification)

//endregion
