// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/LocationDomainException.kt
package com.x3squaredcircles.core.domain.exceptions
/**

Exception thrown when location domain business rules are violated
 */
open class LocationDomainException(
    val code: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    /**

    Initializes a new instance of the LocationDomainException class
     */
    constructor(code: String, message: String) : this(code, message, null)
}