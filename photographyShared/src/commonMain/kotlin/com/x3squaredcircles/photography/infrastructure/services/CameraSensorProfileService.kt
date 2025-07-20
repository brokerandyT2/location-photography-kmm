// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/CameraSensorProfileService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.domain.enums.MountType
import com.x3squaredcircles.photography.domain.services.ICameraSensorProfileService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*

class CameraSensorProfileService(
    private val logger: Logger
) : ICameraSensorProfileService {

    override suspend fun loadCameraSensorProfilesAsync(
        jsonContents: List<String>
    ): Result<List<CameraBodyDto>> {
        return try {
            withContext(Dispatchers.Default) {
                val cameras = mutableListOf<CameraBodyDto>()

                for (jsonContent in jsonContents) {
                    when (val fileCameras = parseCameraJsonAsync(jsonContent)) {
                        is Result.Success -> {
                            cameras.addAll(fileCameras.data)
                        }
                        is Result.Failure -> {
                            logger.w { "Failed to parse JSON content: ${fileCameras.error}" }
                        }
                    }
                }

                logger.i { "Loaded ${cameras.size} cameras from ${jsonContents.size} JSON contents" }
                Result.success(cameras)
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error loading camera sensor profiles from JSON contents" }
            Result.failure("Error retrieving cameras from JSON")
        }
    }

    private suspend fun parseCameraJsonAsync(jsonContent: String): Result<List<CameraBodyDto>> {
        return try {
            withContext(Dispatchers.Default) {
                val cameras = mutableListOf<CameraBodyDto>()
                val jsonElement = Json.parseToJsonElement(jsonContent)

                if (jsonElement is JsonObject && jsonElement.containsKey("Cameras")) {
                    val camerasObject = jsonElement["Cameras"]?.jsonObject

                    camerasObject?.forEach { (cameraName, cameraData) ->
                        val cameraObject = cameraData.jsonObject

                        val brand = cameraObject["Brand"]?.jsonPrimitive?.contentOrNull
                        val sensorType = cameraObject["SensorType"]?.jsonPrimitive?.contentOrNull
                        val sensorObject = cameraObject["Sensor"]?.jsonObject

                        if (brand != null && sensorType != null && sensorObject != null) {
                            val sensorWidth = sensorObject["SensorWidthInMM"]?.jsonPrimitive?.doubleOrNull
                            val sensorHeight = sensorObject["SensorHeightInMM"]?.jsonPrimitive?.doubleOrNull

                            if (sensorWidth != null && sensorHeight != null) {
                                val mountType = determineMountType(brand, cameraName)

                                val cameraDto = CameraBodyDto(
                                    id = 0, // JSON cameras don't have database IDs
                                    name = cameraName,
                                    sensorType = sensorType,
                                    sensorWidth = sensorWidth,
                                    sensorHeight = sensorHeight,
                                    mountType = mountType.name,
                                    isUserCreated = false,
                                    dateAdded = Clock.System.now().toEpochMilliseconds(),
                                    displayName = cameraName // Use the full JSON key as display name
                                )

                                cameras.add(cameraDto)
                            }
                        }
                    }
                }

                Result.success(cameras)
            }
        } catch (ex: Exception) {
            logger.e(ex) { "Error parsing camera JSON content" }
            Result.failure("Error parsing camera data")
        }
    }

    private fun determineMountType(brand: String?, cameraName: String): MountType {
        val brandLower = brand?.lowercase() ?: ""
        val cameraNameLower = cameraName.lowercase()

        return when (brandLower) {
            "canon" -> when {
                cameraNameLower.contains("eos r") -> MountType.CANON_RF
                cameraNameLower.contains("eos m") -> MountType.CANON_EFM
                else -> MountType.CANON_EF
            }
            "nikon" -> when {
                cameraNameLower.contains(" z") -> MountType.NIKON_Z
                else -> MountType.NIKON_F
            }
            "sony" -> when {
                cameraNameLower.contains("fx") || cameraNameLower.contains("a7") -> MountType.SONY_FE
                else -> MountType.SONY_E
            }
            "fujifilm" -> when {
                cameraNameLower.contains("gfx") -> MountType.FUJIFILM_GFX
                else -> MountType.FUJIFILM_X
            }
            "pentax" -> MountType.PENTAX_K
            "olympus" -> MountType.MICRO_FOUR_THIRDS
            "panasonic" -> MountType.MICRO_FOUR_THIRDS
            "leica" -> when {
                cameraNameLower.contains(" sl") -> MountType.LEICA_SL
                cameraNameLower.contains(" m") -> MountType.LEICA_M
                else -> MountType.LEICA_L
            }
            else -> MountType.OTHER
        }
    }
}