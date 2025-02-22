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
	alias(main.plugins.kotlin.dsl)
	alias(main.plugins.kotlinx.serialization)
}


group = "io.github.jacquarde"
version = "0.1"


kotlin {
	jvmToolchain {
		languageVersion = JavaLanguageVersion.of(main.versions.jvm.get())
		vendor = JvmVendorSpec.GRAAL_VM
	}
	sourceSets {
		main {
			dependencies {
				implementation(project.dependencies.gradleApi())
			}
		}
		shared {
			dependencies {
				implementation(libs.kotlinx.serialization)
				implementation(libs.kotlinx.serialization.cbor)
				implementation(libs.arrow.resilience)
				implementation(libs.kotlinx.coroutines)
			}
		}
		testFunctional {
			dependencies {
				implementation(libs.kotest)
				implementation(project.dependencies.gradleTestKit())
				implementation(project.sourceSets.shared.output)
				runtimeOnly(project.sourceSets.shared.runtimeClasspath)
			}
		}
	}
}

gradlePlugin {
	plugins {
		create("buildSummaryPlugin") {
			id = "io.github.jacquarde.gradle.plugins.buildSummary"
			implementationClass = "io.github.jacquarde.gradle.plugins.BuildSummaryPlugin"
		}
	}
	testSourceSets(sourceSets.testFunctional)
}

tasks {
	val testFunctional by registering(Test::class) {
		group = "verification"
		testClassesDirs = sourceSets.testFunctional.output.classesDirs
		classpath = sourceSets.testFunctional.runtimeClasspath
		useJUnitPlatform()
	}
	check {
		dependsOn(testFunctional)
	}
	test {
		useJUnitPlatform()
	}
	wrapper {
		distributionType = Wrapper.DistributionType.ALL
		gradleVersion = main.versions.gradle.get()
	}
}
