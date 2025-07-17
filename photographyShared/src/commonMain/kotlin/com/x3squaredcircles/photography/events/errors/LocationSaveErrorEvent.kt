// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/errors/LocationSaveErrorEvent.kt
package com.x3squaredcircles.photography.events.errors

/**
 * Defines the types of location errors that can occur
 */
enum class LocationErrorType {
    DuplicateTitle,
    InvalidCoordinates,
    NetworkError,
    DatabaseError,
    ValidationError
}

/**
 * Event representing location save errors that occurred during processing
 */
class LocationSaveErrorEvent(
    /**
     * The title of the location that caused the error
     */
    val locationTitle: String,
    /**
     * The specific type of error that occurred
     */
    val errorType: LocationErrorType,
    /**
     * Additional context about the error
     */
    val additionalContext: String? = null
) : DomainErrorEvent("SaveLocationCommandHandler") {

    override fun getResourceKey(): String {
        return when (errorType) {
            LocationErrorType.DuplicateTitle -> "Location_Error_DuplicateTitle"
            LocationErrorType.InvalidCoordinates -> "Location_Error_InvalidCoordinates"
            LocationErrorType.NetworkError -> "Location_Error_NetworkError"
            LocationErrorType.DatabaseError -> "Location_Error_DatabaseError"
            LocationErrorType.ValidationError -> "Location_Error_ValidationError"
        }
    }

    override fun getParameters(): Map<String, Any> {
        val parameters = mutableMapOf<String, Any>(
            "LocationTitle" to locationTitle
        )

        additionalContext?.let {
            if (it.isNotBlank()) {
                parameters["AdditionalContext"] = it
            }
        }

        return parameters
    }
}