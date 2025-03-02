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


package org.eu.jacquarde.gradle.plugins.writers


import org.intellij.lang.annotations.Language
import org.eu.jacquarde.gradle.plugins.BuildSummary


class MarkdownRenderer(
        private val buildSummary: BuildSummary,
): BuildSummaryRenderer {

    @Language("Markdown")
    override fun render() =
        with(buildSummary) {
            "$buildOutcome **$rootProject** `$taskList` ┃ _Gradle $gradleVersion${buildScan}_  "
        }

    private val buildOutcome = if (buildSummary.hasBuildFailed) "✖" else "✔"
    private val taskList     = buildSummary.tasks.joinToString(" ")
    private val buildScan    =
            when (buildSummary.publishStatus) {
                BuildSummary.PublishStatus.Published     -> " [BuildScan](${buildSummary.buildScanUrl})"
                BuildSummary.PublishStatus.PublishFailed -> " ~~BuildScan~~"
                else                                     -> ""
            }
}
