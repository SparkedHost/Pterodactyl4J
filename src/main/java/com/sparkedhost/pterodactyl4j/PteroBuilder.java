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

package com.sparkedhost.pterodactyl4j;

import com.sparkedhost.pterodactyl4j.application.entities.PteroApplication;
import com.sparkedhost.pterodactyl4j.client.entities.PteroClient;
import com.sparkedhost.pterodactyl4j.entities.P4J;
import com.sparkedhost.pterodactyl4j.entities.impl.P4JImpl;
import com.sparkedhost.pterodactyl4j.utils.Checks;
import com.sparkedhost.pterodactyl4j.utils.NamedThreadFactory;
import okhttp3.OkHttpClient;

import java.util.concurrent.*;

/**
 * Used to create new {@link PteroApplication} or {@link PteroClient} instances.
 */
public class PteroBuilder {

    private String applicationUrl;
    private String token;
    private String userAgent;

    private OkHttpClient httpClient = null;
    private ExecutorService actionPool = null;
    private ExecutorService callbackPool = null;
    private ScheduledExecutorService rateLimitPool = null;
    private ExecutorService supplierPool = null;
    private OkHttpClient webSocketClient = null;

    private PteroBuilder(String applicationUrl, String token, String userAgent) {
        this.applicationUrl = applicationUrl;
        this.token = token;
        this.userAgent = userAgent;
    }

    @Deprecated
    public PteroBuilder() {
        throw new UnsupportedOperationException("You cannot use the deprecated constructor anymore. Please use create(...), createApplication(...), or createClient(...) instead.");
    }

    /**
     * Creates a {@link PteroApplication} instance with the recommended default settings.
     *
     * <br>Note that these defaults can potentially change in the future.
     *
     * <p>You should use this method if you'd like to continue using P4J as is.
     *
     * @param url
     *        The URL for your panel
     *
     * @param  token
     *         The Application API key
     *
     * @return A new {@link PteroApplication} instance
     *
     */
    public static PteroApplication createApplication(String url, String token, String userAgent) {
        return new PteroBuilder(url, token, userAgent).buildApplication();
    }

    /**
     * Creates a {@link PteroClient} instance with the recommended default settings.
     * <br>Note that these defaults can potentially change in the future.
     *
     * <p>You should use this method if you'd like to continue using P4J as is.
     *
     * @param url
     *        The URL for your panel
     *
     * @param  token
     *         The Client API key
     *
     * @param userAgent
     *        The user agent string
     *
     * @return A new {@link PteroClient} instance
     *
     */
    public static PteroClient createClient(String url, String token, String userAgent) {
        return new PteroBuilder(url, token, userAgent).buildClient();
    }

    public static PteroClient createClient(String url, String token) {
        return new PteroBuilder(url, token, P4JInfo.DEFAULT_USER_AGENT).buildClient();
    }

    /**
    * Creates a PteroBuilder with the predefined panel URL and API key.
     *
     * @param url
     *        The URL for your panel
     *
     * @param  token
     *         The API key
     *
     * @param userAgent
     *        The user agent string
     *
     * @return The new PteroBuilder
     **/
    public static PteroBuilder create(String url, String token, String userAgent) {
        return new PteroBuilder(url, token, userAgent);
    }

    public static PteroBuilder create(String url, String token) {
        return new PteroBuilder(url, token, P4JInfo.DEFAULT_USER_AGENT);
    }

