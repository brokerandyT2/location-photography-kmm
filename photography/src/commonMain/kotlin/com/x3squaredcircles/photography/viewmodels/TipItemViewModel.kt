// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/TipItemViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
class TipItemViewModel {
    private val _id = MutableStateFlow(0)
    val id: StateFlow<Int> = _id.asStateFlow()

    private val _tipTypeId = MutableStateFlow(0)
    val tipTypeId: StateFlow<Int> = _tipTypeId.asStateFlow()

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _fstop = MutableStateFlow("")
    val fstop: StateFlow<String> = _fstop.asStateFlow()

    private val _shutterSpeed = MutableStateFlow("")
    val shutterSpeed: StateFlow<String> = _shutterSpeed.asStateFlow()

    private val _iso = MutableStateFlow("")
    val iso: StateFlow<String> = _iso.asStateFlow()

    private val _i8n = MutableStateFlow("en-US")
    val i8n: StateFlow<String> = _i8n.asStateFlow()

    val hasCameraSettings: Boolean
        get() = _fstop.value.isNotEmpty() || _shutterSpeed.value.isNotEmpty() || _iso.value.isNotEmpty()

    val cameraSettingsDisplay: String
        get() {
            val fstopText = if (_fstop.value.isEmpty()) "" else "F: ${_fstop.value} "
            val shutterText = if (_shutterSpeed.value.isEmpty()) "" else "Shutter: ${_shutterSpeed.value} "
            val isoText = if (_iso.value.isEmpty()) "" else "ISO: ${_iso.value}"
            return (fstopText + shutterText + isoText).trim()
        }

    fun setId(value: Int) {
        _id.value = value
    }

    fun setTipTypeId(value: Int) {
        _tipTypeId.value = value
    }

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setContent(value: String) {
        _content.value = value
    }

    fun setFstop(value: String) {
        _fstop.value = value
    }

    fun setShutterSpeed(value: String) {
        _shutterSpeed.value = value
    }

    fun setIso(value: String) {
        _iso.value = value
    }

    fun setI8n(value: String) {
        _i8n.value = value
    }
}