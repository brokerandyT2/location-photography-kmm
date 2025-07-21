// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/queries/subscription/validators/GetSubscriptionStatusQueryValidator.kt
package com.x3squaredcircles.photography.application.queries.subscription.validators


import com.x3squaredcircles.photography.application.common.behaviors.IValidator
import com.x3squaredcircles.photography.application.common.behaviors.ValidationResult
import com.x3squaredcircles.photography.application.queries.subscription.GetSubscriptionStatusQuery

class GetSubscriptionStatusQueryValidator : IValidator<GetSubscriptionStatusQuery> {

    override suspend fun validate(request: GetSubscriptionStatusQuery): ValidationResult {
        // No validation rules needed for subscription status query
        // The query has no parameters to validate - it gets current user's subscription status
        return ValidationResult.success()
    }
}