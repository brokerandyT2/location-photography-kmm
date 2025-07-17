// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/commands/tip/UpdateTipCommandValidator.kt
package com.x3squaredcircles.photography.application.commands.tip

/**
 * Provides validation rules for the UpdateTipCommand
 */
class UpdateTipCommandValidator {

    /**
     * Validates the UpdateTipCommand
     */
    fun validate(command: UpdateTipCommand): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        // Id validation
        if (command.id <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "id",
                    message = "Id must be greater than 0"
                )
            )
        }

        // TipTypeId validation
        if (command.tipTypeId <= 0) {
            errors.add(
                ValidationError(
                    propertyName = "tipTypeId",
                    message = "TipTypeId must be greater than 0"
                )
            )
        }

        // Title validation
        if (command.title.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "title",
                    message = "Title is required"
                )
            )
        } else if (command.title.length > 100) {
            errors.add(
                ValidationError(
                    propertyName = "title",
                    message = "Title must not exceed 100 characters"
                )
            )
        }

        // Content validation
        if (command.content.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "content",
                    message = "Content is required"
                )
            )
        } else if (command.content.length > 1000) {
            errors.add(
                ValidationError(
                    propertyName = "content",
                    message = "Content must not exceed 1000 characters"
                )
            )
        }

        // Fstop validation
        if (command.fstop.length > 20) {
            errors.add(
                ValidationError(
                    propertyName = "fstop",
                    message = "Fstop must not exceed 20 characters"
                )
            )
        }

        // ShutterSpeed validation
        if (command.shutterSpeed.length > 20) {
            errors.add(
                ValidationError(
                    propertyName = "shutterSpeed",
                    message = "ShutterSpeed must not exceed 20 characters"
                )
            )
        }

        // ISO validation
        if (command.iso.length > 20) {
            errors.add(
                ValidationError(
                    propertyName = "iso",
                    message = "ISO must not exceed 20 characters"
                )
            )
        }

        // I8n (localization) validation
        if (command.i8n.isBlank()) {
            errors.add(
                ValidationError(
                    propertyName = "i8n",
                    message = "Localization is required"
                )
            )
        } else if (command.i8n.length > 10) {
            errors.add(
                ValidationError(
                    propertyName = "i8n",
                    message = "Localization must not exceed 10 characters"
                )
            )
        }

        return ValidationResult(errors)
    }
}
