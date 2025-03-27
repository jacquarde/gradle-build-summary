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


package org.eu.jacquarde.gradle.plugins.buildsummary.renderers


import org.eu.jacquarde.gradle.plugins.buildsummary.BuildSummary


class MarkdownRenderer: BuildSummaryRenderer {

    override fun render(buildSummary: BuildSummary): String =
        with(buildSummary) {
            "$id$buildOutcome **$rootProject**$taskList ┃ _Gradle $gradleVersion${buildScan}_  "
        }

    override fun getId(string: String): Int =
            """^\[\]\((\d+)\)""".toRegex()
                    .findGroupsIn(string)
                    .firstOrNull()
                    ?.toInt() ?: -1

    private val BuildSummary.id           get() = "[]($invocationId)"
    private val BuildSummary.buildOutcome get() = if (hasBuildFailed) "✖" else "✔"
    private val BuildSummary.taskList     get() = if (tasks.isEmpty()) "" else " `${tasks.joinToString(" ")}`"
    private val BuildSummary.buildScan    get() =
            when (publishStatus) {
                BuildSummary.PublishStatus.Published     -> " [BuildScan](${buildScanUrl})"
                BuildSummary.PublishStatus.PublishFailed -> " ~~BuildScan~~"
                else                                     -> ""
            }
}


//region Private extensions

private fun Regex.findGroupsIn(string: String): List<String> =
        find(string)?.groupValues?.drop(1) ?: emptyList()

//endregion
