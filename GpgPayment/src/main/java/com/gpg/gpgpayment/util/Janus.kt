/*
 * *
 *  * Created by orpheus(Saber Oueslati) of GPG on 10/20/21, 11:04 AM
 *  * Copyright (c) 2021 . All rights reserved.
 *  * Last modified 10/20/21, 10:59 AM
 *
 */

package com.gpg.gpgpayment.util

class UnwrapException(message: String) : Exception(message)

fun <V, E> Janus<V, E>.getOrElse(default: V) = when (this) {
    is Ok<V> -> value
    is Err<E> -> default
}

fun <V, E> Janus<V, E>.getOrElseTransform(transformError: (E) -> V) = when (this) {
    is Ok<V> -> value
    is Err<E> -> transformError(error)
}

/**
 * Maps this [Result<V, E>][Janus] to [Result<U, E>][Janus] by either applying the [transform]
 * function if this [Janus] is [Ok], or returning this [Err].
 */
fun <V, V2, E> Janus<V, E>.andThen(transformValue: (V) -> Janus<V2, E>): Janus<V2, E> = when (this) {
    is Ok<V> -> transformValue(value)
    is Err<E> -> this
}

suspend fun <V, V2, E> Janus<V, E>.andThenThread(transformValue: suspend (V) -> Janus<V2, E>): Janus<V2, E> = when (this) {
    is Ok<V> -> transformValue(value)
    is Err<E> -> this
}

fun <V, E, E2> Janus<V, E>.andThenError(transformError: (E) -> Janus<V, E2>): Janus<V, E2> = when (this) {
    is Ok<V> -> this
    is Err<E> -> transformError(error)
}

sealed class Janus<out V, out E> {
    fun getOrThrow(): V = when (this) {
        is Ok -> value
        is Err -> throw UnwrapException("Cannot unwrap $this")
    }

    fun <V2> map(transformValue: (V) -> V2): Janus<V2, E> = when (this) {
        is Ok -> Ok<V2>(transformValue(value))
        is Err -> this
    }

    fun <E2> mapError(transformError: (E) -> E2): Janus<V, E2> = when (this) {
        is Ok -> this
        is Err -> Err<E2>(transformError(error))
    }

    fun <V2> fold(transformValue: (V) -> V2, transformError: (E) -> V2): V2 = when (this) {
        is Ok -> transformValue(value)
        is Err -> transformError(error)
    }

    override fun toString(): String {
        return when (this) {
            is Ok -> "👌Success[data=${this.value}]"
            is Err -> "🆘 Error[exception=${this.error}]"
        }
    }

    companion object {
        // FACTORIES
        private fun <V> ok(value: V): Ok<V> = Ok(value)
        private fun <E> err(error: E): Err<E> = Err(error)

        // MANY
        fun <V, E> all(vararg results: Janus<V, E>) = all(results.asIterable())

        private fun <V, E> all(results: Iterable<Janus<V, E>>): Janus<List<V>, E> {
            return ok(results.map {
                when (it) {
                    is Ok<V> -> it.value
                    is Err<E> ->
                        // Short-circuit
                        return it
                }
            })
        }

        inline fun <V> wrapException(exception: Exception? = null, function: () -> V): Janus<V, Exception> =
            try {
                Ok(function.invoke())
            } catch (e: Exception) {
                if (exception != null) {
                    Err(exception)
                } else {
                    Err(e)
                }
            }
    }
}

class Ok<out V>(val value: V) : Janus<V, Nothing>() {
    override fun toString() = "Janus.Ok($value)"
    override fun hashCode() = value?.hashCode() ?: 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Ok<*>
        if (value != other.value) return false
        return true
    }
}

class Err<out E>(val error: E) : Janus<Nothing, E>() {
    override fun toString() = "Janus.Err($error)"
    override fun hashCode() = error?.hashCode() ?: 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Err<*>
        if (error != other.error) return false
        return true
    }
}