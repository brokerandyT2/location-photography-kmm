// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/camerabody/handlers/GetCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.camerabody.handlers

import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesQuery
import com.x3squaredcircles.photography.application.queries.camerabody.GetCameraBodiesResultDto
import com.x3squaredcircles.photography.application.queries.camerabody.CameraBodyDto
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository
import com.x3squaredcircles.photography.domain.services.ICameraSensorProfileService
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

data class GetCameraBodiesQueryResult(
    val result: GetCameraBodiesResultDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

class GetCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository,
    private val cameraSensorProfileService: ICameraSensorProfileService,
    private val logger: Logger
) : IQueryHandler<GetCameraBodiesQuery, Result<GetCameraBodiesQueryResult>> {

    override suspend fun handle(query: GetCameraBodiesQuery): Result<GetCameraBodiesQueryResult> {
        logger.d { "Handling GetCameraBodiesQuery with skip: ${query.skip}, take: ${query.take}, userCamerasOnly: ${query.userCamerasOnly}" }

        return try {
            val allCameras = mutableListOf<CameraBodyDto>()

            // Step 1: Load user-created cameras from database
            if (query.userCamerasOnly) {
                when (val userCamerasResult = cameraBodyRepository.getUserCreatedAsync()) {
                    is Result.Success -> {
                        val userCameraDtos = userCamerasResult.data.map { c ->
                            c.copy(displayName = if (c.isUserCreated) "${c.name}*" else c.name)
                        }
                        allCameras.addAll(userCameraDtos)
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to get user cameras: ${userCamerasResult.error}" }
                        return Result.failure("Error getting user cameras")
                    }
                }
            } else {
                // Load all database cameras (user + system)
                when (val allDbCamerasResult = cameraBodyRepository.getPagedAsync(Int.MAX_VALUE, 0)) {
                    is Result.Success -> {
                        val dbCameraDtos = allDbCamerasResult.data.map { c ->
                            c.copy(displayName = if (c.isUserCreated) "${c.name}*" else c.name)
                        }
                        allCameras.addAll(dbCameraDtos)
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to get database cameras: ${allDbCamerasResult.error}" }
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

            // Step 3: Sort cameras (user cameras first, then JSON cameras, then alphabetically)
            val sortedCameras = allCameras.sortedWith(
                compareBy<CameraBodyDto> { if (it.isUserCreated) 0 else 1 }
                    .thenBy { it.displayName ?: it.name }
            )

            // Step 4: Apply paging
            val totalCount = sortedCameras.size
            val pagedCameras = sortedCameras

            val result = GetCameraBodiesResultDto(
                cameraBodies = pagedCameras,
                totalCount = totalCount,
                hasMore = (query.skip + query.take) < totalCount
            )

            logger.i { "Retrieved ${pagedCameras.size} camera bodies (total: $totalCount)" }

            val queryResult = GetCameraBodiesQueryResult(
                result = result,
                isSuccess = true
            )

            Result.success(queryResult)

        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving camera bodies" }
            val queryResult = GetCameraBodiesQueryResult(
                result = null,
                isSuccess = false,
                errorMessage = ex.message
            )
            Result.success(queryResult)
        }
    }
}