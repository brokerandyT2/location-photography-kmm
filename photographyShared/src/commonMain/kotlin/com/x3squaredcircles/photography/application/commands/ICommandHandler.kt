// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/ICommandHandler.kt
package com.x3squaredcircles.photography.application.commands

import com.x3squaredcircles.core.domain.common.Result

interface ICommandHandler<TCommand, TResult> {
    suspend fun handle(command: TCommand): Result<TResult>
}