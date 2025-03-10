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

@file:Suppress(
		"KDocMissingDocumentation",
		"MemberVisibilityCanBePrivate",
		"HttpUrlsUsage"
)


package stubs


import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlin.concurrent.thread
import kotlinx.serialization.Serializable
import utils.randomId


class GradleBuildScanServer(
        private val host:         String       = "127.0.0.1",
        private val port:         Int          = 8080,
        public  var scanUrl:      String       = "http://$host:$port/s/$scanId",
        public  var responseMode: ResponseMode = ResponseMode.Ok,
): AutoCloseable {

	enum class ResponseMode { Ok, Error }

	val url: String = "http://$host:$port"

	override fun close() {
		server.stop()
	}

	private val server = embeddedServer(Netty, host = host, port = port) {
		install(ContentNegotiation) {json()}
		routing {
			post("/scans/publish/gradle/{$agentVersion}/token")  {call.respondScanAck()}
			post("/scans/publish/gradle/{$agentVersion}/upload") {call.respondScanUploadAck()}
		}
	}

	init {
		thread(start = true, isDaemon = true) {
			server.start(wait = true)
		}
	}

	private suspend fun RoutingCall.respondScanUploadAck() {
		response.header(HttpHeaders.ContentType, "application/vnd.gradle.scan-upload-ack+json")
		respond(ScanUploadResponse())
	}

	private suspend fun RoutingCall.respondScanAck() {
		response.header(HttpHeaders.ContentType, "application/vnd.gradle.scan-ack+json")
		when (responseMode) {
			ResponseMode.Ok    -> respond(stubResponse)
			ResponseMode.Error -> respond(HttpStatusCode.BadRequest)
		}
	}

	private val RoutingCall.stubResponse
		get() = ScanResponse(
				scanUrl       = scanUrl,
				scanUploadUrl = "/scans/publish/gradle/${pathParameters.agentVersion}/upload",
		)

	private val agentVersion                  = "buildAgentVersion"
	private val Parameters.agentVersion get() = get(this@GradleBuildScanServer.agentVersion)
}


@Serializable
private class ScanResponse(
        private val id:              String = scanId,
        private val scanUrl:         String,
        private val scanUploadUrl:   String,
        private val scanUploadToken: String = scanToken,
)


@Serializable
private class ScanUploadResponse


private val scanId    = randomId(length = 13)
private val scanToken = randomId(length = 52)
