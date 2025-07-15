// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/UpdateTipTypeCommand.kt
package com.x3squaredcircles.photography.application.commands.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class UpdateTipTypeCommand(
    val id: Int,
    val name: String,
    val i8n: String = "en-US"
)

data class UpdateTipTypeCommandResult(
    val tipType: TipType?,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)