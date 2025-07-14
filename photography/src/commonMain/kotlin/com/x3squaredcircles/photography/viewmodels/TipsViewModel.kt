// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/TipsViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
class TipsViewModel : BaseViewModel() {
    private val _selectedTipTypeId = MutableStateFlow(0)
    val selectedTipTypeId: StateFlow<Int> = _selectedTipTypeId.asStateFlow()

    private val _selectedTipType = MutableStateFlow<TipTypeItemViewModel?>(null)
    val selectedTipType: StateFlow<TipTypeItemViewModel?> = _selectedTipType.asStateFlow()

    private val _tips = MutableStateFlow<List<TipItemViewModel>>(emptyList())
    val tips: StateFlow<List<TipItemViewModel>> = _tips.asStateFlow()

    private val _tipTypes = MutableStateFlow<List<TipTypeItemViewModel>>(emptyList())
    val tipTypes: StateFlow<List<TipTypeItemViewModel>> = _tipTypes.asStateFlow()

    fun setSelectedTipTypeId(value: Int) {
        _selectedTipTypeId.value = value
    }

    fun setSelectedTipType(value: TipTypeItemViewModel?) {
        _selectedTipType.value = value
        value?.let {
            setSelectedTipTypeId(it.id.value)
            // TODO: Launch coroutine to load tips by type
            // Need to call loadTipsByTypeAsync(it.id.value) in a coroutine
        }
    }

    suspend fun loadTipTypesAsync() {
        try {
            setBusy(true)
            clearErrors()

            // TODO: Implement tip types query through mediator
            // Need GetAllTipTypesQuery and mediator dependency
            // val query = GetAllTipTypesQuery()
            // val result = mediator.send(query)
            // if (result.isSuccess && result.data != null) {
            //     val tipTypeItems = result.data.map { item ->
            //         TipTypeItemViewModel().apply {
            //             setId(item.id)
            //             setName(item.name)
            //             setI8n(item.i8n)
            //         }
            //     }
            //     _tipTypes.value = tipTypeItems
            //
            //     // Set default selected tip type if available
            //     if (tipTypeItems.isNotEmpty()) {
            //         setSelectedTipType(tipTypeItems.first())
            //     }
            // } else {
            //     onSystemError(result.errorMessage ?: "Failed to load tip types")
            // }

        } catch (ex: Exception) {
            onSystemError("Error loading tip types: ${ex.message}")
        } finally {
            setBusy(false)
        }
    }

    suspend fun loadTipsByTypeAsync(tipTypeId: Int) {
        try {
            if (tipTypeId <= 0) {
                setValidationError("Please select a valid tip type")
                return
            }

            setBusy(true)
            clearErrors()

            // TODO: Implement tips query through mediator
            // Need GetTipsByTypeQuery and mediator dependency
            // val query = GetTipsByTypeQuery(tipTypeId = tipTypeId)
            // val result = mediator.send(query)
            // if (result.isSuccess && result.data != null) {
            //     val tipItems = result.data.map { item ->
            //         TipItemViewModel().apply {
            //             setId(item.id)
            //             setTipTypeId(item.tipTypeId)
            //             setTitle(item.title)
            //             setContent(item.content)
            //             setFstop(item.fstop)
            //             setShutterSpeed(item.shutterSpeed)
            //             setIso(item.iso)
            //             setI8n(item.i8n)
            //         }
            //     }
            //     _tips.value = tipItems
            // } else {
            //     onSystemError(result.errorMessage ?: "Failed to load tips")
            // }

        } catch (ex: Exception) {
            onSystemError("Error loading tips: ${ex.message}")
        } finally {
            setBusy(false)
        }
    }
}