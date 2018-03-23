/*
 * Copyright 2018 the original author or the original authors
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

package fr.myprysm.pipeline;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;
import io.vertx.junit5.Checkpoint;

import java.util.List;
import java.util.Objects;

public interface QuickRxAssert {
    default <T> Consumer<TestObserver<T>> assertEquals(T value, Checkpoint cp) {
        return assertOf(check -> check.assertValue(value), cp);
    }


    default <T> Consumer<TestObserver<List<T>>> assertSize(int size, Checkpoint cp) {
        return assertOf(check -> check.assertValue(list -> list.size() == size), cp);
    }

    default <T> Consumer<TestObserver<T>> assertOf(Consumer<TestObserver<T>> consumer, Checkpoint cp) {
        return check -> {
            check.assertOf(consumer);
            if (cp != null) {
                cp.flag();
            }
        };
    }

    default <T> Consumer<TestObserver<T>> assertComplete(Checkpoint cp) {
        return assertOf(TestObserver::assertComplete, cp);
    }

    default <T, E extends Throwable> Consumer<TestObserver<T>> assertError(Checkpoint cp) {
        return assertError(null, null, cp);
    }

    default <T, E extends Throwable> Consumer<TestObserver<T>> assertError(Class<E> clazz, Checkpoint cp) {
        return assertError(clazz, null, cp);
    }

    default <T, E extends Throwable> Consumer<TestObserver<T>> assertError(String message, Checkpoint cp) {
        return assertError(null, message, cp);
    }

    default <T, E extends Throwable> Consumer<TestObserver<T>> assertError(Class<E> clazz, String message, Checkpoint cp) {
        return assertOf(check -> {
            if (clazz != null) {
                check.assertError(clazz);
            } else {
                check.assertError(Objects::nonNull);
            }

            if (message != null) {
                check.assertErrorMessage(message);
            }
        }, cp);
    }
}
