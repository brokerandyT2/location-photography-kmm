// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/GetActivePhoneCameraProfilesCountQuery.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile

data class GetActivePhoneCameraProfilesCountQuery(
    val dummy: Boolean = true
)

data class GetActivePhoneCameraProfilesCountQueryResult(
    val count: Long,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)