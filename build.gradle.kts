plugins {
	alias(main.plugins.kotlin)
}

group = "io.github.jacquarde"
version = "0.1"

tasks.wrapper {
	distributionType	= Wrapper.DistributionType.ALL
	gradleVersion		= main.versions.gradle.get()
}

kotlin {
	jvmToolchain {
		languageVersion	= JavaLanguageVersion.of(main.versions.jvm.get())
		vendor			= JvmVendorSpec.GRAAL_VM
	}
}

dependencies {}

tasks.test {
	useJUnitPlatform()
}
