// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/exceptions/InvalidCoordinateException.kt
package com.x3squaredcircles.core.domain.exceptions
/**

Exception thrown when invalid coordinates are provided
 */
class InvalidCoordinateException : LocationDomainException {
    val latitude: Double
    val longitude: Double
    /**

    Initializes a new instance of the InvalidCoordinateException class with the specified latitude and longitude values.
     */
    constructor(latitude: Double, longitude: Double) : super(
        "INVALID_COORDINATE",
        "Invalid coordinates: Latitude=$latitude, Longitude=$longitude"
    ) {
        this.latitude = latitude
        this.longitude = longitude
    }


    /**

    Represents an exception that is thrown when an invalid geographic coordinate is encountered.
     */
    constructor(latitude: Double, longitude: Double, message: String) : super(
        "INVALID_COORDINATE",
        message
    ) {
        this.latitude = latitude
        this.longitude = longitude
    }
}