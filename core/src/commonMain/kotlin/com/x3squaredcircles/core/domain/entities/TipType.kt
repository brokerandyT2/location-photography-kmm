// core/src/commonMain/kotlin/com/x3squaredcircles/core/domain/entities/TipType.kt
package com.x3squaredcircles.core.domain.entities
import com.x3squaredcircles.core.domain.common.Entity
/**

Tip category entity
 */
class TipType private constructor(
    private var _id: Int = 0,
    private var _name: String,
    private var _i8n: String = "en-US"
) : Entity() {
    private val _tips = mutableListOf<Tip>()
    override val id: Int get() = _id
    val name: String get() = _name
    val i8n: String get() = _i8n
    val tips: List<Tip> get() = _tips.toList()
    companion object {
        /**
         * Creates a new TipType instance
         */
        fun create(name: String): TipType {
            require(name.isNotBlank()) { "Name cannot be empty" }
            return TipType(_name = name)
        }

        /**
         * Creates a TipType instance from persistence (for repository use)
         */
        fun fromPersistence(
            id: Int,
            name: String,
            i8n: String = "en-US"
        ): TipType {
            return TipType(
                _id = id,
                _name = name,
                _i8n = i8n
            )
        }
    }
    /**

    Sets localization
     */
    fun setLocalization(i8n: String) {
        _i8n = i8n.ifBlank { "en-US" }
    }

    /**

    Adds a tip to this tip type
     */
    fun addTip(tip: Tip) {
        require(tip.tipTypeId == id || id == 0) { "Tip type ID mismatch" }
        _tips.add(tip)
    }

    /**

    Removes a tip from this tip type
     */
    fun removeTip(tip: Tip) {
        _tips.remove(tip)
    }

    /**

    Internal method for setting ID (used by repositories)
     */
    internal fun setId(id: Int) {
        require(id > 0) { "Id must be greater than zero" }
        _id = id
    }
}