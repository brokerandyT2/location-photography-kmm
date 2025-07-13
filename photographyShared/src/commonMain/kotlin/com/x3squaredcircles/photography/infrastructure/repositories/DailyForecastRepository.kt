// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/repositories/DailyForecastRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.infrastructure.services.ILoggingService
import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.photographyshared.infrastructure.database.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
class DailyForecastRepository(
    private val database: PhotographyDatabase,
    private val logger: ILoggingService
) : IDailyForecastRepository {
    override suspend fun getAllAsync(): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectAll().executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting all daily forecasts", e)
            Result.failure(e)
        }
    }

    override suspend fun getByIdAsync(id: Int): Result<WeatherForecast> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.dailyForecastQueries.selectById(id.toLong()).executeAsOneOrNull()
                if (entity != null) {
                    val forecast = WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                    Result.success(forecast)
                } else {
                    Result.failure(Exception("Daily forecast not found"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecast by ID: $id", e)
            Result.failure(e)
        }
    }

    override suspend fun getByWeatherIdAsync(weatherId: Int): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectByWeatherId(weatherId.toLong()).executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecasts by weather ID: $weatherId", e)
            Result.failure(e)
        }
    }

    override suspend fun getByWeatherAndDateRangeAsync(weatherId: Int, startDate: Long, endDate: Long): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectByWeatherAndDateRange(
                    weatherId.toLong(),
                    startDate,
                    endDate
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecasts by weather and date range", e)
            Result.failure(e)
        }
    }

    override suspend fun getNext7DaysAsync(weatherId: Int, fromDate: Long): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectNext7Days(weatherId.toLong(), fromDate).executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting next 7 days forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun getForDateAsync(weatherId: Int, date: Long): Result<WeatherForecast> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.dailyForecastQueries.selectByDate(weatherId.toLong(), date).executeAsOneOrNull()
                if (entity != null) {
                    val forecast = WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                    Result.success(forecast)
                } else {
                    Result.failure(Exception("Daily forecast not found for date"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecast for date", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentForecastAsync(weatherId: Int): Result<WeatherForecast> {
        return try {
            withContext(Dispatchers.IO) {
                val currentDate = Clock.System.now().epochSeconds
                val entity = database.dailyForecastQueries.selectCurrent(weatherId.toLong(), currentDate).executeAsOneOrNull()
                if (entity != null) {
                    val forecast = WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                    Result.success(forecast)
                } else {
                    Result.failure(Exception("Current forecast not found"))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting current forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun getBestPhotographyDaysAsync(weatherId: Int, startDate: Long, endDate: Long, count: Int): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectBestPhotographyDays(
                    weatherId.toLong(),
                    startDate,
                    endDate,
                    count.toLong()
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting best photography days", e)
            Result.failure(e)
        }
    }

    override suspend fun getByLocationIdAsync(locationId: Int): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectByLocationId(locationId.toLong()).executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecasts by location ID: $locationId", e)
            Result.failure(e)
        }
    }

    override suspend fun getByLocationAndDateRangeAsync(locationId: Int, startDate: Long, endDate: Long): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectByLocationAndDateRange(
                    locationId.toLong(),
                    startDate,
                    endDate
                ).executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        weatherCondition = entity.weatherCondition,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        uvIndex = entity.uvIndex,
                        visibility = entity.visibility
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecasts by location and date range", e)
            Result.failure(e)
        }
    }

    override suspend fun getSunriseSunsetAsync(weatherId: Int, date: Long): Result<Pair<Long?, Long?>> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.dailyForecastQueries.selectSunriseSunset(weatherId.toLong(), date).executeAsOneOrNull()
                if (entity != null) {
                    val sunrise = entity.sunrise
                    val sunset = entity.sunset
                    Result.success(Pair(sunrise, sunset))
                } else {
                    Result.success(Pair(null, null))
                }
            }
        } catch (e: Exception) {
            logger.logError("Error getting sunrise/sunset", e)
            Result.failure(e)
        }
    }

    override suspend fun getMoonPhaseAsync(weatherId: Int, date: Long): Result<Double?> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.dailyForecastQueries.selectMoonPhase(weatherId.toLong(), date).executeAsOneOrNull()
                val moonPhase = entity?.moonPhase
                Result.success(moonPhase)
            }
        } catch (e: Exception) {
            logger.logError("Error getting moon phase", e)
            Result.failure(e)
        }
    }

    override suspend fun createAsync(dailyForecast: WeatherForecast): Result<WeatherForecast> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.insert(
                    weatherId = dailyForecast.weatherId.toLong(),
                    forecastDate = dailyForecast.forecastDate,
                    minTemperature = dailyForecast.minTemperature,
                    maxTemperature = dailyForecast.maxTemperature,
                    humidity = dailyForecast.humidity,
                    pressure = dailyForecast.pressure,
                    windSpeed = dailyForecast.windSpeed,
                    windDirection = dailyForecast.windDirection,
                    cloudCover = dailyForecast.cloudCover,
                    precipitationChance = dailyForecast.precipitationChance,
                    precipitationAmount = dailyForecast.precipitationAmount,
                    weatherCondition = dailyForecast.weatherCondition,
                    sunrise = dailyForecast.sunrise,
                    sunset = dailyForecast.sunset,
                    moonPhase = dailyForecast.moonPhase,
                    uvIndex = dailyForecast.uvIndex,
                    visibility = dailyForecast.visibility
                )

                val insertedId = database.dailyForecastQueries.transactionWithResult {
                    database.dailyForecastQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newForecast = dailyForecast.copy(id = insertedId)

                logger.logInformation("Created daily forecast with ID: $insertedId")
                Result.success(newForecast)
            }
        } catch (e: Exception) {
            logger.logError("Error creating daily forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun createBatchAsync(dailyForecasts: List<WeatherForecast>): Result<List<WeatherForecast>> {
        return try {
            if (dailyForecasts.isEmpty()) {
                return Result.success(emptyList())
            }

            withContext(Dispatchers.IO) {
                val newForecasts = mutableListOf<WeatherForecast>()
                database.transaction {
                    dailyForecasts.forEach { forecast ->
                        database.dailyForecastQueries.insert(
                            weatherId = forecast.weatherId.toLong(),
                            forecastDate = forecast.forecastDate,
                            minTemperature = forecast.minTemperature,
                            maxTemperature = forecast.maxTemperature,
                            humidity = forecast.humidity,
                            pressure = forecast.pressure,
                            windSpeed = forecast.windSpeed,
                            windDirection = forecast.windDirection,
                            cloudCover = forecast.cloudCover,
                            precipitationChance = forecast.precipitationChance,
                            precipitationAmount = forecast.precipitationAmount,
                            weatherCondition = forecast.weatherCondition,
                            sunrise = forecast.sunrise,
                            sunset = forecast.sunset,
                            moonPhase = forecast.moonPhase,
                            uvIndex = forecast.uvIndex,
                            visibility = forecast.visibility
                        )
                        newForecasts.add(forecast)
                    }
                }

                logger.logInformation("Created ${dailyForecasts.size} daily forecasts")
                Result.success(newForecasts)
            }
        } catch (e: Exception) {
            logger.logError("Error creating batch daily forecasts", e)
            Result.failure(e)
        }
    }

    override suspend fun updateAsync(dailyForecast: WeatherForecast): Result<WeatherForecast> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.update(
                    weatherId = dailyForecast.weatherId.toLong(),
                    forecastDate = dailyForecast.forecastDate,
                    minTemperature = dailyForecast.minTemperature,
                    maxTemperature = dailyForecast.maxTemperature,
                    humidity = dailyForecast.humidity,
                    pressure = dailyForecast.pressure,
                    windSpeed = dailyForecast.windSpeed,
                    windDirection = dailyForecast.windDirection,
                    cloudCover = dailyForecast.cloudCover,
                    precipitationChance = dailyForecast.precipitationChance,
                    precipitationAmount = dailyForecast.precipitationAmount,
                    weatherCondition = dailyForecast.weatherCondition,
                    sunrise = dailyForecast.sunrise,
                    sunset = dailyForecast.sunset,
                    moonPhase = dailyForecast.moonPhase,
                    uvIndex = dailyForecast.uvIndex,
                    visibility = dailyForecast.visibility,
                    id = dailyForecast.id.toLong()
                )

                logger.logInformation("Updated daily forecast with ID: ${dailyForecast.id}")
                Result.success(dailyForecast)
            }
        } catch (e: Exception) {
            logger.logError("Error updating daily forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.deleteById(id.toLong())
                logger.logInformation("Deleted daily forecast with ID: $id")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting daily forecast", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.deleteByWeatherId(weatherId.toLong())
                logger.logInformation("Deleted all daily forecasts for weather ID: $weatherId")
                Result.success(true)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting daily forecasts by weather ID", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteOlderThanAsync(olderThan: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val deletedCount = database.dailyForecastQueries.deleteOlderThan(olderThan).executeAsOne().toInt()
                logger.logInformation("Deleted $deletedCount old daily forecasts")
                Result.success(deletedCount)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting old daily forecasts", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteByWeatherAndDateRangeAsync(weatherId: Int, startDate: Long, endDate: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val deletedCount = database.dailyForecastQueries.deleteByWeatherAndDateRange(
                    weatherId.toLong(),
                    startDate,
                    endDate
                ).executeAsOne().toInt()
                logger.logInformation("Deleted $deletedCount daily forecasts in date range")
                Result.success(deletedCount)
            }
        } catch (e: Exception) {
            logger.logError("Error deleting daily forecasts by date range", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.dailyForecastQueries.getCount().executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecast count", e)
            Result.failure(e)
        }
    }

    override suspend fun getCountByWeatherAsync(weatherId: Int): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.dailyForecastQueries.getCountByWeather(weatherId.toLong()).executeAsOne().toInt()
                Result.success(count)
            }
        } catch (e: Exception) {
            logger.logError("Error getting daily forecast count by weather", e)
            Result.failure(e)
        }
    }

    override suspend fun existsForWeatherAndDateAsync(weatherId: Int, date: Long): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.dailyForecastQueries.existsForWeatherAndDate(weatherId.toLong(), date).executeAsOne()
                Result.success(exists)
            }
        } catch (e: Exception) {
            logger.logError("Error checking if daily forecast exists", e)
            Result.failure(e)
        }
    }
}