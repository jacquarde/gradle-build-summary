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


import java.io.File
import kotlin.reflect.jvm.jvmName
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.create
import org.eu.jacquarde.gradle.plugins.buildsummary.renderers.BuildSummaryRenderer
import org.eu.jacquarde.gradle.plugins.buildsummary.renderers.MarkdownRenderer


abstract class BuildSummaryConfiguration {

    private val summaryFileName = "build-summary.md"

    companion object {
        fun createExtensionIn(target: Gradle): BuildSummaryConfiguration =
                target.extensions
                        .create<BuildSummaryConfiguration>(BuildSummaryConfiguration::class.jvmName)
    }

    abstract val renderer       : Property<BuildSummaryRenderer>
    abstract val fileName       : Property<String>
    abstract val activeIf       : Property<()->Boolean>
    abstract val script         : Property<File>
    abstract val excludeIfTasks : ListProperty<String>

    fun createConvention(): BuildSummaryConfiguration =
            apply {
                renderer.convention(MarkdownRenderer())
                fileName.convention(summaryFileName)
                activeIf.convention {inCi()}
                excludeIfTasks.convention(listOf("wrapper", "properties", "clean"))
            }

    private fun inCi(): Boolean =
            System.getenv("CI") != null
}
