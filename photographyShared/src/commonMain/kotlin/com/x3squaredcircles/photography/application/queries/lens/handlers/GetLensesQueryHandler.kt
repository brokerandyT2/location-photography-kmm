// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/lens/handlers/GetLensesQueryHandler.kt
package com.x3squaredcircles.photography.application.queries.lens.handlers

import com.x3squaredcircles.photography.application.queries.lens.GetLensesQuery
import com.x3squaredcircles.photography.application.queries.lens.GetLensesQueryResult
import com.x3squaredcircles.photography.application.queries.IQueryHandler
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILensRepository
import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger

class GetLensesQueryHandler(
    private val lensRepository: ILensRepository,
    private val logger: Logger
) : IQueryHandler<GetLensesQuery, GetLensesQueryResult> {

    override suspend fun handle(query: GetLensesQuery): Result<GetLensesQueryResult> {
        logger.d { "Handling GetLensesQuery with skip: ${query.skip}, take: ${query.take}, userLensesOnly: ${query.userLensesOnly}, compatibleWithCameraId: ${query.compatibleWithCameraId}" }

        return try {
            when {
                query.compatibleWithCameraId != null -> {
                    when (val compatibleResult = lensRepository.getCompatibleLensesAsync(query.compatibleWithCameraId)) {
                        is Result.Success -> {
                            val totalCount = compatibleResult.data.size
                            val pagedLenses = compatibleResult.data.drop(query.skip).take(query.take)
                            val hasMore = (query.skip + query.take) < totalCount

                            logger.i { "Retrieved ${pagedLenses.size} compatible lenses for camera ${query.compatibleWithCameraId} (total: $totalCount)" }
                            Result.success(
                                GetLensesQueryResult(
                                    lenses = pagedLenses,
                                    totalCount = totalCount,
                                    hasMore = hasMore,
                                    isSuccess = true
                                )
                            )
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to get compatible lenses: ${compatibleResult.error}" }
                            Result.success(
                                GetLensesQueryResult(
                                    lenses = emptyList(),
                                    totalCount = 0,
                                    hasMore = false,
                                    isSuccess = false,
                                    errorMessage = compatibleResult.error
                                )
                            )
                        }
                    }
                }
                query.userLensesOnly -> {
                    when (val userLensesResult = lensRepository.getUserCreatedAsync()) {
                        is Result.Success -> {
                            val totalCount = userLensesResult.data.size
                            val pagedLenses = userLensesResult.data.drop(query.skip).take(query.take)
                            val hasMore = (query.skip + query.take) < totalCount

                            logger.i { "Retrieved ${pagedLenses.size} user lenses (total: $totalCount)" }
                            Result.success(
                                GetLensesQueryResult(
                                    lenses = pagedLenses,
                                    totalCount = totalCount,
                                    hasMore = hasMore,
                                    isSuccess = true
                                )
                            )
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to get user lenses: ${userLensesResult.error}" }
                            Result.success(
                                GetLensesQueryResult(
                                    lenses = emptyList(),
                                    totalCount = 0,
                                    hasMore = false,
                                    isSuccess = false,
                                    errorMessage = userLensesResult.error
                                )
                            )
                        }
                    }
                }
                else -> {
                    val pageNumber = (query.skip / query.take) + 1
                    when (val pagedResult = lensRepository.getPagedAsync(pageNumber, query.take)) {
                        is Result.Success -> {
                            when (val countResult = lensRepository.getTotalCountAsync()) {
                                is Result.Success -> {
                                    val totalCount = countResult.data.toInt()
                                    val hasMore = (query.skip + query.take) < totalCount

                                    logger.i { "Retrieved ${pagedResult.data.size} lenses (total: $totalCount)" }
                                    Result.success(
                                        GetLensesQueryResult(
                                            lenses = pagedResult.data,
                                            totalCount = totalCount,
                                            hasMore = hasMore,
                                            isSuccess = true
                                        )
                                    )
                                }
                                is Result.Failure -> {
                                    logger.e { "Failed to get lens count: ${countResult.error}" }
                                    Result.success(
                                        GetLensesQueryResult(
                                            lenses = emptyList(),
                                            totalCount = 0,
                                            hasMore = false,
                                            isSuccess = false,
                                            errorMessage = countResult.error
                                        )
                                    )
                                }
                            }
                        }
                        is Result.Failure -> {
                            logger.e { "Failed to get paged lenses: ${pagedResult.error}" }
                            Result.success(
                                GetLensesQueryResult(
                                    lenses = emptyList(),
                                    totalCount = 0,
                                    hasMore = false,
                                    isSuccess = false,
                                    errorMessage = pagedResult.error
                                )
                            )
                        }
                    }
                }
            }

        } catch (ex: Exception) {
            logger.e(ex) { "Error retrieving lenses" }
            Result.success(
                GetLensesQueryResult(
                    lenses = emptyList(),
                    totalCount = 0,
                    hasMore = false,
                    isSuccess = false,
                    errorMessage = "Error retrieving lenses"
                )
            )
        }
    }
}