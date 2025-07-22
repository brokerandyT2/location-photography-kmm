// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/SettingViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.photography.application.services.IAlertService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant

class SettingViewModel(
    alertService: IAlertService? = null
) : BaseViewModel() {

    private val _id = MutableStateFlow(0)
    val id: StateFlow<Int> = _id.asStateFlow()

    private val _key = MutableStateFlow("")
    val key: StateFlow<String> = _key.asStateFlow()

    private val _value = MutableStateFlow("")
    val value: StateFlow<String> = _value.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _timestamp = MutableStateFlow(kotlinx.datetime.Clock.System.now())
    val timestamp: StateFlow<Instant> = _timestamp.asStateFlow()

    fun setId(value: Int) {
        _id.value = value
    }

    fun setKey(value: String) {
        _key.value = value
    }

    fun setValue(value: String) {
        _value.value = value
    }

    fun setDescription(value: String) {
        _description.value = value
    }

    fun setTimestamp(value: Instant) {
        _timestamp.value = value
    }
}