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
	id("com.gradle.develocity") version("3.19.2")
}


rootProject.name = "gradle-build-summary"


dependencyResolutionManagement {
	repositories {
		mavenCentral()
	}
	versionCatalogs {
		create("main") {
			version("gradle", "8.12.1")
			version("jvm",    "22")
			plugin ("kotlin.dsl",             "org.gradle.kotlin.kotlin-dsl").version("5.1.2")
			plugin ("kotlinx.serialization",  "org.jetbrains.kotlin.plugin.serialization").version("2.0.21")
		}
		create("libs") {
			version("kotest",                "5.9.1")
			version("kotlinx.serialization", "1.8.0")
			version("kotlinx.coroutines",    "1.10.1")
			version("ktor",                  "3.1.0")
			version("arrow",                 "2.0.1")
			library("arrow.resilience",           "io.arrow-kt", "arrow-resilience").versionRef("arrow")
			library("kotest",                     "io.kotest", "kotest-runner-junit5").versionRef("kotest")
			library("kotlinx.coroutines",         "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlinx.coroutines")
			library("kotlinx.serialization",      "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef("kotlinx.serialization")
			library("kotlinx.serialization.cbor", "org.jetbrains.kotlinx", "kotlinx-serialization-cbor").versionRef("kotlinx.serialization")
			library("ktor.server.core",           "io.ktor", "ktor-server-core").versionRef("ktor")
			library("ktor.server.netty",          "io.ktor", "ktor-server-netty").versionRef("ktor")
			library("ktor.server.content",        "io.ktor", "ktor-server-content-negotiation").versionRef("ktor")
			library("ktor.server.json",           "io.ktor", "ktor-serialization-kotlinx-json").versionRef("ktor")
			library("jetbrains.annotations",      "org.jetbrains:annotations:26.0.2")

		}
	}
}

develocity {
	buildScan {
		publishing.onlyIf{ System.getenv("CI") != null  }
		termsOfUseUrl   = "https://gradle.com/help/legal-terms-of-use"
		termsOfUseAgree = "yes"
	}
}
