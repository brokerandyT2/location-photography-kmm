// core/src/commonMain/kotlin/com/x3squaredcircles/core/application/mediator/IMediator.kt
package com.x3squaredcircles.photography.application.mediator

import com.x3squaredcircles.core.domain.common.Result

/**
 * Marker interface for queries
 */
interface IQuery<T>

/**
 * Marker interface for commands
 */
interface ICommand<T>

/**
 * Mediator interface for handling commands and queries using CQRS pattern
 * Provides clean separation between ViewModels and handlers
 */
interface IMediator {

    /**
     * Send a query and return the result
     * @param query The query to execute
     * @return Result<T> containing the response data or error
     */
    suspend fun <T> send(query: IQuery<T>): Result<T>

    /**
     * Send a command and return the result
     * @param command The command to execute
     * @return Result<T> containing the response data or error
     */
    suspend fun <T> send(command: ICommand<T>): Result<T>
}