    /**
     * Sets the panel URL that will be used when P4J makes a Request
     *
     * @param  applicationUrl
     *         The URL of the Pterodactyl panel
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
        return this;
    }

    /**
     * Sets the API key that will be used when P4J makes a Request
     *
     * @param  token
     *         The API key for the user or application
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    /**
     * Sets the user agent string that will be used when P4J makes a Request
     *
     * @param userAgent
     *        The user agent string
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Sets the {@link okhttp3.OkHttpClient OkHttpClient} that will be used by P4Js requester.
     *
     * <br>This can be used to set things such as connection timeout and proxy.
     *
     * @param  client
     *         The new {@link okhttp3.OkHttpClient OkHttpClient} to use
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setHttpClient(OkHttpClient client) {
        this.httpClient = client;
        return this;
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used in the P4J request handler.
     *
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used to queue the request and finalize its request body for {@link PteroAction#executeAsync()} tasks.
     *
     * <p>Default: {@link ThreadPoolExecutor} with 1 thread.
     *
     * @param  pool
     *         The thread pool to use for action handling
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setActionPool(ExecutorService pool) {
        this.actionPool = pool;
        return this;
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used in
     * the P4J callback handler which consists of {@link PteroAction PteroAction} callbacks.
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used to handle callbacks of {@link PteroAction#executeAsync()}, similarly it is used to
     * finish {@link PteroAction#execute()} tasks which build on queue.
     *
     * <p>Default: {@link ForkJoinPool#commonPool()}
     *
     * @param  pool
     *         The thread pool to use for callback handling
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setCallbackPool(ExecutorService pool) {
        this.callbackPool = pool;
        return this;
    }

    /**
     * Sets the {@link ScheduledExecutorService ScheduledExecutorService} that should be used in
     * the P4J rate limiter. Changing this can affect the P4J behavior for PteroAction execution
     * and should be handled carefully.
     *
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used by the rate limiter to handle backoff delays by using scheduled executions.
     *
     * <p>Default: {@link ScheduledThreadPoolExecutor} with 5 threads.
     *
     * @param  pool
     *         The thread pool to use for rate limiting
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setRateLimitPool(ScheduledExecutorService pool) {
        this.rateLimitPool = pool;
        return this;
    }

    /**
     * Sets the {@link ExecutorService ExecutorService} that should be used in
     * the P4J Action CompletableFutures.
     *
     * <br><b>Only change this pool if you know what you're doing.</b>
     *
     * <p>This is used to execute Suppliers mainly used by PteroActions that aren't requests.
     *
     * <p>Default: {@link ThreadPoolExecutor} with 3 threads.
     *
     * @param  pool
     *         The thread pool to use for CompletableFutures
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setSupplierPool(ExecutorService pool) {
        this.supplierPool = pool;
        return this;
    }

    /**
     * Sets the {@link okhttp3.OkHttpClient OkHttpClient} that will be used by P4Js websocket client.
     * <br>This can be used to set things such as connection timeout and proxy.
     *
     * @param  client
     *         The new {@link okhttp3.OkHttpClient OkHttpClient} to use
     *
     * @return The PteroBuilder instance. Useful for chaining.
     */
    public PteroBuilder setWebSocketClient(OkHttpClient client) {
        this.webSocketClient = client;
        return this;
    }

    /**
     * The URL of the Pterodactyl panel that is currently being used with P4J.
     *
     * @return The panel URL
     */
    public String getApplicationUrl() {
        return this.applicationUrl;
    }

    /**
     * The API key that is currently being used for P4J authentication.
     *
     * @return The API key
     */
    public String getToken() {
        return this.token;
    }

    /**
     * The user agent string that is currently being used with P4J.
     *
     * @return The user agent string
     */
    public String getUserAgent() {
        return this.userAgent;
    }

    private P4J build() {
        Checks.notBlank(token, "API Key");
        Checks.notBlank(applicationUrl, "Application URL");
        Checks.notBlank(userAgent, "User Agent");
        if (httpClient == null)
            this.httpClient = new OkHttpClient();
        if (callbackPool == null)
            this.callbackPool = ForkJoinPool.commonPool();
        if (actionPool == null)
            this.actionPool = Executors.newSingleThreadExecutor(new NamedThreadFactory("Action"));
        if (rateLimitPool == null)
            this.rateLimitPool = Executors.newScheduledThreadPool(5, new NamedThreadFactory("RateLimit"));
        if (supplierPool == null)
            this.supplierPool = Executors.newFixedThreadPool(3, new NamedThreadFactory("Supplier"));
        if (webSocketClient == null)
            this.webSocketClient = new OkHttpClient();
        return new P4JImpl(this.applicationUrl, this.token, this.userAgent, this.httpClient, this.callbackPool, this.actionPool,
                this.rateLimitPool, this.supplierPool, this.webSocketClient);
    }

    /**
     * Builds a new {@link PteroApplication PteroApplication} instance
     * and uses the provided panel URL and application API key to make requests.
     *
     * <p>This provides access to the <b>Application API</b>. Use a {@link PteroClient PteroClient} if you need access
     * to the <b>Client API</b>.
     *
     * @throws IllegalArgumentException
     *         If the provided URL or token is empty or null.
     *
     * @return A PteroApplication instance that is ready to execute requests.
     *
     * @see PteroBuilder#buildClient()
     */
    public PteroApplication buildApplication() {
        return build().asApplication();
    }

    /**
     * Builds a new {@link PteroClient PteroClient} instance
     * and uses the provided panel URL and client API key to make requests and offer websocket access.
     *
     * <p>This provides access to the <b>Client API</b>. Use a
     * {@link PteroApplication PteroApplication} if you need access
     * to the <b>Application API</b>.
     *
     * @throws IllegalArgumentException
     *         If the provided URL or token is empty or null.
     *
     * @return A PteroClient instance that is ready to execute requests.
     *
     * @see PteroBuilder#buildApplication()
     */
    public PteroClient buildClient() { return build().asClient(); }
}
