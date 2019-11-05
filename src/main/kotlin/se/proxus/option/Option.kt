/*
 * Copyright 2019 Oliver Berg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package se.proxus.option

@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class Option<out T> @PublishedApi internal constructor(@PublishedApi internal val value: Any?) {
    companion object {
        @PublishedApi
        internal val none: Option<Nothing> = Option(null)

        inline operator fun <T> invoke(value: T?): Option<T> = Option(value)

        inline fun <T> nullable(value: T?): Option<T> = Option(value)

        inline fun <T> some(value: T): Option<T> = Option(value)

        fun <T> none(): Option<T> = none

        inline fun <T> tryCatch(value: () -> T): Option<T> = try {
            Option(value())
        } catch (t: Throwable) {
            require(t is Exception)
            none
        }
    }

    val iterator: Iterator<T>
        get() = when (value) {
            null -> EmptyIterator
            else -> SingletonIterator(value as T)
        }

    val isPresent get() = value != null

    val isEmpty get() = value == null

    val hash: Int get() = value?.hashCode() ?: 0

    inline fun unwrap(): T = when (value) {
        null -> throw NoSuchElementException("Option is empty")
        else -> value as T
    }

    inline fun orNull(): T? = value as T?

    inline fun orElse(default: () -> @UnsafeVariance T): T = when (value) {
        null -> default()
        else -> value as T
    }

    inline fun orElse(default: @UnsafeVariance T): T = value as? T ?: default

    inline fun <X : Exception> orThrow(exception: () -> X): T = when (value) {
        null -> throw exception()
        else -> value as T
    }

    inline fun ifPresent(action: (T) -> Unit) {
        if (value != null) action(value as T)
    }

    inline fun ifEmpty(action: () -> Unit) {
        if (value == null) action()
    }

    inline fun <R> fold(ifEmpty: () -> R, ifPresent: (T) -> R): R = when (value) {
        null -> ifEmpty()
        else -> ifPresent(value as T)
    }

    inline fun <R> map(transformer: (T) -> R?): Option<R> = when (value) {
        null -> none
        else -> Option(transformer(value as T))
    }

    inline fun <R> flatMap(transformer: (T) -> Option<R>): Option<R> = when (value) {
        null -> none
        else -> transformer(value as T)
    }

    inline fun filter(predicate: (T) -> Boolean): Option<T> = when (value) {
        null -> none
        else -> if (predicate(value as T)) Option(value) else none
    }

    inline fun filterNot(predicate: (T) -> Boolean): Option<T> = when (value) {
        null -> none
        else -> if (!predicate(value as T)) Option(value) else none
    }

    inline fun any(predicate: (T) -> Boolean): Boolean = when (value) {
        null -> false
        else -> predicate(value as T)
    }

    inline fun all(predicate: (T) -> Boolean): Boolean = when (value) {
        null -> true
        else -> predicate(value as T)
    }

    inline fun none(predicate: (T) -> Boolean): Boolean = when (value) {
        null -> true
        else -> !predicate(value as T)
    }

    infix fun <U> and(other: Option<U>): Option<U> = if (isEmpty) none else other

    infix fun or(other: Option<@UnsafeVariance T>): Option<T> = if (isPresent) this else other

    operator fun contains(value: @UnsafeVariance T): Boolean = value?.let { it == value } ?: false

    operator fun component1(): T = unwrap()

    /**
     * Returns a [Iterable] that's based on the [iterator] of `this` option.
     */
    fun asIterable(): Iterable<T> = Iterable { iterator }

    /**
     * Returns a [Sequence] that's based on the [iterator] of `this` option.
     */
    fun asSequence(): Sequence<T> = Sequence { iterator }

    override fun toString(): String = when (value) {
        null -> "None"
        else -> "Some[$value]"
    }
}

private object EmptyIterator : Iterator<Nothing> {
    override fun hasNext(): Boolean = false

    override fun next(): Nothing = throw UnsupportedOperationException("Can not iterate over a empty iterator")
}

private class SingletonIterator<out E>(private val item: E) : Iterator<E> {
    private var hasNext: Boolean = true

    override fun hasNext(): Boolean = hasNext

    override fun next(): E = if (hasNext) item.also { hasNext = false } else throw NoSuchElementException()
}