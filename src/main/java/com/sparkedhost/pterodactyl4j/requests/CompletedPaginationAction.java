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

package com.sparkedhost.pterodactyl4j.requests;

import com.sparkedhost.pterodactyl4j.PteroAction;
import com.sparkedhost.pterodactyl4j.entities.P4J;
import com.sparkedhost.pterodactyl4j.exceptions.RateLimitedException;
import com.sparkedhost.pterodactyl4j.requests.action.impl.PaginationActionImpl;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CompletedPaginationAction<T> extends PaginationActionImpl<T> {

    private final P4J api;
    protected final List<T> value;
    protected final Throwable error;

    public CompletedPaginationAction(P4J api, List<T> value, Throwable error) {
        super(api);
        this.api = api;
        this.value = value;
        this.error = error;
    }

    public CompletedPaginationAction(P4J api, List<T> value) {
        this(api, value, null);
    }

    @Override
    public P4J getP4J() {
        return api;
    }

    @Override
    public List<T> execute(boolean shouldQueue) throws RateLimitedException {
        if (error != null) {
            if (error instanceof RateLimitedException)
                throw (RateLimitedException) error;
            if (error instanceof RuntimeException)
                throw (RuntimeException) error;
            throw new IllegalStateException(error);
        }
        return value;
    }

    @Override
    public void executeAsync(Consumer<? super List<T>> success, Consumer<? super Throwable> failure) {
        if (error == null) {
            if (success == null)
                PteroAction.getDefaultSuccess().accept(value);
            else
                success.accept(value);
        } else {
            if (failure == null)
                PteroAction.getDefaultFailure().accept(error);
            else
                failure.accept(error);
        }
    }

    @Override
    public PaginationIterator<T> iterator() {
        return new PaginationIterator<>(value, Collections::emptyList);
    }
}
