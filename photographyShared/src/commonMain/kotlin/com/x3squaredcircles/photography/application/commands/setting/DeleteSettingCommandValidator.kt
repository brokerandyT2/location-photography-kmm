// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/DeleteSettingCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.setting

/**
 * Provides validation logic for the DeleteSettingCommand
 */
class DeleteSettingCommandValidator {

    /**
     * Validates the DeleteSettingCommand
     */
    fun validate(command: DeleteSettingCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        if (command.key.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "key",
                    message = "Key is required"
                )
            )
        }

        return ValidationResult(errors)
    }
}
