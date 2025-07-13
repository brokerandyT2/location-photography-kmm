// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/LensRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.domain.entities.Lens
import com.x3squaredcircles.photographyshared.infrastructure.database.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlin.math.abs
class LensRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : ILensRepository {
    override suspend fun getAllAsync(): Result<List<Lens>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensQueries.selectAll().executeAsList()
                val lenses = entities.map { entity ->
                    Lens(
                        id = entity.id.toInt(),
                        minMM = entity.minMM,
                        maxMM = entity.maxMM,
                        minFStop = entity.minFStop,
                        maxFStop = entity.maxFStop,
                        isPrime = entity.isPrime == 1L,
                        isUserCreated = entity.isUserCreated == 1L,
                        nameForLens = entity.nameForLens,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(lenses)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all lenses", e)
            Result.failure(e)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<Lens> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.lensQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val lens = Lens(
                        id = entity.id.toInt(),
                        minMM = entity.minMM,
                        maxMM = entity.maxMM,
                        minFStop = entity.minFStop,
                        maxFStop = entity.maxFStop,
                        isPrime = entity.isPrime == 1L,
                        isUserCreated = entity.isUserCreated == 1L,
                        nameForLens = entity.nameForLens,
                        dateAdded = entity.dateAdded
                    )
                    Result.success(lens)
                } else {
                    Result.failure(Exception("Lens not found"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting lens by ID: $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getPagedAsync(skip: Int, take: Int): Result<List<Lens>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensQueries.selectPaged(take.toLong(), skip.toLong()).executeAsList()
                val lenses = entities.map { entity ->
                    Lens(
                        id = entity.id.toInt(),
                        minMM = entity.minMM,
                        maxMM = entity.maxMM,
                        minFStop = entity.minFStop,
                        maxFStop = entity.maxFStop,
                        isPrime = entity.isPrime == 1L,
                        isUserCreated = entity.isUserCreated == 1L,
                        nameForLens = entity.nameForLens,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(lenses)
            }
        } catch (e: Exception) {
            logger.logError("Error getting paged lenses", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserCreatedAsync(): Result<List<Lens>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensQueries.selectUserCreated().executeAsList()
                val lenses = entities.map { entity ->
                    Lens(
                        id = entity.id.toInt(),
                        minMM = entity.minMM,
                        maxMM = entity.maxMM,
                        minFStop = entity.minFStop,
                        maxFStop = entity.maxFStop,
                        isPrime = entity.isPrime == 1L,
                        isUserCreated = entity.isUserCreated == 1L,
                        nameForLens = entity.nameForLens,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(lenses)
            }
        } catch (e: Exception) {
            logger.logError("Error getting user created lenses", e)
            Result.failure(e)
        }
    }

    override suspend fun getByFocalLengthRangeAsync(focalLength: Double): Result<List<Lens>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensQueries.selectAll().executeAsList()
                val matchingLenses = entities.filter { entity ->
                    focalLength >= entity.minMM && focalLength <= entity.maxMM
                }.map { entity ->
                    Lens(
                        id = entity.id.toInt(),
                        minMM = entity.minMM,
                        maxMM = entity.maxMM,
                        minFStop = entity.minFStop,
                        maxFStop = entity.maxFStop,
                        isPrime = entity.isPrime == 1L,
                        isUserCreated = entity.isUserCreated == 1L,
                        nameForLens = entity.nameForLens,
                        dateAdded = entity.dateAdded
                    )
                }.sortedWith(compareBy(
                    { if (it.isUserCreated) 0 else 1 },
                    { abs(it.minMM - focalLength) }
                ))
                Result.success(matchingLenses)
            }
        } catch (e: Exception) {
            logger.logError("Error getting lenses by focal length: $focalLength", e)
            Result.failure(e)
        }
    }

    override suspend fun getCompatibleLensesAsync(cameraBodyId: Int): Result<List<Lens>> {
        return try {
            withContext(Dispatchers.IO) {
                val compatibleLenses = database.lensCameraCompatibilityQueries
                    .selectByCameraId(cameraBodyId.toLong())
                    .executeAsList()
                    .mapNotNull { compatibility ->
                        database.lensQueries.selectById(compatibility.lensId).executeAsOneOrNull()
                    }
                    .map { entity ->
                        Lens(
                            id = entity.id.toInt(),
                            minMM = entity.minMM,
                            maxMM = entity.maxMM,
                            minFStop = entity.minFStop,
                            maxFStop = entity.maxFStop,
                            isPrime = entity.isPrime == 1L,
                            isUserCreated = entity.isUserCreated == 1L,
                            nameForLens = entity.nameForLens,
                            dateAdded = entity.dateAdded
                        )
                    }
                Result.success(compatibleLenses)
            }
        } catch (e: Exception) {
            logger.logError("Error getting compatible lenses for camera: $cameraBodyId", e)
            Result.failure(e)
        }
    }

    override suspend fun getPrimesAsync(): Result<List<Lens>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensQueries.selectPrimes().executeAsList()
                val lenses = entities.map { entity ->
                    Lens(
                        id = entity.id.toInt(),
                        minMM = entity.minMM,
                        maxMM = entity.maxMM,
                        minFStop = entity.minFStop,
                        maxFStop = entity.maxFStop,
                        isPrime = entity.isPrime == 1L,
                        isUserCreated = entity.isUserCreated == 1L,
                        nameForLens = entity.nameForLens,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(lenses)
            }
        } catch (e: Exception) {
            logger.logError("Error getting prime lenses", e)
            Result.failure(e)
        }
    }

    override suspend fun getZoomsAsync(): Result<List<Lens>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.lensQueries.selectZooms().executeAsList()
                val lenses = entities.map { entity ->
                    Lens(
                        id = entity.id.toInt(),
                        minMM = entity.minMM,
                        maxMM = entity.maxMM,
                        minFStop = entity.minFStop,
                        maxFStop = entity.maxFStop,
                        isPrime = entity.isPrime == 1L,
                        isUserCreated = entity.isUserCreated == 1L,
                        nameForLens = entity.nameForLens,
                        dateAdded = entity.dateAdded
                    )
                }
                Result.success(lenses)
            }
        } catch (e: Exception) {
            logger.logError("Error getting zoom lenses", e)
            Result.failure(e)
        }
    }

    override suspend fun createAsync(lens: Lens): Result<Lens> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.lensQueries.insert(
                    minMM = lens.minMM,
                    maxMM = lens.maxMM,
                    minFStop = lens.minFStop,
                    maxFStop = lens.maxFStop,
                    isPrime = if (lens.isPrime) 1L else 0L,
                    isUserCreated = if (lens.isUserCreated) 1L else 0L,
                    nameForLens = lens.nameForLens,
                    dateAdded = currentTime
                )

                val insertedId = database.lensQueries.transactionWithResult {
                    database.lensQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newLens = lens.copy(
                    id = insertedId,
                    dateAdded = currentTime
                )

                logger.logInformation("Created lens with ID: $insertedId")
                Result.success(newLens)
            }
        } catch (e: Exception) {
            logger.logError("Error creating lens", e)
            Result.failure(e)
        }
    }

    override suspend fun updateAsync(lens: Lens): Result<Lens> {
        return try {
            withContext(Dispatchers.IO) {
                database.lensQueries.update(
                    minMM = lens.minMM,
                    maxMM = lens.maxMM,
                    minFStop = lens.minFStop,
                    maxFStop = lens.maxFStop,
                    isPrime = if (lens.isPrime) 1L else 0L,
                    nameForLens = lens.nameForLens,
                    id = lens.id.toLong()
                )

                logger.logInformation("Updated lens with ID: ${lens.id}")
                Result.success(lens)
            }
        } catch (e: Exception) {
            logger.logError("Error updating lens", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.lensQueries.deleteById(id.toLong())
                logger.logInformation("Deleted lens with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting lens", e)
            Result.failure(e)
        }
    }

    override suspend fun getTotalCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.lensQueries.getTotalCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting lens count", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountByTypeAsync(): Result<Pair<Int, Int>> {
        return try {
            withContext(Dispatchers.IO) {
                val primeCount = database.lensQueries.getPrimeCount().executeAsOne().toInt()
                val zoomCount = database.lensQueries.getZoomCount().executeAsOne().toInt()
                Result.success(Pair(primeCount, zoomCount))
            }
        } catch (e: Exception) {
            logger.logError("Error getting lens count by type", e)
            Result.failure(e)
        }
    }
}