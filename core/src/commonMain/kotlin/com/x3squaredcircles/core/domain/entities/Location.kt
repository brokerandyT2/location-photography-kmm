// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Location.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.AggregateRoot
import com.x3squaredcircles.core.domain.events.LocationSavedEvent
import com.x3squaredcircles.core.domain.events.PhotoAttachedEvent
import com.x3squaredcircles.core.domain.events.LocationDeletedEvent
import com.x3squaredcircles.core.domain.valueobjects.Coordinate
import com.x3squaredcircles.core.domain.valueobjects.Address
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
/**

Location aggregate root
 */
class Location private constructor(
    private var _id: Int = 0,
    private var _title: String,
    private var _description: String,
    private var _coordinate: Coordinate,
    private var _address: Address,
    private var _photoPath: String? = null,
    private var _isDeleted: Boolean = false,
    private var _timestamp: Instant = Clock.System.now()
) : AggregateRoot() {
    override val id: Int get() = _id
    val title: String get() = _title
    val description: String get() = _description
    val coordinate: Coordinate get() = _coordinate
    val address: Address get() = _address
    val photoPath: String? get() = _photoPath
    val isDeleted: Boolean get() = _isDeleted
    val timestamp: Instant get() = _timestamp
    companion object {
        /**
         * Creates a new Location instance
         */
        fun create(title: String, description: String, coordinate: Coordinate, address: Address): Location {
            require(title.isNotBlank()) { "Title cannot be empty" }
            val location = Location(
                _title = title,
                _description = description,
                _coordinate = coordinate,
                _address = address
            )

            location.addDomainEvent(LocationSavedEvent(location))
            return location
        }

        /**
         * Creates a Location instance from persistence (for repository use)
         */
        fun fromPersistence(
            id: Int,
            title: String,
            description: String,
            coordinate: Coordinate,
            address: Address,
            photoPath: String? = null,
            isDeleted: Boolean = false,
            timestamp: Instant
        ): Location {
            return Location(
                _id = id,
                _title = title,
                _description = description,
                _coordinate = coordinate,
                _address = address,
                _photoPath = photoPath,
                _isDeleted = isDeleted,
                _timestamp = timestamp
            )
        }
    }
    /**

    Updates location details
     */
    fun updateDetails(title: String, description: String) {
        require(title.isNotBlank()) { "Title cannot be empty" }
        _title = title
        _description = description
        addDomainEvent(LocationSavedEvent(this))
    }

    /**

    Updates location coordinate
     */
    fun updateCoordinate(coordinate: Coordinate) {
        _coordinate = coordinate
        addDomainEvent(LocationSavedEvent(this))
    }

    /**

    Attaches a photo to the location
     */
    fun attachPhoto(photoPath: String) {
        require(photoPath.isNotBlank()) { "Photo path cannot be empty" }
        _photoPath = photoPath
        addDomainEvent(PhotoAttachedEvent(id, photoPath))
    }

    /**

    Removes the photo from the location
     */
    fun removePhoto() {
        _photoPath = null
    }

    /**

    Marks the location as deleted
     */
    fun delete() {
        _isDeleted = true
        addDomainEvent(LocationDeletedEvent(id))
    }

    /**

    Restores a deleted location
     */
    fun restore() {
        _isDeleted = false
    }

    /**

    Internal method for setting ID (used by repositories)
     */
    internal fun setId(id: Int) {
        require(id > 0) { "Id must be greater than zero" }
        _id = id
    }
}