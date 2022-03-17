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

package com.sparkedhost.pterodactyl4j.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// i love you jda
public class LockUtils {

    public static void locked(ReentrantLock lock, Runnable task) {
        try {
            tryLock(lock);
            task.run();
        } finally {
            if (lock.isHeldByCurrentThread())
                lock.unlock();
        }
    }

    public static void tryLock(Lock lock) {
        try {
            if (!lock.tryLock() && !lock.tryLock(10, TimeUnit.SECONDS))
                throw new IllegalStateException("Could not acquire lock in a reasonable timeframe! (10 seconds)");
        } catch (InterruptedException e) {
            throw new IllegalStateException("Unable to acquire lock while thread is interrupted!");
        }
    }

}
