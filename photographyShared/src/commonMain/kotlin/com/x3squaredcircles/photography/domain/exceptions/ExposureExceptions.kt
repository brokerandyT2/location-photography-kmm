// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/exceptions/ExposureExceptions.kt
package com.x3squaredcircles.photography.domain.exceptions

abstract class ExposureError(message: String, cause: Throwable? = null) : Exception(message, cause)

class OverexposedError(val stops: Double) : ExposureError("Overexposed by $stops stops")

class UnderexposedError(val stops: Double) : ExposureError("Underexposed by $stops stops")

class ExposureParameterLimitError(
    val parameter: String,
    val actualValue: String,
    val limitValue: String
) : ExposureError("$parameter value $actualValue exceeds limit of $limitValue")