// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ExposureCalculatorService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.exceptions.ExposureError
import com.x3squaredcircles.photography.domain.models.ExposureIncrements
import com.x3squaredcircles.photography.domain.models.ExposureSettingsDto
import com.x3squaredcircles.photography.domain.models.ExposureTriangleDto
import com.x3squaredcircles.photography.domain.services.IExposureCalculatorService
import com.x3squaredcircles.photography.domain.services.IExposureTriangleService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExposureCalculatorService(
    private val exposureTriangleService: IExposureTriangleService,
    private val logger: Logger
) : IExposureCalculatorService {

    override suspend fun calculateShutterSpeedAsync(
        baseExposure: ExposureTriangleDto,
        targetAperture: String,
        targetIso: String,
        increments: ExposureIncrements,
        evCompensation: Double
    ): Result<ExposureSettingsDto> {
        return try {
            val result = withContext(Dispatchers.Default) {
                val scale = getIncrementScale(increments)
                val shutterSpeed = exposureTriangleService.calculateShutterSpeed(
                    baseExposure.shutterSpeed,
                    baseExposure.aperture,
                    baseExposure.iso,
                    targetAperture,
                    targetIso,
                    scale,
                    evCompensation
                )

                ExposureSettingsDto(
                    shutterSpeed = shutterSpeed,
                    aperture = targetAperture,
                    iso = targetIso
                )
            }

            Result.success(result)
        } catch (ex: ExposureError) {
            logger.w(ex) { "Exposure error calculating shutter speed" }
            Result.failure(ex.message ?: "Exposure calculation error")
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating shutter speed" }
            Result.failure("Error calculating shutter speed: ${ex.message}")
        }
    }

    override suspend fun calculateApertureAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetIso: String,
        increments: ExposureIncrements,
        evCompensation: Double
    ): Result<ExposureSettingsDto> {
        return try {
            val result = withContext(Dispatchers.Default) {
                val scale = getIncrementScale(increments)
                val aperture = exposureTriangleService.calculateAperture(
                    baseExposure.shutterSpeed,
                    baseExposure.aperture,
                    baseExposure.iso,
                    targetShutterSpeed,
                    targetIso,
                    scale,
                    evCompensation
                )

                ExposureSettingsDto(
                    shutterSpeed = targetShutterSpeed,
                    aperture = aperture,
                    iso = targetIso
                )
            }

            Result.success(result)
        } catch (ex: ExposureError) {
            logger.w(ex) { "Exposure error calculating aperture" }
            Result.failure(ex.message ?: "Exposure calculation error")
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating aperture" }
            Result.failure("Error calculating aperture: ${ex.message}")
        }
    }

    override suspend fun calculateIsoAsync(
        baseExposure: ExposureTriangleDto,
        targetShutterSpeed: String,
        targetAperture: String,
        increments: ExposureIncrements,
        evCompensation: Double
    ): Result<ExposureSettingsDto> {
        return try {
            val result = withContext(Dispatchers.Default) {
                val scale = getIncrementScale(increments)
                val iso = exposureTriangleService.calculateIso(
                    baseExposure.shutterSpeed,
                    baseExposure.aperture,
                    baseExposure.iso,
                    targetShutterSpeed,
                    targetAperture,
                    scale,
                    evCompensation
                )

                ExposureSettingsDto(
                    shutterSpeed = targetShutterSpeed,
                    aperture = targetAperture,
                    iso = iso
                )
            }

            Result.success(result)
        } catch (ex: ExposureError) {
            logger.w(ex) { "Exposure error calculating ISO" }
            Result.failure(ex.message ?: "Exposure calculation error")
        } catch (ex: Exception) {
            logger.e(ex) { "Error calculating ISO" }
            Result.failure("Error calculating ISO: ${ex.message}")
        }
    }

    override suspend fun getShutterSpeedsAsync(increments: ExposureIncrements): Result<Array<String>> {
        return try {
            val shutterSpeeds = withContext(Dispatchers.Default) {
                getShutterSpeedScale(getIncrementScale(increments))
            }
            Result.success(shutterSpeeds)
        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving shutter speeds" }
            Result.failure("Error retrieving shutter speeds: ${ex.message}")
        }
    }

    override suspend fun getAperturesAsync(increments: ExposureIncrements): Result<Array<String>> {
        return try {
            val apertures = withContext(Dispatchers.Default) {
                getApertureScale(getIncrementScale(increments))
            }
            Result.success(apertures)
        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving apertures" }
            Result.failure("Error retrieving apertures: ${ex.message}")
        }
    }

    override suspend fun getIsosAsync(increments: ExposureIncrements): Result<Array<String>> {
        return try {
            val isos = withContext(Dispatchers.Default) {
                getIsoScale(getIncrementScale(increments))
            }
            Result.success(isos)
        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving ISOs" }
            Result.failure("Error retrieving ISOs: ${ex.message}")
        }
    }

    private fun getIncrementScale(increments: ExposureIncrements): Int {
        return when (increments) {
            ExposureIncrements.FULL -> 1
            ExposureIncrements.HALF -> 2
            ExposureIncrements.THIRD -> 3
        }
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