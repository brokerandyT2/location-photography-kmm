// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/LocationsViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
class LocationsViewModel : BaseViewModel() {
    private val _locations = MutableStateFlow<List<LocationListItemViewModel>>(emptyList())
    val locations: StateFlow<List<LocationListItemViewModel>> = _locations.asStateFlow()

    suspend fun loadLocationsAsync() {
        try {
            setBusy(true)
            clearErrors()

            // TODO: Implement load locations query through mediator
            // Need GetLocationsQuery and mediator dependency
            // Create query for active locations (not deleted)
            // val query = GetLocationsQuery(
            //     pageNumber = 1,
            //     pageSize = 100,
            //     includeDeleted = false
            // )
            // val result = mediator.send(query)
            // if (result.isSuccess && result.data != null) {
            //     val locationItems = result.data.items.map { locationDto ->
            //         LocationListItemViewModel().apply {
            //             setId(locationDto.id)
            //             setTitle(locationDto.title)
            //             setLatitude(locationDto.latitude)
            //             setLongitude(locationDto.longitude)
            //             setPhoto(locationDto.photoPath)
            //             setIsDeleted(locationDto.isDeleted)
            //         }
            //     }
            //     _locations.value = locationItems
            // } else {
            //     onSystemError(result.errorMessage ?: "Failed to load locations")
            // }

        } catch (ex: Exception) {
            onSystemError("Error loading locations: ${ex.message}")
        } finally {
            setBusy(false)
        }
    }
}