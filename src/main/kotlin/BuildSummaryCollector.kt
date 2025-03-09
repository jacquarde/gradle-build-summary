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


import com.gradle.develocity.agent.gradle.scan.PublishedBuildScan
import org.gradle.api.flow.BuildWorkResult
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle


internal class BuildSummaryCollector(
        gradle:                 Gradle,
        flowScope:              FlowScope,
        flowProviders:          FlowProviders,
        private val onFinished: (BuildSummary)->Unit,
): GradleLifecycle(gradle, flowScope, flowProviders) {

    private val summary = Summary()

    override fun onSettingsEvaluated(settings: Settings) {
        summary.rootProject = settings.rootProject.name
        summary.tasks       = settings.startParameter.taskNames.toList()
    }

    override fun onProjectsEvaluated(gradle: Gradle) {
        summary.gradleVersion = gradle.gradleVersion
    }

    override fun onBuildFinished(buildResult: BuildWorkResult) {
        summary.hasBuildFailed = buildResult.failure.isPresent
    }

    override fun onBuildScanError(error: String) {
        summary.hasPublishFailed = true
    }

    override fun onBuildScanPublished(publishedBuildScan: PublishedBuildScan) {
        summary.hasPublishFailed = false
        summary.buildScanUrl     = publishedBuildScan.buildScanUri.toString()
    }

    override fun onExit() {
        onFinished(summary.toBuildSummary())
    }
}


private class Summary {
    var rootProject:      String?      = null
    var tasks:            List<String> = emptyList()
    var gradleVersion:    String?      = null
    var hasBuildFailed:   Boolean?     = null
    var buildScanUrl:     String       = ""
    var hasPublishFailed: Boolean      = false

    fun toBuildSummary(): BuildSummary =
            BuildSummary(rootProject!!, tasks, gradleVersion!!, hasBuildFailed!!, buildScanUrl, hasPublishFailed)
}
