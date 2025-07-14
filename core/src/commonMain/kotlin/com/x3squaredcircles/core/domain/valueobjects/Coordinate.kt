// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/valueobjects/Coordinate.kt
package com.x3squaredcircles.core.domain.valueobjects
import kotlin.math.*
/**

Value object representing geographic coordinates
 */
class Coordinate private constructor(
    val latitude: Double,
    val longitude: Double,
    private val preCalculatedHashCode: Int
) : ValueObject() {
    companion object {
        // Pre-calculated constants for distance calculations
        private const val EARTH_RADIUS_KM = 6371.0
        private const val DEGREES_TO_RADIANS = PI / 180.0
        private const val RADIANS_TO_DEGREES = 180.0 / PI
        // Cache for distance calculations between commonly used coordinates
        private val distanceCache = mutableMapOf<Pair<Pair<Double, Double>, Pair<Double, Double>>, Double>()

        // Cache for string representations to avoid repeated formatting
        private val stringCache = mutableMapOf<Pair<Double, Double>, String>()

        /**
         * Creates a validated coordinate instance
         */
        fun create(latitude: Double, longitude: Double): Coordinate {
            validateCoordinates(latitude, longitude)
            val roundedLat = (latitude * 1000000).roundToInt() / 1000000.0
            val roundedLon = (longitude * 1000000).roundToInt() / 1000000.0
            val hashCode = calculateHashCode(roundedLat, roundedLon)
            return Coordinate(roundedLat, roundedLon, hashCode)
        }

        /**
         * Fast coordinate validation without exceptions
         */
        fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
            return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180
        }

        /**
         * Batch coordinate creation for multiple points
         */
        fun createBatch(coordinates: List<Pair<Double, Double>>): List<Coordinate> {
            return coordinates.map { (lat, lon) -> create(lat, lon) }
        }

        /**
         * Calculate midpoint between two coordinates
         */
        fun midpoint(coord1: Coordinate, coord2: Coordinate): Coordinate {
            val lat1Rad = coord1.latitude * DEGREES_TO_RADIANS
            val lon1Rad = coord1.longitude * DEGREES_TO_RADIANS
            val lat2Rad = coord2.latitude * DEGREES_TO_RADIANS
            val deltaLonRad = (coord2.longitude - coord1.longitude) * DEGREES_TO_RADIANS

            val bx = cos(lat2Rad) * cos(deltaLonRad)
            val by = cos(lat2Rad) * sin(deltaLonRad)

            val lat3Rad = atan2(
                sin(lat1Rad) + sin(lat2Rad),
                sqrt((cos(lat1Rad) + bx) * (cos(lat1Rad) + bx) + by * by)
            )

            val lon3Rad = lon1Rad + atan2(by, cos(lat1Rad) + bx)

            val midLat = lat3Rad * RADIANS_TO_DEGREES
            val midLon = lon3Rad * RADIANS_TO_DEGREES

            return create(midLat, midLon)
        }

        /**
         * Static cache cleanup method for memory management
         */
        fun clearCaches() {
            distanceCache.clear()
            stringCache.clear()
        }

        /**
         * Get cache statistics for monitoring
         */
        fun getCacheStats(): Triple<Int, Int, Int> {
            return Triple(distanceCache.size, stringCache.size, 0)
        }

        private fun validateCoordinates(latitude: Double, longitude: Double) {
            if (latitude < -90 || latitude > 90) {
                throw IllegalArgumentException("Latitude must be between -90 and 90")
            }
            if (longitude < -180 || longitude > 180) {
                throw IllegalArgumentException("Longitude must be between -180 and 180")
            }
        }

        private fun calculateHashCode(latitude: Double, longitude: Double): Int {
            var result = 17
            result = result * 23 + latitude.hashCode()
            result = result * 23 + longitude.hashCode()
            return result
        }

        private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val lat1Rad = lat1 * DEGREES_TO_RADIANS
            val lon1Rad = lon1 * DEGREES_TO_RADIANS
            val lat2Rad = lat2 * DEGREES_TO_RADIANS
            val lon2Rad = lon2 * DEGREES_TO_RADIANS

            val dLat = lat2Rad - lat1Rad
            val dLon = lon2Rad - lon1Rad

            val sinDLat = sin(dLat * 0.5)
            val sinDLon = sin(dLon * 0.5)
            val cosLat1 = cos(lat1Rad)
            val cosLat2 = cos(lat2Rad)

            val a = sinDLat * sinDLat + cosLat1 * cosLat2 * sinDLon * sinDLon
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return EARTH_RADIUS_KM * c
        }
    }
    /**

    Optimized distance calculation with caching
     */
    fun distanceTo(other: Coordinate): Double {
        val cacheKey = Pair(Pair(latitude, longitude), Pair(other.latitude, other.longitude))
        return distanceCache.getOrPut(cacheKey) {
            calculateHaversineDistance(latitude, longitude, other.latitude, other.longitude)
        }
    }

    /**

    Fast distance check without full calculation for nearby coordinates
     */
    fun isWithinDistance(other: Coordinate, maxDistanceKm: Double): Boolean {
        val latDiff = abs(latitude - other.latitude)
        val lonDiff = abs(longitude - other.longitude)
// Quick check using simple distance approximation for nearby points
        if (latDiff < 1.0 && lonDiff < 1.0) {
            val approximateDistance = sqrt(latDiff * latDiff + lonDiff * lonDiff) * 111.32
            if (approximateDistance > maxDistanceKm) return false
        }
        return distanceTo(other) <= maxDistanceKm
    }

    /**

    Calculate bearing to another coordinate
     */
    fun bearingTo(other: Coordinate): Double {
        val lat1Rad = latitude * DEGREES_TO_RADIANS
        val lat2Rad = other.latitude * DEGREES_TO_RADIANS
        val deltaLonRad = (other.longitude - longitude) * DEGREES_TO_RADIANS
        val y = sin(deltaLonRad) * cos(lat2Rad)
        val x = cos(lat1Rad) * sin(lat2Rad) - sin(lat1Rad) * cos(lat2Rad) * cos(deltaLonRad)
        val bearingRad = atan2(y, x)
        val bearingDeg = bearingRad * RADIANS_TO_DEGREES
        return (bearingDeg + 360) % 360
    }

    /**

    Find nearest coordinate from a collection
     */
    fun findNearest(candidates: List<Coordinate>): Coordinate {
        require(candidates.isNotEmpty()) { "Candidates collection cannot be empty" }
        var nearest = candidates[0]
        var minDistance = distanceTo(nearest)
        for (i in 1 until candidates.size) {
            val distance = distanceTo(candidates[i])
            if (distance < minDistance) {
                minDistance = distance
                nearest = candidates[i]
                // Early exit for very close matches (within 1 meter)
                if (distance < 0.001) break
            }
        }
        return nearest
    }

    /**

    Get coordinates within specified radius
     */
    fun getCoordinatesWithinRadius(candidates: List<Coordinate>, radiusKm: Double): List<Coordinate> {
        return candidates.filter { isWithinDistance(it, radiusKm) }
    }

    override fun getEqualityComponents(): Sequence<Any?> = sequenceOf(latitude, longitude)
    override fun hashCode(): Int = preCalculatedHashCode
    override fun toString(): String {
        val key = Pair(latitude, longitude)
        return stringCache.getOrPut(key) {
            "${latitude.format(6)}, ${longitude.format(6)}"
        }
    }
    private fun Double.format(decimals: Int): String = "%.${decimals}f".format(this)
}