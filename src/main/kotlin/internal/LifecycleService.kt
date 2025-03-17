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


package org.eu.jacquarde.gradle.plugins.buildsummary.internal


import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.eu.jacquarde.gradle.plugins.buildsummary.BuildSummary
import org.eu.jacquarde.gradle.plugins.buildsummary.Writer


public abstract class LifecycleService:
        BuildService<LifecycleService.Parameters>
{

    interface Parameters: BuildServiceParameters {
        val rootProject: Property<String>
        val tasks: ListProperty<String>
        val gradleVersion: Property<String>
        val hasBuildFailed: Property<Boolean>
        val builsScanUrl: Property<String>
        val hasBuildScanaFailed: Property<Boolean>
    }

    fun onEvent(lifecycle: LifecycleEvent) {
            when (lifecycle) {
                is LifecycleEvent.SettingsEvaluated -> {
                    parameters.rootProject.set(lifecycle.settings.rootProject.name)
                    parameters.tasks.set(lifecycle.settings.startParameter.taskNames.toList())
                    parameters.gradleVersion.set(lifecycle.settings.gradle.gradleVersion)
                }
                is LifecycleEvent.BuildFinished -> {
                    parameters.hasBuildFailed.set(lifecycle.buildResult.failure.isPresent)
                    writeSummary()
                }
                is LifecycleEvent.BuildScanPublished -> {
                    parameters.hasBuildScanaFailed.set(false)
                    parameters.builsScanUrl.set(lifecycle.buildScanUrl)
                    writeSummary()
                }
                is LifecycleEvent.BuildScanFailed -> {
                    parameters.hasBuildScanaFailed.set(true)
                    writeSummary()
                }
            }
    }

    private fun writeSummary() {
        Writer.write(
                BuildSummary(
                        invocationId = this.hashCode(),
                        parameters.rootProject.get(),
                        parameters.tasks.get().toList(),
                        parameters.gradleVersion.get(),
                        parameters.hasBuildFailed.get(),
                        parameters.builsScanUrl.get(),
                        parameters.hasBuildScanaFailed.get()
                )
        )
    }
}
