// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/CreateTipCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class CreateTipCommand(
    val tipTypeId: Int,
    val title: String,
    val content: String,
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val i8n: String = "en-US"
)

data class CreateTipCommandResult(
    val tip: Tip,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)