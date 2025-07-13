// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/TipRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService

import com.x3squaredcircles.photography.domain.entities.Tip
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase

import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock

class TipRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : ITipRepository {

    override suspend fun getAllAsync(): Result<List<Tip>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.tipQueries.selectAll().executeAsList()
                val tips = entities.map { entity ->
                    Tip(
                        id = entity.id.toInt(),
                        tipTypeId = entity.tipTypeId.toInt(),
                        title = entity.title,
                        content = entity.content,
                        fstop = entity.fstop,
                        shutterSpeed = entity.shutterSpeed,
                        iso = entity.iso
                    )
                }
                Result.success(tips)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all tips", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<Tip> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.tipQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val tip = Tip(
                        id = entity.id.toInt(),
                        tipTypeId = entity.tipTypeId.toInt(),
                        title = entity.title,
                        content = entity.content,
                        fstop = entity.fstop,
                        shutterSpeed = entity.shutterSpeed,
                        iso = entity.iso
                    )
                    Result.success(tip)
                } else {
                    Result.failure("Tip not found")
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting tip by ID: $id", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getByTypeAsync(tipTypeId: Int): Result<List<Tip>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.tipQueries.selectByTypeId(tipTypeId.toLong()).executeAsList()
                val tips = entities.map { entity ->
                    Tip(
                        id = entity.id.toInt(),
                        tipTypeId = entity.tipTypeId.toInt(),
                        title = entity.title,
                        content = entity.content,
                        fstop = entity.fstop,
                        shutterSpeed = entity.shutterSpeed,
                        iso = entity.iso
                    )
                }
                Result.success(tips)
            }
        } catch (e: Exception) {
            logger.logError("Error getting tips by type: $tipTypeId", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getWithCameraSettingsAsync(): Result<List<Tip>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.tipQueries.selectWithCameraSettings().executeAsList()
                val tips = entities.map { entity ->
                    Tip(
                        id = entity.id.toInt(),
                        tipTypeId = entity.tipTypeId.toInt(),
                        title = entity.title,
                        content = entity.content,
                        fstop = entity.fstop,
                        shutterSpeed = entity.shutterSpeed,
                        iso = entity.iso
                    )
                }
                Result.success(tips)
            }
        } catch (e: Exception) {
            logger.logError("Error getting tips with camera settings", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun searchAsync(searchTerm: String): Result<List<Tip>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.tipQueries.selectBySearch(searchTerm, "", "", "").executeAsList()
                val tips = entities.map { entity ->
                    Tip(
                        id = entity.id.toInt(),
                        tipTypeId = entity.tipTypeId.toInt(),
                        title = entity.title,
                        content = entity.content,
                        fstop = entity.fstop,
                        shutterSpeed = entity.shutterSpeed,
                        iso = entity.iso
                    )
                }
                Result.success(tips)
            }
        } catch (e: Exception) {
            logger.logError("Error searching tips: $searchTerm", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getRandomAsync(count: Int): Result<List<Tip>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.tipQueries.selectRandom(count.toLong()).executeAsList()
                val tips = entities.map { entity ->
                    Tip(
                        id = entity.id.toInt(),
                        tipTypeId = entity.tipTypeId.toInt(),
                        title = entity.title,
                        content = entity.content,
                        fstop = entity.fstop,
                        shutterSpeed = entity.shutterSpeed,
                        iso = entity.iso
                    )
                }
                Result.success(tips)
            }
        } catch (e: Exception) {
            logger.logError("Error getting random tips", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun createAsync(tip: Tip): Result<Tip> {
        return try {
            withContext(Dispatchers.IO) {
                val currentTime = Clock.System.now().epochSeconds
                database.tipQueries.insert(
                    tipTypeId = tip.tipTypeId.toLong(),
                    title = tip.title,
                    content = tip.content,
                    fstop = tip.fstop,
                    shutterSpeed = tip.shutterSpeed,
                    iso = tip.iso,
                    i8n = tip.i8n
                )

                val insertedId = database.tipQueries.transactionWithResult {
                    database.tipQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newTip = tip.copy(
                    id = insertedId,
                    i8n = "en-US"
                )

                logger.logInfo("Created tip with ID: $insertedId")
                Result.success(newTip)
            }
        } catch (e: Exception) {
            logger.logError("Error creating tip", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun updateAsync(tip: Tip): Result<Tip> {
        return try {
            withContext(Dispatchers.IO) {
                database.tipQueries.update(
                    tipTypeId = tip.tipTypeId.toLong(),
                    title = tip.title,
                    content = tip.content,
                    fstop = tip.fstop,
                    shutterSpeed = tip.shutterSpeed,
                    iso = tip.iso,
                    id = tip.id.toLong(),
                    i8n = tip.i8n
                )

                logger.logInfo("Updated tip with ID: ${tip.id}")
                Result.success(tip)
            }
        } catch (e: Exception) {
            logger.logError("Error updating tip", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun updateCameraSettingsAsync(id: Int, fstop: String, shutterSpeed: String, iso: String): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.tipQueries.updateCameraSettings(
                    fstop = fstop,
                    shutterSpeed = shutterSpeed,
                    iso = iso,
                    id = id.toLong()
                )

                logger.logInfo("Updated camera settings for tip ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error updating camera settings for tip", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.tipQueries.deleteById(id.toLong())
                logger.logInfo("Deleted tip with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting tip", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun deleteByTypeAsync(tipTypeId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.tipQueries.deleteByTypeId(tipTypeId.toLong())
                logger.logInfo("Deleted all tips for type ID: $tipTypeId")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting tips by type", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.tipQueries.getCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting tip count", e)
            Result.failure(e.message!!)
        }
    }

    override suspend fun getCountByTypeAsync(tipTypeId: Int): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.tipQueries.getCountByType(tipTypeId.toLong()).executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting tip count by type", e)
            Result.failure(e.message!!)
        }
    }
}








