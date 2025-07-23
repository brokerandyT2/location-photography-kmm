// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/InfrastructureExceptionMappingService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.photography.services.IInfrastructureExceptionMappingService
import com.x3squaredcircles.core.domain.exceptions.*

import kotlinx.coroutines.CancellationException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class InfrastructureExceptionMappingService : IInfrastructureExceptionMappingService {

    override fun mapToLocationDomainException(exception: Exception, operation: String): LocationDomainException {
        return when (exception) {
            is CancellationException -> LocationDomainException(
                code = "LOCATION_OPERATION_CANCELLED",
                message = "Location operation '$operation' was cancelled",
                cause = exception
            )

            is IllegalArgumentException -> LocationDomainException(
                code = "LOCATION_INVALID_ARGUMENT",
                message = "Invalid argument provided for location operation '$operation': ${exception.message}",
                cause = exception
            )

            is IllegalStateException -> LocationDomainException(
                code = "LOCATION_INVALID_STATE",
                message = "Invalid state encountered during location operation '$operation': ${exception.message}",
                cause = exception
            )

            is ConnectException, is UnknownHostException -> LocationDomainException(
                code = "LOCATION_NETWORK_ERROR",
                message = "Network error during location operation '$operation': Unable to connect to location services",
                cause = exception
            )

            is SocketTimeoutException -> LocationDomainException(
                code = "LOCATION_TIMEOUT_ERROR",
                message = "Timeout error during location operation '$operation': Request took too long to complete",
                cause = exception
            )

            is IOException -> LocationDomainException(
                code = "LOCATION_IO_ERROR",
                message = "I/O error during location operation '$operation': ${exception.message}",
                cause = exception
            )

            else -> when {
                exception.message?.contains("database", ignoreCase = true) == true ||
                        exception.message?.contains("sql", ignoreCase = true) == true -> LocationDomainException(
                    code = "LOCATION_DATABASE_ERROR",
                    message = "Database error during location operation '$operation': ${exception.message}",
                    cause = exception
                )

                else -> LocationDomainException(
                    code = "LOCATION_UNKNOWN_ERROR",
                    message = "Unknown error during location operation '$operation': ${exception.message ?: exception::class.simpleName}",
                    cause = exception
                )
            }
        }
    }

    override fun mapToWeatherDomainException(exception: Exception, operation: String): WeatherDomainException {
        return when (exception) {
            is CancellationException -> WeatherDomainException(
                code = "WEATHER_OPERATION_CANCELLED",
                message = "Weather operation '$operation' was cancelled",
                cause = exception
            )

            is IllegalArgumentException -> WeatherDomainException(
                code = "WEATHER_INVALID_ARGUMENT",
                message = "Invalid argument provided for weather operation '$operation': ${exception.message}",
                cause = exception
            )

            is IllegalStateException -> WeatherDomainException(
                code = "WEATHER_INVALID_STATE",
                message = "Invalid state encountered during weather operation '$operation': ${exception.message}",
                cause = exception
            )

            is ConnectException, is UnknownHostException -> WeatherDomainException(
                code = "WEATHER_NETWORK_ERROR",
                message = "Network error during weather operation '$operation': Unable to connect to weather services",
                cause = exception
            )

            is SocketTimeoutException -> WeatherDomainException(
                code = "WEATHER_TIMEOUT_ERROR",
                message = "Timeout error during weather operation '$operation': Request took too long to complete",
                cause = exception
            )

            is IOException -> WeatherDomainException(
                code = "WEATHER_IO_ERROR",
                message = "I/O error during weather operation '$operation': ${exception.message}",
                cause = exception
            )

            else -> when {
                exception.message?.contains("database", ignoreCase = true) == true ||
                        exception.message?.contains("sql", ignoreCase = true) == true -> WeatherDomainException(
                    code = "WEATHER_DATABASE_ERROR",
                    message = "Database error during weather operation '$operation': ${exception.message}",
                    cause = exception
                )

                exception.message?.contains("api", ignoreCase = true) == true ||
                        exception.message?.contains("service", ignoreCase = true) == true -> WeatherDomainException(
                    code = "WEATHER_SERVICE_ERROR",
                    message = "Weather service error during operation '$operation': ${exception.message}",
                    cause = exception
                )

                else -> WeatherDomainException(
                    code = "WEATHER_UNKNOWN_ERROR",
                    message = "Unknown error during weather operation '$operation': ${exception.message ?: exception::class.simpleName}",
                    cause = exception
                )
            }
        }
    }

    override fun mapToSettingDomainException(exception: Exception, operation: String): SettingDomainException {
        return when (exception) {
            is CancellationException -> SettingDomainException(
                code = "SETTING_OPERATION_CANCELLED",
                message = "Setting operation '$operation' was cancelled",
                cause = exception
            )

            is IllegalArgumentException -> SettingDomainException(
                code = "SETTING_INVALID_ARGUMENT",
                message = "Invalid argument provided for setting operation '$operation': ${exception.message}",
                cause = exception
            )

            is IllegalStateException -> SettingDomainException(
                code = "SETTING_INVALID_STATE",
                message = "Invalid state encountered during setting operation '$operation': ${exception.message}",
                cause = exception
            )

            is IOException -> SettingDomainException(
                code = "SETTING_IO_ERROR",
                message = "I/O error during setting operation '$operation': ${exception.message}",
                cause = exception
            )

            else -> when {
                exception.message?.contains("database", ignoreCase = true) == true ||
                        exception.message?.contains("sql", ignoreCase = true) == true -> SettingDomainException(
                    code = "SETTING_DATABASE_ERROR",
                    message = "Database error during setting operation '$operation': ${exception.message}",
                    cause = exception
                )

                exception.message?.contains("key", ignoreCase = true) == true ||
                        exception.message?.contains("duplicate", ignoreCase = true) == true -> SettingDomainException(
                    code = "SETTING_KEY_CONFLICT",
                    message = "Setting key conflict during operation '$operation': ${exception.message}",
                    cause = exception
                )

                else -> SettingDomainException(
                    code = "SETTING_UNKNOWN_ERROR",
                    message = "Unknown error during setting operation '$operation': ${exception.message ?: exception::class.simpleName}",
                    cause = exception
                )
            }
        }
    }

    override fun mapToTipDomainException(exception: Exception, operation: String): TipDomainException {
        return when (exception) {
            is CancellationException -> TipDomainException(
                code = "TIP_OPERATION_CANCELLED",
                message = "Tip operation '$operation' was cancelled",
                cause = exception
            )

            is IllegalArgumentException -> TipDomainException(
                code = "TIP_INVALID_ARGUMENT",
                message = "Invalid argument provided for tip operation '$operation': ${exception.message}",
                cause = exception
            )

            is IllegalStateException -> TipDomainException(
                code = "TIP_INVALID_STATE",
                message = "Invalid state encountered during tip operation '$operation': ${exception.message}",
                cause = exception
            )

            is IOException -> TipDomainException(
                code = "TIP_IO_ERROR",
                message = "I/O error during tip operation '$operation': ${exception.message}",
                cause = exception
            )

            else -> when {
                exception.message?.contains("database", ignoreCase = true) == true ||
                        exception.message?.contains("sql", ignoreCase = true) == true -> TipDomainException(
                    code = "TIP_DATABASE_ERROR",
                    message = "Database error during tip operation '$operation': ${exception.message}",
                    cause = exception
                )

                exception.message?.contains("not found", ignoreCase = true) == true -> TipDomainException(
                    code = "TIP_NOT_FOUND",
                    message = "Tip not found during operation '$operation': ${exception.message}",
                    cause = exception
                )

                else -> TipDomainException(
                    code = "TIP_UNKNOWN_ERROR",
                    message = "Unknown error during tip operation '$operation': ${exception.message ?: exception::class.simpleName}",
                    cause = exception
                )
            }
        }
    }

    override fun mapToTipTypeDomainException(exception: Exception, operation: String): TipTypeDomainException {
        return when (exception) {
            is CancellationException -> TipTypeDomainException(
                code = "TIPTYPE_OPERATION_CANCELLED",
                message = "Tip type operation '$operation' was cancelled",
                cause = exception
            )

            is IllegalArgumentException -> TipTypeDomainException(
                code = "TIPTYPE_INVALID_ARGUMENT",
                message = "Invalid argument provided for tip type operation '$operation': ${exception.message}",
                cause = exception
            )

            is IllegalStateException -> TipTypeDomainException(
                code = "TIPTYPE_INVALID_STATE",
                message = "Invalid state encountered during tip type operation '$operation': ${exception.message}",
                cause = exception
            )

            is IOException -> TipTypeDomainException(
                code = "TIPTYPE_IO_ERROR",
                message = "I/O error during tip type operation '$operation': ${exception.message}",
                cause = exception
            )

            else -> when {
                exception.message?.contains("database", ignoreCase = true) == true ||
                        exception.message?.contains("sql", ignoreCase = true) == true -> TipTypeDomainException(
                    code = "TIPTYPE_DATABASE_ERROR",
                    message = "Database error during tip type operation '$operation': ${exception.message}",
                    cause = exception
                )

                exception.message?.contains("duplicate", ignoreCase = true) == true ||
                        exception.message?.contains("exists", ignoreCase = true) == true -> TipTypeDomainException(
                    code = "TIPTYPE_DUPLICATE_NAME",
                    message = "Duplicate tip type name during operation '$operation': ${exception.message}",
                    cause = exception
                )

                exception.message?.contains("not found", ignoreCase = true) == true -> TipTypeDomainException(
                    code = "TIPTYPE_NOT_FOUND",
                    message = "Tip type not found during operation '$operation': ${exception.message}",
                    cause = exception
                )

                exception.message?.contains("in use", ignoreCase = true) == true ||
                        exception.message?.contains("referenced", ignoreCase = true) == true -> TipTypeDomainException(
                    code = "TIPTYPE_IN_USE",
                    message = "Tip type is in use and cannot be modified during operation '$operation': ${exception.message}",
                    cause = exception
                )

                else -> TipTypeDomainException(
                    code = "TIPTYPE_UNKNOWN_ERROR",
                    message = "Unknown error during tip type operation '$operation': ${exception.message ?: exception::class.simpleName}",
                    cause = exception
                )
            }
        }
    }
}