// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/GetPhoneCameraProfileByIdQuery.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile

data class GetPhoneCameraProfileByIdQuery(
    val id: Int
)

data class GetPhoneCameraProfileByIdQueryResult(
    val profile: PhoneCameraProfileDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)