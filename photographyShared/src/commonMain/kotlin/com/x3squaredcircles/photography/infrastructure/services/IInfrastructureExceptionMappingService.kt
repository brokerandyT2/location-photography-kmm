// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/services/IInfrastructureExceptionMappingService.kt
package com.x3squaredcircles.photography.services

import com.x3squaredcircles.core.domain.exceptions.*

interface IInfrastructureExceptionMappingService {
    fun mapToLocationDomainException(exception: Exception, operation: String): LocationDomainException
    fun mapToWeatherDomainException(exception: Exception, operation: String): WeatherDomainException
    fun mapToSettingDomainException(exception: Exception, operation: String): SettingDomainException
    fun mapToTipDomainException(exception: Exception, operation: String): TipDomainException
    fun mapToTipTypeDomainException(exception: Exception, operation: String): TipTypeDomainException
}