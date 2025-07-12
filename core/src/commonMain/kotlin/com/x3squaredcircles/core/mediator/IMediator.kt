// core/src/commonMain/kotlin/com/x3squaredcircles/core/mediator/IMediator.kt
package com.x3squaredcircles.core.mediator

/**
 * Base request interface for all commands and queries.
 * All requests must specify their response type.
 */
interface IRequest<TResponse>

/**
 * Base handler interface for processing requests.
 */
interface IRequestHandler<in TRequest : IRequest<TResponse>, TResponse> {
    /**
     * Handles the request and returns the response.
     */
    suspend fun handle(request: TRequest): TResponse
}

/**
 * Command interface for operations that modify state.
 * Commands typically return Result<T> or Result<Unit>.
 */
interface ICommand<TResponse> : IRequest<TResponse>

/**
 * Query interface for operations that read state.
 * Queries typically return Result<T> where T is the data requested.
 */
interface IQuery<TResponse> : IRequest<TResponse>

/**
 * Handler type aliases for clarity.
 */
typealias ICommandHandler<TCommand, TResponse> = IRequestHandler<TCommand, TResponse>
typealias IQueryHandler<TQuery, TResponse> = IRequestHandler<TQuery, TResponse>

/**
 * Mediator interface for sending commands and queries.
 * This is the main entry point for the CQRS pattern.
 */
interface IMediator {
    /**
     * Sends a request (command or query) and returns the response.
     */
    suspend fun <TResponse> send(request: IRequest<TResponse>): TResponse
    
    /**
     * Registers a handler for a specific request type.
     * This is typically called during dependency injection setup.
     */
    fun <TRequest : IRequest<TResponse>, TResponse> registerHandler(
        requestClass: String,
        handler: IRequestHandler<TRequest, TResponse>
    )
}