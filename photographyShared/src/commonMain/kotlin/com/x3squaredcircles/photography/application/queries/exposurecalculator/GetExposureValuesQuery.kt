// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/exposurecalculator/GetExposureValuesQuery.kt
package com.x3squaredcircles.photography.application.queries.exposurecalculator

import com.x3squaredcircles.photography.domain.models.ExposureIncrements

data class GetExposureValuesQuery(
    val increments: ExposureIncrements
)

data class GetExposureValuesQueryResult(
    val shutterSpeeds: Array<String>,
    val apertures: Array<String>,
    val isos: Array<String>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GetExposureValuesQueryResult

        if (!shutterSpeeds.contentEquals(other.shutterSpeeds)) return false
        if (!apertures.contentEquals(other.apertures)) return false
        if (!isos.contentEquals(other.isos)) return false
        if (isSuccess != other.isSuccess) return false
        if (errorMessage != other.errorMessage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shutterSpeeds.contentHashCode()
        result = 31 * result + apertures.contentHashCode()
        result = 31 * result + isos.contentHashCode()
        result = 31 * result + isSuccess.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
}