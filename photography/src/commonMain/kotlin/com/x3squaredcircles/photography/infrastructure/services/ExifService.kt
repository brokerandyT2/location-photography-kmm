// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ExifService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photographyshared.infrastructure.services.IExifService
import com.x3squaredcircles.photographyshared.infrastructure.services.ExifData

expect class ExifService(
    logger: ILoggingService
) : IExifService {

    override suspend fun extractExifDataAsync(imagePath: String): Result<ExifData>
    override suspend fun hasRequiredExifDataAsync(imagePath: String): Result<Boolean>
}