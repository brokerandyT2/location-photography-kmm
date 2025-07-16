package com.x3squaredcircles.photography.application.queries

interface IQueryHandler<TQuery, TResult> {
    suspend fun handle(query: TQuery): TResult
}