// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/queries/GetTipTypeByIdQuery.kt
package com.x3squaredcircles.photography.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.photography.dtos.TipTypeDto

data class GetTipTypeByIdQuery(
    val id: Int
) : IQuery<Result<TipTypeDto>>