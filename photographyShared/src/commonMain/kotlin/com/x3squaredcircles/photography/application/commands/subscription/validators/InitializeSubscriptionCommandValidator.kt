// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/validators/InitializeSubscriptionCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.subscription.validators

import com.x3squaredcircles.photography.application.commands.subscription.InitializeSubscriptionCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult

class InitializeSubscriptionCommandValidator : IValidator<InitializeSubscriptionCommand> {

    override suspend fun validate(request: InitializeSubscriptionCommand): ValidationResult {
        // No validation rules needed for initialization command
        // The command has no parameters to validate - it's just a trigger
        return ValidationResult.success()
    }
}