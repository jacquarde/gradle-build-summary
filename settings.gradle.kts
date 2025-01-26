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


plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}


rootProject.name = "gradle-build-summary"


dependencyResolutionManagement {
	repositories {
		mavenCentral()
	}
	versionCatalogs {
		create("main") {
			version("gradle", "8.12.1")
			version("jvm", "23")
			plugin("kotlin","org.jetbrains.kotlin.jvm").version("2.1.0")
		}
		create("libs") {
			version("kotest", "5.9.1")
			library("kotest", "io.kotest", "kotest-runner-junit5").versionRef("kotest")
		}
	}
}
