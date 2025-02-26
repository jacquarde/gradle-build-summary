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


package org.eu.jacquarde.extensions


import java.util.Optional


/**
 * Waits for this process to complete adding a grace time after.
 *
 * @see java.util.concurrent.CompletableFuture.get
 */
fun Optional<ProcessHandle>.waitWithDelay(milliseconds: Long): Unit {
	wait()
	Thread.sleep(milliseconds)
}

/**
 * Waits if necessary for this future to complete.
 *
 * @see java.util.concurrent.CompletableFuture.get
 */
fun Optional<ProcessHandle>.wait() {
	ifPresent {
		it.onExit().get()
	}
}
