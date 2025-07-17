// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/Result.kt
package com.x3squaredcircles.core.domain.common

sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure<T>(val error: String, val exception: Throwable? = null) : Result<T>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> failure(error: String, exception: Throwable? = null): Result<T> = Failure(error, exception)
    }
}