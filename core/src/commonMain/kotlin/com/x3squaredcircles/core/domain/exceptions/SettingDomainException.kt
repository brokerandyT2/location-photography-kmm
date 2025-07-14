// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/SettingDomainException.kt
package com.x3squaredcircles.core.domain.exceptions
/**

Exception thrown when setting domain business rules are violated
 */
class SettingDomainException(
    val code: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    /**

    Initializes a new instance of the SettingDomainException class
     */
    constructor(code: String, message: String) : this(code, message, null)
}