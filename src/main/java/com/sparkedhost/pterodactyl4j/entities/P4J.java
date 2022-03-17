/*
 *    Copyright 2021 Matt Malec, and the Pterodactyl4J contributors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sparkedhost.pterodactyl4j.entities;

import com.sparkedhost.pterodactyl4j.application.entities.PteroApplication;
import com.sparkedhost.pterodactyl4j.client.entities.PteroClient;
import com.sparkedhost.pterodactyl4j.requests.Requester;
import okhttp3.OkHttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface P4J {

	String getToken();
	Requester getRequester();
	String getApplicationUrl();
	String getUserAgent();
	OkHttpClient getHttpClient();
	ExecutorService getCallbackPool();
	ExecutorService getActionPool();
	ScheduledExecutorService getRateLimitPool();
	ExecutorService getSupplierPool();
	OkHttpClient getWebSocketClient();
	PteroClient asClient();
	PteroApplication asApplication();

}
