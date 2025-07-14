// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/events/WeatherUpdatedEvent.kt
package com.x3squaredcircles.core.domain.events
import com.x3squaredcircles.core.domain.common.DomainEvent
import kotlinx.datetime.Instant
/**

Domain event raised when weather data is updated
 */
class WeatherUpdatedEvent(
    val locationId: Int,
    val updateTime: Instant
) : DomainEvent()