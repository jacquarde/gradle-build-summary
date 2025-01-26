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
	alias(main.plugins.kotlin)
}


group	= "io.github.jacquarde"
version	= "0.1"


kotlin {
	jvmToolchain {
		languageVersion	= JavaLanguageVersion.of(main.versions.jvm.get())
		vendor			= JvmVendorSpec.GRAAL_VM
	}
	sourceSets {
		test {
			kotlin.srcDirs("src/test.functional/kotlin")
			resources.srcDirs("src/test.functional/resources")
		}
	}
	dependencies {
		testImplementation(libs.kotest)
	}
}

tasks {
	test {
		useJUnitPlatform()
	}
	wrapper {
		distributionType	= Wrapper.DistributionType.ALL
		gradleVersion		= main.versions.gradle.get()
	}
}
