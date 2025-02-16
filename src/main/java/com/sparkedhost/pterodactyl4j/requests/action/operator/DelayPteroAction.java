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

package com.sparkedhost.pterodactyl4j.requests.action.operator;

import com.sparkedhost.pterodactyl4j.PteroAction;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

// big thanks to JDA for this tremendous code

public class DelayPteroAction<T> extends PteroActionOperator<T, T> {

    private final TimeUnit unit;
    private final long delay;
    private final ScheduledExecutorService scheduler;

    public DelayPteroAction(PteroAction<T> action, TimeUnit unit, long delay, ScheduledExecutorService scheduler) {
        super(action);
        this.unit = unit;
        this.delay = delay;
        this.scheduler = scheduler == null ? action.getP4J().getRateLimitPool() : scheduler;
    }

    @Override
    public void executeAsync(Consumer<? super T> success, Consumer<? super Throwable> failure) {
        action.executeAsync((result) ->
                        scheduler.schedule(() ->
                                doSuccess(success, result), delay, unit), failure);
    }

    @Override
    public T execute(boolean shouldQueue) {
        T result = action.execute(shouldQueue);
        try {
            unit.sleep(delay);
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}