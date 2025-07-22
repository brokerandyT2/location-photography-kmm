// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/ExposureArrays.kt
package com.x3squaredcircles.photography.models

data class ExposureArrays(
    val apertures: Array<String> = emptyArray(),
    val isos: Array<String> = emptyArray(),
    val shutterSpeeds: Array<String> = emptyArray(),
    val evCompensation: Array<String> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ExposureArrays

        if (!apertures.contentEquals(other.apertures)) return false
        if (!isos.contentEquals(other.isos)) return false
        if (!shutterSpeeds.contentEquals(other.shutterSpeeds)) return false
        if (!evCompensation.contentEquals(other.evCompensation)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = apertures.contentHashCode()
        result = 31 * result + isos.contentHashCode()
        result = 31 * result + shutterSpeeds.contentHashCode()
        result = 31 * result + evCompensation.contentHashCode()
        return result
    }
}