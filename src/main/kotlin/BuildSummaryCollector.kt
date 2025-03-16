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
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.LogLevel
import org.gradle.internal.logging.text.StyledTextOutput.Style
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf

//private val summary = object : Summary() {}

internal class BuildSummaryCollector(
        private val onFinished:      (BuildSummaryConfiguration)->Writer,
) {


    fun xx(gradleLifecycle: GradleLifecycle, configuration: BuildSummaryConfiguration) {
        val summary         = SummaryOld()
        lateinit var pro : String
        lateinit var buildDirectory: Path
        println("###### BuildSummaryCollector ######")
        gradleLifecycle.apply {
            onSettingsEvaluated = {settings ->
                pro = settings.rootProject.name
                println("1) $pro (!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                summary.rootProject = settings.rootProject.name
                summary.tasks       = settings.startParameter.taskNames.toList()
                summary.gradleVersion = settings.gradle.gradleVersion
            }
            onProjectsEvaluated = {projects ->
//                println("2)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                buildDirectory = projects.buildDirectory
                val log = projects.serviceOf<StyledTextOutputFactory>().create("BuildSummary", LogLevel.LIFECYCLE)
                log.withStyle(Style.Success).println("2) $pro (!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//                summary.gradleVersion = projects.gradleVersion
            }
            onBuildFinished = {buildResult ->
                println("3) $pro (!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ${buildResult.failure.isPresent}")
                summary.hasBuildFailed = buildResult.failure.isPresent
            }
            onBuildScanError = {
                println("4.1)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                summary.hasPublishFailed = true
            }
//            onBuildScanPublished = { publishedBuildScan ->
//                println("4.2)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//                summary.hasPublishFailed = false
//                summary.buildScanUrl     = publishedBuildScan.buildScanUri.toString()
//            }
            onExit = {
                println("5) $pro (!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//                summary.toBuildSummary()
//                        .also { println(it) }
//                        .let{onFinished(configuration).write(it, buildDirectory.resolve(configuration.fileName.get())) }
            }
        }.xx()
    }


    private val Gradle.buildDirectory: Path get() =
        rootProject.layout.buildDirectory
                .ensureIsCreated()
}

private fun DirectoryProperty.ensureIsCreated() =
        Files.createDirectories(this.toPath)

private val DirectoryProperty.toPath: Path
    get() =
        get().asFile.toPath()


private class SummaryOld {
    var rootProject:      String?      = null
    var tasks:            List<String> = emptyList()
    var gradleVersion:    String?      = null
    var hasBuildFailed:   Boolean?     = null
    var buildScanUrl:     String       = ""
    var hasPublishFailed: Boolean      = false

    fun toBuildSummary(): BuildSummary =
            BuildSummary(rootProject!!, tasks, gradleVersion!!, hasBuildFailed!!, buildScanUrl, hasPublishFailed)
}
