// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/LocationListItemViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.round
class LocationListItemViewModel {
    private val _id = MutableStateFlow(0)
    val id: StateFlow<Int> = _id.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _latitude = MutableStateFlow(0.0)
    val latitude: StateFlow<Double> = _latitude.asStateFlow()

    private val _longitude = MutableStateFlow(0.0)
    val longitude: StateFlow<Double> = _longitude.asStateFlow()

    private val _photo = MutableStateFlow("")
    val photo: StateFlow<String> = _photo.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()

    val formattedCoordinates: String
        get() = "${round(_latitude.value * 1000000) / 1000000}, ${round(_longitude.value * 1000000) / 1000000}"

    fun setId(value: Int) {
        _id.value = value
    }

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setLatitude(value: Double) {
        _latitude.value = value
    }

    fun setLongitude(value: Double) {
        _longitude.value = value
    }

    fun setPhoto(value: String) {
        _photo.value = value
    }

    fun setIsDeleted(value: Boolean) {
        _isDeleted.value = value
    }
}