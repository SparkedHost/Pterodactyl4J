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

package com.mattmalec.pterodactyl4j.requests;

import com.mattmalec.pterodactyl4j.P4JInfo;
import com.mattmalec.pterodactyl4j.entities.P4J;
import com.mattmalec.pterodactyl4j.exceptions.HttpException;
import com.mattmalec.pterodactyl4j.exceptions.LoginException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Requester {

    private final P4J api;
    private final Logger REQUESTER_LOG = LoggerFactory.getLogger(Requester.class);

    public static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0], null);

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf8");
    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain; charset=utf8");
    public static final MediaType MEDIA_TYPE_OCTET = MediaType.parse("application/octet-stream; charset=utf-8");

    private static final String PTERODACTYL_API_PREFIX = "%s/api/";

    public static String USER_AGENT = "";

    private final RateLimiter rateLimiter;
    private final OkHttpClient client;

    public Requester(P4J api) {
        this.api = api;
        this.rateLimiter = new RateLimiter(this, api);
        this.client = api.getHttpClient();
        USER_AGENT = api.getUserAgent();
    }

    public <T> void request(Request<T> request) {
        if (request.shouldQueue())
            rateLimiter.queueRequest(request);
        else execute(request, true);
    }

    public Long execute(Request<?> apiRequest) {
        return execute(apiRequest, false);
    }

    public Long execute(Request<?> request, boolean handleOnRateLimit) {
        return execute(request, false, handleOnRateLimit);
    }

    public Long execute(Request<?> apiRequest, boolean retried, boolean handleOnRateLimit) {

        Route.CompiledRoute route = apiRequest.getRoute();
        Long retryAfter = rateLimiter.getRateLimit();

        if (retryAfter > 0) {
            if (handleOnRateLimit)
                apiRequest.handleResponse(new Response(retryAfter));
            return retryAfter;
        }

        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

        if (api.getApplicationUrl() == null || api.getApplicationUrl().isEmpty())
            throw new HttpException("No Pterodactyl URL was defined.");
        String applicationUrl = api.getApplicationUrl();
        if (applicationUrl.endsWith("/"))
            applicationUrl = applicationUrl.substring(0, applicationUrl.length() - 1);
        String url = String.format(PTERODACTYL_API_PREFIX, applicationUrl) + apiRequest.getRoute().getCompiledRoute();

        builder.url(url);
        String method = route.getMethod().toString();
        if (apiRequest.getRequestBody() != null)
            builder.method(method, apiRequest.getRequestBody());
        else if (HttpMethod.requiresRequestBody(method))
            builder.method(method, EMPTY_BODY);
        else
            builder.method(method, null);

        builder.header("Accept", "application/vnd.pterodactyl.v1+json")
                .header("User-Agent", USER_AGENT);

        if (api.getToken() == null || api.getToken().isEmpty())
            throw new LoginException("No authorization token was defined.");
        builder.header("Authorization", "Bearer " + api.getToken());

        okhttp3.Request request = builder.build();

        okhttp3.Response[] responses = new okhttp3.Response[4];
        okhttp3.Response lastResponse = null;

        try {
            REQUESTER_LOG.debug("Executing request {} {}", route.getMethod(), route.getCompiledRoute());
            int attempt = 0;
            do {
                if (apiRequest.isSkipped())
                    return null;

                Call call = client.newCall(request);
                lastResponse = call.execute();
                responses[attempt] = lastResponse;

                if (lastResponse.code() < 500)
                    break;

                attempt++;
                REQUESTER_LOG.debug("Requesting {} -> {} returned status {}... retrying (attempt {})",
                        route.getMethod(),
                        route.getCompiledRoute(), lastResponse.code(), attempt);
                try {
                    Thread.sleep(50L * attempt);
                } catch (InterruptedException ignored) {}
            } while (attempt < 3 && lastResponse.code() >= 500);

            REQUESTER_LOG.trace("Finished Request {} {} with code {}", route.getMethod(), lastResponse.request().url(), lastResponse.code());

            if (lastResponse.code() >= 500) {
                // epic fucking fail
                Response response = new Response(lastResponse, -1);
                apiRequest.handleResponse(response);
                return null;
            }

            retryAfter = rateLimiter.handleResponse(apiRequest, lastResponse);

            if (retryAfter == null)
                apiRequest.handleResponse(new Response(lastResponse, -1));
            else if (handleOnRateLimit)
                apiRequest.handleResponse(new Response(lastResponse, retryAfter));

            return retryAfter;
        } catch (SocketTimeoutException e) {
            if (!retried)
                return execute(apiRequest, true, handleOnRateLimit);
            REQUESTER_LOG.error("Requester timed out while executing a request {}", e.getMessage());
            apiRequest.handleResponse(new Response(lastResponse, e));
            return null;
        } catch (Exception e) {
            if (!retried && isRetry(e))
                return execute(apiRequest, true, handleOnRateLimit);
            if (e.getMessage() == null)
                REQUESTER_LOG.error("There was an exception while executing a request");
            else
                REQUESTER_LOG.error("{}", e.getMessage());
            apiRequest.handleResponse(new Response(lastResponse, e));
            return null;
        } finally {
            for (okhttp3.Response r : responses) {
                if (r == null)
                    break;
                r.close();
            }
        }
    }

    private static boolean isRetry(Throwable e) {
        return e instanceof SocketException                 // Socket couldn't be created or access failed
                || e instanceof SocketTimeoutException      // Connection timed out
                || e instanceof SSLPeerUnverifiedException; // SSL Certificate was wrong
    }
}