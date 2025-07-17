// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ExifService.kt
package com.x3squaredcircles.photography.infrastructure.services

import co.touchlab.kermit.Logger
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.ExifData
import com.x3squaredcircles.photography.domain.services.IExifService
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant

class ExifService(
    private val logger: Logger
) : IExifService {

    override suspend fun extractExifDataAsync(imagePath: String): Result<ExifData> {
        return try {
            if (imagePath.isBlank()) {
                return Result.failure("Image path cannot be null or empty")
            }

            if (!fileExists(imagePath)) {
                return Result.failure("Image file does not exist")
            }

            val maxRetries = 5
            val delayMs = 200L

            for (attempt in 0 until maxRetries) {
                try {
                    val exifData = extractDataFromFile(imagePath)
                    return Result.success(exifData)
                } catch (ex: Exception) {
                    logger.w(ex) { "File locked on attempt ${attempt + 1}, retrying in ${delayMs * (attempt + 1)}ms: $imagePath" }

                    if (attempt < maxRetries - 1) {
                        delay(delayMs * (attempt + 1))
                        continue
                    }

                    logger.e(ex) { "Failed to extract EXIF data from $imagePath on attempt ${attempt + 1}" }
                    return Result.failure("Failed to extract EXIF data after $maxRetries attempts: ${ex.message}")
                }
            }

            Result.failure("Failed to extract EXIF data after $maxRetries attempts")
        } catch (ex: Exception) {
            logger.e(ex) { "Error extracting EXIF data from $imagePath" }
            Result.failure("Error extracting EXIF data: ${ex.message}")
        }
    }

    override suspend fun hasRequiredExifDataAsync(imagePath: String): Result<Boolean> {
        return try {
            val exifResult = extractExifDataAsync(imagePath)

            if (!exifResult.isSuccess) {
                return Result.success(false)
            }

            val hasRequired = (exifResult as Result.Success).data.hasValidFocalLength &&
                    (exifResult as Result.Success).data.fullCameraModel.isNotEmpty()

            Result.success(hasRequired)
        } catch (ex: Exception) {
            logger.e(ex) { "Error checking EXIF data requirements for $imagePath" }
            Result.failure("Error checking EXIF data: ${ex.message}")
        }
    }

    private fun fileExists(path: String): Boolean {
        return try {
            java.io.File(path).exists()
        } catch (ex: Exception) {
            false
        }
    }

    private fun extractDataFromFile(imagePath: String): ExifData {
        val directories = com.drew.imaging.ImageMetadataReader.readMetadata(java.io.File(imagePath))
        val exifData = ExifData()

        for (directory in directories.directories) {
            when (directory) {
                is com.drew.metadata.exif.ExifIFD0Directory -> {
                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_MAKE)) {
                        exifData.cameraMake = directory.getString(com.drew.metadata.exif.ExifDirectoryBase.TAG_MAKE)?.trim()
                    }

                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_MODEL)) {
                        exifData.cameraModel = directory.getString(com.drew.metadata.exif.ExifDirectoryBase.TAG_MODEL)?.trim()
                    }

                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_DATETIME)) {
                        directory.getDate(com.drew.metadata.exif.ExifDirectoryBase.TAG_DATETIME)?.let { date ->
                            exifData.dateTaken = Instant.fromEpochMilliseconds(date.time)
                        }
                    }
                }

                is com.drew.metadata.exif.ExifSubIFDDirectory -> {
                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_FOCAL_LENGTH)) {
                        directory.getRational(com.drew.metadata.exif.ExifDirectoryBase.TAG_FOCAL_LENGTH)?.let { rational ->
                            exifData.focalLength = rational.toDouble()
                        }
                    }

                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_FNUMBER)) {
                        directory.getRational(com.drew.metadata.exif.ExifDirectoryBase.TAG_FNUMBER)?.let { rational ->
                            exifData.aperture = rational.toDouble()
                        }
                    }

                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_EXIF_IMAGE_WIDTH)) {
                        directory.getInteger(com.drew.metadata.exif.ExifDirectoryBase.TAG_EXIF_IMAGE_WIDTH)?.let { width ->
                            exifData.imageWidth = width
                        }
                    }

                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_EXIF_IMAGE_HEIGHT)) {
                        directory.getInteger(com.drew.metadata.exif.ExifDirectoryBase.TAG_EXIF_IMAGE_HEIGHT)?.let { height ->
                            exifData.imageHeight = height
                        }
                    }

                    if (directory.hasTagName(com.drew.metadata.exif.ExifDirectoryBase.TAG_LENS_MODEL)) {
                        exifData.lensModel = directory.getString(com.drew.metadata.exif.ExifDirectoryBase.TAG_LENS_MODEL)?.trim()
                    }
                }
            }
        }

        return exifData
    }
}