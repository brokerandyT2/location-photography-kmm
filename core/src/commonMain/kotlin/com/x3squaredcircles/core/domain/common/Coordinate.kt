// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/common/Coordinate.kt
package com.x3squaredcircles.core.domain.common

import kotlinx.serialization.Serializable
import kotlin.math.*

/**
 * Value object representing a geographic coordinate (latitude, longitude).
 * Provides geographic calculations and validations.
 */
@Serializable
data class Coordinate(
    val latitude: Double,
    val longitude: Double
) {
    
    init {
        require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90 degrees" }
        require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180 degrees" }
    }
    
    /**
     * Returns a formatted string representation of the coordinates.
     */
    val formatted: String
        get() = String.format("%.6f, %.6f", latitude, longitude)
    
    /**
     * Returns the coordinate in degrees, minutes, seconds format.
     */
    val dms: String
        get() {
            fun toDMS(value: Double, isLatitude: Boolean): String {
                val abs = kotlin.math.abs(value)
                val degrees = abs.toInt()
                val minutes = ((abs - degrees) * 60).toInt()
                val seconds = ((abs - degrees - minutes / 60.0) * 3600)
                
                val direction = when {
                    isLatitude -> if (value >= 0) "N" else "S"
                    else -> if (value >= 0) "E" else "W"
                }
                
                return String.format("%d°%d'%.2f\"%s", degrees, minutes, seconds, direction)
            }
            
            return "${toDMS(latitude, true)} ${toDMS(longitude, false)}"
        }
    
    /**
     * Calculates the distance to another coordinate in kilometers using the Haversine formula.
     */
    fun distanceTo(other: Coordinate): Double {
        val earthRadiusKm = 6371.0
        
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(other.latitude)
        val deltaLatRad = Math.toRadians(other.latitude - latitude)
        val deltaLonRad = Math.toRadians(other.longitude - longitude)
        
        val a = sin(deltaLatRad / 2) * sin(deltaLatRad / 2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLonRad / 2) * sin(deltaLonRad / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadiusKm * c
    }
    
    /**
     * Calculates the distance to another coordinate in miles.
     */
    fun distanceToMiles(other: Coordinate): Double {
        return distanceTo(other) * 0.621371
    }
    
    /**
     * Determines if this coordinate is within the specified distance of another coordinate.
     */
    fun isWithinDistance(other: Coordinate, distanceKm: Double): Boolean {
        return distanceTo(other) <= distanceKm
    }
    
    /**
     * Calculates the bearing (direction) to another coordinate in degrees.
     */
    fun bearingTo(other: Coordinate): Double {
        val lat1Rad = Math.toRadians(latitude)
        val lat2Rad = Math.toRadians(other.latitude)
        val deltaLonRad = Math.toRadians(other.longitude - longitude)
        
        val y = sin(deltaLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLonRad)
        
        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)
        
        return (bearingDeg + 360) % 360
    }
    
    /**
     * Returns a coordinate offset by the specified distance and bearing.
     */
    fun offsetBy(distanceKm: Double, bearingDegrees: Double): Coordinate {
        val earthRadiusKm = 6371.0
        val bearingRad = Math.toRadians(bearingDegrees)
        val distRad = distanceKm / earthRadiusKm
        
        val lat1Rad = Math.toRadians(latitude)
        val lon1Rad = Math.toRadians(longitude)
        
        val lat2Rad = asin(sin(lat1Rad) * cos(distRad) + cos(lat1Rad) * sin(distRad) * cos(bearingRad))
        val lon2Rad = lon1Rad + atan2(
            sin(bearingRad) * sin(distRad) * cos(lat1Rad),
            cos(distRad) - sin(lat1Rad) * sin(lat2Rad)
        )
        
        return Coordinate(
            latitude = Math.toDegrees(lat2Rad),
            longitude = Math.toDegrees(lon2Rad)
        )
    }
    
    companion object {
        /**
         * Creates a Coordinate from a pair of doubles.
         */
        fun fromPair(pair: Pair<Double, Double>): Coordinate {
            return Coordinate(pair.first, pair.second)
        }
        
        /**
         * Validates if the given latitude and longitude values are valid.
         */
        fun isValid(latitude: Double, longitude: Double): Boolean {
            return latitude in -90.0..90.0 && longitude in -180.0..180.0
        }
        
        /**
         * Tries to create a Coordinate, returning null if invalid.
         */
        fun tryCreate(latitude: Double, longitude: Double): Coordinate? {
            return if (isValid(latitude, longitude)) {
                Coordinate(latitude, longitude)
            } else {
                null
            }
        }
    }
}