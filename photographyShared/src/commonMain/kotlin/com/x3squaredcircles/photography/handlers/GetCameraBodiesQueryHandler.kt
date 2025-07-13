// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/GetCameraBodiesQueryHandler.kt
package com.x3squaredcircles.photographyshared.handlers
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetCameraBodiesQuery
import com.x3squaredcircles.photography.dtos.CameraBodyDto
import com.x3squaredcircles.photography.dtos.GetCameraBodiesResultDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ICameraBodyRepository
class GetCameraBodiesQueryHandler(
    private val cameraBodyRepository: ICameraBodyRepository
) : IQueryHandler<GetCameraBodiesQuery, Result<GetCameraBodiesResultDto>> {
    override suspend fun handle(request: GetCameraBodiesQuery): Result<GetCameraBodiesResultDto> {
        return try {
            val allCameras = mutableListOf<CameraBodyDto>()

            if (request.userCamerasOnly) {
                val userCamerasResult = cameraBodyRepository.getUserCreatedAsync()
                if (!userCamerasResult.isSuccess) {
                    return Result.failure("Error retrieving user cameras")
                }

                val userCameraDtos = userCamerasResult.getOrNull()?.map { camera ->
                    CameraBodyDto(
                        id = camera.id,
                        name = camera.name,
                        sensorType = camera.sensorType,
                        sensorWidth = camera.sensorWidth,
                        sensorHeight = camera.sensorHeight,
                        mountType = camera.mountType,
                        isUserCreated = camera.isUserCreated,
                        dateAdded = camera.dateAdded,
                        displayName = camera.getDisplayName()
                    )
                } ?: emptyList()

                allCameras.addAll(userCameraDtos)
            } else {
                val allDbCamerasResult = cameraBodyRepository.getPagedAsync(0, Int.MAX_VALUE)
                if (allDbCamerasResult.isSuccess) {
                    val dbCameraDtos = allDbCamerasResult.getOrNull()?.map { camera ->
                        CameraBodyDto(
                            id = camera.id,
                            name = camera.name,
                            sensorType = camera.sensorType,
                            sensorWidth = camera.sensorWidth,
                            sensorHeight = camera.sensorHeight,
                            mountType = camera.mountType,
                            isUserCreated = camera.isUserCreated,
                            dateAdded = camera.dateAdded,
                            displayName = camera.getDisplayName()
                        )
                    } ?: emptyList()
                    allCameras.addAll(dbCameraDtos)
                }
            }

            val sortedCameras = allCameras.sortedWith(
                compareBy<CameraBodyDto> { if (it.isUserCreated) 0 else 1 }
                    .thenBy { it.displayName }
            )

            val totalCount = sortedCameras.size
            val pagedCameras = sortedCameras.drop(request.skip).take(request.take)

            val result = GetCameraBodiesResultDto(
                cameraBodies = pagedCameras,
                totalCount = totalCount,
                hasMore = (request.skip + request.take) < totalCount
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure("Error retrieving camera bodies: ${e.message}")
        }
    }
}