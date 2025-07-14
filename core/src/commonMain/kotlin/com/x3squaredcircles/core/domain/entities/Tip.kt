// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/Tip.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.Entity
/**

Core tip entity (extensible per vertical)
 */
class Tip private constructor(
    private var _id: Int = 0,
    val tipTypeId: Int,
    private var _title: String,
    private var _content: String,
    private var _fstop: String = "",
    private var _shutterSpeed: String = "",
    private var _iso: String = "",
    private var _i8n: String = "en-US"
) : Entity() {
    override val id: Int get() = _id
    val title: String get() = _title
    val content: String get() = _content
    val fstop: String get() = _fstop
    val shutterSpeed: String get() = _shutterSpeed
    val iso: String get() = _iso
    val i8n: String get() = _i8n
    companion object {
        /**
         * Creates a new Tip instance
         */
        fun create(tipTypeId: Int, title: String, content: String): Tip {
            require(title.isNotBlank()) { "Title cannot be empty" }
            return Tip(
                tipTypeId = tipTypeId,
                _title = title,
                _content = content
            )
        }

        /**
         * Creates a Tip instance from persistence (for repository use)
         */
        fun fromPersistence(
            id: Int,
            tipTypeId: Int,
            title: String,
            content: String,
            fstop: String = "",
            shutterSpeed: String = "",
            iso: String = "",
            i8n: String = "en-US"
        ): Tip {
            return Tip(
                _id = id,
                tipTypeId = tipTypeId,
                _title = title,
                _content = content,
                _fstop = fstop,
                _shutterSpeed = shutterSpeed,
                _iso = iso,
                _i8n = i8n
            )
        }
    }
    /**

    Updates photography settings (vertical-specific extension)
     */
    fun updatePhotographySettings(fstop: String, shutterSpeed: String, iso: String) {
        _fstop = fstop
        _shutterSpeed = shutterSpeed
        _iso = iso
    }

    /**

    Updates tip content
     */
    fun updateContent(title: String, content: String) {
        require(title.isNotBlank()) { "Title cannot be empty" }
        _title = title
        _content = content
    }

    /**

    Sets localization
     */
    fun setLocalization(i8n: String) {
        _i8n = i8n.ifBlank { "en-US" }
    }

    /**

    Internal method for setting ID (used by repositories)
     */
    internal fun setId(id: Int) {
        require(id > 0) { "Id must be greater than zero" }
        _id = id
    }
}