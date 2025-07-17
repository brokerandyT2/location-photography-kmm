// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/setting/CreateSettingCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.setting

/**
 * Provides validation rules for the CreateSettingCommand class
 */
class CreateSettingCommandValidator {

    /**
     * Validates the CreateSettingCommand
     */
    fun validate(command: CreateSettingCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Key validation
        if (command.key.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "key",
                    message = "Key is required"
                )
            )
        } else if (command.key.length > 50) {
            errors.add(
                ValidationError(
                    propertyName = "key",
                    message = "Key must not exceed 50 characters"
                )
            )
        }

        // Value validation
        if (command.value.length > 500) {
            errors.add(
                ValidationError(
                    propertyName = "value",
                    message = "Value must not exceed 500 characters"
                )
            )
        }

        // Description validation
        if (command.description.length > 200) {
            errors.add(
                ValidationError(
                    propertyName = "description",
                    message = "Description must not exceed 200 characters"
                )
            )
        }

        return ValidationResult(errors)
    }
}
