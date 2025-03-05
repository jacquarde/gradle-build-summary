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

@file:Suppress("KDocMissingDocumentation", "UnstableApiUsage")


package org.eu.jacquarde.gradle.plugins


import javax.inject.Inject
import org.gradle.api.Plugin
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.api.invocation.Gradle
import org.eu.jacquarde.gradle.plugins.writers.MarkdownRenderer


abstract class BuildSummaryPlugin @Inject constructor(
        private val flowScope: FlowScope,
        private val flowProviders: FlowProviders,
): Plugin<Gradle> {

    public override fun apply(gradle: Gradle) {
        BuildSummaryCollector(gradle, flowScope, flowProviders) {buildSummary ->
            println("#".repeat(120))
            println(MarkdownRenderer(buildSummary).render())
            println("#".repeat(120))
        }
    }
}
