// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tiptype/BulkCreateTipTypesCommand.kt
package com.x3squaredcircles.photography.application.commands.tiptype

import com.x3squaredcircles.core.domain.entities.TipType

data class BulkCreateTipTypesCommand(
    val tipTypes: List<TipTypeData>
)

data class TipTypeData(
    val name: String,
    val i8n: String = "en-US"
)

data class BulkCreateTipTypesCommandResult(
    val createdTipTypes: List<TipType>,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)