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


data class BuildSummary(
		val invocationId:	  Int,
		val rootProject:      String,
		val tasks:            List<String>,
		val gradleVersion:    String,
		val hasBuildFailed:   Boolean,
		val buildScanUrl:     String  = "",
		val hasPublishFailed: Boolean = false,
) {
	enum class PublishStatus { NotPublished, Published, PublishFailed }

	val publishStatus: PublishStatus get() =
			when {
				hasPublishFailed       -> PublishStatus.PublishFailed
				buildScanUrl.isBlank() -> PublishStatus.NotPublished
				else                   -> PublishStatus.Published
			}
}
