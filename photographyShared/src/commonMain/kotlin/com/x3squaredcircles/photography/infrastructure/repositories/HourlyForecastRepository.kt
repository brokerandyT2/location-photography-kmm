// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/HourlyForecastRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.HourlyForecast
import com.x3squaredcircles.core.infrastructure.repositories.IHourlyForecastRepository
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class HourlyForecastRepository(
    private val database: PhotographyDatabase
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting all hourly forecasts: ${e.message}", e)
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                    Result.success(forecast)
                } else {
                    Result.failure("Hourly forecast not found")
                }
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecast by ID: $id - ${e.message}", e)
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecasts by weather ID: $weatherId - ${e.message}", e)
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecasts by weather and time range - ${e.message}", e)
        }
    }

    override suspend fun getNext24HoursAsync(weatherId: Int, fromTime: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val endTime = fromTime + (24 * 60 * 60 * 1000)
                val entities = database.hourlyForecastQueries.selectNext24Hours(
                    weatherId.toLong(),
                    fromTime,
                    endTime
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting next 24 hours forecast - ${e.message}", e)
        }
    }

    override suspend fun getNext7DaysAsync(weatherId: Int, fromTime: Long): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val endTime = fromTime + (7 * 24 * 60 * 60 * 1000)
                val entities = database.hourlyForecastQueries.selectNext7Days(
                    weatherId.toLong(),
                    fromTime,
                    endTime
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    HourlyForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastTime = entity.forecastTime,
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting next 7 days hourly forecast - ${e.message}", e)
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecasts for day - ${e.message}", e)
        }
    }

    override suspend fun getBestConditionsInRangeAsync(weatherId: Int, startTime: Long, endTime: Long, count: Int): Result<List<HourlyForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.hourlyForecastQueries.selectBestConditionsInRange(
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting best conditions in range - ${e.message}", e)
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover.toInt(),
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting golden hours forecast - ${e.message}", e)
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecasts by location ID: $locationId - ${e.message}", e)
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
                        temperature = entity.temperature ?: 0.0,
                        feelsLike = entity.feelsLike ?: 0.0,
                        humidity = entity.humidity?.toInt() ?: 0,
                        pressure = entity.pressure?.toInt() ?: 0,
                        visibility = entity.visibility ?: 0.0,
                        uvIndex = entity.uvIndex ?: 0.0,
                        windSpeed = entity.windSpeed ?: 0.0,
                        windDirection = entity.windDirection ?: 0.0,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover?.toInt() ?: 0,
                        precipitationChance = entity.precipitationChance ?: 0.0,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition ?: "",
                        description = entity.description ?: "",
                        icon = entity.icon ?: ""
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecasts by location and time range - ${e.message}", e)
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
                    humidity = hourlyForecast.humidity.toDouble(),
                    pressure = hourlyForecast.pressure.toDouble(),
                    visibility = hourlyForecast.visibility,
                    uvIndex = hourlyForecast.uvIndex,
                    windSpeed = hourlyForecast.windSpeed,
                    windDirection = hourlyForecast.windDirection,
                    windGust = hourlyForecast.windGust,
                    cloudCover = hourlyForecast.cloudCover.toDouble(),
                    precipitationChance = hourlyForecast.precipitationChance,
                    precipitationAmount = hourlyForecast.precipitationAmount,
                    condition = hourlyForecast.condition,
                    description = hourlyForecast.description,
                    icon = hourlyForecast.icon
                )

                val insertedId = database.hourlyForecastQueries.transactionWithResult {
                    database.hourlyForecastQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newForecast = hourlyForecast.copy(id = insertedId)
                Result.success(newForecast)
            }
        } catch (e: Exception) {
            Result.failure("Error creating hourly forecast - ${e.message}", e)
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
                            humidity = forecast.humidity.toDouble(),
                            pressure = forecast.pressure.toDouble(),
                            visibility = forecast.visibility,
                            uvIndex = forecast.uvIndex,
                            windSpeed = forecast.windSpeed,
                            windDirection = forecast.windDirection,
                            windGust = forecast.windGust,
                            cloudCover = forecast.cloudCover.toDouble(),
                            precipitationChance = forecast.precipitationChance,
                            precipitationAmount = forecast.precipitationAmount,
                            condition = forecast.condition,
                            description = forecast.description,
                            icon = forecast.icon
                        )
                        newForecasts.add(forecast)
                    }
                }

                Result.success(newForecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error creating batch hourly forecasts - ${e.message}", e)
        }
    }

    override suspend fun updateAsync(hourlyForecast: HourlyForecast): Result<HourlyForecast> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.update(
                    temperature = hourlyForecast.temperature,
                    feelsLike = hourlyForecast.feelsLike,
                    humidity = hourlyForecast.humidity.toDouble(),
                    pressure = hourlyForecast.pressure.toDouble(),
                    visibility = hourlyForecast.visibility,
                    uvIndex = hourlyForecast.uvIndex,
                    windSpeed = hourlyForecast.windSpeed,
                    windDirection = hourlyForecast.windDirection,
                    windGust = hourlyForecast.windGust,
                    cloudCover = hourlyForecast.cloudCover.toDouble(),
                    precipitationChance = hourlyForecast.precipitationChance,
                    precipitationAmount = hourlyForecast.precipitationAmount,
                    condition = hourlyForecast.condition,
                    description = hourlyForecast.description,
                    icon = hourlyForecast.icon,
                    id = hourlyForecast.id.toLong()
                )

                Result.success(hourlyForecast)
            }
        } catch (e: Exception) {
            Result.failure("Error updating hourly forecast - ${e.message}", e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.deleteById(id.toLong())
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting hourly forecast - ${e.message}", e)
        }
    }

    override suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.deleteByWeatherId(weatherId.toLong())
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting hourly forecasts by weather ID - ${e.message}", e)
        }
    }

    override suspend fun deleteOlderThanAsync(olderThan: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.deleteOlderThan(olderThan)
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting old hourly forecasts - ${e.message}", e)
        }
    }

    override suspend fun deleteByWeatherAndTimeRangeAsync(weatherId: Int, startTime: Long, endTime: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                database.hourlyForecastQueries.deleteByWeatherAndTimeRange(
                    weatherId.toLong(),
                    startTime,
                    endTime
                )
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting hourly forecasts by time range - ${e.message}", e)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.hourlyForecastQueries.getCount().executeAsOne()
                Result.success(count.toInt())
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecast count - ${e.message}", e)
        }
    }

    override suspend fun getCountByWeatherAsync(weatherId: Int): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.hourlyForecastQueries.getCountByWeather(weatherId.toLong()).executeAsOne()
                Result.success(count.toInt())
            }
        } catch (e: Exception) {
            Result.failure("Error getting hourly forecast count by weather - ${e.message}", e)
        }
    }

    override suspend fun existsForWeatherAndTimeAsync(weatherId: Int, forecastTime: Long): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.hourlyForecastQueries.existsForWeatherAndTime(weatherId.toLong(), forecastTime).executeAsOne()

                Result.success(exists)
            }
        } catch (e: Exception) {
            Result.failure("Error checking if hourly forecast exists - ${e.message}", e)
        }
    }
}