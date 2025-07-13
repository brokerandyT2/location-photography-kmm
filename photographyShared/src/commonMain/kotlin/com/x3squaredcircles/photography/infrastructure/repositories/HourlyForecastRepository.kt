// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/HourlyForecastRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.core.domain.entities.HourlyForecast
import com.x3squaredcircles.photographyshared.infrastructure.database.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock

class HourlyForecastRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : IHourlyForecastRepository {

    override suspend fun getAllAsync(): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectAll().executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all hourly forecasts", e)
            Result.failure(e)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<HourlyForecast> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.hourlyForecastQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val forecast = HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                    Result.success(forecast)
                } else {
                    Result.failure(Exception("Hourly forecast not found"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecast by ID: $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getByWeatherIdAsync(weatherId: Int): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectByWeatherId(weatherId.toLong()).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecasts by weather ID: $weatherId", e)
            Result.failure(e)
        }
    }

    override suspend fun getByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Long, endTime: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectByWeatherAndTimeRange(
                    weatherId.toLong(),
                    startTime,
                    endTime
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecasts by weather and time range", e)
            Result.failure(e)
        }
    }

    override suspend fun getNext24HoursAsync(weatherId: Int, fromTime: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectNext24Hours(weatherId.toLong(), fromTime).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting next 24 hours forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun getNext7DaysAsync(weatherId: Int, fromTime: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectNext7Days(weatherId.toLong(), fromTime).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting next 7 days hourly forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun getForDayAsync(weatherId: Int, startOfDay: Long, endOfDay: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectForDay(
                    weatherId.toLong(),
                    startOfDay,
                    endOfDay
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecasts for day", e)
            Result.failure(e)
        }
    }

    override suspend fun getBestConditionsInRangeAsync(weatherId: Int, startTime: Long, endTime: Long, count: Int): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectBestConditions(
                    weatherId.toLong(),
                    startTime,
                    endTime,
                    count.toLong()
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting best conditions in range", e)
            Result.failure(e)
        }
    }

    override suspend fun getGoldenHoursAsync(weatherId: Int, startTime: Long, endTime: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectGoldenHours(
                    weatherId.toLong(),
                    startTime,
                    endTime
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting golden hours forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun getByLocationIdAsync(locationId: Int): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectByLocationId(locationId.toLong()).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecasts by location ID: $locationId", e)
            Result.failure(e)
        }
    }

    override suspend fun getByLocationAndTimeRangeAsync(locationId: Int, startTime: Long, endTime: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectByLocationAndTimeRange(
                    locationId.toLong(),
                    startTime,
                    endTime
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature,
                        feelsLike = entity.feelsLike,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility,
                        dewPoint = entity.dewPoint
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecasts by location and time range", e)
            Result.failure(e)
        }
    }

    override suspend fun createAsync(hourlyForecast: HourlyForecast): Result<HourlyForecast> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.insert(
                    weatherId = hourlyForecast.weatherId.toLong(),
                    forecastTime = hourlyForecast.forecastTime,
                    temperature = hourlyForecast.temperature,
                    feelsLike = hourlyForecast.feelsLike,
                    humidity = hourlyForecast.humidity,
                    pressure = hourlyForecast.pressure,
                    windSpeed = hourlyForecast.windSpeed,
                    windDirection = hourlyForecast.windDirection,
                    windGust = hourlyForecast.windGust,
                    cloudCover = hourlyForecast.cloudCover,
                    precipitationChance = hourlyForecast.precipitationChance,
                    precipitationAmount = hourlyForecast.precipitationAmount,
                    weatherCondition = hourlyForecast.weatherCondition,
                    uvIndex = hourlyForecast.uvIndex,
                    visibility = hourlyForecast.visibility,
                    dewPoint = hourlyForecast.dewPoint
                )

                val insertedId = database.hourlyForecastQueries.transactionWithResult {
                    database.hourlyForecastQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newForecast = hourlyForecast.copy(id = insertedId)

                logger.logInformation("Created hourly forecast with ID: $insertedId")
                Result.success(newForecast)
            }
        } catch (e: Exception) {
            logger.logError("Error creating hourly forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun createBatchAsync(hourlyForecasts: List<HourlyForecast>): Result<List<HourlyForecast>> {
        return try {
            if (hourlyForecasts.isEmpty()) {
                return Result.success(emptyList())
            }

            withContext(Dispatchers.IO) {
                val newForecasts = mutableListOf<HourlyForecast>()
                database.transaction {
                    hourlyForecasts.forEach { forecast ->
                        database.hourlyForecastQueries.insert(
                            weatherId = forecast.weatherId.toLong(),
                            forecastTime = forecast.forecastTime,
                            temperature = forecast.temperature,
                            feelsLike = forecast.feelsLike,
                            humidity = forecast.humidity,
                            pressure = forecast.pressure,
                            windSpeed = forecast.windSpeed,
                            windDirection = forecast.windDirection,
                            windGust = forecast.windGust,
                            cloudCover = forecast.cloudCover,
                            precipitationChance = forecast.precipitationChance,
                            precipitationAmount = forecast.precipitationAmount,
                            weatherCondition = forecast.weatherCondition,
                            uvIndex = forecast.uvIndex,
                            visibility = forecast.visibility,
                            dewPoint = forecast.dewPoint
                        )
                        newForecasts.add(forecast)
                    }
                }

                logger.logInformation("Created ${hourlyForecasts.size} hourly forecasts")
                Result.success(newForecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error creating batch hourly forecasts", e)
            Result.failure(e)
        }
    }

    override suspend fun updateAsync(hourlyForecast: HourlyForecast): Result<HourlyForecast> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.update(
                    weatherId = hourlyForecast.weatherId.toLong(),
                    forecastTime = hourlyForecast.forecastTime,
                    temperature = hourlyForecast.temperature,
                    feelsLike = hourlyForecast.feelsLike,
                    humidity = hourlyForecast.humidity,
                    pressure = hourlyForecast.pressure,
                    windSpeed = hourlyForecast.windSpeed,
                    windDirection = hourlyForecast.windDirection,
                    windGust = hourlyForecast.windGust,
                    cloudCover = hourlyForecast.cloudCover,
                    precipitationChance = hourlyForecast.precipitationChance,
                    precipitationAmount = hourlyForecast.precipitationAmount,
                    weatherCondition = hourlyForecast.weatherCondition,
                    uvIndex = hourlyForecast.uvIndex,
                    visibility = hourlyForecast.visibility,
                    dewPoint = hourlyForecast.dewPoint,
                    id = hourlyForecast.id.toLong()
                )

                logger.logInformation("Updated hourly forecast with ID: ${hourlyForecast.id}")
                Result.success(hourlyForecast)
            }
        } catch (e: Exception) {
            logger.logError("Error updating hourly forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.deleteById(id.toLong())
                logger.logInformation("Deleted hourly forecast with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting hourly forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.deleteByWeatherId(weatherId.toLong())
                logger.logInformation("Deleted all hourly forecasts for weather ID: $weatherId")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting hourly forecasts by weather ID", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteOlderThanAsync(olderThan: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val deletedCount = database.hourlyForecastQueries.deleteOlderThan(olderThan).executeAsOne().toInt()
                logger.logInformation("Deleted $deletedCount old hourly forecasts")
                Result.success(deletedCount)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting old hourly forecasts", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Long, endTime: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val deletedCount = database.hourlyForecastQueries.deleteByWeatherAndTimeRange(
                    weatherId.toLong(),
                    startTime,
                    endTime
                ).executeAsOne().toInt()
                logger.logInformation("Deleted $deletedCount hourly forecasts in time range")
                Result.success(deletedCount)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting hourly forecasts by time range", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.hourlyForecastQueries.getCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecast count", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountByWeatherAsync(weatherId: Int): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.hourlyForecastQueries.getCountByWeather(weatherId.toLong()).executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting hourly forecast count by weather", e)
            Result.failure(e)
        }
    }

    override suspend fun existsForWeatherAndTimeAsync(weatherId: Int, forecastTime: Long): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.hourlyForecastQueries.existsForWeatherAndTime(weatherId.toLong(), forecastTime).executeAsOne()
                Result.success(exists)
            }
        } catch (e: Exception) {
            logger.logError("Error checking if hourly forecast exists", e)
            Result.failure(e)
        }
    }
}




