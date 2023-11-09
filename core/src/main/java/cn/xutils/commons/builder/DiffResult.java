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
package cn.xutils.commons.builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A {@link DiffResult} contains a collection of the differences between two
 * {@link cn.xutils.commons.builder.Diffable} objects. Typically these differences are displayed using
 * {@link #toString()} method, which returns a string describing the fields that
 * differ between the objects.
 *
 * <p>
 * Use a {@link DiffBuilder} to build a {@link DiffResult} comparing two objects.
 * </p>
 * @param <T> type of the left and right object.
 *
 * @since 3.3
 */
public class DiffResult<T> implements Iterable<cn.xutils.commons.builder.Diff<?>> {

    /**
     * The {@link String} returned when the objects have no differences:
     * {@value}
     *
     */
    public static final String OBJECTS_SAME_STRING = "";

    private static final String DIFFERS_STRING = "differs from";

    private final List<cn.xutils.commons.builder.Diff<?>> diffList;
    private final T lhs;
    private final T rhs;
    private final cn.xutils.commons.builder.ToStringStyle style;

    /**
     * Creates a {@link DiffResult} containing the differences between two
     * objects.
     *
     * @param lhs
     *            the left-hand object
     * @param rhs
     *            the right-hand object
     * @param diffList
     *            the list of differences, may be empty
     * @param style
     *            the style to use for the {@link #toString()} method. May be
     *            {@code null}, in which case
     *            {@link cn.xutils.commons.builder.ToStringStyle#DEFAULT_STYLE} is used
     * @throws NullPointerException if {@code lhs}, {@code rhs} or {@code diffs} is {@code null}
     */
    DiffResult(final T lhs, final T rhs, final List<cn.xutils.commons.builder.Diff<?>> diffList,
            final cn.xutils.commons.builder.ToStringStyle style) {
        Objects.requireNonNull(lhs, "lhs");
        Objects.requireNonNull(rhs, "rhs");
        Objects.requireNonNull(diffList, "diffList");

        this.diffList = diffList;
        this.lhs = lhs;
        this.rhs = rhs;

        if (style == null) {
            this.style = cn.xutils.commons.builder.ToStringStyle.DEFAULT_STYLE;
        } else {
            this.style = style;
        }
    }

    /**
     * Returns the object the right object has been compared to.
     *
     * @return the left object of the diff
     * @since 3.10
     */
    public T getLeft() {
        return this.lhs;
    }

    /**
     * Returns the object the left object has been compared to.
     *
     * @return the right object of the diff
     * @since 3.10
     */
    public T getRight() {
        return this.rhs;
    }

    /**
     * Returns an unmodifiable list of {@link cn.xutils.commons.builder.Diff}s. The list may be empty if
     * there were no differences between the objects.
     *
     * @return an unmodifiable list of {@link cn.xutils.commons.builder.Diff}s
     */
    public List<cn.xutils.commons.builder.Diff<?>> getDiffs() {
        return Collections.unmodifiableList(diffList);
    }

    /**
     * Returns the number of differences between the two objects.
     *
     * @return the number of differences
     */
    public int getNumberOfDiffs() {
        return diffList.size();
    }

    /**
     * Returns the style used by the {@link #toString()} method.
     *
     * @return the style
     */
    public cn.xutils.commons.builder.ToStringStyle getToStringStyle() {
        return style;
    }

    /**
     * Builds a {@link String} description of the differences contained within
     * this {@link DiffResult}. A {@link cn.xutils.commons.builder.ToStringBuilder} is used for each object
     * and the style of the output is governed by the {@link cn.xutils.commons.builder.ToStringStyle}
     * passed to the constructor.
     *
     * <p>
     * If there are no differences stored in this list, the method will return
     * {@link #OBJECTS_SAME_STRING}. Otherwise, using the example given in
     * {@link Diffable} and {@link cn.xutils.commons.builder.ToStringStyle#SHORT_PREFIX_STYLE}, an output
     * might be:
     * </p>
     *
     * <pre>
     * Person[name=John Doe,age=32] differs from Person[name=Joe Bloggs,age=26]
     * </pre>
     *
     * <p>
     * This indicates that the objects differ in name and age, but not in
     * smoking status.
     * </p>
     *
     * <p>
     * To use a different {@link cn.xutils.commons.builder.ToStringStyle} for an instance of this class,
     * use {@link #toString(cn.xutils.commons.builder.ToStringStyle)}.
     * </p>
     *
     * @return a {@link String} description of the differences.
     */
    @Override
    public String toString() {
        return toString(style);
    }

    /**
     * Builds a {@link String} description of the differences contained within
     * this {@link DiffResult}, using the supplied {@link cn.xutils.commons.builder.ToStringStyle}.
     *
     * @param style
     *            the {@link cn.xutils.commons.builder.ToStringStyle} to use when outputting the objects
     *
     * @return a {@link String} description of the differences.
     */
    public String toString(final ToStringStyle style) {
        if (diffList.isEmpty()) {
            return OBJECTS_SAME_STRING;
        }

        final cn.xutils.commons.builder.ToStringBuilder lhsBuilder = new cn.xutils.commons.builder.ToStringBuilder(lhs, style);
        final cn.xutils.commons.builder.ToStringBuilder rhsBuilder = new ToStringBuilder(rhs, style);

        diffList.forEach(diff -> {
            lhsBuilder.append(diff.getFieldName(), diff.getLeft());
            rhsBuilder.append(diff.getFieldName(), diff.getRight());
        });

        return String.format("%s %s %s", lhsBuilder.build(), DIFFERS_STRING, rhsBuilder.build());
    }

    /**
     * Returns an iterator over the {@link cn.xutils.commons.builder.Diff} objects contained in this list.
     *
     * @return the iterator
     */
    @Override
    public Iterator<Diff<?>> iterator() {
        return diffList.iterator();
    }
}
