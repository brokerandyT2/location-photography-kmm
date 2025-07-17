// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ExposureTriangleService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.photography.domain.exceptions.ExposureParameterLimitError
import com.x3squaredcircles.photography.domain.exceptions.OverexposedError
import com.x3squaredcircles.photography.domain.exceptions.UnderexposedError
import com.x3squaredcircles.photography.domain.services.IExposureTriangleService
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

class ExposureTriangleService : IExposureTriangleService {

    companion object {
        private const val LOG2 = 0.6931471805599453
    }

    override fun calculateShutterSpeed(
        baseShutterSpeed: String,
        baseAperture: String,
        baseIso: String,
        targetAperture: String,
        targetIso: String,
        scale: Int,
        evCompensation: Double
    ): String {
        val baseShutterValue = parseShutterSpeed(baseShutterSpeed)
        val baseApertureValue = parseAperture(baseAperture)
        val baseIsoValue = parseIso(baseIso)
        val targetApertureValue = parseAperture(targetAperture)
        val targetIsoValue = parseIso(targetIso)

        val apertureEvDiff = 2 * ln(targetApertureValue / baseApertureValue) / LOG2
        val isoEvDiff = ln(baseIsoValue / targetIsoValue) / LOG2
        val evDiff = apertureEvDiff + isoEvDiff + evCompensation

        val newShutterValue = baseShutterValue * 2.0.pow(evDiff)
        val shutterSpeeds = getShutterSpeedScale(scale)
        val newShutterSpeed = findClosestValue(shutterSpeeds, newShutterValue, ValueType.SHUTTER)

        val maxShutterValue = 30.0
        val minShutterValue = 1.0 / 8000.0

        when {
            newShutterValue > maxShutterValue * 1.5 -> {
                val stopsOver = ln(newShutterValue / maxShutterValue) / LOG2
                throw OverexposedError(stopsOver)
            }
            newShutterValue < minShutterValue / 1.5 -> {
                val stopsUnder = ln(minShutterValue / newShutterValue) / LOG2
                throw UnderexposedError(stopsUnder)
            }
        }

        return newShutterSpeed
    }

    override fun calculateAperture(
        baseShutterSpeed: String,
        baseAperture: String,
        baseIso: String,
        targetShutterSpeed: String,
        targetIso: String,
        scale: Int,
        evCompensation: Double
    ): String {
        val baseShutterValue = parseShutterSpeed(baseShutterSpeed)
        val baseApertureValue = parseAperture(baseAperture)
        val baseIsoValue = parseIso(baseIso)
        val targetShutterValue = parseShutterSpeed(targetShutterSpeed)
        val targetIsoValue = parseIso(targetIso)

        val shutterEvDiff = ln(targetShutterValue / baseShutterValue) / LOG2
        val isoEvDiff = ln(targetIsoValue / baseIsoValue) / LOG2
        val evDiff = shutterEvDiff + isoEvDiff + evCompensation

        val newApertureValue = baseApertureValue * kotlin.math.sqrt(2.0).pow(evDiff)
        val apertures = getApertureScale(scale)
        val newAperture = findClosestValue(apertures, newApertureValue, ValueType.APERTURE)

        val minApertureValue = parseAperture(apertures[0])

        if (newApertureValue < minApertureValue * 0.7 && scale == 1) {
            val stopsUnder = 2 * ln(minApertureValue / newApertureValue) / LOG2
            throw UnderexposedError(stopsUnder)
        }

        return newAperture
    }

    override fun calculateIso(
        baseShutterSpeed: String,
        baseAperture: String,
        baseIso: String,
        targetShutterSpeed: String,
        targetAperture: String,
        scale: Int,
        evCompensation: Double
    ): String {
        val baseShutterValue = parseShutterSpeed(baseShutterSpeed)
        val baseApertureValue = parseAperture(baseAperture)
        val baseIsoValue = parseIso(baseIso)
        val targetShutterValue = parseShutterSpeed(targetShutterSpeed)
        val targetApertureValue = parseAperture(targetAperture)

        val shutterEvDiff = ln(baseShutterValue / targetShutterValue) / LOG2
        val apertureEvDiff = 2 * ln(targetApertureValue / baseApertureValue) / LOG2
        val evDiff = shutterEvDiff + apertureEvDiff - evCompensation

        val newIsoValue = baseIsoValue * 2.0.pow(evDiff)
        val isoValues = getIsoScale(scale)
        val newIso = findClosestValue(isoValues, newIsoValue, ValueType.ISO)

        val maxIsoValue = parseIso(isoValues[isoValues.size - 1])
        val minIsoValue = parseIso(isoValues[0])

        when {
            newIsoValue > maxIsoValue * 1.5 -> {
                throw ExposureParameterLimitError("ISO", newIsoValue.toString(), maxIsoValue.toString())
            }
            newIsoValue < minIsoValue * 0.67 -> {
                throw ExposureParameterLimitError("ISO", newIsoValue.toString(), minIsoValue.toString())
            }
        }

        return newIso
    }

    private enum class ValueType {
        SHUTTER, APERTURE, ISO
    }

    private fun parseShutterSpeed(shutterSpeed: String): Double {
        if (shutterSpeed.isBlank()) return 0.0

        return when {
            shutterSpeed.contains('/') -> {
                val parts = shutterSpeed.split('/')
                if (parts.size == 2) {
                    val numerator = parts[0].toDoubleOrNull() ?: 0.0
                    val denominator = parts[1].toDoubleOrNull() ?: 0.0
                    if (denominator != 0.0) numerator / denominator else 0.0
                } else 0.0
            }
            shutterSpeed.endsWith("\"") -> {
                val value = shutterSpeed.trimEnd('\"')
                value.toDoubleOrNull() ?: 0.0
            }
            else -> shutterSpeed.toDoubleOrNull() ?: 0.0
        }
    }

