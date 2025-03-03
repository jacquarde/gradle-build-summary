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

@file:Suppress("KDocMissingDocumentation", "UnstableApiUsage")


package org.eu.jacquarde.gradle.plugins


import com.gradle.develocity.agent.gradle.internal.DevelocityConfigurationInternal
import com.gradle.develocity.agent.gradle.scan.PublishedBuildScan
import java.util.Locale
import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.flow.BuildWorkResult
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.always
import org.gradle.kotlin.dsl.registerIfAbsent
import org.eu.jacquarde.gradle.plugins.writers.MarkdownRenderer


abstract class BuildSummaryPlugin @Inject constructor(
        private val flowScope: FlowScope,
        private val flowProviders: FlowProviders,
): Plugin<Gradle> {

    public override fun apply(gradle: Gradle) {
        val buildSummaryService = gradle.sharedServices.registerIfAbsent("xxx", BuildSummaryService::class).get()
        gradle.buildScanError(buildSummaryService::onBuildScanFailed)
        gradle.buildScanPublished(buildSummaryService::onBuildScanPublished)
        gradle.projectsEvaluated(buildSummaryService::onProjectsEvaluated)
        gradle.buildWorkFinished(buildSummaryService::onBuildWorkFinished)
        gradle.finished(buildSummaryService::onExit)
    }

    private fun Gradle.buildWorkFinished(action: BuildWorkResult.()->Unit) {
        flowScope.always(BuildFinishedAction::class) {
            parameters.action.set(action)
            parameters.buildResult.set(flowProviders.buildWorkResult)
        }
    }

    private fun Gradle.finished(action: ()->Unit) {
        settingsEvaluated {
            val x =  extensions.findByName("develocity")
            if (x is DevelocityConfigurationInternal) {
                x.buildScan.buildScanPublished{action()}
                x.buildScan.onError {action()}
            }
            else {
                buildWorkFinished {action()}
            }
        }
    }

    private fun Gradle.buildScanError(action: (String)->Unit) {
        settingsEvaluated {
            val x =  extensions.findByName("develocity")
            if (x is DevelocityConfigurationInternal) {
                x.buildScan.onError(action)
            }
        }
    }

    private fun Gradle.buildScanPublished(action: PublishedBuildScan.() -> Unit) {
        settingsEvaluated {
            val x =  extensions.findByName("develocity")
            if (x is DevelocityConfigurationInternal) {
                x.buildScan.buildScanPublished(action)
            }
        }
    }
}


abstract class BuildSummaryService: BuildService<BuildServiceParameters.None>{

    private val summary = Summary()

    private fun log(message: String) = println("#################### ${message.uppercase(Locale.getDefault())} ####################")

    fun onProjectsEvaluated(gradle: Gradle) {
        log("projects evaluated")
        summary.rootProject = gradle.rootProject.name
        summary.tasks = gradle.startParameter.taskNames.toList()
        summary.gradleVersion = gradle.gradleVersion
    }

    fun onBuildWorkFinished(buildResult: BuildWorkResult) {
        log("build finished")
        summary.hasBuildFailed = buildResult.failure.isPresent
    }

    fun onBuildScanFailed(errorMessage: String) {
        log("build scan failed")
        summary.hasPublishFailed = true
    }

    fun onBuildScanPublished(publishedBuildScan: PublishedBuildScan) {
        log("build scan published")
        summary.buildScanUrl = publishedBuildScan.buildScanUri.toASCIIString()
        summary.hasPublishFailed = false
    }

    fun onExit() {
        println("#".repeat(120))
        println(MarkdownRenderer(summary.toBuildSummary()).render())
        println("#".repeat(120))
    }
}


private class Summary {
    var rootProject: String? = null
    var tasks: List<String> = emptyList()
    var gradleVersion: String? = null
    var hasBuildFailed: Boolean? = null
    var buildScanUrl: String = ""
    var hasPublishFailed: Boolean = false

    fun toBuildSummary(): BuildSummary {
        require(rootProject?.isNotBlank() ?: false)
        require(tasks.isNotEmpty())
        require(gradleVersion?.isNotBlank() ?: false)
        require(hasBuildFailed != null)
        return BuildSummary(rootProject!!, tasks, gradleVersion!!, hasBuildFailed!!, buildScanUrl, hasPublishFailed)
    }
}

private class BuildFinishedAction: FlowAction<BuildFinishedAction.Parameters> {
    interface Parameters: FlowParameters {
        @get:Input val buildResult: Property<BuildWorkResult>
        @get:Input val action: Property<BuildWorkResult.()->Unit>
    }

    override fun execute(parameters: Parameters) {
        parameters.action.get().invoke(parameters.buildResult.get())
    }
}
