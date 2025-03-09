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


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.eu.jacquarde.gradle.plugins.BuildSummary
import org.eu.jacquarde.gradle.plugins.renderers.MarkdownRenderer


class `MarkdownRenderer should`: StringSpec({

    "generate a markdown for a successful `BuildSummary` without build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "root-project",
                tasks          = listOf(":build"),
                gradleVersion  = "8.12.1",
                hasBuildFailed = false
        )

        val actualMarkdown = MarkdownRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe "✔ **root-project** `:build` ┃ _Gradle 8.12.1_  "
    }

    "generate a markdown for a failed `BuildSummary` without build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "another-root-project",
                tasks          = listOf(":check-all"),
                gradleVersion  = "8.12.1-rc2",
                hasBuildFailed = true
        )

        val actualMarkdown = MarkdownRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe "✖ **another-root-project** `:check-all` ┃ _Gradle 8.12.1-rc2_  "
    }

    "generate a markdown for a successful `BuildSummary` with build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "project",
                tasks          = listOf(":build"),
                gradleVersion  = "8.12",
                hasBuildFailed = false,
                buildScanUrl   = "test://buildscan"
        )

        val actualMarkdown = MarkdownRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe "✔ **project** `:build` ┃ _Gradle 8.12 [BuildScan](test://buildscan)_  "
    }

    "generate a markdown for a successful `BuildSummary` failing to publish build scan URL." {

        val givenBuildSummary = BuildSummary(
                rootProject      = "root-project",
                tasks            = listOf(":build"),
                gradleVersion    = "8.9",
                hasBuildFailed   = false,
                hasPublishFailed = true
        )

        val actualMarkdown = MarkdownRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe "✔ **root-project** `:build` ┃ _Gradle 8.9 ~~BuildScan~~_  "
    }

    "generate a markdown for a successful `BuildSummary` with multiple tasks." {

        val givenBuildSummary = BuildSummary(
                rootProject    = "root-project",
                tasks          = listOf(":clean", ":check", ":build"),
                gradleVersion  = "8.12.1",
                hasBuildFailed = false
        )

        val actualMarkdown = MarkdownRenderer().render(givenBuildSummary)

        //language=Markdown
        actualMarkdown shouldBe "✔ **root-project** `:clean :check :build` ┃ _Gradle 8.12.1_  "
    }

})
