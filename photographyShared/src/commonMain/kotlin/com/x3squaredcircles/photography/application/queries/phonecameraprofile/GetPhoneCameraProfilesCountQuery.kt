// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/GetPhoneCameraProfilesCountQuery.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile

data class GetPhoneCameraProfilesCountQuery(
    val dummy: Boolean = true
)

data class GetPhoneCameraProfilesCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)