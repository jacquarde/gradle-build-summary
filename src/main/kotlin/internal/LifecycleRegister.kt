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


import com.gradle.develocity.agent.gradle.internal.DevelocityConfigurationInternal
import java.net.URL
import java.net.URLClassLoader
import org.gradle.api.invocation.Gradle
import org.gradle.api.plugins.ExtensionContainer


public object LifecycleRegister {

    private var lifecycle: LifecycleService? = null

    fun setup(
            target: Gradle,
            lifecycleService: LifecycleService,
    ) {
        lifecycle = lifecycleService
        target.settingsEvaluated {
            notify(LifecycleEvent.SettingsEvaluated(this))
            pluginManager.withPlugin("com.gradle.develocity") {
                extensions.configure("develocity",
                        onPublished = {scanBuildUrl -> notify(LifecycleEvent.BuildScanPublished(scanBuildUrl))},
                        onFailed    = {notify(LifecycleEvent.BuildScanFailed)}
                )
            }
        }
    }

    internal fun notify(event: LifecycleEvent) {
        lifecycle?.onEvent(event)
    }

    private fun ExtensionContainer.configure(name: String, onPublished: (String) -> Unit, onFailed: () -> Unit) {
        with(getByName(name)::class.java.classLoader) {
            addClassPathsOf(LifecycleRegister::class.java.classLoader)
            loadClass(ScanRegister::class.java.name).constructors[0].newInstance(
                    getByName("develocity"),
                    onPublished,
                    onFailed
            )
        }
    }
}


public class ScanRegister public constructor(
        develocityConfiguration: DevelocityConfigurationInternal,
        onPublished: (String)->Unit,
        onError:()->Unit,
) {
    init {
        with(develocityConfiguration.buildScan) {
            buildScanPublished { onPublished(buildScanUri.toASCIIString()) }
            onError { onError() }
            onErrorInternal { onError() }
        }
    }
}


private fun ClassLoader.addClassPathsOf(classloader: ClassLoader?) {
    val m = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java).apply { isAccessible = true }
    (classloader as URLClassLoader).urLs.forEach {
        m.invoke(this, it)
    }
}
