// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/GetAllPhoneCameraProfilesQuery.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile

data class GetAllPhoneCameraProfilesQuery(
    val dummy: Boolean = true
)

data class GetAllPhoneCameraProfilesQueryResult(
    val profiles: List<PhoneCameraProfileDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class PhoneCameraProfileDto(
    val id: Int,
    val phoneModel: String,
    val mainLensFocalLength: Double,
    val mainLensFOV: Double,
    val ultraWideFocalLength: Double?,
    val telephotoFocalLength: Double?,
    val dateCalibrated: Long,
    val isActive: Boolean
)