// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/phonecameraprofile/CreatePhoneCameraProfileCommand.kt
package com.x3squaredcircles.photography.application.commands.phonecameraprofile

import com.x3squaredcircles.photography.application.queries.phonecameraprofile.PhoneCameraProfileDto

data class CreatePhoneCameraProfileCommand(
    val imagePath: String,
    val deleteImageAfterProcessing: Boolean = true
)

data class CreatePhoneCameraProfileCommandResult(
    val profile: PhoneCameraProfileDto,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)