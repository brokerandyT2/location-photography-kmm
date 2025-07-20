// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesQueryResult
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.photography.domain.services.ICameraSensorProfileService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val cameraSensorProfileService: ICameraSensorProfileService,
    private val logger: Logger
) : IQueryHandler<GetCameraBodiesQuery, GetCameraBodiesQueryResult> {

    override suspend fun handle(query: GetCameraBodiesQuery): Result<GetCameraBodiesQueryResult> {
        logger.d { "Handling GetCameraBodiesQuery with skip: ${query.skip}, take: ${query.take}, userCamerasOnly: ${query.userCamerasOnly}" }

        return try {
            val allCameras = mutableListOf<CameraBodyDto>()

            // Step 1: Load user-created cameras from database
            if (query.userCamerasOnly) {
                when (val userCamerasResult = cameraBodyRepository.getUserCreatedAsync()) {
                    is Result.Success -> {
                        allCameras.addAll(userCamerasResult.data)
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to get user cameras: ${userCamerasResult.error}" }
                        return Result.success(
                            GetCameraBodiesQueryResult(
                                cameraBodies = emptyList(),
                                totalCount = 0,
                                hasMore = false,
                                isSuccess = false,
                                errorMessage = userCamerasResult.error
                            )
                        )
                    }
                }
            } else {
                // Load all database cameras (user + system)
                when (val allDbCamerasResult = cameraBodyRepository.getAllAsync()) {
                    is Result.Success -> {
                        allCameras.addAll(allDbCamerasResult.data)
                    }
                    is Result.Failure -> {
                        logger.w { "Failed to load database cameras: ${allDbCamerasResult.error}" }
                    }
                }

                // Step 2: Load cameras from JSON sensor profiles
                when (val jsonCamerasResult = cameraSensorProfileService.loadCameraSensorProfilesAsync(emptyList())) {
                    is Result.Success -> {
                        allCameras.addAll(jsonCamerasResult.data)
                    }
                    is Result.Failure -> {
                        logger.w { "Failed to load JSON camera profiles: ${jsonCamerasResult.error}" }
                    }
                }
            }

            // Step 3: Sort cameras (user cameras first, then alphabetically by display name)
            val sortedCameras = allCameras.sortedWith(compareBy<CameraBodyDto> {
                if (it.isUserCreated) 0 else 1
            }.thenBy { it.displayName })

            // Step 4: Apply paging
            val totalCount = sortedCameras.size
            val pagedCameras = sortedCameras.drop(query.skip).take(query.take)
            val hasMore = (query.skip + query.take) < totalCount

            logger.i { "Retrieved ${pagedCameras.size} camera bodies (total: $totalCount, hasMore: $hasMore)" }

            Result.success(
                GetCameraBodiesQueryResult(
                    cameraBodies = pagedCameras,
                    totalCount = totalCount,
                    hasMore = hasMore,
                    isSuccess = true
                )
            )

        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving camera bodies" }
            Result.success(
                GetCameraBodiesQueryResult(
                    cameraBodies = emptyList(),
                    totalCount = 0,
                    hasMore = false,
                    isSuccess = false,
                    errorMessage = "Error retrieving camera bodies"
                )
            )
        }
    }
}