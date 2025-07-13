// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/repositories/DailyForecastRepository.kt
package com.x3squaredcircles.photographyshared.infrastructure.repositories
import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.domain.entities.WeatherForecast
import com.x3squaredcircles.core.infrastructure.repositories.IDailyForecastRepository
import com.x3squaredcircles.photographyshared.db.PhotographyDatabase
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
class DailyForecastRepository(
    private val database: PhotographyDatabase
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting all daily forecasts: ${e.message}", e)
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                    Result.success(forecast)
                } else {
                    Result.failure("Daily forecast not found")
                }
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecast by ID: ${e.message}", e)
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecasts by weather ID: ${e.message}", e)
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecasts by weather and date range: ${e.message}", e)
        }
    }

    override suspend fun getNext7DaysAsync(weatherId: Int, fromDate: Long): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val endDate = fromDate + (7 * 24 * 60 * 60 * 1000L)
                val entities = database.dailyForecastQueries.selectNext7Days(weatherId.toLong(), fromDate, endDate).executeAsList()
                val forecasts = entities.map { entity ->
                    WeatherForecast(
                        id = entity.id.toInt(),
                        weatherId = entity.weatherId.toInt(),
                        forecastDate = entity.forecastDate,
                        minTemperature = entity.minTemperature,
                        maxTemperature = entity.maxTemperature,
                        humidity = entity.humidity,
                        pressure = entity.pressure,
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting next 7 days forecast: ${e.message}", e)
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                    Result.success(forecast)
                } else {
                    Result.failure("Daily forecast not found for date")
                }
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecast for date: ${e.message}", e)
        }
    }

    override suspend fun getCurrentForecastAsync(weatherId: Int): Result<WeatherForecast> {
        return try {
            withContext(Dispatchers.IO) {
                val currentDate = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                    Result.success(forecast)
                } else {
                    Result.failure("Current forecast not found")
                }
            }
        } catch (e: Exception) {
            Result.failure("Error getting current forecast: ${e.message}", e)
        }
    }

    override suspend fun getBestPhotographyDaysAsync(weatherId: Int, startDate: Long, endDate: Long, count: Int): Result<List<WeatherForecast>> {
        return try {
            withContext(Dispatchers.IO) {
                val entities = database.dailyForecastQueries.selectBestPhotographyDays(
                    weatherId.toLong(),
                    startDate,
                    endDate,
                    30.0, // cloudCover threshold
                    0.2,  // precipitationChance threshold
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting best photography days: ${e.message}", e)
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecasts by location ID: ${e.message}", e)
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
                        uvIndex = entity.uvIndex,
                        windSpeed = entity.windSpeed,
                        windDirection = entity.windDirection,
                        windGust = entity.windGust,
                        cloudCover = entity.cloudCover,
                        precipitationChance = entity.precipitationChance,
                        precipitationAmount = entity.precipitationAmount,
                        condition = entity.condition,
                        description = entity.description,
                        icon = entity.icon,
                        sunrise = entity.sunrise,
                        sunset = entity.sunset,
                        moonPhase = entity.moonPhase,
                        moonrise = entity.moonrise,
                        moonset = entity.moonset
                    )
                }
                Result.success(forecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecasts by location and date range: ${e.message}", e)
        }
    }

    override suspend fun getSunriseSunsetAsync(weatherId: Int, date: Long): Result<Pair<Long?, Long?>> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.dailyForecastQueries.selectSunriseSunset(weatherId.toLong(), date).executeAsOneOrNull()
                if (entity != null) {
                    Result.success(Pair(entity.sunrise, entity.sunset))
                } else {
                    Result.success(Pair(null, null))
                }
            }
        } catch (e: Exception) {
            Result.failure("Error getting sunrise/sunset: ${e.message}", e)
        }
    }

    override suspend fun getMoonPhaseAsync(weatherId: Int, date: Long): Result<Double?> {
        return try {
            withContext(Dispatchers.IO) {
                val entity = database.dailyForecastQueries.selectMoonPhase(weatherId.toLong(), date).executeAsOneOrNull()
                Result.success(entity?.moonPhase)
            }
        } catch (e: Exception) {
            Result.failure("Error getting moon phase: ${e.message}", e)
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
                    uvIndex = dailyForecast.uvIndex,
                    windSpeed = dailyForecast.windSpeed,
                    windDirection = dailyForecast.windDirection,
                    windGust = dailyForecast.windGust,
                    cloudCover = dailyForecast.cloudCover,
                    precipitationChance = dailyForecast.precipitationChance,
                    precipitationAmount = dailyForecast.precipitationAmount,
                    condition = dailyForecast.condition,
                    description = dailyForecast.description,
                    icon = dailyForecast.icon,
                    sunrise = dailyForecast.sunrise,
                    sunset = dailyForecast.sunset,
                    moonPhase = dailyForecast.moonPhase,
                    moonrise = dailyForecast.moonrise,
                    moonset = dailyForecast.moonset
                )

                val insertedId = database.dailyForecastQueries.transactionWithResult {
                    database.dailyForecastQueries.selectAll().executeAsList().lastOrNull()?.id?.toInt() ?: 0
                }

                val newForecast = dailyForecast.copy(id = insertedId)
                Result.success(newForecast)
            }
        } catch (e: Exception) {
            Result.failure("Error creating daily forecast: ${e.message}", e)
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
                            uvIndex = forecast.uvIndex,
                            windSpeed = forecast.windSpeed,
                            windDirection = forecast.windDirection,
                            windGust = forecast.windGust,
                            cloudCover = forecast.cloudCover,
                            precipitationChance = forecast.precipitationChance,
                            precipitationAmount = forecast.precipitationAmount,
                            condition = forecast.condition,
                            description = forecast.description,
                            icon = forecast.icon,
                            sunrise = forecast.sunrise,
                            sunset = forecast.sunset,
                            moonPhase = forecast.moonPhase,
                            moonrise = forecast.moonrise,
                            moonset = forecast.moonset
                        )
                        newForecasts.add(forecast)
                    }
                }

                Result.success(newForecasts)
            }
        } catch (e: Exception) {
            Result.failure("Error creating batch daily forecasts: ${e.message}", e)
        }
    }

    override suspend fun updateAsync(dailyForecast: WeatherForecast): Result<WeatherForecast> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.update(
                    minTemperature = dailyForecast.minTemperature,
                    maxTemperature = dailyForecast.maxTemperature,
                    humidity = dailyForecast.humidity,
                    pressure = dailyForecast.pressure,
                    uvIndex = dailyForecast.uvIndex,
                    windSpeed = dailyForecast.windSpeed,
                    windDirection = dailyForecast.windDirection,
                    windGust = dailyForecast.windGust,
                    cloudCover = dailyForecast.cloudCover,
                    precipitationChance = dailyForecast.precipitationChance,
                    precipitationAmount = dailyForecast.precipitationAmount,
                    condition = dailyForecast.condition,
                    description = dailyForecast.description,
                    icon = dailyForecast.icon,
                    sunrise = dailyForecast.sunrise,
                    sunset = dailyForecast.sunset,
                    moonPhase = dailyForecast.moonPhase,
                    moonrise = dailyForecast.moonrise,
                    moonset = dailyForecast.moonset,
                    id = dailyForecast.id.toLong()
                )

                Result.success(dailyForecast)
            }
        } catch (e: Exception) {
            Result.failure("Error updating daily forecast: ${e.message}", e)
        }
    }

    override suspend fun deleteAsync(id: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.deleteById(id.toLong())
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting daily forecast: ${e.message}", e)
        }
    }

    override suspend fun deleteByWeatherIdAsync(weatherId: Int): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.deleteByWeatherId(weatherId.toLong())
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting daily forecasts by weather ID: ${e.message}", e)
        }
    }

    override suspend fun deleteOlderThanAsync(olderThan: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.deleteOlderThan(olderThan)
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting old daily forecasts: ${e.message}", e)
        }
    }

    override suspend fun deleteByWeatherAndDateRangeAsync(weatherId: Int, startDate: Long, endDate: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                database.dailyForecastQueries.deleteByWeatherAndDateRange(
                    weatherId.toLong(),
                    startDate,
                    endDate
                )
                Result.success(0)
            }
        } catch (e: Exception) {
            Result.failure("Error deleting daily forecasts by date range: ${e.message}", e)
        }
    }

    override suspend fun getCountAsync(): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.dailyForecastQueries.getCount().executeAsOne()
                Result.success(count.toInt())
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecast count: ${e.message}", e)
        }
    }

    override suspend fun getCountByWeatherAsync(weatherId: Int): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                val count = database.dailyForecastQueries.getCountByWeather(weatherId.toLong()).executeAsOne()
                Result.success(count.toInt())
            }
        } catch (e: Exception) {
            Result.failure("Error getting daily forecast count by weather: ${e.message}", e)
        }
    }

    override suspend fun existsForWeatherAndDateAsync(weatherId: Int, date: Long): Result<Boolean> {
        return try {
            withContext(Dispatchers.IO) {
                val exists = database.dailyForecastQueries.existsForWeatherAndDate(weatherId.toLong(), date).executeAsOne()
                Result.success(exists)
            }
        } catch (e: Exception) {
            Result.failure("Error checking if daily forecast exists: ${e.message}", e)
        }
    }
}