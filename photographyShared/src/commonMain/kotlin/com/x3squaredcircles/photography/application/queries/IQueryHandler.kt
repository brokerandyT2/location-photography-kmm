package com.x3squaredcircles.photography.application.queries
import com.x3squaredcircles.core.domain.common.Result

interface IQueryHandler<TQuery, TResult> {
    suspend fun handle(query: TQuery): Result<TResult>
}