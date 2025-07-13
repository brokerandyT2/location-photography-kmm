// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/handlers/GetLensesQueryHandler.kt
package com.x3squaredcircles.photographyshared.handlers
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQueryHandler
import com.x3squaredcircles.photography.queries.GetLensesQuery
import com.x3squaredcircles.photography.domain.entities.Lens
import com.x3squaredcircles.photography.dtos.LensDto
import com.x3squaredcircles.photography.dtos.GetLensesResultDto
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ILensRepository
class GetLensesQueryHandler(
    private val lensRepository: ILensRepository
) : IQueryHandler<GetLensesQuery, Result<GetLensesResultDto>> {
    override suspend fun handle(request: GetLensesQuery): Result<GetLensesResultDto> {
        return try {
            val lenses: List<Lens>
            val totalCount: Int

            when {
                request.compatibleWithCameraId != null -> {
                    val compatibleResult = lensRepository.getCompatibleLensesAsync(request.compatibleWithCameraId)
                    if (!compatibleResult.isSuccess) {
                        return Result.failure("Error retrieving compatible lenses")
                    }

                    val allCompatible = compatibleResult.getOrNull() ?: emptyList()
                    lenses = allCompatible.drop(request.skip).take(request.take)
                    totalCount = allCompatible.size
                }
                request.userLensesOnly -> {
                    val userLensesResult = lensRepository.getUserCreatedAsync()
                    if (!userLensesResult.isSuccess) {
                        return Result.failure("Error retrieving user lenses")
                    }

                    val allUserLenses = userLensesResult.getOrNull() ?: emptyList()
                    lenses = allUserLenses.drop(request.skip).take(request.take)
                    totalCount = allUserLenses.size
                }
                else -> {
                    val pagedResult = lensRepository.getPagedAsync(request.skip, request.take)
                    if (!pagedResult.isSuccess) {
                        return Result.failure("Error retrieving lenses")
                    }

                    val countResult = lensRepository.getTotalCountAsync()
                    if (!countResult.isSuccess) {
                        return Result.failure("Error retrieving lens count")
                    }

                    lenses = pagedResult.getOrNull() ?: emptyList()
                    totalCount = countResult.getOrNull() ?: 0
                }
            }

            val lensDtos = lenses.map { lens ->
                LensDto(
                    id = lens.id,
                    minMM = lens.minMM,
                    maxMM = lens.maxMM,
                    minFStop = lens.minFStop,
                    maxFStop = lens.maxFStop,
                    isPrime = lens.isPrime,
                    isUserCreated = lens.isUserCreated,
                    dateAdded = lens.dateAdded,
                    displayName = lens.getDisplayName()
                )
            }

            val result = GetLensesResultDto(
                lenses = lensDtos,
                totalCount = totalCount,
                hasMore = (request.skip + request.take) < totalCount
            )

            Result.success(result)
        } catch (e: Exception) {
            Result.failure("Error retrieving lenses: ${e.message}")
        }
    }
}