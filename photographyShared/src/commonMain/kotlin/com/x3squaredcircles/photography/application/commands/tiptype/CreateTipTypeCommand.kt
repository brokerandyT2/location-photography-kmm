// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/CreateTipTypeCommand.kt
package com.x3squaredcircles.photography.application.commands.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class CreateTipTypeCommand(
    val name: String,
    val i8n: String = "en-US"
)

data class CreateTipTypeCommandResult(
    val tipType: TipType,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)