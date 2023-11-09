/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.xutils.commons.function;

import java.util.function.DoubleToLongFunction;

/**
 * A functional interface like {@link DoubleToLongFunction} that declares a {@link Throwable}.
 *
 * @param <E> The kind of thrown exception or error.
 * @since 3.11
 */
@FunctionalInterface
public interface FailableDoubleToLongFunction<E extends Throwable> {

    /** NOP singleton */
    @SuppressWarnings("rawtypes")
    FailableDoubleToLongFunction NOP = t -> 0;

    /**
     * Returns The NOP singleton.
     *
     * @param <E> The kind of thrown exception or error.
     * @return The NOP singleton.
     */
    @SuppressWarnings("unchecked")
    static <E extends Throwable> FailableDoubleToLongFunction<E> nop() {
        return NOP;
    }

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     * @throws E if the operation fails
     */
    int applyAsLong(double value) throws E;
}
