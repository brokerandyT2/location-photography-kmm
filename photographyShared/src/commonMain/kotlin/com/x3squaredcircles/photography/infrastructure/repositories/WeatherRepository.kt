// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/WeatherRepository.kt
package com.x3squaredcircles.photography.infrastructure.repositories

import com.x3squaredcircles.core.domain.entities.Weather
import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.core.domain.entities.HourlyForecast
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.WindInfo
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.IWeatherRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.WeatherCacheType
import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import co.touchlab.kermit.Logger
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

    override suspend fun getByIdAsync(id: Int): Weather? {
        return executeWithExceptionMapping("GetById") {
            val entity = database.weatherQueries.selectById(id.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getAllAsync(): List<Weather> {
        return executeWithExceptionMapping("GetAll") {
            database.weatherQueries.selectAll()
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getByLocationIdAsync(locationId: Int): Weather? {
        return executeWithExceptionMapping("GetByLocationId") {
            val entity = database.weatherQueries.selectByLocationId(locationId.toLong()).executeAsOneOrNull()
            entity?.let { mapToDomain(it) }
        }
    }

    override suspend fun getByCoordinatesAsync(coordinate: Coordinate): Weather? {
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
    ): List<Weather> {
        return executeWithExceptionMapping("GetByLocationAndTimeRange") {
            database.weatherQueries.selectByLocationAndTimeRange(
                locationId.toLong(),
                startTime.toEpochMilliseconds(),
                endTime.toEpochMilliseconds()
            ).executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getRecentAsync(count: Int): List<Weather> {
        return executeWithExceptionMapping("GetRecent") {
            database.weatherQueries.selectRecent(count.toLong())
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun getExpiredAsync(olderThan: Instant): List<Weather> {
        return executeWithExceptionMapping("GetExpired") {
            database.weatherQueries.selectExpired(olderThan.toEpochMilliseconds())
                .executeAsList()
                .map { mapToDomain(it) }
        }
    }

    override suspend fun addAsync(weather: Weather): Weather {
        return executeWithExceptionMapping("Add") {
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

    override suspend fun updateAsync(weather: Weather) {
        executeWithExceptionMapping("Update") {
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

    override suspend fun deleteAsync(weather: Weather) {
        softDeleteAsync(weather)
    }

    override suspend fun softDeleteAsync(weather: Weather) {
        executeWithExceptionMapping("SoftDelete") {
            database.weatherQueries.softDelete(weather.id.toLong())
            val rowsAffected = database.weatherQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Weather with ID ${weather.id} not found for soft delete")
            }

            logger.i { "Soft deleted weather with ID ${weather.id}" }
        }
    }

    override suspend fun softDeleteByLocationIdAsync(locationId: Int) {
        executeWithExceptionMapping("SoftDeleteByLocationId") {
            database.weatherQueries.softDeleteByLocationId(locationId.toLong())
            val rowsAffected = database.weatherQueries.changes().executeAsOne()

            logger.i { "Soft deleted $rowsAffected weather records for location ID $locationId" }
        }
    }

    override suspend fun hasFreshDataAsync(locationId: Int, maxAge: Instant): Boolean {
        return executeWithExceptionMapping("HasFreshData") {
            database.weatherQueries.hasFreshData(
                locationId.toLong(),
                maxAge.toEpochMilliseconds()
            ).executeAsOne()
        }
    }

    override suspend fun hasFreshDataForCoordinatesAsync(coordinate: Coordinate, maxAge: Instant): Boolean {
        return executeWithExceptionMapping("HasFreshDataForCoordinates") {
            database.weatherQueries.hasFreshDataForCoordinates(
                coordinate.latitude,
                coordinate.longitude,
                maxAge.toEpochMilliseconds()
            ).executeAsOne()
        }
    }

    override suspend fun cleanupExpiredAsync(olderThan: Instant): Int {
        return executeWithExceptionMapping("CleanupExpired") {
            database.weatherQueries.cleanupExpired(olderThan.toEpochMilliseconds())
            val rowsAffected = database.weatherQueries.changes().executeAsOne()

            logger.i { "Cleaned up $rowsAffected expired weather records" }
            rowsAffected.toInt()
        }
    }

    override suspend fun getCountAsync(): Long {
        return executeWithExceptionMapping("GetCount") {
            database.weatherQueries.getCount().executeAsOne()
        }
    }

    override suspend fun addForecastAsync(forecast: WeatherForecast): WeatherForecast {
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
                precipitationAmount = forecast.precipitation,
                condition = forecast.description,
                description = forecast.description,
                icon = forecast.icon,
                sunrise = forecast.sunrise.toEpochMilliseconds(),
                sunset = forecast.sunset.toEpochMilliseconds(),
                moonPhase = forecast.moonPhase,
                moonrise = forecast.moonRise?.toEpochMilliseconds(),
                moonset = forecast.moonSet?.toEpochMilliseconds()
            )

            val id = database.dailyForecastQueries.lastInsertRowId().executeAsOne()
            val savedForecast = WeatherForecast.fromPersistence(
                id = id.toInt(),
                weatherId = forecast.weatherId,
                date = forecast.date,
                sunrise = forecast.sunrise,
                sunset = forecast.sunset,
                temperature = forecast.temperature,
                minTemperature = forecast.minTemperature,
                maxTemperature = forecast.maxTemperature,
                description = forecast.description,
                icon = forecast.icon,
                wind = forecast.wind,
                humidity = forecast.humidity,
                pressure = forecast.pressure,
                clouds = forecast.clouds,
                uvIndex = forecast.uvIndex,
                precipitation = forecast.precipitation,
                moonRise = forecast.moonRise,
                moonSet = forecast.moonSet,
                moonPhase = forecast.moonPhase
            )

            logger.i { "Created weather forecast with ID ${savedForecast.id}" }
            savedForecast
        }
    }

    override suspend fun addForecastsAsync(forecasts: List<WeatherForecast>): List<WeatherForecast> {
        return executeWithExceptionMapping("AddForecasts") {
            if (forecasts.isEmpty()) return@executeWithExceptionMapping forecasts

            database.transactionWithResult {
                val result = mutableListOf<WeatherForecast>()

                forecasts.forEach { forecast ->
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
                        precipitationAmount = forecast.precipitation,
                        condition = forecast.description,
                        description = forecast.description,
                        icon = forecast.icon,
                        sunrise = forecast.sunrise.toEpochMilliseconds(),
                        sunset = forecast.sunset.toEpochMilliseconds(),
                        moonPhase = forecast.moonPhase,
                        moonrise = forecast.moonRise?.toEpochMilliseconds(),
                        moonset = forecast.moonSet?.toEpochMilliseconds()
                    )

                    val id = database.dailyForecastQueries.lastInsertRowId().executeAsOne()
                    val savedForecast = WeatherForecast.fromPersistence(
                        id = id.toInt(),
                        weatherId = forecast.weatherId,
                        date = forecast.date,
                        sunrise = forecast.sunrise,
                        sunset = forecast.sunset,
                        temperature = forecast.temperature,
                        minTemperature = forecast.minTemperature,
                        maxTemperature = forecast.maxTemperature,
                        description = forecast.description,
                        icon = forecast.icon,
                        wind = forecast.wind,
                        humidity = forecast.humidity,
                        pressure = forecast.pressure,
                        clouds = forecast.clouds,
                        uvIndex = forecast.uvIndex,
                        precipitation = forecast.precipitation,
                        moonRise = forecast.moonRise,
                        moonSet = forecast.moonSet,
                        moonPhase = forecast.moonPhase
                    )
                    result.add(savedForecast)
                }

                logger.i { "Bulk created ${result.size} weather forecasts" }
                result
            }
        }
    }

    override suspend fun updateForecastAsync(forecast: WeatherForecast) {
        executeWithExceptionMapping("UpdateForecast") {
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
                precipitationChance = forecast.precipitation,
                precipitationAmount = forecast.precipitation,
                condition = forecast.description,
                description = forecast.description,
                icon = forecast.icon,
                sunrise = forecast.sunrise.toEpochMilliseconds(),
                sunset = forecast.sunset.toEpochMilliseconds(),
                moonPhase = forecast.moonPhase,
                moonrise = forecast.moonRise?.toEpochMilliseconds(),
                moonset = forecast.moonSet?.toEpochMilliseconds(),
                id = forecast.id.toLong()
            )
            val rowsAffected = database.dailyForecastQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Weather forecast with ID ${forecast.id} not found for update")
            }

            logger.i { "Updated weather forecast with ID ${forecast.id}" }
        }
    }

    override suspend fun deleteForecastAsync(forecast: WeatherForecast) {
        executeWithExceptionMapping("DeleteForecast") {
            database.dailyForecastQueries.deleteById(forecast.id.toLong())
            val rowsAffected = database.dailyForecastQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Weather forecast with ID ${forecast.id} not found for deletion")
            }

            logger.i { "Deleted weather forecast with ID ${forecast.id}" }
        }
    }

    override suspend fun deleteForecastsByWeatherIdAsync(weatherId: Int) {
        executeWithExceptionMapping("DeleteForecastsByWeatherId") {
            database.dailyForecastQueries.deleteByWeatherId(weatherId.toLong())
            val rowsAffected = database.dailyForecastQueries.changes().executeAsOne()

            logger.i { "Deleted $rowsAffected weather forecasts for weather ID $weatherId" }
        }
    }

    override suspend fun getForecastsByWeatherIdAsync(weatherId: Int): List<WeatherForecast> {
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
    ): List<WeatherForecast> {
        return executeWithExceptionMapping("GetForecastsByWeatherAndDateRange") {
            database.dailyForecastQueries.selectByWeatherAndDateRange(
                weatherId.toLong(),
                startDate.toEpochMilliseconds(),
                endDate.toEpochMilliseconds()
            ).executeAsList()
                .map { mapForecastToDomain(it) }
        }
    }

    override suspend fun getForecastByWeatherAndDateAsync(weatherId: Int, date: Instant): WeatherForecast? {
        return executeWithExceptionMapping("GetForecastByWeatherAndDate") {
            val entity = database.dailyForecastQueries.selectByDate(
                weatherId.toLong(),
                date.toEpochMilliseconds()
            ).executeAsOneOrNull()
            entity?.let { mapForecastToDomain(it) }
        }
    }

    override suspend fun addHourlyForecastAsync(hourlyForecast: HourlyForecast): HourlyForecast {
        return executeWithExceptionMapping("AddHourlyForecast") {
            database.hourlyForecastQueries.insert(
                weatherId = hourlyForecast.weatherId.toLong(),
                forecastTime = hourlyForecast.dateTime.toEpochMilliseconds(),
                temperature = hourlyForecast.temperature,
                feelsLike = hourlyForecast.feelsLike,
                humidity = hourlyForecast.humidity.toDouble(),
                pressure = hourlyForecast.pressure.toDouble(),
                visibility = hourlyForecast.visibility.toDouble(),
                uvIndex = hourlyForecast.uvIndex,
                windSpeed = hourlyForecast.wind.speed,
                windDirection = hourlyForecast.wind.direction,
                windGust = hourlyForecast.wind.gust,
                cloudCover = hourlyForecast.clouds.toDouble(),
                precipitationChance = hourlyForecast.probabilityOfPrecipitation,
                precipitationAmount = null,
                condition = hourlyForecast.description,
                description = hourlyForecast.description,
                icon = hourlyForecast.icon
            )

            val id = database.hourlyForecastQueries.lastInsertRowId().executeAsOne()
            val savedHourlyForecast = HourlyForecast.fromPersistence(
                id = id.toInt(),
                weatherId = hourlyForecast.weatherId,
                dateTime = hourlyForecast.dateTime,
                temperature = hourlyForecast.temperature,
                feelsLike = hourlyForecast.feelsLike,
                description = hourlyForecast.description,
                icon = hourlyForecast.icon,
                wind = hourlyForecast.wind,
                humidity = hourlyForecast.humidity,
                pressure = hourlyForecast.pressure,
                clouds = hourlyForecast.clouds,
                uvIndex = hourlyForecast.uvIndex,
                probabilityOfPrecipitation = hourlyForecast.probabilityOfPrecipitation,
                visibility = hourlyForecast.visibility,
                dewPoint = hourlyForecast.dewPoint
            )

            logger.i { "Created hourly forecast with ID ${savedHourlyForecast.id}" }
            savedHourlyForecast
        }
    }

    override suspend fun addHourlyForecastsAsync(hourlyForecasts: List<HourlyForecast>): List<HourlyForecast> {
        return executeWithExceptionMapping("AddHourlyForecasts") {
            if (hourlyForecasts.isEmpty()) return@executeWithExceptionMapping hourlyForecasts

            database.transactionWithResult {
                val result = mutableListOf<HourlyForecast>()

                hourlyForecasts.forEach { hourlyForecast ->
                    database.hourlyForecastQueries.insert(
                        weatherId = hourlyForecast.weatherId.toLong(),
                        forecastTime = hourlyForecast.dateTime.toEpochMilliseconds(),
                        temperature = hourlyForecast.temperature,
                        feelsLike = hourlyForecast.feelsLike,
                        humidity = hourlyForecast.humidity.toDouble(),
                        pressure = hourlyForecast.pressure.toDouble(),
                        visibility = hourlyForecast.visibility.toDouble(),
                        uvIndex = hourlyForecast.uvIndex,
                        windSpeed = hourlyForecast.wind.speed,
                        windDirection = hourlyForecast.wind.direction,
                        windGust = hourlyForecast.wind.gust,
                        cloudCover = hourlyForecast.clouds.toDouble(),
                        precipitationChance = hourlyForecast.probabilityOfPrecipitation,
                        precipitationAmount = null,
                        condition = hourlyForecast.description,
                        description = hourlyForecast.description,
                        icon = hourlyForecast.icon
                    )

                    val id = database.hourlyForecastQueries.lastInsertRowId().executeAsOne()
                    val savedHourlyForecast = HourlyForecast.fromPersistence(
                        id = id.toInt(),
                        weatherId = hourlyForecast.weatherId,
                        dateTime = hourlyForecast.dateTime,
                        temperature = hourlyForecast.temperature,
                        feelsLike = hourlyForecast.feelsLike,
                        description = hourlyForecast.description,
                        icon = hourlyForecast.icon,
                        wind = hourlyForecast.wind,
                        humidity = hourlyForecast.humidity,
                        pressure = hourlyForecast.pressure,
                        clouds = hourlyForecast.clouds,
                        uvIndex = hourlyForecast.uvIndex,
                        probabilityOfPrecipitation = hourlyForecast.probabilityOfPrecipitation,
                        visibility = hourlyForecast.visibility,
                        dewPoint = hourlyForecast.dewPoint
                    )
                    result.add(savedHourlyForecast)
                }

                logger.i { "Bulk created ${result.size} hourly forecasts" }
                result
            }
        }
    }

    override suspend fun updateHourlyForecastAsync(hourlyForecast: HourlyForecast) {
        executeWithExceptionMapping("UpdateHourlyForecast") {
            database.hourlyForecastQueries.update(
                temperature = hourlyForecast.temperature,
                feelsLike = hourlyForecast.feelsLike,
                humidity = hourlyForecast.humidity.toDouble(),
                pressure = hourlyForecast.pressure.toDouble(),
                visibility = hourlyForecast.visibility.toDouble(),
                uvIndex = hourlyForecast.uvIndex,
                windSpeed = hourlyForecast.wind.speed,
                windDirection = hourlyForecast.wind.direction,
                windGust = hourlyForecast.wind.gust,
                cloudCover = hourlyForecast.clouds.toDouble(),
                precipitationChance = hourlyForecast.probabilityOfPrecipitation,
                precipitationAmount = null,
                condition = hourlyForecast.description,
                description = hourlyForecast.description,
                icon = hourlyForecast.icon,
                id = hourlyForecast.id.toLong()
            )
            val rowsAffected = database.hourlyForecastQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Hourly forecast with ID ${hourlyForecast.id} not found for update")
            }

            logger.i { "Updated hourly forecast with ID ${hourlyForecast.id}" }
        }
    }

    override suspend fun deleteHourlyForecastAsync(hourlyForecast: HourlyForecast) {
        executeWithExceptionMapping("DeleteHourlyForecast") {
            database.hourlyForecastQueries.deleteById(hourlyForecast.id.toLong())
            val rowsAffected = database.hourlyForecastQueries.changes().executeAsOne()

            if (rowsAffected == 0L) {
                throw IllegalArgumentException("Hourly forecast with ID ${hourlyForecast.id} not found for deletion")
            }

            logger.i { "Deleted hourly forecast with ID ${hourlyForecast.id}" }
        }
    }

    override suspend fun deleteHourlyForecastsByWeatherIdAsync(weatherId: Int) {
        executeWithExceptionMapping("DeleteHourlyForecastsByWeatherId") {
            database.hourlyForecastQueries.deleteByWeatherId(weatherId.toLong())
            val rowsAffected = database.hourlyForecastQueries.changes().executeAsOne()

            logger.i { "Deleted $rowsAffected hourly forecasts for weather ID $weatherId" }
        }
    }

    override suspend fun getHourlyForecastsByWeatherIdAsync(weatherId: Int): List<HourlyForecast> {
        return executeWithExceptionMapping("GetHourlyForecastsByWeatherId") {
            database.hourlyForecastQueries.selectByWeatherId(weatherId.toLong())
                .executeAsList()
                .map { mapHourlyForecastToDomain(it) }
        }
    }

    override suspend fun getHourlyForecastsByWeatherAndTimeRangeAsync(
        weatherId: Int,
        startTime: Instant,
        endTime: Instant
    ): List<HourlyForecast> {
        return executeWithExceptionMapping("GetHourlyForecastsByWeatherAndTimeRange") {
            database.hourlyForecastQueries.selectByWeatherAndTimeRange(
                weatherId.toLong(),
                startTime.toEpochMilliseconds(),
                endTime.toEpochMilliseconds()
            ).executeAsList()
                .map { mapHourlyForecastToDomain(it) }
        }
    }

    override suspend fun getNext24HoursForecastAsync(weatherId: Int, fromTime: Instant): List<HourlyForecast> {
        return executeWithExceptionMapping("GetNext24HoursForecast") {
            val endTime = fromTime.plus(24.hours)
            database.hourlyForecastQueries.selectNext24Hours(
                weatherId.toLong(),
                fromTime.toEpochMilliseconds(),
                endTime.toEpochMilliseconds()
            ).executeAsList()
                .map { mapHourlyForecastToDomain(it) }
        }
    }

    override suspend fun getHourlyForecastsForDayAsync(weatherId: Int, date: Instant): List<HourlyForecast> {
        return executeWithExceptionMapping("GetHourlyForecastsForDay") {
            val startOfDay = date.toEpochMilliseconds()
            val endOfDay = startOfDay + 86400000L // 24 hours in milliseconds
            database.hourlyForecastQueries.selectForDay(
                weatherId.toLong(),
                startOfDay,
                endOfDay
            ).executeAsList()
                .map { mapHourlyForecastToDomain(it) }
        }
    }

    override suspend fun cleanupOldForecastsAsync(olderThan: Instant): Int {
        return executeWithExceptionMapping("CleanupOldForecasts") {
            database.dailyForecastQueries.deleteOlderThan(olderThan.toEpochMilliseconds())
            val rowsAffected = database.dailyForecastQueries.changes().executeAsOne()

            logger.i { "Cleaned up $rowsAffected old daily forecasts" }
            rowsAffected.toInt()
        }
    }

    override suspend fun cleanupOldHourlyForecastsAsync(olderThan: Instant): Int {
        return executeWithExceptionMapping("CleanupOldHourlyForecasts") {
            database.hourlyForecastQueries.deleteOlderThan(olderThan.toEpochMilliseconds())
            val rowsAffected = database.hourlyForecastQueries.changes().executeAsOne()

            logger.i { "Cleaned up $rowsAffected old hourly forecasts" }
            rowsAffected.toInt()
        }
    }

    override fun clearCache() {
        if (cacheMutex.tryLock()) {
            try {
                weatherCache.clear()
                logger.i { "Weather cache cleared" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(id: Int) {
        if (cacheMutex.tryLock()) {
            try {
                weatherCache.remove(id)
                logger.d { "Removed weather $id from cache" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    override fun clearCache(locationId: Int, cacheType: WeatherCacheType) {
        if (cacheMutex.tryLock()) {
            try {
                when (cacheType) {
                    WeatherCacheType.WEATHER -> {
                        weatherCache.entries.removeAll { it.value.weather.locationId == locationId }
                    }
                    WeatherCacheType.ALL -> {
                        weatherCache.entries.removeAll { it.value.weather.locationId == locationId }
                    }
                    else -> {
                        // For forecast-specific cache types, we'd need separate caches
                        logger.d { "Cache type $cacheType not implemented for location-specific clearing" }
                    }
                }
                logger.d { "Cleared weather cache for location $locationId, type $cacheType" }
            } finally {
                cacheMutex.unlock()
            }
        }
    }

    private fun mapToDomain(entity: com.x3squaredcircles.photographyshared.db.Weather): Weather {
        return Weather.fromPersistence(
            id = entity.id.toInt(),
            locationId = entity.locationId.toInt(),
            coordinate = Coordinate.create(entity.latitude, entity.longitude),
            timezone = entity.timezone,
            timezoneOffset = entity.timezoneOffset.toInt(),
            lastUpdate = Instant.fromEpochMilliseconds(entity.lastUpdate)
        )
    }

    private fun mapForecastToDomain(entity: com.x3squaredcircles.photographyshared.db.DailyForecast): WeatherForecast {
        val wind = WindInfo(
            speed = entity.windSpeed ?: 0.0,
            direction = entity.windDirection ?: 0.0,
            gust = entity.windGust
        )

        return WeatherForecast.fromPersistence(
            id = entity.id.toInt(),
            weatherId = entity.weatherId.toInt(),
            date = Instant.fromEpochMilliseconds(entity.forecastDate),
            sunrise = Instant.fromEpochMilliseconds(entity.sunrise ?: 0L),
            sunset = Instant.fromEpochMilliseconds(entity.sunset ?: 0L),
            temperature = (entity.minTemperature ?: 0.0 + entity.maxTemperature!! ?: 0.0) / 2,
            minTemperature = entity.minTemperature ?: 0.0,
            maxTemperature = entity.maxTemperature ?: 0.0,
            description = entity.description ?: "",
            icon = entity.icon ?: "",
            wind = wind,
            humidity = entity.humidity?.toInt() ?: 0,
            pressure = entity.pressure?.toInt() ?: 0,
            clouds = entity.cloudCover?.toInt() ?: 0,
            uvIndex = entity.uvIndex ?: 0.0,
            precipitation = entity.precipitationAmount,
            moonRise = entity.moonrise?.let { Instant.fromEpochMilliseconds(it) },
            moonSet = entity.moonset?.let { Instant.fromEpochMilliseconds(it) },
            moonPhase = entity.moonPhase ?: 0.0
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
    ): T {
        return try {
            operation()
        } catch (ex: Exception) {
            logger.e(ex) { "Repository operation $operationName failed for weather" }
            throw exceptionMapper.mapToWeatherDomainException(ex, operationName)
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