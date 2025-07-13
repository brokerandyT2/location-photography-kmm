// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/queries/GetTipsByTypeQuery.kt
package com.x3squaredcircles.photography.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.photography.dtos.TipDto

data class GetTipsByTypeQuery(
    val tipTypeId: Int
) : IQuery<Result<List<TipDto>>>