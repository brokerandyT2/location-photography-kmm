// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/PhoneCameraProfileRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.PhoneCameraProfileDto
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IPhoneCameraProfileRepository
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class PhoneCameraProfileRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : IPhoneCameraProfileRepository {

    private val profileCache = mutableMapOf<Int, CachedProfile>()
    private val activeProfileCache = mutableMapOf<String, CachedProfile>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 15.minutes

    override suspend fun getByIdAsync(id: Int): Result<PhoneCameraProfileDto?> {
        return executeWithExceptionMapping("GetById") {
            cacheMutex.withLock {
                val cached = profileCache[id]
                if (cached != null && !cached.isExpired) {
                    logger.d { "Cache hit for phone camera profile ID: $id" }
                    return@executeWithExceptionMapping cached.profile
                }
            }

            val entity = database.phoneCameraProfileQueries.selectById(id.toLong()).executeAsOneOrNull()
            val profile = entity?.let { mapToDto(it) }

            if (profile != null) {
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    profileCache[id] = CachedProfile(profile, expiration)
                    logger.d { "Cached phone camera profile for ID: $id" }
                }
            }

            profile
        }
    }

    override suspend fun getAllAsync(): Result<List<PhoneCameraProfileDto>> {
        return executeWithExceptionMapping("GetAll") {
            database.phoneCameraProfileQueries.selectAll()
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun getActiveAsync(): Result<PhoneCameraProfileDto?> {
        return executeWithExceptionMapping("GetActive") {
            val cacheKey = "active"
            cacheMutex.withLock {
                val cached = activeProfileCache[cacheKey]
                if (cached != null && !cached.isExpired) {
                    logger.d { "Cache hit for active phone camera profile" }
                    return@executeWithExceptionMapping cached.profile
                }
            }

            val entity = database.phoneCameraProfileQueries.selectActive().executeAsOneOrNull()
            val profile = entity?.let { mapToDto(it) }

            if (profile != null) {
                cacheMutex.withLock {
                    val expiration = Clock.System.now() + cacheExpiration
                    activeProfileCache[cacheKey] = CachedProfile(profile, expiration)
                    logger.d { "Cached active phone camera profile" }
                }
            }

            profile
        }
    }

    override suspend fun getByPhoneModelAsync(phoneModel: String): Result<List<PhoneCameraProfileDto>> {
        return executeWithExceptionMapping("GetByPhoneModel") {
            database.phoneCameraProfileQueries.selectByPhoneModel(phoneModel)
                .executeAsList()
                .map { mapToDto(it) }
        }
    }

    override suspend fun createAsync(profile: PhoneCameraProfileDto): Result<PhoneCameraProfileDto> {
        return executeWithExceptionMapping("Create") {
            val now = Clock.System.now().toEpochMilliseconds()

            database.phoneCameraProfileQueries.insert(
                phoneModel = profile.phoneModel,
                mainLensFocalLength = profile.mainLensFocalLength,
                mainLensFOV = profile.mainLensFOV,
                ultraWideFocalLength = profile.ultraWideFocalLength,
                telephotoFocalLength = profile.telephotoFocalLength,
                dateCalibrated = now,
                isActive = if (profile.isActive) 1L else 0L
            )

            val newId = database.phoneCameraProfileQueries.lastInsertRowId().executeAsOne().toInt()
            val created = profile.copy(id = newId, dateCalibrated = now)

            clearCache()
            logger.i { "Created phone camera profile with ID: $newId for model: ${profile.phoneModel}" }

            created
        }
    }

    override suspend fun updateAsync(profile: PhoneCameraProfileDto): Result<Unit> {
        return executeWithExceptionMapping("Update") {
            database.phoneCameraProfileQueries.update(
                phoneModel = profile.phoneModel,
                mainLensFocalLength = profile.mainLensFocalLength,
                mainLensFOV = profile.mainLensFOV,
                ultraWideFocalLength = profile.ultraWideFocalLength,
                telephotoFocalLength = profile.telephotoFocalLength,
                dateCalibrated = profile.dateCalibrated,
                isActive = if (profile.isActive) 1L else 0L,
                id = profile.id.toLong()
            )

            val rowsAffected = database.phoneCameraProfileQueries.changes().executeAsOne()
            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Phone camera profile with ID ${profile.id} not found")
            }

            clearCache()
            logger.i { "Updated phone camera profile with ID: ${profile.id}" }
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Unit> {
        return executeWithExceptionMapping("Delete") {
            database.phoneCameraProfileQueries.deleteById(id.toLong())
            val rowsAffected = database.phoneCameraProfileQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Phone camera profile with ID $id not found")
            }

            clearCache()
            logger.i { "Deleted phone camera profile with ID: $id" }
        }
    }

    override suspend fun setActiveAsync(id: Int): Result<Unit> {
        return executeWithExceptionMapping("SetActive") {
            database.transaction {
                // First deactivate all profiles
                database.phoneCameraProfileQueries.deactivateAllProfiles()

                // Then activate the specified profile
                database.phoneCameraProfileQueries.setActiveProfile(id.toLong())
                val rowsAffected = database.phoneCameraProfileQueries.changes().executeAsOne()

                if (rowsAffected == 0L) {
                    rollback()
                    throw IllegalArgumentException("Phone camera profile with ID $id not found")
                }
            }

            clearCache()
            logger.i { "Set phone camera profile with ID $id as active" }
        }
    }

    override suspend fun deactivateAllAsync(): Result<Unit> {
        return executeWithExceptionMapping("DeactivateAll") {
            database.phoneCameraProfileQueries.deactivateAllProfiles()
            val rowsAffected = database.phoneCameraProfileQueries.changes().executeAsOne()

            clearCache()
            logger.i { "Deactivated all phone camera profiles (affected rows: $rowsAffected)" }
        }
    }

    override suspend fun getTotalCountAsync(): Result<Long> {
        return executeWithExceptionMapping("GetTotalCount") {
            database.phoneCameraProfileQueries.getCount().executeAsOne()
        }
    }

    override suspend fun getActiveCountAsync(): Result<Long> {
        return executeWithExceptionMapping("GetActiveCount") {
            database.phoneCameraProfileQueries.getActiveCount().executeAsOne()
        }
    }

    override suspend fun createBulkAsync(profiles: List<PhoneCameraProfileDto>): Result<List<PhoneCameraProfileDto>> {
        return executeWithExceptionMapping("CreateBulk") {
            val now = Clock.System.now().toEpochMilliseconds()
            val created = mutableListOf<PhoneCameraProfileDto>()

            database.transaction {
                for (profile in profiles) {
                    database.phoneCameraProfileQueries.insert(
                        phoneModel = profile.phoneModel,
                        mainLensFocalLength = profile.mainLensFocalLength,
                        mainLensFOV = profile.mainLensFOV,
                        ultraWideFocalLength = profile.ultraWideFocalLength,
                        telephotoFocalLength = profile.telephotoFocalLength,
                        dateCalibrated = now,
                        isActive = if (profile.isActive) 1L else 0L
                    )

                    val newId = database.phoneCameraProfileQueries.lastInsertRowId().executeAsOne().toInt()
                    created.add(profile.copy(id = newId, dateCalibrated = now))
                }
            }

            clearCache()
            logger.i { "Created ${created.size} phone camera profiles" }

            created
        }
    }

    override suspend fun updateBulkAsync(profiles: List<PhoneCameraProfileDto>): Result<Int> {
        return executeWithExceptionMapping("UpdateBulk") {
            var totalAffected = 0

            database.transaction {
                for (profile in profiles) {
                    database.phoneCameraProfileQueries.update(
                        phoneModel = profile.phoneModel,
                        mainLensFocalLength = profile.mainLensFocalLength,
                        mainLensFOV = profile.mainLensFOV,
                        ultraWideFocalLength = profile.ultraWideFocalLength,
                        telephotoFocalLength = profile.telephotoFocalLength,
                        dateCalibrated = profile.dateCalibrated,
                        isActive = if (profile.isActive) 1L else 0L,
                        id = profile.id.toLong()
                    )

                    val rowsAffected = database.phoneCameraProfileQueries.changes().executeAsOne()
                    totalAffected += rowsAffected.toInt()
                }
            }

            clearCache()
            logger.i { "Updated $totalAffected phone camera profiles" }

            totalAffected
        }
    }

    override suspend fun deleteBulkAsync(ids: List<Int>): Result<Int> {
        return executeWithExceptionMapping("DeleteBulk") {
            var totalAffected = 0

            database.transaction {
                for (id in ids) {
                    database.phoneCameraProfileQueries.deleteById(id.toLong())
                    val rowsAffected = database.phoneCameraProfileQueries.changes().executeAsOne()
                    totalAffected += rowsAffected.toInt()
                }
            }

            clearCache()
            logger.i { "Deleted $totalAffected phone camera profiles" }

            totalAffected
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                profileCache.clear()
                activeProfileCache.clear()
                logger.i { "Phone camera profile cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                profileCache.remove(id)
                activeProfileCache.clear() // Clear active cache as well since active state might change
                logger.d { "Removed phone camera profile $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDto(entity: com.x3squaredcircles.photographyshared.db.PhoneCameraProfile): PhoneCameraProfileDto {
        return PhoneCameraProfileDto(
            id = entity.id.toInt(),
            phoneModel = entity.phoneModel,
            mainLensFocalLength = entity.mainLensFocalLength,
            mainLensFOV = entity.mainLensFOV,
            ultraWideFocalLength = entity.ultraWideFocalLength,
            telephotoFocalLength = entity.telephotoFocalLength,
            dateCalibrated = entity.dateCalibrated,
            isActive = entity.isActive == 1L
        )
    }

    private suspend fun <T> executeWithExceptionMapping(
        operationName: String,
        operation: suspend () -> T
    ): Result<T> {
        return try {
            val result = operation()
            Result.success(result)
        } catch (ex: Exception) {
            logger.e(ex) { "Repository operation $operationName failed for phone camera profile" }
            val mappedException = exceptionMapper.mapToSettingDomainException(ex, operationName)
            Result.failure(mappedException.message ?: "Unknown error", mappedException)
        }
    }

    private data class CachedProfile(
        val profile: PhoneCameraProfileDto,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}