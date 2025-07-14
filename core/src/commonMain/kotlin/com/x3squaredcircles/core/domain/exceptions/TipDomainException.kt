// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/TipDomainException.kt
package com.x3squaredcircles.core.domain.exceptions
/**

Exception thrown when tip domain business rules are violated
 */
class TipDomainException(
    val code: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    /**

    Initializes a new instance of the TipDomainException class
     */
    constructor(code: String, message: String) : this(code, message, null)
}