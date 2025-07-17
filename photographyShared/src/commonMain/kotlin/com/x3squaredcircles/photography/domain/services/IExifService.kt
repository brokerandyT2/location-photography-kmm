// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/IExifService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.domain.models.ExifData

interface IExifService {

    suspend fun extractExifDataAsync(imagePath: String): Result<ExifData>

    suspend fun hasRequiredExifDataAsync(imagePath: String): Result<Boolean>
}