// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/CameraBody.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import com.x3squaredcircles.photography.domain.enums.MountType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class CameraBody private constructor(
    private var _id: Int = 0,
    private var _name: String,
    private var _sensorType: String,
    private var _sensorWidth: Double,
    private var _sensorHeight: Double,
    private var _mountType: MountType,
    private var _isUserCreated: Boolean = false,
    private var _dateAdded: Instant = Clock.System.now()
) : Entity() {

    override val id: Int get() = _id
    val name: String get() = _name
    val sensorType: String get() = _sensorType
    val sensorWidth: Double get() = _sensorWidth
    val sensorHeight: Double get() = _sensorHeight
    val mountType: MountType get() = _mountType
    val isUserCreated: Boolean get() = _isUserCreated
    val dateAdded: Instant get() = _dateAdded

    companion object {
        fun create(
            name: String,
            sensorType: String,
            sensorWidth: Double,
            sensorHeight: Double,
            mountType: MountType,
            isUserCreated: Boolean = false
        ): CameraBody {
            require(name.isNotBlank()) { "Camera name cannot be null or empty" }
            require(sensorType.isNotBlank()) { "Sensor type cannot be null or empty" }
            require(sensorWidth > 0) { "Sensor width must be positive" }
            require(sensorHeight > 0) { "Sensor height must be positive" }

            return CameraBody(
                _name = name.trim(),
                _sensorType = sensorType.trim(),
                _sensorWidth = sensorWidth,
                _sensorHeight = sensorHeight,
                _mountType = mountType,
                _isUserCreated = isUserCreated,
                _dateAdded = Clock.System.now()
            )
        }

        fun fromPersistence(
            id: Int,
            name: String,
            sensorType: String,
            sensorWidth: Double,
            sensorHeight: Double,
            mountType: MountType,
            isUserCreated: Boolean,
            dateAdded: Instant
        ): CameraBody {
            return CameraBody(
                _id = id,
                _name = name,
                _sensorType = sensorType,
                _sensorWidth = sensorWidth,
                _sensorHeight = sensorHeight,
                _mountType = mountType,
                _isUserCreated = isUserCreated,
                _dateAdded = dateAdded
            )
        }
    }

    fun updateDetails(
        name: String,
        sensorType: String,
        sensorWidth: Double,
        sensorHeight: Double,
        mountType: MountType
    ) {
        require(name.isNotBlank()) { "Camera name cannot be null or empty" }
        require(sensorType.isNotBlank()) { "Sensor type cannot be null or empty" }
        require(sensorWidth > 0) { "Sensor width must be positive" }
        require(sensorHeight > 0) { "Sensor height must be positive" }

        _name = name.trim()
        _sensorType = sensorType.trim()
        _sensorWidth = sensorWidth
        _sensorHeight = sensorHeight
        _mountType = mountType
    }

    fun getDisplayName(): String {
        return if (_isUserCreated) "${_name}*" else _name
    }

    internal fun setId(id: Int) {
        require(id > 0) { "Id must be greater than zero" }
        _id = id
    }
}