// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/WeatherRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.infrastructure.repositories.IWeatherRepository
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlin.math.*

class WeatherRepository(
    private val database: PhotographyDatabase
) : IWeatherRepository {

    companion object {
        private const val WEATHER_ERROR_NOT_FOUND = "Weather not found"
        private const val WEATHER_ERROR_INVALID_ID = "Invalid weather ID"
        private const val WEATHER_ERROR_INVALID_LOCATION_ID = "Invalid location ID"
        private const val WEATHER_ERROR_INVALID_COORDINATES = "Invalid coordinates"
        private const val WEATHER_ERROR_DATABASE_ERROR = "Database error occurred"
    }

    override suspend fun getAllAsync(): Result<List<Weather>> {
        return try {
            withContext(Dispatchers.IO) {
                val weatherList = database.weatherQueries.selectAll().executeAsList()
                val result = weatherList.map { it.toWeatherEntity() }
                Result.success(result)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<Weather> {
        return try {
            if (id <= 0) {
                return Result.failure(WEATHER_ERROR_INVALID_ID)
            }

            withContext(Dispatchers.IO) {
                val weather = database.weatherQueries.selectById(id.toLong()).executeAsOneOrNull()

                if (weather != null) {
                    Result.success(weather.toWeatherEntity())
                } else {
                    Result.failure(WEATHER_ERROR_NOT_FOUND)
                }
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getByLocationIdAsync(locationId: Int): Result<Weather> {
        return try {
            if (locationId <= 0) {
                return Result.failure(WEATHER_ERROR_INVALID_LOCATION_ID)
            }

            withContext(Dispatchers.IO) {
                val weather = database.weatherQueries.selectByLocationId(locationId.toLong()).executeAsOneOrNull()

                if (weather != null) {
                    Result.success(weather.toWeatherEntity())
                } else {
                    Result.failure(WEATHER_ERROR_NOT_FOUND)
                }
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getByCoordinatesAsync(latitude: Double, longitude: Double): Result<Weather> {
        return try {
            if (!isValidCoordinate(latitude, longitude)) {
                return Result.failure(WEATHER_ERROR_INVALID_COORDINATES)
            }

            withContext(Dispatchers.IO) {
                val weather = database.weatherQueries.selectByCoordinates(latitude, longitude).executeAsOneOrNull()

                if (weather != null) {
                    Result.success(weather.toWeatherEntity())
                } else {
                    Result.failure(WEATHER_ERROR_NOT_FOUND)
                }
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getByLocationAndTimeRangeAsync(
        locationId: Int,
        startTime: Long,
        endTime: Long
    ): Result<List<Weather>> {
        return try {
            if (locationId <= 0) {
                return Result.failure(WEATHER_ERROR_INVALID_LOCATION_ID)
            }

            withContext(Dispatchers.IO) {
                val weatherList = database.weatherQueries.selectByLocationAndTimeRange(
                    locationId.toLong(),
                    startTime,
                    endTime
                ).executeAsList()

                val result = weatherList.map { it.toWeatherEntity() }
                Result.success(result)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getNearbyWeatherAsync(
        latitude: Double,
        longitude: Double,
        radiusKm: Double
    ): Result<List<Weather>> {
        return try {
            if (!isValidCoordinate(latitude, longitude) || radiusKm <= 0) {
                return Result.failure(WEATHER_ERROR_INVALID_COORDINATES)
            }

            withContext(Dispatchers.IO) {
                // Get all weather and filter by distance (SQLDelight doesn't have geographic functions)
                val allWeather = database.weatherQueries.selectAll().executeAsList()
                val nearby = allWeather.filter {
                    calculateDistance(latitude, longitude, it.latitude, it.longitude) <= radiusKm
                }.map { it.toWeatherEntity() }

                Result.success(nearby)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getRecentAsync(count: Int): Result<List<Weather>> {
        return try {
            withContext(Dispatchers.IO) {
                val weatherList = database.weatherQueries.selectRecent(count.toLong()).executeAsList()
                val result = weatherList.map { it.toWeatherEntity() }
                Result.success(result)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getExpiredAsync(maxAgeMillis: Long): Result<List<Weather>> {
        return try {
            withContext(Dispatchers.IO) {
                val cutoffTime = System.currentTimeMillis() - maxAgeMillis
                val weatherList = database.weatherQueries.selectExpired(cutoffTime).executeAsList()
                val result = weatherList.map { it.toWeatherEntity() }
                Result.success(result)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun createAsync(weather: Weather): Result<Weather> {
        return try {
            withContext(Dispatchers.IO) {
                database.weatherQueries.insert(
                    locationId = weather.locationId.toLong(),
                    latitude = weather.latitude,
                    longitude = weather.longitude,
                    timezone = weather.timezone,
                    timezoneOffset = weather.timezoneOffset.toLong(),
                    lastUpdate = weather.lastUpdate
                )

                val insertedId = database.weatherQueries.lastInsertRowId().executeAsOne()
                val insertedWeather = weather.copy(id = insertedId.toInt())
                Result.success(insertedWeather)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun updateAsync(weather: Weather): Result<Weather> {
        return try {
            withContext(Dispatchers.IO) {
                database.weatherQueries.update(
                    locationId = weather.locationId.toLong(),
                    latitude = weather.latitude,
                    longitude = weather.longitude,
                    timezone = weather.timezone,
                    timezoneOffset = weather.timezoneOffset.toLong(),
                    lastUpdate = weather.lastUpdate,
                    id = weather.id.toLong()
                )

                Result.success(weather)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun upsertAsync(weather: Weather): Result<Weather> {
        return try {
            withContext(Dispatchers.IO) {
                // Check if weather exists for this location
                val existing = database.weatherQueries.selectByLocationId(weather.locationId.toLong()).executeAsOneOrNull()

                if (existing != null) {
                    // Update existing weather
                    updateAsync(weather.copy(id = existing.id.toInt()))
                } else {
                    // Create new weather
                    createAsync(weather)
                }
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            if (id <= 0) {
                return Result.failure(WEATHER_ERROR_INVALID_ID)
            }

            withContext(Dispatchers.IO) {
                database.weatherQueries.softDelete(id.toLong())
                Result.success(true)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun deleteByLocationIdAsync(locationId: Int): Result<Boolean> {
        return try {
            if (locationId <= 0) {
                return Result.failure(WEATHER_ERROR_INVALID_LOCATION_ID)
            }

            withContext(Dispatchers.IO) {
                database.weatherQueries.softDeleteByLocationId(locationId.toLong())
                Result.success(true)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun hasFreshDataAsync(locationId: Int, maxAgeMillis: Long): Result<Boolean> {
        return try {
            if (locationId <= 0) {
                return Result.failure(WEATHER_ERROR_INVALID_LOCATION_ID)
            }

            withContext(Dispatchers.IO) {
                val cutoffTime = System.currentTimeMillis() - maxAgeMillis
                val hasFresh = database.weatherQueries.hasFreshData(locationId.toLong(), cutoffTime).executeAsOne()
                Result.success(hasFresh)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun hasFreshDataForCoordinatesAsync(
        latitude: Double,
        longitude: Double,
        maxAgeMillis: Long
    ): Result<Boolean> {
        return try {
            if (!isValidCoordinate(latitude, longitude)) {
                return Result.failure(WEATHER_ERROR_INVALID_COORDINATES)
            }

            withContext(Dispatchers.IO) {
                val cutoffTime = System.currentTimeMillis() - maxAgeMillis
                val hasFresh = database.weatherQueries.hasFreshDataForCoordinates(
                    latitude,
                    longitude,
                    cutoffTime
                ).executeAsOne()
                Result.success(hasFresh)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun cleanupExpiredDataAsync(maxAgeMillis: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val cutoffTime = System.currentTimeMillis() - maxAgeMillis
                database.weatherQueries.cleanupExpired(cutoffTime)

                // Return count of affected rows (SQLDelight doesn't provide this directly)
                Result.success(0) // Placeholder - actual implementation would need changes() function
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.weatherQueries.getCount().executeAsOne()
                Result.success(count.toInt())
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    override suspend fun getGroupedByLocationAsync(): Result<Map<Int, List<Weather>>> {
        return try {
            withContext(Dispatchers.IO) {
                val weatherList = database.weatherQueries.selectAll().executeAsList()
                val grouped = weatherList
                    .map { it.toWeatherEntity() }
                    .groupBy { it.locationId }

                Result.success(grouped)
            }
        } catch (ex: Exception) {
            Result.failure("$WEATHER_ERROR_DATABASE_ERROR: ${ex.message}")
        }
    }

    private fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
        return latitude >= -90.0 && latitude <= 90.0 &&
                longitude >= -180.0 && longitude <= 180.0
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    // Extension function to convert SQLDelight generated class to domain entity
    private fun com.x3squaredcircles.photographyshared.db.Weather.toWeatherEntity(): Weather {
        return Weather(
            id = this.id.toInt(),
            locationId = this.locationId.toInt(),
            latitude = this.latitude,
            longitude = this.longitude,
            timezone = this.timezone,
            timezoneOffset = this.timezoneOffset.toInt(),
            lastUpdate = this.lastUpdate,
            temperature = null, // Weather table only has metadata, detailed data is in forecasts
            feelsLike = null,
            humidity = null,
            pressure = null,
            visibility = null,
            uvIndex = null,
            windSpeed = null,
            windDirection = null,
            windGust = null,
            cloudCover = null,
            condition = null,
            description = null,
            icon = null,
            sunrise = null,
            sunset = null,
            isDeleted = this.isDeleted == 1L
        )
    }
}