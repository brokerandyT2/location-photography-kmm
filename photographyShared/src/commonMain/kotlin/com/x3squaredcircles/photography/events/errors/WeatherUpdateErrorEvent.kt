// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/errors/WeatherUpdateErrorEvent.kt
package com.x3squaredcircles.photography.events.errors

/**
 * Defines the types of weather errors that can occur
 */
enum class WeatherErrorType {
    ApiUnavailable,
    InvalidLocation,
    NetworkTimeout,
    InvalidApiKey,
    RateLimitExceeded,
    DatabaseError
}

/**
 * Event representing weather update errors that occurred during processing
 */
class WeatherUpdateErrorEvent(
    /**
     * The location ID associated with the weather update error
     */
    val locationId: Int,
    /**
     * The specific type of error that occurred
     */
    val errorType: WeatherErrorType,
    /**
     * Additional context about the error
     */
    val additionalContext: String? = null
) : DomainErrorEvent("UpdateWeatherCommandHandler") {

    override fun getResourceKey(): String {
        return when (errorType) {
            WeatherErrorType.ApiUnavailable -> "Weather_Error_ApiUnavailable"
            WeatherErrorType.InvalidLocation -> "Weather_Error_InvalidLocation"
            WeatherErrorType.NetworkTimeout -> "Weather_Error_NetworkTimeout"
            WeatherErrorType.InvalidApiKey -> "Weather_Error_InvalidApiKey"
            WeatherErrorType.RateLimitExceeded -> "Weather_Error_RateLimitExceeded"
            WeatherErrorType.DatabaseError -> "Weather_Error_DatabaseError"
        }
    }

    override fun getParameters(): Map<String, Any> {
        val parameters = mutableMapOf<String, Any>(
            "LocationId" to locationId
        )

        additionalContext?.let {
            if (it.isNotBlank()) {
                parameters["AdditionalContext"] = it
            }
        }

        return parameters
    }
}