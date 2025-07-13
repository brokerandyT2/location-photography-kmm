// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/Tip.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable

/**
 * Represents a photography tip with technical camera settings.
 * Contains advice, techniques, and camera configuration for specific photography scenarios.
 */
@Serializable
data class Tip(
    override val id: Int = 0,
    val tipTypeId: Int,
    val title: String,
    val content: String,
    val fstop: String = "",
    val shutterSpeed: String = "",
    val iso: String = "",
    val i8n: String = "en-US"
) : Entity() {
    
    /**
     * Checks if this tip has camera settings (any of the technical fields are populated).
     */
    val hasCameraSettings: Boolean
        get() = fstop.isNotBlank() || shutterSpeed.isNotBlank() || iso.isNotBlank()
    
    /**
     * Gets a formatted string of all camera settings.
     */
    val cameraSettingsDisplay: String
        get() {
            val settings = mutableListOf<String>()
            if (fstop.isNotBlank()) settings.add("f/$fstop")
            if (shutterSpeed.isNotBlank()) settings.add("$shutterSpeed")
            if (iso.isNotBlank()) settings.add("ISO $iso")
            return settings.joinToString(" • ")
        }
    
    /**
     * Gets a compact camera settings display for limited space.
     */
    val compactCameraSettings: String
        get() {
            val settings = mutableListOf<String>()
            if (fstop.isNotBlank()) settings.add("F:$fstop")
            if (shutterSpeed.isNotBlank()) settings.add("S:$shutterSpeed")
            if (iso.isNotBlank()) settings.add("ISO:$iso")
            return settings.joinToString(" ")
        }
    
    /**
     * Checks if this tip has valid content.
     */
    val isValid: Boolean
        get() = title.isNotBlank() && content.isNotBlank() && tipTypeId > 0
    
    /**
     * Gets the content preview (first 100 characters).
     */
    val contentPreview: String
        get() = if (content.length > 100) {
            "${content.take(97)}..."
        } else {
            content
        }
    
    /**
     * Creates a copy of this tip with updated camera settings.
     */
    fun updateCameraSettings(
        newFstop: String = fstop,
        newShutterSpeed: String = shutterSpeed,
        newIso: String = iso
    ): Tip {
        return copy(
            fstop = newFstop.trim(),
            shutterSpeed = newShutterSpeed.trim(),
            iso = newIso.trim()
        )
    }
    
    /**
     * Creates a copy of this tip with updated content.
     */
    fun updateContent(newTitle: String, newContent: String): Tip {
        return copy(
            title = newTitle.trim(),
            content = newContent.trim()
        )
    }
    
    companion object {
        /**
         * Creates a new photography tip.
         */
        fun create(
            tipTypeId: Int,
            title: String,
            content: String,
            fstop: String = "",
            shutterSpeed: String = "",
            iso: String = "",
            i8n: String = "en-US"
        ): Tip {
            require(tipTypeId > 0) { "Tip type ID must be positive" }
            require(title.isNotBlank()) { "Tip title cannot be blank" }
            require(content.isNotBlank()) { "Tip content cannot be blank" }
            
            return Tip(
                tipTypeId = tipTypeId,
                title = title.trim(),
                content = content.trim(),
                fstop = fstop.trim(),
                shutterSpeed = shutterSpeed.trim(),
                iso = iso.trim(),
                i8n = i8n
            )
        }
        
        /**
         * Creates a basic tip without camera settings.
         */
        fun createBasic(
            tipTypeId: Int,
            title: String,
            content: String,
            i8n: String = "en-US"
        ): Tip {
            return create(tipTypeId, title, content, "", "", "", i8n)
        }
    }
}