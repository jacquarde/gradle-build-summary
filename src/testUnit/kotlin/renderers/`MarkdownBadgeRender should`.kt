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


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.eu.jacquarde.gradle.plugins.buildsummary.BuildSummary

class `MarkdownBadgeRender should`: StringSpec({

    "generate a markdown for a successful `BuildSummary` without build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "root-project",
                tasks          = listOf(":build"),
                gradleVersion  = "8.12.1",
                hasBuildFailed = false
        )

        val actualMarkdown = MarkdownBadgeRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe """
            ![](https://img.shields.io/badge/✔_root--project_-:build-0A0?&style=flat-square)
            ![](https://img.shields.io/badge/8.12.1-555?&style=flat-square&logo=Gradle)  
		""".trimIndent()
    }

    "generate a markdown for a failed `BuildSummary` without build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "another-root-project",
                tasks          = listOf(":check-all"),
                gradleVersion  = "8.12.1-rc2",
                hasBuildFailed = true
        )

        val actualMarkdown = MarkdownBadgeRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe """
            ![](https://img.shields.io/badge/❌_another--root--project_-:check--all-F55?&style=flat-square)
            ![](https://img.shields.io/badge/8.12.1--rc2-555?&style=flat-square&logo=Gradle)  
		""".trimIndent()
    }

    "generate a markdown for a successful `BuildSummary` with build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "project",
                tasks          = listOf(":build"),
                gradleVersion  = "8.12",
                hasBuildFailed = false,
                buildScanUrl   = "test://buildscan"
        )

        val actualMarkdown = MarkdownBadgeRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe """
            ![](https://img.shields.io/badge/✔_project_-:build-0A0?&style=flat-square)
            [![](https://img.shields.io/badge/8.12-BuildScan-06A0CE?&style=flat-square&logo=Gradle)](test://buildscan)  
		""".trimIndent()
    }

    "generate a markdown for a successful `BuildSummary` failing to publish build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject      = "root-project",
                tasks            = listOf(":build"),
                gradleVersion    = "8.9",
                hasBuildFailed   = false,
                hasPublishFailed = true
        )

        val actualMarkdown = MarkdownBadgeRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe """
            ![](https://img.shields.io/badge/✔_root--project_-:build-0A0?&style=flat-square)
            ![](https://img.shields.io/badge/8.9-BuildScan_failed-F55?&style=flat-square&logo=Gradle)  
		""".trimIndent()
    }

    "generate a markdown for a successful `BuildSummary` with multiple tasks." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "root-project",
                tasks          = listOf(":clean", ":check", ":build"),
                gradleVersion  = "8.12.1",
                hasBuildFailed = false
        )

        val actualMarkdown = MarkdownBadgeRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe """
            ![](https://img.shields.io/badge/✔_root--project_-:clean_:check_:build-0A0?&style=flat-square)
            ![](https://img.shields.io/badge/8.12.1-555?&style=flat-square&logo=Gradle)  
		""".trimIndent()
    }

})
