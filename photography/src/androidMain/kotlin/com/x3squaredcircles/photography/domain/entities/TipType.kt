// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/TipType.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable

/**
 * Represents a category or type of photography tip.
 * Examples: "Portrait", "Landscape", "Macro", "Night Photography", etc.
 */
@Serializable
data class TipType(
    override val id: Int = 0,
    val name: String,
    val i8n: String = "en-US"
) : Entity() {
    
    /**
     * Returns a display-friendly version of the name.
     */
    val displayName: String
        get() = name.trim()
    
    /**
     * Checks if this tip type has a valid name.
     */
    val isValid: Boolean
        get() = name.isNotBlank()
    
    companion object {
        /**
         * Creates a new TipType with default internationalization.
         */
        fun create(name: String, i8n: String = "en-US"): TipType {
            return TipType(
                name = name.trim(),
                i8n = i8n
            )
        }
        
        // Common photography tip type names
        object StandardTypes {
            const val PORTRAIT = "Portrait"
            const val LANDSCAPE = "Landscape"
            const val MACRO = "Macro"
            const val NIGHT_PHOTOGRAPHY = "Night Photography"
            const val STREET_PHOTOGRAPHY = "Street Photography"
            const val WILDLIFE = "Wildlife"
            const val SPORTS = "Sports"
            const val WEDDING = "Wedding"
            const val FASHION = "Fashion"
            const val DOCUMENTARY = "Documentary"
            const val ARCHITECTURAL = "Architectural"
            const val FOOD = "Food"
            const val PRODUCT = "Product"
            const val ASTROPHOTOGRAPHY = "Astrophotography"
        }
    }
}