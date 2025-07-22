// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/AstroEventViewModel.kt
package com.x3squaredcircles.photography.viewmodels

import com.x3squaredcircles.photography.domain.models.AstroTarget

import com.x3squaredcircles.photography.domain.models.PlanetPositionData
import com.x3squaredcircles.photography.domain.models.DeepSkyObjectData
import com.x3squaredcircles.photography.domain.models.MeteorShowerData
import com.x3squaredcircles.photography.domain.models.EnhancedMoonData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.hours

class AstroEventViewModel : BaseViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _target = MutableStateFlow(AstroTarget.MilkyWayCore)
    val target: StateFlow<AstroTarget> = _target.asStateFlow()

    private val _startTime = MutableStateFlow(kotlinx.datetime.Clock.System.now())
    val startTime: StateFlow<Instant> = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow(kotlinx.datetime.Clock.System.now())
    val endTime: StateFlow<Instant> = _endTime.asStateFlow()

    private val _peakTime = MutableStateFlow<Instant?>(null)
    val peakTime: StateFlow<Instant?> = _peakTime.asStateFlow()

    private val _azimuth = MutableStateFlow(0.0)
    val azimuth: StateFlow<Double> = _azimuth.asStateFlow()

    private val _altitude = MutableStateFlow(0.0)
    val altitude: StateFlow<Double> = _altitude.asStateFlow()

    private val _magnitude = MutableStateFlow(0.0)
    val magnitude: StateFlow<Double> = _magnitude.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _constellation = MutableStateFlow("")
    val constellation: StateFlow<String> = _constellation.asStateFlow()

    private val _isVisible = MutableStateFlow(false)
    val isVisible: StateFlow<Boolean> = _isVisible.asStateFlow()

    private val _eventType = MutableStateFlow("")
    val eventType: StateFlow<String> = _eventType.asStateFlow()

    private val _angularSize = MutableStateFlow(0.0)
    val angularSize: StateFlow<Double> = _angularSize.asStateFlow()

    private val _recommendedEquipment = MutableStateFlow("")
    val recommendedEquipment: StateFlow<String> = _recommendedEquipment.asStateFlow()

    private val _timeFormat = MutableStateFlow("HH:mm")
    val timeFormat: StateFlow<String> = _timeFormat.asStateFlow()

    private val _dateFormat = MutableStateFlow("yyyy-MM-dd")
    val dateFormat: StateFlow<String> = _dateFormat.asStateFlow()

    constructor() : super()

    constructor(result: AstroCalculationResult) : super() {
        _target.value = result.target
        _name.value = getTargetDisplayName(result.target)
        _startTime.value = result.calculationTime
        _endTime.value = result.setTime ?: result.calculationTime.plus(8.hours)
        _peakTime.value = result.optimalTime
        _azimuth.value = result.azimuth
        _altitude.value = result.altitude
        _isVisible.value = result.isVisible
        _description.value = result.description
        _recommendedEquipment.value = result.equipment
        _eventType.value = getEventType(result.target)
    }

    constructor(planetData: PlanetPositionData) : super() {
        _target.value = AstroTarget.Planets
        _name.value = getPlanetDisplayName(planetData.planet)
        _startTime.value = planetData.rise ?: planetData.dateTime
        _endTime.value = planetData.set ?: planetData.dateTime.plus(12.hours)
        _peakTime.value = planetData.transit
        _azimuth.value = planetData.azimuth
        _altitude.value = planetData.altitude
        _magnitude.value = planetData.apparentMagnitude
        _angularSize.value = planetData.angularDiameter
        _isVisible.value = planetData.isVisible
        _description.value = "Magnitude ${planetData.apparentMagnitude.format(1)}, ${planetData.angularDiameter.format(1)}″"
        _recommendedEquipment.value = planetData.recommendedEquipment
        _eventType.value = "Planet"
    }

    constructor(dsoData: DeepSkyObjectData) : super() {
        _target.value = AstroTarget.DeepSkyObjects
        _name.value = dsoData.commonName.ifEmpty { dsoData.catalogId }
        _startTime.value = dsoData.dateTime
        _endTime.value = dsoData.dateTime.plus(8.hours)
        _peakTime.value = dsoData.optimalViewingTime
        _azimuth.value = dsoData.azimuth
        _altitude.value = dsoData.altitude
        _magnitude.value = dsoData.magnitude
        _angularSize.value = dsoData.angularSize
        _isVisible.value = dsoData.isVisible
        _description.value = "${dsoData.objectType}, Mag ${dsoData.magnitude.format(1)}, ${dsoData.angularSize.format(1)}'"
        _constellation.value = dsoData.parentConstellation.toString()
        _recommendedEquipment.value = dsoData.recommendedEquipment
        _eventType.value = dsoData.objectType
    }

    constructor(meteorData: MeteorShowerData) : super() {
        _target.value = AstroTarget.MeteorShowers
        _name.value = meteorData.name
        _startTime.value = meteorData.activityStart
        _endTime.value = meteorData.activityEnd
        _peakTime.value = meteorData.peakDate
        _azimuth.value = meteorData.radiantAzimuth
        _altitude.value = meteorData.radiantAltitude
        _isVisible.value = meteorData.optimalConditions
        _description.value = "ZHR: ${meteorData.zenithHourlyRate}, Moon: ${(meteorData.moonIllumination * 100).toInt()}%"
        _eventType.value = "Meteor Shower"
    }

    constructor(moonData: EnhancedMoonData) : super() {
        _target.value = AstroTarget.Moon
        _name.value = "Moon (${moonData.phaseName})"
        _startTime.value = moonData.rise ?: moonData.dateTime
        _endTime.value = moonData.set ?: moonData.dateTime.plus(12.hours)
        _peakTime.value = moonData.transit
        _azimuth.value = moonData.azimuth
        _altitude.value = moonData.altitude
        _magnitude.value = moonData.m
        _angularSize.value = moonData.angularDiameter
        _isVisible.value = moonData.isVisible
        _description.value = "${moonData.phaseName}, ${moonData.illumination.format(1)}% illuminated"
        _recommendedEquipment.value = moonData.recommendedEquipment
        _eventType.value = "Moon Phase"
    }

    fun getOptimalTime(): Instant {
        return _peakTime.value ?: _startTime.value
    }

    fun getFormattedTime(): String {
        val time = getOptimalTime()
        // Simplified formatting - would need proper timezone conversion
        return time.toString().substring(11, 16) // Extract HH:mm
    }

    private fun getTargetDisplayName(target: AstroTarget): String {
        return when (target) {
            AstroTarget.MilkyWayCore -> "Milky Way Core"
            AstroTarget.M31_Andromeda -> "Andromeda Galaxy (M31)"
            AstroTarget.M42_Orion -> "Orion Nebula (M42)"
            AstroTarget.M51_Whirlpool -> "Whirlpool Galaxy (M51)"
            AstroTarget.M13_Hercules -> "Hercules Cluster (M13)"
            AstroTarget.M27_Dumbbell -> "Dumbbell Nebula (M27)"
            AstroTarget.M57_Ring -> "Ring Nebula (M57)"
            AstroTarget.M81_Bodes -> "Bode's Galaxy (M81)"
            AstroTarget.M104_Sombrero -> "Sombrero Galaxy (M104)"
            AstroTarget.Moon -> "Moon"
            AstroTarget.Planets -> "Planets"
            AstroTarget.MeteorShowers -> "Meteor Showers"
            AstroTarget.DeepSkyObjects -> "Deep Sky Objects"
            else -> target.toString()
        }
    }

    private fun getPlanetDisplayName(planet: Any): String {
        // Implementation would depend on the Planet type structure
        return planet.toString()
    }

    private fun getEventType(target: AstroTarget): String {
        return when (target) {
            AstroTarget.MilkyWayCore -> "Galaxy Core"
            AstroTarget.Moon -> "Moon Phase"
            AstroTarget.Planets -> "Planet"
            AstroTarget.MeteorShowers -> "Meteor Shower"
            AstroTarget.DeepSkyObjects -> "Deep Sky Object"
            else -> "Astronomical Event"
        }
    }

    private fun Double.format(decimals: Int): String {
        return "%.${decimals}f".format(this)
    }
}