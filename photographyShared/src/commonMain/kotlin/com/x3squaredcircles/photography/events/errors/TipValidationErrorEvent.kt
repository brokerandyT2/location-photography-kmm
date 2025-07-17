// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/errors/TipValidationErrorEvent.kt
package com.x3squaredcircles.photography.events.errors

/**
 * Event representing tip-specific validation errors that occurred during processing
 */
class TipValidationErrorEvent(
    /**
     * The ID of the tip that failed validation (null for new tips)
     */
    val tipId: Int?,
    /**
     * The tip type ID associated with the validation error
     */
    val tipTypeId: Int,
    /**
     * Dictionary of validation errors grouped by property name
     */
    val validationErrors: Map<String, List<String>>,
    source: String
) : DomainErrorEvent(source) {

    /**
     * Constructor that accepts a collection of Error objects
     */
    constructor(tipId: Int?, tipTypeId: Int, errors: List<Error>, source: String) : this(
        tipId,
        tipTypeId,
        errors.filter { !it.message.isNullOrBlank() }
            .groupBy { it.message!! }
            .mapValues { entry -> entry.value.map { it.message!! } },
        source
    )

    override fun getResourceKey(): String {
        return if (validationErrors.size == 1) {
            "Tip_Validation_Error_Single"
        } else {
            "Tip_Validation_Error_Multiple"
        }
    }

    override fun getParameters(): Map<String, Any> {
        val parameters = mutableMapOf<String, Any>(
            "TipTypeId" to tipTypeId,
            "ErrorCount" to validationErrors.size
        )

        tipId?.let {
            parameters["TipId"] = it
        }

        if (validationErrors.size == 1) {
            val firstError = validationErrors.entries.first()
            parameters["PropertyName"] = firstError.key
            parameters["ErrorMessage"] = firstError.value.first()
        }

        return parameters
    }

    override val severity: ErrorSeverity = ErrorSeverity.Warning
}