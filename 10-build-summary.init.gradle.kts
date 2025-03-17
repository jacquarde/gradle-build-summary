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

import org.eu.jacquarde.gradle.plugins.buildsummary.*
import org.eu.jacquarde.gradle.plugins.buildsummary.renderers.*


initscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.eu.jacquarde:gradle-build-summary:0.2-beta-3")
    }
}

/**
 * Generates a build summary file in markdown to be used in a GitHub action step summary.
 *
 * The file is located by default in `<root-project>/build/build-summary.md`.
 * To add it to a job summary, include this in a step of the workflow:
 * 
 *     `run: cat $GITHUB_WORKSPACE/build/build-summary.md >> $GITHUB_STEP_SUMMARY`
 * 
 * @see https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/workflow-commands-for-github-actions#example-of-adding-a-job-summary
 */
apply<BuildSummaryPlugin>()

/**
 * Configura the build summary plugin
 *
 * Default values are commented
 *
 */
configure<BuildSummaryConfiguration> {
//    fileName = "build-summary.md"
//    renderer = MarkdownBadgeRenderer()
//    activeIf = {System.getenv("CI") != null}
    script   = initscript.sourceFile
}
