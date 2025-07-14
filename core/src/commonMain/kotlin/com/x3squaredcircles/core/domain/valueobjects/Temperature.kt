// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/Temperature.kt
package com.x3squaredcircles.core.domain.valueobjects
import kotlin.math.round
/**

Value object representing temperature with unit conversions
 */
class Temperature private constructor(
    private val celsius: Double
) : ValueObject() {
    val celsiusValue: Double = celsius
    val fahrenheitValue: Double = (celsius * 9 / 5) + 32
    val kelvinValue: Double = celsius + 273.15
    companion object {
        /**
         * Creates a Temperature instance from a specified temperature in degrees Celsius.
         */
        fun fromCelsius(celsius: Double): Temperature {
            return Temperature(round(celsius * 100) / 100)
        }
        /**
         * Creates a Temperature instance from a temperature value in degrees Fahrenheit.
         */
        fun fromFahrenheit(fahrenheit: Double): Temperature {
            val celsius = (fahrenheit - 32) * 5 / 9
            return Temperature(round(celsius * 100) / 100)
        }

        /**
         * Creates a Temperature instance from a temperature value in Kelvin.
         */
        fun fromKelvin(kelvin: Double): Temperature {
            val celsius = kelvin - 273.15
            return Temperature(round(celsius * 100) / 100)
        }
    }
    /**

    Provides the components used to determine equality for the current object.
     */
    override fun getEqualityComponents(): Sequence<Any?> = sequenceOf(celsius)

    /**

    Returns a string representation of the temperature in both Celsius and Fahrenheit.
     */
    override fun toString(): String {
        return "${celsius.format(1)}°C / ${fahrenheitValue.format(1)}°F"
    }

    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
}