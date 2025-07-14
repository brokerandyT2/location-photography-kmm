// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/WindInfo.kt
package com.x3squaredcircles.core.domain.valueobjects
import kotlin.math.round
/**

Value object representing wind information
 */
class WindInfo(
    speed: Double,
    direction: Double,
    gust: Double? = null
) : ValueObject() {
    val speed: Double
    val direction: Double
    val gust: Double?
    init {
        require(speed >= 0) { "Wind speed cannot be negative" }
        require(direction in 0.0..360.0) { "Wind direction must be between 0 and 360 degrees" }
        this.speed = round(speed * 100) / 100
        this.direction = round(direction)
        this.gust = gust?.let { round(it * 100) / 100 }
    }
    /**

    Gets cardinal direction from degrees
     */
    fun getCardinalDirection(): String {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val index = (round(direction / 22.5).toInt()) % 16
        return directions[index]
    }

    override fun getEqualityComponents(): Sequence<Any?> = sequenceOf(
        speed,
        direction,
        gust ?: 0.0
    )
    override fun toString(): String {
        val gustInfo = gust?.let { ", Gust: ${it.format(1)}" } ?: ""
        return "{speed.format(1)} mph from ${getCardinalDirection()} ({direction.toInt()}°)$gustInfo"
    }

    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
}