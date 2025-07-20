// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/services/ICameraSensorProfileService.kt
package com.x3squaredcircles.photography.domain.services

import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto

interface ICameraSensorProfileService {

    /**
     * Loads camera sensor profiles from JSON files in Resources/CameraSensorProfiles
     */
    suspend fun loadCameraSensorProfilesAsync(
        jsonContents: List<String>
    ): Result<List<CameraBodyDto>>
}