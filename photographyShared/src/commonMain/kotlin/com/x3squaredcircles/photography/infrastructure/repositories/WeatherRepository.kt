// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/WeatherRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.core.domain.entities.HourlyForecast
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.WindInfo
import com.x3squaredcircles.core.domain.common.Result
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.WeatherCacheType
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
import com.x3squaredcircles.photographyshared.db.DailyForecast
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class WeatherRepository(
    private val database: PhotographyDatabase,
    private val logger: Logger,
    private val exceptionMapper: IInfrastructureExceptionMappingService
) : IWeatherRepository {

    private val weatherCache = mutableMapOf<Int, CachedWeather>()
    private val cacheMutex = Mutex()
    private val cacheExpiration = 10.minutes

    override suspend fun getByIdAsync(id: Int): Result<Weather?> {
        return executeWithExceptionMapping("GetById") {
            val entity = database.weatherQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getAllAsync(): Result<List<Weather>> {
        return executeWithExceptionMapping("GetAll") {
            database.weatherQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByLocationIdAsync(locationId: Int): Result<Weather?> {
        return executeWithExceptionMapping("GetByLocationId") {
            val entity = database.weatherQueries.selectByLocationId(locationId.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getByCoordinatesAsync(coordinate: Coordinate): Result<Weather?> {
        return executeWithExceptionMapping("GetByCoordinates") {
            val entity = database.weatherQueries.selectByCoordinates(
                coordinate.latitude,
                coordinate.longitude
            ).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getByLocationAndTimeRangeAsync(
        locationId: Int,
        startTime: Instant,
        endTime: Instant
    ): Result<List<Weather>> {
        return executeWithExceptionMapping("GetByLocationAndTimeRange") {
            database.weatherQueries.selectByLocationAndTimeRange(
                locationId.toLong(),
                startTime.toEpochMilliseconds(),
                endTime.toEpochMilliseconds()
            ).executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getRecentAsync(count: Int): Result<List<Weather>> {
        return executeWithExceptionMapping("GetRecent") {
            database.weatherQueries.selectRecent(count.toLong())
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getExpiredAsync(olderThan: Instant): Result<List<Weather>> {
        return executeWithExceptionMapping("GetExpired") {
            database.weatherQueries.selectExpired(olderThan.toEpochMilliseconds())
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun createAsync(weather: Weather): Result<Weather> {
        return executeWithExceptionMapping("Create") {
            database.weatherQueries.insert(
                locationId = weather.locationId.toLong(),
                latitude = weather.coordinate.latitude,
                longitude = weather.coordinate.longitude,
                timezone = weather.timezone,
                timezoneOffset = weather.timezoneOffset.toLong(),
                lastUpdate = weather.lastUpdate.toEpochMilliseconds()
            )

            val id = database.weatherQueries.lastInsertRowId().executeAsOne()
            val savedWeather = Weather.fromPersistence(
                id = id.toInt(),
                locationId = weather.locationId,
                coordinate = weather.coordinate,
                timezone = weather.timezone,
                timezoneOffset = weather.timezoneOffset,
                lastUpdate = weather.lastUpdate
            )

            logger.i { "Created weather with ID ${savedWeather.id}" }
            savedWeather
        }
    }

    override suspend fun updateAsync(weather: Weather): Result<Unit> {
        return executeWithExceptionMapping("Update") {
            database.weatherQueries.update(
                locationId = weather.locationId.toLong(),
                latitude = weather.coordinate.latitude,
                longitude = weather.coordinate.longitude,
                timezone = weather.timezone,
                timezoneOffset = weather.timezoneOffset.toLong(),
                lastUpdate = weather.lastUpdate.toEpochMilliseconds(),
                id = weather.id.toLong()
            )
            val rowsAffected = database.weatherQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Weather with ID ${weather.id} not found for update")
            }

            logger.i { "Updated weather with ID ${weather.id}" }
        }
    }

    override suspend fun deleteAsync(weather: Weather): Result<Unit> {
        return softDeleteAsync(weather)
    }

    override suspend fun softDeleteAsync(weather: Weather): Result<Unit> {
        return executeWithExceptionMapping("SoftDelete") {
            database.weatherQueries.softDelete(weather.id.toLong())
            val rowsAffected = database.weatherQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Weather with ID ${weather.id} not found for soft delete")
            }

            logger.i { "Soft deleted weather with ID ${weather.id}" }
        }
    }

    override suspend fun softDeleteByLocationIdAsync(locationId: Int): Result<Unit> {
        return executeWithExceptionMapping("SoftDeleteByLocationId") {
            database.weatherQueries.softDeleteByLocationId(locationId.toLong())
            val rowsAffected = database.weatherQueries.changes().executeAsOne()

            logger.i { "Soft deleted $rowsAffected weather records for location ID $locationId" }
        }
    }

    override suspend fun hasFreshDataAsync(locationId: Int, maxAge: Instant): Result<Boolean> {
        return executeWithExceptionMapping("HasFreshData") {
            database.weatherQueries.hasFreshData(
                locationId.toLong(),
                maxAge.toEpochMilliseconds()
            ).executeAsOne()
        }
    }

    override suspend fun hasFreshDataForCoordinatesAsync(coordinate: Coordinate, maxAge: Instant): Result<Boolean> {
        return executeWithExceptionMapping("HasFreshDataForCoordinates") {
            database.weatherQueries.hasFreshDataForCoordinates(
                coordinate.latitude,
                coordinate.longitude,
                maxAge.toEpochMilliseconds()
            ).executeAsOne()
        }
    }

    override suspend fun cleanupExpiredAsync(olderThan: Instant): Result<Int> {
        return executeWithExceptionMapping("CleanupExpired") {
            database.weatherQueries.cleanupExpired(olderThan.toEpochMilliseconds())
            val rowsAffected = database.weatherQueries.changes().executeAsOne()

            logger.i { "Cleaned up $rowsAffected expired weather records" }
            rowsAffected.toInt()
        }
    }

    override suspend fun getCountAsync(): Result<Long> {
        return executeWithExceptionMapping("GetCount") {
            database.weatherQueries.getCount().executeAsOne()
        }
    }

    override suspend fun addForecastAsync(forecast: WeatherForecast): Result<WeatherForecast> {
        return executeWithExceptionMapping("AddForecast") {
            database.dailyForecastQueries.insert(
                weatherId = forecast.weatherId.toLong(),
                forecastDate = forecast.date.toEpochMilliseconds(),
                minTemperature = forecast.minTemperature,
                maxTemperature = forecast.maxTemperature,
                humidity = forecast.humidity.toDouble(),
                pressure = forecast.pressure.toDouble(),
                uvIndex = forecast.uvIndex,
                windSpeed = forecast.wind.speed,
                windDirection = forecast.wind.direction,
                windGust = forecast.wind.gust,
                cloudCover = forecast.clouds.toDouble(),
                precipitationChance = forecast.precipitation,
                description = forecast.description,
                icon = forecast.icon,
                precipitationAmount = forecast.precipitation,
                condition = forecast.description,
                sunrise = forecast.sunrise.toEpochMilliseconds(),
                sunset = forecast.sunset.toEpochMilliseconds(),
                moonPhase = forecast.moonPhase,
                moonrise = forecast.moonRise?.toEpochMilliseconds(),
                moonset = forecast.moonSet?.toEpochMilliseconds(),

            )

            val id = database.dailyForecastQueries.lastInsertRowId().executeAsOne()
            val savedForecast = WeatherForecast.fromPersistence(
                id = id.toInt(),
                weatherId = forecast.weatherId,
                date = forecast.date,
                minTemperature = forecast.minTemperature,
                maxTemperature = forecast.maxTemperature,
                humidity = forecast.humidity,
                pressure = forecast.pressure,
                uvIndex = forecast.uvIndex,
                wind = forecast.wind,
                clouds = forecast.clouds,
               description = forecast.description,
                icon = forecast.icon,
                sunset = forecast.sunset,
                sunrise = forecast.sunrise,
                temperature = forecast.temperature
            )

            logger.i { "Created weather forecast with ID ${savedForecast.id}" }
            savedForecast
        }
    }

    override suspend fun addForecastsAsync(forecasts: List<WeatherForecast>): Result<List<WeatherForecast>> {
        return executeWithExceptionMapping("AddForecasts") {
            if (forecasts.isEmpty()) return@executeWithExceptionMapping forecasts

            database.transactionWithResult {
                val result = mutableListOf<WeatherForecast>()

                for (forecast in forecasts) {
                    database.dailyForecastQueries.insert(
                        weatherId = forecast.weatherId.toLong(),
                        forecastDate = forecast.date.toEpochMilliseconds(),
                        minTemperature = forecast.minTemperature,
                        maxTemperature = forecast.maxTemperature,
                        humidity = forecast.humidity.toDouble(),
                        pressure = forecast.pressure.toDouble(),
                        uvIndex = forecast.uvIndex,
                        windSpeed = forecast.wind.speed,
                        windDirection = forecast.wind.direction,
                        windGust = forecast.wind.gust,
                        cloudCover = forecast.clouds.toDouble(),
                        precipitationChance = forecast.precipitation,
                        description = forecast.description,
                        icon = forecast.icon,
                        condition = forecast.description,
                        precipitationAmount = forecast.precipitation,
                        sunrise = forecast.sunrise.toEpochMilliseconds(),
                        sunset = forecast.sunset.toEpochMilliseconds(),
                        moonPhase = forecast.moonPhase,
                        moonrise = forecast.moonRise?.toEpochMilliseconds(),
                        moonset = forecast.moonSet?.toEpochMilliseconds(),
                    )

                    val id = database.dailyForecastQueries.lastInsertRowId().executeAsOne()
                    val savedForecast = WeatherForecast.fromPersistence(
                        id = id.toInt(),
                        weatherId = forecast.weatherId,
                        date = forecast.date,
                        minTemperature = forecast.minTemperature,
                        maxTemperature = forecast.maxTemperature,
                        humidity = forecast.humidity,
                        pressure = forecast.pressure,
                        uvIndex = forecast.uvIndex,
                        wind = forecast.wind,
                        clouds = forecast.clouds,
                        description = forecast.description,
                        icon = forecast.icon,
                        sunset = forecast.sunset,
                        sunrise = forecast.sunrise,
                        temperature = forecast.temperature
                    )
                    result.add(savedForecast)
                }

                logger.i { "Created ${result.size} weather forecasts" }
                result
            }
        }
    }

    override suspend fun updateForecastAsync(forecast: WeatherForecast): Result<Unit> {
        return executeWithExceptionMapping("UpdateForecast") {
            database.dailyForecastQueries.update(
                minTemperature = forecast.minTemperature,
                maxTemperature = forecast.maxTemperature,
                humidity = forecast.humidity.toDouble(),
                pressure = forecast.pressure.toDouble(),
                uvIndex = forecast.uvIndex,
                windSpeed = forecast.wind.speed,
                windDirection = forecast.wind.direction,
                windGust = forecast.wind.gust,
                cloudCover = forecast.clouds.toDouble(),
                description = forecast.description,
                icon = forecast.icon,
                id = forecast.id.toLong(),
                precipitationAmount = forecast.precipitation,
                condition = forecast.description,
                sunrise = forecast.sunrise.toEpochMilliseconds(),
                sunset = forecast.sunset.toEpochMilliseconds(),
                moonPhase = forecast.moonPhase,
                moonrise = forecast.moonRise?.toEpochMilliseconds(),
                moonset = forecast.moonSet?.toEpochMilliseconds(),
                precipitationChance = forecast.precipitation

            )
            val rowsAffected = database.dailyForecastQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Weather forecast with ID ${forecast.id} not found for update")
            }

            logger.i { "Updated weather forecast with ID ${forecast.id}" }
        }
    }

    override suspend fun deleteForecastAsync(forecast: WeatherForecast): Result<Unit> {
        return executeWithExceptionMapping("DeleteForecast") {
            database.dailyForecastQueries.deleteById(forecast.id.toLong())
            val rowsAffected = database.dailyForecastQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Weather forecast with ID ${forecast.id} not found for deletion")
            }

            logger.i { "Deleted weather forecast with ID ${forecast.id}" }
        }
    }

    override suspend fun deleteForecastsByWeatherIdAsync(weatherId: Int): Result<Unit> {
        return executeWithExceptionMapping("DeleteForecastsByWeatherId") {
            database.dailyForecastQueries.deleteByWeatherId(weatherId.toLong())
            val rowsAffected = database.dailyForecastQueries.changes().executeAsOne()

            logger.i { "Deleted $rowsAffected weather forecasts for weather ID $weatherId" }
        }
    }

    override suspend fun getForecastsByWeatherIdAsync(weatherId: Int): Result<List<WeatherForecast>> {
        return executeWithExceptionMapping("GetForecastsByWeatherId") {
            database.dailyForecastQueries.selectByWeatherId(weatherId.toLong())
                .executeAsList()
                .map { mapForecastToDomain(it) }
        }
    }

    override suspend fun getForecastsByWeatherAndDateRangeAsync(
        weatherId: Int,
        startDate: Instant,
        endDate: Instant
    ): Result<List<WeatherForecast>> {
        return executeWithExceptionMapping("GetForecastsByWeatherAndDateRange") {
            database.dailyForecastQueries.selectByWeatherAndDateRange(
                weatherId.toLong(),
                startDate.toEpochMilliseconds(),
                endDate.toEpochMilliseconds()
            ).executeAsList()
                .map { mapForecastToDomain(it) }
        }
    }

    override suspend fun getForecastByWeatherAndDateAsync(weatherId: Int, date: Instant): Result<WeatherForecast?> {
        return executeWithExceptionMapping("GetForecastByWeatherAndDate") {
            val entity = database.dailyForecastQueries.selectByDate(
                weatherId.toLong(),
                date.toEpochMilliseconds()
            ).executeAsOneOrNull()
            entity?.let { mapForecastToDomain(it) }
        }
    }

    override fun clearCache() {
        cacheMutex.tryLock()
        try {
            weatherCache.clear()
            logger.d { "Cleared all weather cache" }
        } finally {
            cacheMutex.unlock()
        }
    }

    override fun clearCache(id: Int) {
        cacheMutex.tryLock()
        try {
            weatherCache.remove(id)
            logger.d { "Cleared weather cache for ID $id" }
        } finally {
            cacheMutex.unlock()
        }
    }

    override fun clearCache(locationId: Int, cacheType: WeatherCacheType) {
        cacheMutex.tryLock()
        try {
            when (cacheType) {
                WeatherCacheType.BY_LOCATION_ID -> {
                    weatherCache.entries.removeAll { it.value.weather.locationId == locationId }
                    logger.d { "Cleared weather cache for location ID $locationId" }
                }
                WeatherCacheType.ALL -> {
                    weatherCache.clear()
                    logger.d { "Cleared all weather cache" }
                }
                else -> {
                    logger.d { "Cache type $cacheType not handled for location-based clearing" }
                }
            }
        } finally {
            cacheMutex.unlock()
        }
    }

    private fun mapToDomain(entity: com.x3squaredcircles.photographyshared.db.Weather): Weather {
        val coordinate = Coordinate.create(
            latitude = entity.latitude,
            longitude = entity.longitude
        )



        return Weather.fromPersistence(
            id = entity.id.toInt(),
            locationId = entity.locationId.toInt(),
            coordinate = coordinate,
            timezone = entity.timezone,
            timezoneOffset = entity.timezoneOffset.toInt(),
            lastUpdate = Instant.fromEpochMilliseconds(entity.lastUpdate),


        )
    }

    private fun mapForecastToDomain(entity: DailyForecast): WeatherForecast {
        val wind = WindInfo(
            speed = entity.windSpeed ?: 0.0,
            direction = entity.windDirection ?: 0.0,
            gust = entity.windGust
        )

        return WeatherForecast.fromPersistence(
            id = entity.id.toInt(),
            weatherId = entity.weatherId.toInt(),
            date = Instant.fromEpochMilliseconds(entity.forecastDate),
            minTemperature = entity.minTemperature ?: 0.0,
            maxTemperature = entity.maxTemperature ?: 0.0,
            humidity = entity.humidity?.toInt() ?: 0,
            pressure = entity.pressure?.toInt() ?: 0,
            uvIndex = entity.uvIndex ?: 0.0,
            wind = wind,
            clouds = entity.cloudCover?.toInt() ?: 0,
            precipitation = entity.precipitationChance ?: 0.0,
            description = entity.description ?: "",
            icon = entity.icon ?: "",
            sunrise = Instant.fromEpochMilliseconds(entity.sunrise!!),
            sunset = Instant.fromEpochMilliseconds(entity.sunset!!),
            moonPhase = entity.moonPhase ?: 0.0,
            moonRise = entity.moonrise?.let { Instant.fromEpochMilliseconds(it) },
            moonSet = entity.moonset?.let { Instant.fromEpochMilliseconds(it) },
            temperature = entity.maxTemperature!!
        )
    }

    private fun mapHourlyForecastToDomain(entity: com.x3squaredcircles.photographyshared.db.HourlyForecast): HourlyForecast {
        val wind = WindInfo(
            speed = entity.windSpeed ?: 0.0,
            direction = entity.windDirection ?: 0.0,
            gust = entity.windGust
        )

        return HourlyForecast.fromPersistence(
            id = entity.id.toInt(),
            weatherId = entity.weatherId.toInt(),
            dateTime = Instant.fromEpochMilliseconds(entity.forecastTime),
            temperature = entity.temperature ?: 0.0,
            feelsLike = entity.feelsLike ?: 0.0,
            description = entity.description ?: "",
            icon = entity.icon ?: "",
            wind = wind,
            humidity = entity.humidity?.toInt() ?: 0,
            pressure = entity.pressure?.toInt() ?: 0,
            clouds = entity.cloudCover?.toInt() ?: 0,
            uvIndex = entity.uvIndex ?: 0.0,
            probabilityOfPrecipitation = entity.precipitationChance ?: 0.0,
            visibility = entity.visibility?.toInt() ?: 0,
            dewPoint = 0.0 // Not stored in database, would need to calculate or add field
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
            logger.e(ex) { "Repository operation $operationName failed for weather" }
            val mappedException = exceptionMapper.mapToWeatherDomainException(ex, operationName)
            Result.failure(mappedException.message ?: "Unknown error", mappedException)
        }
    }

    private data class CachedWeather(
        val weather: Weather,
        val expiresAt: Instant
    ) {
        val isExpired: Boolean
            get() = Clock.System.now() > expiresAt
    }
}