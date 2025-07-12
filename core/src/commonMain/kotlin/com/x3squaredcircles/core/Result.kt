// core/src/commonMain/kotlin/com/x3squaredcircles/core/Result.kt
package com.x3squaredcircles.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

/**
 * A discriminated union that encapsulates a successful outcome with a value of type [T]
 * or a failure with an error message.
 */
@Serializable
sealed class Result<out T> {
    
    /**
     * Represents a successful result containing a value of type [T].
     */
    @Serializable
    data class Success<out T>(val data: T) : Result<T>()
    
    /**
     * Represents a failed result containing an error message.
     */
    @Serializable
    data class Failure<out T>(val errorMessage: String, @Contextual val exception: Throwable? = null) : Result<T>()
    
    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = this is Success
    
    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = this is Failure
    
    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isFailure].
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }
    
    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or
     * the result of [onFailure] function for the encapsulated error message if it is [failure][Result.isFailure].
     */
    inline fun getOrElse(onFailure: (errorMessage: String) -> @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Failure -> onFailure(errorMessage)
    }
    
    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or
     * throws the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Failure -> throw exception ?: RuntimeException(errorMessage)
    }
    
    /**
     * Performs the given [action] on the encapsulated value if this instance represents [success][Result.isSuccess].
     * Returns the original `Result` unchanged.
     */
    inline fun onSuccess(action: (value: T) -> Unit): Result<T> {
        if (isSuccess) action(getOrNull()!!)
        return this
    }
    
    /**
     * Performs the given [action] on the encapsulated error message if this instance represents [failure][Result.isFailure].
     * Returns the original `Result` unchanged.
     */
    inline fun onFailure(action: (errorMessage: String) -> Unit): Result<T> {
        if (isFailure) action((this as Failure).errorMessage)
        return this
    }
    
    /**
     * Returns the result of applying [transform] to the encapsulated value if this instance represents [success][Result.isSuccess]
     * or returns the original encapsulated error if this instance represents [failure][Result.isFailure].
     */
    inline fun <R> map(transform: (value: T) -> R): Result<R> = when (this) {
        is Success -> success(transform(data))
        is Failure -> failure(errorMessage, exception)
    }
    
    /**
     * Returns the result of applying [transform] to the encapsulated value if this instance represents [success][Result.isSuccess]
     * or returns the original encapsulated error if this instance represents [failure][Result.isFailure].
     * 
     * This is the monadic bind operation for Result.
     */
    inline fun <R> flatMap(transform: (value: T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(data)
        is Failure -> failure(errorMessage, exception)
    }
    
    companion object {
        /**
         * Creates a [Result.Success] instance with the specified [value].
         */
        fun <T> success(value: T): Result<T> = Success(value)
        
        /**
         * Creates a [Result.Failure] instance with the specified [errorMessage] and optional [exception].
         */
        fun <T> failure(errorMessage: String, exception: Throwable? = null): Result<T> = 
            Failure(errorMessage, exception)
        
        /**
         * Creates a [Result] by catching any exception thrown by [block] and encapsulating it as a failure.
         */
        inline fun <T> runCatching(block: () -> T): Result<T> = try {
            success(block())
        } catch (e: Throwable) {
            failure(e.message ?: "An unknown error occurred", e)
        }
        
        /**
         * Creates a [Result] by catching any exception thrown by [block] and encapsulating it as a failure.
         * This is the suspending version for coroutines.
         */
        suspend inline fun <T> runCatchingSuspend(crossinline block: suspend () -> T): Result<T> = try {
            success(block())
        } catch (e: Throwable) {
            failure(e.message ?: "An unknown error occurred", e)
        }
    }
}

/**
 * Extension function to convert a nullable value to a Result.
 * Returns [Result.Success] if the value is not null, or [Result.Failure] with the provided error message if it is null.
 */
fun <T : Any> T?.toResult(errorMessage: String = "Value is null"): Result<T> = 
    if (this != null) Result.success(this) else Result.failure(errorMessage)

/**
 * Extension function to convert a boolean to a Result.
 * Returns [Result.Success] with Unit if true, or [Result.Failure] with the provided error message if false.
 */
fun Boolean.toResult(errorMessage: String = "Condition failed"): Result<Unit> = 
    if (this) Result.success(Unit) else Result.failure(errorMessage)