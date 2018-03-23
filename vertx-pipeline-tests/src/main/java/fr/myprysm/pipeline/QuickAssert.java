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

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public interface QuickAssert {
    default <T> Handler<AsyncResult<T>> assertEquals(T value, Checkpoint cp, VertxTestContext ctx) {
        return assertSuccess(val -> assertThat(val).isEqualTo(value), cp, ctx);
    }

    default <T> Handler<AsyncResult<List<T>>> assertSize(int size, Checkpoint cp, VertxTestContext ctx) {
        return assertSuccess(list -> assertThat(list).hasSize(size), cp, ctx);
    }

    default <T, E extends Throwable> Handler<AsyncResult<T>> assertThrows(Class<E> clazz, Checkpoint cp, VertxTestContext ctx) {
        return assertThrows(clazz, null, cp, ctx);
    }

    default <T, E extends Throwable> Handler<AsyncResult<T>> assertThrows(Class<E> clazz, String message, Checkpoint cp, VertxTestContext ctx) {
        return assertFail(throwable -> {
            assertThat(throwable).isInstanceOf(clazz);
            if (message != null) {
                assertThat(throwable.getMessage()).isEqualTo(message);
            }
        }, cp, ctx);
    }


    default <T> Handler<AsyncResult<T>> assertSuccess(Checkpoint cp, VertxTestContext ctx) {
        return assertSuccess(null, cp, ctx);
    }

    default <T> Handler<AsyncResult<T>> assertSuccess(Consumer<T> consumer, Checkpoint cp, VertxTestContext ctx) {
        return ctx.succeeding(item -> ctx.verify(() -> {
            if (consumer != null) {
                consumer.accept(item);
            }

            if (cp != null) {
                cp.flag();
            }
        }));
    }

    default <T> Handler<AsyncResult<T>> assertFail(Checkpoint cp, VertxTestContext ctx) {
        return assertFail(null, cp, ctx);
    }

    /**
     * Assert that the handler will receive an error.
     * <p>
     * Each item except <code>ctx</code> is optional.
     *
     * @param consumer the error consumer for custom assertions
     * @param cp       the checkpoint
     * @param ctx      the context
     * @param <T>      the expected type of the handler
     * @return the assertion handler
     */
    default <T> Handler<AsyncResult<T>> assertFail(Consumer<Throwable> consumer, Checkpoint cp, VertxTestContext ctx) {
        return ctx.failing(throwable -> ctx.verify(() -> {
            if (consumer != null) {
                consumer.accept(throwable);
            }

            if (cp != null) {
                cp.flag();
            }
        }));
    }
}
