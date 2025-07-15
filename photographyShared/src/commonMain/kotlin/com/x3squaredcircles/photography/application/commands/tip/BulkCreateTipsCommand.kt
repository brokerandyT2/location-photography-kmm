// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/BulkCreateTipsCommand.kt
package com.x3squaredcircles.photography.application.commands.tip

import com.x3squaredcircles.core.domain.entities.Tip

data class BulkCreateTipsCommand(
    val tips: List<TipData>
)

data class TipData(
    val tipTypeId: Int,
    val title: String,
    val content: String,
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val i8n: String = "en-US"
)

data class BulkCreateTipsCommandResult(
    val createdTips: List<Tip>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)