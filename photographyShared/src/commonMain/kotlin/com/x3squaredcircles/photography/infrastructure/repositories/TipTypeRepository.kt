// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/TipTypeRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.photography.domain.entities.TipType
import com.x3squaredcircles.photographyshared.infrastructure.database.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
class TipTypeRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : ITipTypeRepository {
    override suspend fun getAllAsync(): Result<List<TipType>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.tipTypeQueries.selectAll().executeAsList()
                val tipTypes = entities.map { entity ->
                    TipType(
                        id = entity.id.toInt(),
                        name = entity.name,
                        description = entity.description,
                        dateAdded = entity.dateAdded,
                        isUserCreated = entity.isUserCreated == 1L
                    )
                }
                Result.success(tipTypes)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all tip types", e)
            Result.failure(e)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<TipType> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.tipTypeQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val tipType = TipType(
                        id = entity.id.toInt(),
                        name = entity.name,
                        description = entity.description,
                        dateAdded = entity.dateAdded,
                        isUserCreated = entity.isUserCreated == 1L
                    )
                    Result.success(tipType)
                } else {
                    Result.failure(Exception("Tip type not found"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting tip type by ID: $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getByNameAsync(name: String): Result<TipType> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.tipTypeQueries.selectByName(name).executeAsOneOrNull()
                if (entity != null) {
                    val tipType = TipType(
                        id = entity.id.toInt(),
                        name = entity.name,
                        description = entity.description,
                        dateAdded = entity.dateAdded,
                        isUserCreated = entity.isUserCreated == 1L
                    )
                    Result.success(tipType)
                } else {
                    Result.failure(Exception("Tip type not found for name: $name"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting tip type by name: $name", e)
            Result.failure(e)
        }
    }

    override suspend fun getWithTipCountAsync(): Result<List<Pair<TipType, Int>>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.tipTypeQueries.selectWithTipCount().executeAsList()
                val tipTypesWithCount = entities.map { entity ->
                    val tipType = TipType(
                        id = entity.id.toInt(),
                        name = entity.name,
                        description = entity.description,
                        dateAdded = entity.dateAdded,
                        isUserCreated = entity.isUserCreated == 1L
                    )
                    val tipCount = entity.tip_count?.toInt() ?: 0
                    Pair(tipType, tipCount)
                }
                Result.success(tipTypesWithCount)
            }
        } catch (e: Exception) {
            logger.logError("Error getting tip types with count", e)
            Result.failure(e)
        }
    }

    override suspend fun createAsync(tipType: TipType): Result<TipType> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.tipTypeQueries.insert(
                    name = tipType.name,
                    description = tipType.description,
                    dateAdded = currentTime,
                    isUserCreated = if (tipType.isUserCreated) 1L else 0L
                )

                val insertedId = database.tipTypeQueries.transactionWithResult {
                    database.tipTypeQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newTipType = tipType.copy(
                    id = insertedId,
                    dateAdded = currentTime
                )

                logger.logInformation("Created tip type with ID: $insertedId")
                Result.success(newTipType)
            }
        } catch (e: Exception) {
            logger.logError("Error creating tip type", e)
            Result.failure(e)
        }
    }

    override suspend fun updateAsync(tipType: TipType): Result<TipType> {
        return try {
            withContext(Dispatchers.IO) {
                database.tipTypeQueries.update(
                    name = tipType.name,
                    description = tipType.description,
                    isUserCreated = if (tipType.isUserCreated) 1L else 0L,
                    id = tipType.id.toLong()
                )

                logger.logInformation("Updated tip type with ID: ${tipType.id}")
                Result.success(tipType)
            }
        } catch (e: Exception) {
            logger.logError("Error updating tip type", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.tipTypeQueries.deleteById(id.toLong())
                logger.logInformation("Deleted tip type with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting tip type", e)
            Result.failure(e)
        }
    }

    override suspend fun existsByNameAsync(name: String, excludeId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.tipTypeQueries.existsByName(name, excludeId.toLong()).executeAsOne()
                Result.success(exists)
            }
        } catch (e: Exception) {
            logger.logError("Error checking if tip type exists by name", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.tipTypeQueries.getCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting tip type count", e)
            Result.failure(e)
        }
    }
}