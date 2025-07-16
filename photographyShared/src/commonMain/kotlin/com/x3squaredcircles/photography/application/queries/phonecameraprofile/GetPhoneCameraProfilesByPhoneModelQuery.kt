// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/phonecameraprofile/GetPhoneCameraProfilesByPhoneModelQuery.kt
package com.x3squaredcircles.photography.application.queries.phonecameraprofile

data class GetPhoneCameraProfilesByPhoneModelQuery(
    val phoneModel: String
)

data class GetPhoneCameraProfilesByPhoneModelQueryResult(
    val profiles: List<PhoneCameraProfileDto>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)