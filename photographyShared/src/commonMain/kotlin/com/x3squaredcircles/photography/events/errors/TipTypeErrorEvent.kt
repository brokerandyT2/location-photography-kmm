// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/events/errors/TipTypeErrorEvent.kt
package com.x3squaredcircles.photography.events.errors

/**
 * Defines the types of tip type errors that can occur
 */
enum class TipTypeErrorType {
    DuplicateName,
    TipTypeNotFound,
    TipTypeInUse,
    InvalidName,
    DatabaseError
}

/**
 * Event representing tip type-specific errors that occurred during processing
 */
class TipTypeErrorEvent(
    /**
     * The name of the tip type that caused the error
     */
    val tipTypeName: String,
    /**
     * The ID of the tip type (null for new tip types)
     */
    val tipTypeId: Int?,
    /**
     * The specific type of error that occurred
     */
    val errorType: TipTypeErrorType,
    /**
     * Additional context about the error
     */
    val additionalContext: String? = null
) : DomainErrorEvent("TipTypeCommandHandler") {

    override fun getResourceKey(): String {
        return when (errorType) {
            TipTypeErrorType.DuplicateName -> "TipType_Error_DuplicateName"
            TipTypeErrorType.TipTypeNotFound -> "TipType_Error_NotFound"
            TipTypeErrorType.TipTypeInUse -> "TipType_Error_InUse"
            TipTypeErrorType.InvalidName -> "TipType_Error_InvalidName"
            TipTypeErrorType.DatabaseError -> "TipType_Error_DatabaseError"
        }
    }

    override fun getParameters(): Map<String, Any> {
        val parameters = mutableMapOf<String, Any>(
            "TipTypeName" to tipTypeName
        )

        tipTypeId?.let {
            parameters["TipTypeId"] = it
        }

        additionalContext?.let {
            if (it.isNotBlank()) {
                parameters["AdditionalContext"] = it
            }
        }

        return parameters
    }
}