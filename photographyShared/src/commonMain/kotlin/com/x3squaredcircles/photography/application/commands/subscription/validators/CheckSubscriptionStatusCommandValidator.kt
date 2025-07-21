// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/subscription/validators/CheckSubscriptionStatusCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.subscription.validators

import com.x3squaredcircles.photography.application.commands.subscription.CheckSubscriptionStatusCommand
import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult

class CheckSubscriptionStatusCommandValidator : IValidator<CheckSubscriptionStatusCommand> {

    override suspend fun validate(request: CheckSubscriptionStatusCommand): ValidationResult {
        // No validation rules needed for status check command
        // The command has no parameters to validate - it's just a trigger to check status
        return ValidationResult.success()
    }
}