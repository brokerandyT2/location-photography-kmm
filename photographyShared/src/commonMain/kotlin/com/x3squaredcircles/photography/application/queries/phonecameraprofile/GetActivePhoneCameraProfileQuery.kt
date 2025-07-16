// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/GetActivePhoneCameraProfileQuery.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile

data class GetActivePhoneCameraProfileQuery(
    val dummy: Boolean = true
)

data class GetActivePhoneCameraProfileQueryResult(
    val profile: PhoneCameraProfileDto?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)