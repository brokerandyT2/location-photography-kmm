// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/CameraBodyRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService

import com.x3squaredcircles.photography.domain.entities.CameraBody
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photographyshared.infrastructure.repositories.ICameraBodyRepository

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock

class CameraBodyRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : ICameraBodyRepository {
    override suspend fun getAllAsync(): Result<List<CameraBody>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.cameraBodyQueries.selectAll().executeAsList()
                val cameraBodies = entities.map { entity ->
                    CameraBody(
                        id = entity.id.toInt(),
                        name = entity.name,
                        sensorType = entity.sensorType,
                        sensorWidth = entity.sensorWidth,
                        sensorHeight = entity.sensorHeight,
                        mountType = entity.mountType,
                        isUserCreated = entity.isUserCreated == 1L,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(cameraBodies)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all camera bodies", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<CameraBody> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.cameraBodyQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val cameraBody = CameraBody(
                        id = entity.id.toInt(),
                        name = entity.name,
                        sensorType = entity.sensorType,
                        sensorWidth = entity.sensorWidth,
                        sensorHeight = entity.sensorHeight,
                        mountType = entity.mountType,
                        isUserCreated = entity.isUserCreated == 1L,
                        dateAdded = entity.dateAdded
                    )
                    Result.success(cameraBody)
                } else {
                    Result.failure("Camera body not found")
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting camera body by ID: $id", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getPagedAsync(skip: Int, take: Int): Result<List<CameraBody>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.cameraBodyQueries.selectPaged(take.toLong(), skip.toLong()).executeAsList()
                val cameraBodies = entities.map { entity ->
                    CameraBody(
                        id = entity.id.toInt(),
                        name = entity.name,
                        sensorType = entity.sensorType,
                        sensorWidth = entity.sensorWidth,
                        sensorHeight = entity.sensorHeight,
                        mountType = entity.mountType,
                        isUserCreated = entity.isUserCreated == 1L,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(cameraBodies)
            }
        } catch (e: Exception) {
            logger.logError("Error getting paged camera bodies", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getUserCreatedAsync(): Result<List<CameraBody>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.cameraBodyQueries.selectUserCreated().executeAsList()
                val cameraBodies = entities.map { entity ->
                    CameraBody(
                        id = entity.id.toInt(),
                        name = entity.name,
                        sensorType = entity.sensorType,
                        sensorWidth = entity.sensorWidth,
                        sensorHeight = entity.sensorHeight,
                        mountType = entity.mountType,
                        isUserCreated = entity.isUserCreated == 1L,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(cameraBodies)
            }
        } catch (e: Exception) {
            logger.logError("Error getting user created camera bodies", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByMountTypeAsync(mountType: String): Result<List<CameraBody>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.cameraBodyQueries.selectByMountType(mountType).executeAsList()
                val cameraBodies = entities.map { entity ->
                    CameraBody(
                        id = entity.id.toInt(),
                        name = entity.name,
                        sensorType = entity.sensorType,
                        sensorWidth = entity.sensorWidth,
                        sensorHeight = entity.sensorHeight,
                        mountType = entity.mountType,
                        isUserCreated = entity.isUserCreated == 1L,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(cameraBodies)
            }
        } catch (e: Exception) {
            logger.logError("Error getting camera bodies by mount type: $mountType", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun searchByNameAsync(name: String): Result<List<CameraBody>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.cameraBodyQueries.selectByName(name, name, "$name%").executeAsList()
                val cameraBodies = entities.map { entity ->
                    CameraBody(
                        id = entity.id.toInt(),
                        name = entity.name,
                        sensorType = entity.sensorType,
                        sensorWidth = entity.sensorWidth,
                        sensorHeight = entity.sensorHeight,
                        mountType = entity.mountType,
                        isUserCreated = entity.isUserCreated == 1L,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(cameraBodies)
            }
        } catch (e: Exception) {
            logger.logError("Error searching camera bodies by name: $name", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun createAsync(cameraBody: CameraBody): Result<CameraBody> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.cameraBodyQueries.insert(
                    name = cameraBody.name,
                    sensorType = cameraBody.sensorType,
                    sensorWidth = cameraBody.sensorWidth,
                    sensorHeight = cameraBody.sensorHeight,
                    mountType = cameraBody.mountType,
                    isUserCreated = if (cameraBody.isUserCreated) 1L else 0L,
                    dateAdded = currentTime
                )

                val insertedId = database.cameraBodyQueries.transactionWithResult {
                    database.cameraBodyQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newCameraBody = cameraBody.copy(
                    id = insertedId,
                    dateAdded = currentTime
                )

                logger.logInfo("Created camera body with ID: $insertedId")
                Result.success(newCameraBody)
            }
        } catch (e: Exception) {
            logger.logError("Error creating camera body", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun updateAsync(cameraBody: CameraBody): Result<CameraBody> {
        return try {
            withContext(Dispatchers.IO) {
                database.cameraBodyQueries.update(
                    name = cameraBody.name,
                    sensorType = cameraBody.sensorType,
                    sensorWidth = cameraBody.sensorWidth,
                    sensorHeight = cameraBody.sensorHeight,
                    mountType = cameraBody.mountType,
                    id = cameraBody.id.toLong()
                )

                logger.logInfo("Updated camera body with ID: ${cameraBody.id}")
                Result.success(cameraBody)
            }
        } catch (e: Exception) {
            logger.logError("Error updating camera body", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.cameraBodyQueries.deleteById(id.toLong())
                logger.logInfo("Deleted camera body with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting camera body", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun existsByNameAsync(name: String, excludeId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.cameraBodyQueries.existsByName(name, excludeId.toLong()).executeAsOne()
                Result.success(exists)
            }
        } catch (e: Exception) {
            logger.logError("Error checking if camera body exists by name", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getTotalCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.cameraBodyQueries.getTotalCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting camera body count", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getCountByMountTypeAsync(): Result<Map<String, Int>> {
        return try {
            withContext(Dispatchers.IO) {
                val results = database.cameraBodyQueries.getCountByMountType().executeAsList()
                val countMap = results.associate { result ->
                    result.mountType to result.mountType.toInt()
                }
                Result.success(countMap)
            }
        } catch (e: Exception) {
            logger.logError("Error getting camera body count by mount type", e)
            Result.failure(e.message!!)
        }
    }
}