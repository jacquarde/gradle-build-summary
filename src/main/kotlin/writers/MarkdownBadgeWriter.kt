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


import org.eu.jacquarde.gradle.plugins.BuildSummary


// SEE: https://shields.io/badges/static-badge
class MarkdownBadgeWriter(
        private val buildSummary: BuildSummary,
) {

    fun write() =
            "$buildBadge$newLine$publishBadge$markdownHardLineBreak"

    private val buildBadge get() =
        "![](${shieldBadgeUrl}/${buildOutcome}${rootProject}-${tasks}${buildColor}?${badgeStyle})"

    private val gradleBadge  get() =
        "![](${shieldBadgeUrl}/${version}${label}${color}?${badgeStyle}${badgeLogo})"

    private val publishBadge get() =
        when (buildSummary.publishStatus) {
            BuildSummary.PublishStatus.Published -> "[$gradleBadge]($buildScanUrl)"
            else                                 -> gradleBadge
        }

    private val badgeLogo             = "&logo=Gradle"
    private val badgeStyle            = "&style=flat-square"
    private val markdownHardLineBreak = "  "
    private val newLine               = "\n"
    private val shieldBadgeUrl        = "https://img.shields.io/badge"

    private val buildOutcome = if (buildSummary.hasBuildFailed) "❌" else "✔"
    private val buildColor   = if (buildSummary.hasBuildFailed) "-F55" else "-0A0"
    private val buildScanUrl = buildSummary.buildScanUrl
    private val rootProject  = " ${buildSummary.rootProject} ".scape()
    private val tasks        = buildSummary.tasks.joinToString(separator = " ").scape()
    private val version      = buildSummary.gradleVersion.scape()

    private val label =
            when (buildSummary.publishStatus) {
                BuildSummary.PublishStatus.Published     -> "-BuildScan"
                BuildSummary.PublishStatus.PublishFailed -> "-BuildScan_failed"
                BuildSummary.PublishStatus.NotPublished  -> ""
            }

    private val color =
            when (buildSummary.publishStatus) {
                BuildSummary.PublishStatus.Published     -> "-06A0CE"
                BuildSummary.PublishStatus.PublishFailed -> "-F55"
                BuildSummary.PublishStatus.NotPublished  -> "-555"
            }

    private fun String.scape() = replace("-" to "--", "_" to "__", " " to "_")
}


private fun String.replace(vararg replacements: Pair<String, String>): String =
        replacements.fold(this) {string, (old, new) ->
            string.replace(old, new)
        }
