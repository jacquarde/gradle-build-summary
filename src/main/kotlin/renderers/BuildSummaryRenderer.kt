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


/**
 * Functional interface to [render].
 */
interface BuildSummaryRenderer {


    /**
     * Transforms a [BuildSummary] to a [String] so to be consumed.
     */
    fun render(buildSummary: BuildSummary): String

    fun getId(string: String): Int

    fun add(summary: BuildSummary, to: List<String>): List<String> =
            to.withIndex()
                    .associateBy { getId(it.value) }
                    .toMutableMap()
                    .apply {
                        putWithIndex(getId(render(summary)), render(summary))
                    }
                    .values
                    .sortedBy { it.index }
                    .map { it.value }

}


private fun MutableMap<Int, IndexedValue<String>>.putWithIndex(key: Int, value: String) {
    put(key, IndexedValue(get(key)?.index?:Int.MAX_VALUE, value))
}
