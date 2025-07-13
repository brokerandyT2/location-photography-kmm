// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/queries/GetAllTipTypesQuery.kt
package com.x3squaredcircles.photography.queries

import com.x3squaredcircles.core.Result
import com.x3squaredcircles.core.mediator.IQuery
import com.x3squaredcircles.photography.dtos.TipTypeDto

class GetAllTipTypesQuery : IQuery<Result<List<TipTypeDto>>>