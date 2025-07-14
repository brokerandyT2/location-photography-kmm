// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/TipTypeItemViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
class TipTypeItemViewModel {
    private val _id = MutableStateFlow(0)
    val id: StateFlow<Int> = _id.asStateFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _i8n = MutableStateFlow("en-US")
    val i8n: StateFlow<String> = _i8n.asStateFlow()

    fun setId(value: Int) {
        _id.value = value
    }

    fun setName(value: String) {
        _name.value = value
    }

    fun setI8n(value: String) {
        _i8n.value = value
    }
}