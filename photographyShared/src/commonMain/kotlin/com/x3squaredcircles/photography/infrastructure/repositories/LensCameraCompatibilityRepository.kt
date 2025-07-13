// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/LensCameraCompatibilityRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility
import com.x3squaredcircles.photographyshared.infrastructure.database.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock

class LensCameraCompatibilityRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : ILensCameraCompatibilityRepository {

    override suspend fun getAllAsync(): Result<List<LensCameraCompatibility>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensCameraCompatibilityQueries.selectAll().executeAsList()
                val compatibilities = entities.map { entity ->
                    LensCameraCompatibility(
                        id = entity.id.toInt(),
                        lensId = entity.lensId.toInt(),
                        cameraBodyId = entity.cameraBodyId.toInt(),
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(compatibilities)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all lens-camera compatibilities", e)
            Result.failure(e)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<LensCameraCompatibility> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.lensCameraCompatibilityQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val compatibility = LensCameraCompatibility(
                        id = entity.id.toInt(),
                        lensId = entity.lensId.toInt(),
                        cameraBodyId = entity.cameraBodyId.toInt(),
                        dateAdded = entity.dateAdded
                    )
                    Result.success(compatibility)
                } else {
                    Result.failure(Exception("Lens-camera compatibility not found"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting lens-camera compatibility by ID: $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getByLensIdAsync(lensId: Int): Result<List<LensCameraCompatibility>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensCameraCompatibilityQueries.selectByLensId(lensId.toLong()).executeAsList()
                val compatibilities = entities.map { entity ->
                    LensCameraCompatibility(
                        id = entity.id.toInt(),
                        lensId = entity.lensId.toInt(),
                        cameraBodyId = entity.cameraBodyId.toInt(),
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(compatibilities)
            }
        } catch (e: Exception) {
            logger.logError("Error getting lens-camera compatibilities by lens ID: $lensId", e)
            Result.failure(e)
        }
    }

    override suspend fun getByCameraIdAsync(cameraBodyId: Int): Result<List<LensCameraCompatibility>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensCameraCompatibilityQueries.selectByCameraId(cameraBodyId.toLong()).executeAsList()
                val compatibilities = entities.map { entity ->
                    LensCameraCompatibility(
                        id = entity.id.toInt(),
                        lensId = entity.lensId.toInt(),
                        cameraBodyId = entity.cameraBodyId.toInt(),
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(compatibilities)
            }
        } catch (e: Exception) {
            logger.logError("Error getting lens-camera compatibilities by camera ID: $cameraBodyId", e)
            Result.failure(e)
        }
    }

    override suspend fun createAsync(compatibility: LensCameraCompatibility): Result<LensCameraCompatibility> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.lensCameraCompatibilityQueries.insert(
                    lensId = compatibility.lensId.toLong(),
                    cameraBodyId = compatibility.cameraBodyId.toLong(),
                    dateAdded = currentTime
                )

                val insertedId = database.lensCameraCompatibilityQueries.transactionWithResult {
                    database.lensCameraCompatibilityQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newCompatibility = compatibility.copy(
                    id = insertedId,
                    dateAdded = currentTime
                )

                logger.logInformation("Created lens-camera compatibility with ID: $insertedId")
                Result.success(newCompatibility)
            }
        } catch (e: Exception) {
            logger.logError("Error creating lens-camera compatibility", e)
            Result.failure(e)
        }
    }

    override suspend fun createBatchAsync(compatibilities: List<LensCameraCompatibility>): Result<List<LensCameraCompatibility>> {
        return try {
            if (compatibilities.isEmpty()) {
                return Result.success(emptyList())
            }

            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.transaction {
                    compatibilities.forEach { compatibility ->
                        database.lensCameraCompatibilityQueries.insert(
                            lensId = compatibility.lensId.toLong(),
                            cameraBodyId = compatibility.cameraBodyId.toLong(),
                            dateAdded = currentTime
                        )
                    }
                }

                val newCompatibilities = compatibilities.map { compatibility ->
                    compatibility.copy(dateAdded = currentTime)
                }

                logger.logInformation("Created ${compatibilities.size} lens-camera compatibilities")
                Result.success(newCompatibilities)
            }
        } catch (e: Exception) {
            logger.logError("Error creating batch lens-camera compatibilities", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.lensCameraCompatibilityQueries.deleteById(id.toLong())
                logger.logInformation("Deleted lens-camera compatibility with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting lens-camera compatibility", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByLensAndCameraAsync(lensId: Int, cameraBodyId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.lensCameraCompatibilityQueries.deleteByLensAndCamera(lensId.toLong(), cameraBodyId.toLong())
                logger.logInformation("Deleted lens-camera compatibility: LensId=$lensId, CameraId=$cameraBodyId")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting lens-camera compatibility by lens and camera", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByLensIdAsync(lensId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.lensCameraCompatibilityQueries.deleteByLensId(lensId.toLong())
                logger.logInformation("Deleted all compatibilities for lens ID: $lensId")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting lens-camera compatibilities by lens ID", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByCameraIdAsync(cameraBodyId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.lensCameraCompatibilityQueries.deleteByCameraId(cameraBodyId.toLong())
                logger.logInformation("Deleted all compatibilities for camera ID: $cameraBodyId")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting lens-camera compatibilities by camera ID", e)
            Result.failure(e)
        }
    }

    override suspend fun existsAsync(lensId: Int, cameraBodyId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.lensCameraCompatibilityQueries.exists(lensId.toLong(), cameraBodyId.toLong()).executeAsOne()
                Result.success(exists)
            }
        } catch (e: Exception) {
            logger.logError("Error checking if lens-camera compatibility exists", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.lensCameraCompatibilityQueries.getCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting lens-camera compatibility count", e)
            Result.failure(e)
        }
    }
}








