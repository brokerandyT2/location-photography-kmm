// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/errors/ValidationErrorEvent.kt
package com.x3squaredcircles.photography.events.errors



/**
 * Event representing validation errors that occurred during processing
 */
class ValidationErrorEvent(
    /**
     * The type of entity that failed validation
     */
    val entityType: String,
    /**
     * Dictionary of validation errors grouped by property name
     */
    val validationErrors: Map<String, List<String>>,
    source: String
) : DomainErrorEvent(source) {

    /**
     * Constructor that accepts a collection of Error objects
     */
    constructor(entityType: String, errors: List<Error>, source: String) : this(
        entityType,
        errors.filter { !it.propertyName.isNullOrBlank() }
            .groupBy { it.propertyName!! }
            .mapValues { entry -> entry.value.map { it.message } },
        source
    )

    override fun getResourceKey(): String {
        return if (validationErrors.size == 1) {
            "Validation_Error_Single"
        } else {
            "Validation_Error_Multiple"
        }
    }

    override fun getParameters(): Map<String, Any> {
        val parameters = mutableMapOf<String, Any>(
            "EntityType" to entityType,
            "ErrorCount" to validationErrors.size
        )

        if (validationErrors.size == 1) {
            val firstError = validationErrors.entries.first()
            parameters["PropertyName"] = firstError.key
            parameters["ErrorMessage"] = firstError.value.first()
        }

        return parameters
    }

    override val severity: ErrorSeverity = ErrorSeverity.Warning
}