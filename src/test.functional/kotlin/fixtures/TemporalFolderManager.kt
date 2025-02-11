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


@file:UseSerializers(PathSerializer::class)


package io.github.jacquarde.gradle.plugins.fixtures


import java.nio.file.Files
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createFile
import kotlin.io.path.deleteRecursively
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers


@Serializable
class TemporalFolderManager(
		private val tag: String = "temp",
) {

	private val temporalRootFolder		= System.getProperty("java.io.tmpdir")
	private val namePrefix get()		= "$tag-$timeStamp"
	private val timeStamp get()			= OffsetDateTime.now(ZoneOffset.UTC).format(dateTimeFormatter)
	private val dateTimeFormatter get()	= DateTimeFormatter.ofPattern("uuuuMMdd.HHmmss.nnnnnnnnn")

	private val createdFolders = mutableSetOf<Path>()

	init {
		afterShutdown {
			cleanUpFolders()
		}
	}

	fun createFolder(name: String): Path =
			Path.of(temporalRootFolder, "$namePrefix-$name")
					.also(Files::createDirectory)
					.also(createdFolders::add)

	@OptIn(ExperimentalPathApi::class)
	fun cleanUpFolders(): Unit =
			createdFolders
					.forEach(Path::deleteRecursively)
					.also(createdFolders::clear)
}


fun Path.createFile(name: String): Path =
		resolve(name).createFile()


// TODO: move to shared utils package in another source set
private fun Unit.also(block: ()->Unit) = block()
