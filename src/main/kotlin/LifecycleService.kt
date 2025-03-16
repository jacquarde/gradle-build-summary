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


import com.gradle.develocity.agent.gradle.internal.DevelocityConfigurationInternal
import java.net.URL
import java.net.URLClassLoader
import org.gradle.api.Project
import org.gradle.api.flow.BuildWorkResult
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.Input


public abstract class LifecycleService:
        BuildService<LifecycleService.Parameters>
{

    interface Parameters: BuildServiceParameters {
        val rootProject: Property<String>
        val tasks: ListProperty<String>
        val gradleVersion: Property<String>
        val hasBuildFailed: Property<Boolean>
        val builsScanUrl: Property<String>
        val hasBuildScanaFailed: Property<Boolean>
    }

    fun onEvent(lifecycle: LifecycleEvent) {
        println("EVENT  :  ${this.hashCode().toString().take(8)} <== ${lifecycle::class.simpleName}")
            when (lifecycle) {
                is LifecycleEvent.Started           -> {
                }
                is LifecycleEvent.SettingsEvaluated -> {
                    parameters.rootProject.set(lifecycle.settings.rootProject.name)
                    parameters.tasks.set(lifecycle.settings.startParameter.taskNames.toList())
                    parameters.gradleVersion.set(lifecycle.settings.gradle.gradleVersion)
                }
                is LifecycleEvent.ProjectsEvaluated -> {
//                    parameters.hasBuildFailed.set(false)
                }
                is LifecycleEvent.BuildFinished      -> {
                    parameters.hasBuildFailed.set(lifecycle.buildResult.failure.isPresent)
                }
                is LifecycleEvent.BuildFinishedXXX   -> {}
                is LifecycleEvent.BuildScanPublished -> {
                    parameters.hasBuildScanaFailed.set(false)
                    parameters.builsScanUrl.set(lifecycle.buildScanUrl)
                }
                is LifecycleEvent.BuildScanFailed -> {
                    parameters.hasBuildScanaFailed.set(true)
                }
                is LifecycleEvent.Finished          -> {
//                parameters.summary.get().print()
                }
            }
            println("SUMMARY: (${parameters.rootProject.get()}, ${parameters.gradleVersion.get()}, ${parameters.tasks.get()}, ${parameters.hasBuildFailed.get()}, ${parameters.builsScanUrl.get()}, ${parameters.hasBuildScanaFailed.get()})")
    }
}


sealed interface LifecycleEvent {
    class  Started            (val gradle       :Gradle)          :LifecycleEvent
    class  SettingsEvaluated  (val settings     :Settings)        :LifecycleEvent
    class  ProjectsEvaluated  (val rootProject  :Project)         :LifecycleEvent
    class  BuildFinished      (val buildResult  :BuildWorkResult) :LifecycleEvent
    class  BuildFinishedXXX     ()     : LifecycleEvent
    class  BuildScanPublished (val buildScanUrl :String)          :LifecycleEvent
    object BuildScanFailed                                        :LifecycleEvent
    object Finished                                               :LifecycleEvent
}


public object LifecycleRegistry {

    private var lifecycle: LifecycleService? = null

    fun setup(
            target: Gradle,
            lifecycleService: LifecycleService,
    ) {
        lifecycle = lifecycleService
        target.beforeSettings {
            lifecycleService.onEvent(LifecycleEvent.Started(target))
        }
        target.settingsEvaluated {
            pluginManager.withPlugin("com.gradle.develocity") {
                val cl : URLClassLoader = extensions.getByName("develocity")::class.java.classLoader as URLClassLoader
                cl.addClassPathsOf(LifecycleRegistry::class.java.classLoader)
                cl.loadClass(ScanRegistry::class.java.name).constructors[0].newInstance(
                        extensions.getByName("develocity"),
                        {scanBuildUri: String -> notify(LifecycleEvent.BuildScanPublished(scanBuildUri))},
                        {notify(LifecycleEvent.BuildScanFailed)},
                )
            }
            lifecycleService.onEvent(LifecycleEvent.SettingsEvaluated(this))
        }
        target.projectsEvaluated {
            lifecycleService.onEvent(LifecycleEvent.ProjectsEvaluated(this.rootProject))
        }
    }

    public fun notify(event: LifecycleEvent) {
        lifecycle?.onEvent(event)
    }

}

public class ScanRegistry(
        develocityConfiguration: DevelocityConfigurationInternal,
        onPublished: (String)->Unit,
        onError:()->Unit,
) {
    init {
        with(develocityConfiguration.buildScan) {
            buildScanPublished { onPublished(buildScanUri.toASCIIString()) }
            onError { onError() }
        }
    }
}

abstract class XXX: FlowAction<XXX.Parameters> {

    interface Parameters: FlowParameters {
        @get:Input val buildWorkResult: Property<BuildWorkResult>
    }

    override fun execute(parameters: Parameters) {
        LifecycleRegistry.notify(
                LifecycleEvent.BuildFinished(parameters.buildWorkResult.get())
        )
    }
}


/* public abstract class EventListenerService:
        BuildService<BuildServiceParameters.None>,
        OperationCompletionListener,
        AutoCloseable {

    public var lifecycleService: LifecycleService? = null

    override fun onFinish(event: FinishEvent?) = Unit

    override fun close() {
        lifecycleService?.onEvent(LifecycleEvent.Finished)
    }
} */

/* interface Summary
    : BuildService<BuildServiceParameters.None> {

    val rootProject: Property<String>
    val tasks: ListProperty<String>
    val gradleVersion: Property<String>
    val hasBuildFailed: Property<Boolean>
    val buildScanUrl: Property<String>
    val hasPublishFailed: Property<Boolean>

    fun toBuildSummary() =
            BuildSummary(
                    rootProject.get(),
                    tasks.get(),
                    gradleVersion.get(),
                    hasBuildFailed.get(),
                    buildScanUrl.get(),
                    hasPublishFailed.get()
            )
} */

/*
class Summary {
    var rootProject: String? = null
    var tasks: List<String> = emptyList()
    var gradleVersion: String? = null
    var hasBuildFailed: Boolean? = null
    var buildScanUrl: String = ""
    var hasPublishFailed: Boolean = false

    fun toBuildSummary(): BuildSummary =
            BuildSummary(rootProject!!, tasks, gradleVersion!!, hasBuildFailed!!, buildScanUrl, hasPublishFailed)
}
*/

//region Private extensions

public fun ClassLoader.addClassPathsOf(classloader: ClassLoader?) {
    val m = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java).apply { isAccessible = true }
    (classloader as URLClassLoader).urLs.forEach {
        m.invoke(this, it)
    }
}

//endregion
