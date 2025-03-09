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


import com.gradle.develocity.agent.gradle.internal.DevelocityConfigurationInternal
import com.gradle.develocity.agent.gradle.scan.PublishedBuildScan
import org.gradle.api.flow.BuildWorkResult
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.kotlin.dsl.always


internal abstract class GradleLifecycle(
        private val gradle: Gradle,
        private val flowScope: FlowScope,
        private val flowProviders: FlowProviders,
) {
    init {
        gradle.settingsEvaluated(::onSettingsEvaluated)
        gradle.projectsEvaluated(::onProjectsEvaluated)
        gradle.settingsEvaluated {
            develocity?.buildScan?.onError(::onBuildScanError)
            develocity?.buildScan?.buildScanPublished(::onBuildScanPublished)
        }
        flowScope.always(BuildFinishedAction::class) {
            parameters.buildResult.set(flowProviders.buildWorkResult)
            parameters.action.set(::onBuildFinished)
        }
        gradleFinished()
    }

    private fun gradleFinished() {
        gradle.settingsEvaluated {
            when (val x = extensions.findByName("develocity")) {
                is DevelocityConfigurationInternal -> develocityIsLast(x)
                else                               -> buildIsLast()
            }
        }
    }

    private fun develocityIsLast(e: DevelocityConfigurationInternal) {
        e.buildScan.onError {onExit()}
        e.buildScan.buildScanPublished {onExit()}
    }

    private fun buildIsLast() {
        flowScope.always(BuildFinishedAction::class) {
            parameters.buildResult.set(flowProviders.buildWorkResult)
            parameters.action.set{onExit()}
        }
    }

    private val Settings.develocity: DevelocityConfigurationInternal?
        get() =
            extensions.findByName("develocity") as? DevelocityConfigurationInternal

    public abstract fun onSettingsEvaluated(settings: Settings)
    public abstract fun onProjectsEvaluated(gradle: Gradle)
    public abstract fun onBuildFinished(buildResult: BuildWorkResult)
    public abstract fun onBuildScanError(error: String)
    public abstract fun onBuildScanPublished(publishedBuildScan: PublishedBuildScan)
    public abstract fun onExit()
}


public class BuildFinishedAction
    : FlowAction<BuildFinishedAction.Parameters> {

    public interface Parameters: FlowParameters {
        @get:Input val buildResult: Property<BuildWorkResult>
        @get:Input val action: Property<(BuildWorkResult)->Unit>
    }

    override fun execute(parameters: Parameters) {
        parameters.action.get().invoke(parameters.buildResult.get())
    }
}
