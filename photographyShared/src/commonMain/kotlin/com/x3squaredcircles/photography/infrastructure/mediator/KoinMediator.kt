// core/src/commonMain/kotlin/com/x3squaredcircles/core/application/mediator/KoinMediator.kt
package com.x3squaredcircles.photography.infrastructure.mediator

import com.x3squaredcircles.core.domain.common.Result
import co.touchlab.kermit.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KClass

/**
 * Koin-based mediator implementation that dynamically resolves handlers
 * Uses reflection to find appropriate command/query handlers at runtime
 */
class KoinMediator(
    private val logger: Logger
) : com.x3squaredcircles.photography.application.mediator.IMediator, KoinComponent {

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> send(query: com.x3squaredcircles.photography.application.mediator.IQuery<T>): Result<T> {
        return try {
            logger.d { "Processing query: ${query::class.simpleName}" }

            // Get the handler from Koin using the query type
            val handler = resolveQueryHandler(query::class)
                ?: return Result.failure<T>("No query handler found for ${query::class.simpleName}")

            // Use reflection to call handle method
            val handleMethod = handler::class.members.find { it.name == "handle" }
                ?: return Result.failure<T>("No handle method found on handler for ${query::class.simpleName}")

            val result = handleMethod.call(handler, query) as Result<T>

            when (result) {
                is Result.Success<*> -> {
                    logger.d { "Query ${query::class.simpleName} completed successfully" }
                    result as Result<T>
                }
                is Result.Failure<*> -> {
                    logger.w { "Query ${query::class.simpleName} failed: ${result.error}" }
                    result as Result<T>
                }
            }
        } catch (exception: Exception) {
            logger.e(exception) { "Error processing query ${query::class.simpleName}" }
            Result.failure<T>("Error processing query: ${exception.message}", exception)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> send(command: com.x3squaredcircles.photography.application.mediator.ICommand<T>): Result<T> {
        return try {
            logger.d { "Processing command: ${command::class.simpleName}" }

            // Get the handler from Koin using the command type
            val handler = resolveCommandHandler(command::class)
                ?: return Result.failure<T>("No command handler found for ${command::class.simpleName}")

            // Use reflection to call handle method
            val handleMethod = handler::class.members.find { it.name == "handle" }
                ?: return Result.failure<T>("No handle method found on handler for ${command::class.simpleName}")

            val result = handleMethod.call(handler, command) as Result<T>

            when (result) {
                is Result.Success<*> -> {
                    logger.d { "Command ${command::class.simpleName} completed successfully" }
                    result as Result<T>
                }
                is Result.Failure<*> -> {
                    logger.w { "Command ${command::class.simpleName} failed: ${result.error}" }
                    result as Result<T>
                }
            }
        } catch (exception: Exception) {
            logger.e(exception) { "Error processing command ${command::class.simpleName}" }
            Result.failure<T>("Error processing command: ${exception.message}", exception)
        }
    }

    /**
     * Resolve query handler from Koin container
     * Uses naming convention: handlers are registered with their full interface type
     */
    private fun resolveQueryHandler(queryClass: KClass<*>): Any? {
        return try {
            // Try to get the handler directly from Koin using the query class name
            // This assumes handlers are registered with proper generic types
            get<Any>(
                qualifier = null,
                parameters = { parametersOf(queryClass) }
            )
        } catch (exception: Exception) {
            logger.w(exception) { "Could not resolve query handler for ${queryClass.simpleName}" }
            null
        }
    }

    /**
     * Resolve command handler from Koin container
     * Uses naming convention: handlers are registered with their full interface type
     */
    private fun resolveCommandHandler(commandClass: KClass<*>): Any? {
        return try {
            // Try to get the handler directly from Koin using the command class name
            // This assumes handlers are registered with proper generic types
            get<Any>(
                qualifier = null,
                parameters = { parametersOf(commandClass) }
            )
        } catch (exception: Exception) {
            logger.w(exception) { "Could not resolve command handler for ${commandClass.simpleName}" }
            null
        }
    }
}