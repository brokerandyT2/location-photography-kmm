// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/TipTypeDomainException.kt
package com.x3squaredcircles.core.domain.exceptions
/**

Exception thrown when tip type domain business rules are violated
 */
class TipTypeDomainException(
    val code: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    /**

    Initializes a new instance of the TipTypeDomainException class
     */
    constructor(code: String, message: String) : this(code, message, null)
}