    private fun parseAperture(aperture: String): Double {
        if (aperture.isBlank()) return 0.0

        return when {
            aperture.startsWith("f/") -> {
                val value = aperture.substring(2)
                value.toDoubleOrNull() ?: 0.0
            }
            else -> aperture.toDoubleOrNull() ?: 0.0
        }
    }

    private fun parseIso(iso: String): Double {
        if (iso.isBlank()) return 0.0
        return iso.toDoubleOrNull() ?: 0.0
    }

    private fun getShutterSpeedScale(scale: Int): Array<String> {
        return when (scale) {
            1 -> ShutterSpeeds.FULL
            2 -> ShutterSpeeds.HALVES
            3 -> ShutterSpeeds.THIRDS
            else -> ShutterSpeeds.FULL
        }
    }

    private fun getApertureScale(scale: Int): Array<String> {
        return when (scale) {
            1 -> Apertures.FULL
            2 -> Apertures.HALVES
            3 -> Apertures.THIRDS
            else -> Apertures.FULL
        }
    }

    private fun getIsoScale(scale: Int): Array<String> {
        return when (scale) {
            1 -> Isos.FULL
            2 -> Isos.HALVES
            3 -> Isos.THIRDS
            else -> Isos.FULL
        }
    }

    private fun findClosestValue(values: Array<String>, target: Double, valueType: ValueType): String {
        if (values.isEmpty()) throw IllegalArgumentException("Values array is empty")

        var closest = values[0]
        var closestDiff = Double.MAX_VALUE

        for (value in values) {
            val current = when (valueType) {
                ValueType.SHUTTER -> parseShutterSpeed(value)
                ValueType.APERTURE -> parseAperture(value)
                ValueType.ISO -> parseIso(value)
            }

            val diff = abs(ln(current / target) / LOG2)

            if (diff < closestDiff) {
                closest = value
                closestDiff = diff
            }
        }

        return closest
    }

    private object ShutterSpeeds {
        val FULL = arrayOf(
            "30\"", "15\"", "8\"", "4\"", "2\"", "1\"",
            "1/2", "1/4", "1/8", "1/15", "1/30", "1/60",
            "1/125", "1/250", "1/500", "1/1000", "1/2000", "1/4000", "1/8000"
        )

        val HALVES = arrayOf(
            "30\"", "20\"", "15\"", "10\"", "8\"", "6\"", "4\"", "3\"", "2\"", "1.5\"", "1\"",
            "1/1.5", "1/2", "1/3", "1/4", "1/6", "1/8", "1/10", "1/15", "1/20", "1/30",
            "1/45", "1/60", "1/90", "1/125", "1/180", "1/250", "1/350", "1/500", "1/750",
            "1/1000", "1/1500", "1/2000", "1/3000", "1/4000", "1/6000", "1/8000"
        )

        val THIRDS = arrayOf(
            "30\"", "25\"", "20\"", "15\"", "13\"", "10\"", "8\"", "6\"", "5\"", "4\"", "3.2\"", "2.5\"",
            "2\"", "1.6\"", "1.3\"", "1\"", "0.8\"", "0.6\"", "1/2", "1/2.5", "1/3", "1/4", "1/5",
            "1/6", "1/8", "1/10", "1/13", "1/15", "1/20", "1/25", "1/30", "1/40", "1/50", "1/60",
            "1/80", "1/100", "1/125", "1/160", "1/200", "1/250", "1/320", "1/400", "1/500", "1/640",
            "1/800", "1/1000", "1/1250", "1/1600", "1/2000", "1/2500", "1/3200", "1/4000", "1/5000",
            "1/6400", "1/8000"
        )
    }

    private object Apertures {
        val FULL = arrayOf(
            "f/1.0", "f/1.4", "f/2.0", "f/2.8", "f/4.0", "f/5.6", "f/8.0", "f/11", "f/16", "f/22", "f/32"
        )

        val HALVES = arrayOf(
            "f/1.0", "f/1.2", "f/1.4", "f/1.7", "f/2.0", "f/2.4", "f/2.8", "f/3.3", "f/4.0", "f/4.8",
            "f/5.6", "f/6.7", "f/8.0", "f/9.5", "f/11", "f/13", "f/16", "f/19", "f/22", "f/27", "f/32"
        )

        val THIRDS = arrayOf(
            "f/1.0", "f/1.1", "f/1.2", "f/1.4", "f/1.6", "f/1.8", "f/2.0", "f/2.2", "f/2.5", "f/2.8",
            "f/3.2", "f/3.5", "f/4.0", "f/4.5", "f/5.0", "f/5.6", "f/6.3", "f/7.1", "f/8.0", "f/9.0",
            "f/10", "f/11", "f/13", "f/14", "f/16", "f/18", "f/20", "f/22", "f/25", "f/29", "f/32"
        )
    }

    private object Isos {
        val FULL = arrayOf(
            "50", "100", "200", "400", "800", "1600", "3200", "6400", "12800", "25600", "51200", "102400"
        )

        val HALVES = arrayOf(
            "50", "70", "100", "140", "200", "280", "400", "560", "800", "1100", "1600", "2200", "3200",
            "4500", "6400", "9000", "12800", "18000", "25600", "36000", "51200", "72000", "102400"
        )

        val THIRDS = arrayOf(
            "50", "60", "80", "100", "125", "160", "200", "250", "320", "400", "500", "640", "800",
            "1000", "1250", "1600", "2000", "2500", "3200", "4000", "5000", "6400", "8000", "10000",
            "12800", "16000", "20000", "25600", "32000", "40000", "51200", "64000", "80000", "102400"
        )
    }
}