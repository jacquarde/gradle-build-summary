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
//	id("com.gradle.develocity") version("3.19.2")
}


rootProject.name = "gradle-build-summary"


dependencyResolutionManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
	}
	versionCatalogs {
		create("main") {
			version("gradle", "8.12.1")
			version("jvm",    "22")
			plugin ("publish",                "com.gradle.plugin-publish").version("1.2.1")
			plugin ("kotlin.dsl",             "org.gradle.kotlin.kotlin-dsl").version("5.1.2")
		}
		create("libs") {
			version("kotest",                "5.9.1")
			version("gradle",                "3.19.2")
			library("gradle_develocity",          "com.gradle.develocity", "com.gradle.develocity.gradle.plugin").versionRef("gradle")
			library("kotest",                     "io.kotest", "kotest-runner-junit5").versionRef("kotest")
		}
	}
}


buildCache {
	local {
		isEnabled = false
	}
	remote(HttpBuildCache::class) {
		isPush = true
		isAllowUntrustedServer = true
		isAllowInsecureProtocol = true
		url = uri("http://localhost/cache/")
	}
}

//develocity {
//	buildScan {
//		publishing.onlyIf{ System.getenv("CI") != null  }
//		termsOfUseUrl   = "https://gradle.com/help/legal-terms-of-use"
//		termsOfUseAgree = "yes"
//	}
//}
