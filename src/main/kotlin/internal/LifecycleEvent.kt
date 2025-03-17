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

package org.eu.jacquarde.gradle.plugins.buildsummary.internal


import org.gradle.api.flow.BuildWorkResult
import org.gradle.api.initialization.Settings


@Suppress("UnstableTypeUsedInSignature", "UnstableApiUsage")
sealed interface LifecycleEvent {
    class  SettingsEvaluated  (val settings     : Settings)        :LifecycleEvent
    class  BuildFinished      (val buildResult  : BuildWorkResult) :LifecycleEvent
    class  BuildScanPublished (val buildScanUrl :String)           :LifecycleEvent
    object BuildScanFailed                                         :LifecycleEvent
}
