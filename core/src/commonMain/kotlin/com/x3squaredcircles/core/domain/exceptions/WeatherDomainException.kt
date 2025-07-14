// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/WeatherDomainException.kt
package com.x3squaredcircles.core.domain.exceptions
/**

Exception thrown when weather domain business rules are violated
 */
class WeatherDomainException(
    val code: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    /**

    Initializes a new instance of the WeatherDomainException class
     */
    constructor(code: String, message: String) : this(code, message, null)
}