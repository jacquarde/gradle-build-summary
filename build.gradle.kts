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


group	= "io.github.jacquarde"
version	= "0.1"


kotlin {
	jvmToolchain {
		languageVersion	= JavaLanguageVersion.of(main.versions.jvm.get())
		vendor			= JvmVendorSpec.GRAAL_VM
	}
}

modules {
	val main by existing {
		implementation(project.dependencies.gradleApi())
	}
	val shared by registering {
		implementation(libs.kotlinx.serialization)
		implementation(libs.kotlinx.serialization.cbor)
	}
	val testFunctional by registering {
		implementation(shared.output)
		implementation(libs.kotest)
		implementation(project.dependencies.gradleTestKit())
		runtimeOnly(libs.kotlinx.serialization)
		runtimeOnly(libs.kotlinx.serialization.cbor)
	}
}

gradlePlugin {
	plugins {
		create("buildSummaryPlugin") {
			id = "io.github.jacquarde.gradle.plugins.buildSummary"
			implementationClass = "io.github.jacquarde.gradle.plugins.BuildSummaryPlugin"
		}
	}
	testSourceSets(sourceSets.named("testFunctional").get())
}

tasks {
	val testFunctional by registering(Test::class) {
		val testFunctional by sourceSets.getting
		val shared by sourceSets.getting
		group = "verification"
		testClassesDirs = testFunctional.output.classesDirs
		classpath = shared.runtimeClasspath + testFunctional.runtimeClasspath
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
