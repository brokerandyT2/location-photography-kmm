// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/entities/Subscription.kt
package com.x3squaredcircles.photography.domain.entities

import com.x3squaredcircles.core.domain.common.Entity
import kotlinx.serialization.Serializable
import kotlinx.datetime.*
/**
 * Represents a user subscription for premium features.
 */
@Serializable
data class Subscription(
    override val id: Int = 0,
    val userId: String,
    val transactionId: String,
    val purchaseToken: String,
    val productId: String,
    val isActive: Boolean = true,
    val expirationDate: Long,
    val purchaseDate: Long,
    val lastVerified: Long
) : Entity() {
    
    /**
     * Checks if the subscription is currently valid (active and not expired).
     */
    val isValid: Boolean
        get() = isActive && !isExpired
    
    /**
     * Checks if the subscription has expired.
     */
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expirationDate
    
    /**
     * Gets the number of days remaining until expiration.
     */
    val daysRemaining: Int
        get() {
            val remaining = expirationDate - System.currentTimeMillis()
            return if (remaining > 0) (remaining / (24 * 60 * 60 * 1000)).toInt() else 0
        }
    
    /**
     * Gets the number of days since last verification.
     */
    val daysSinceLastVerification: Int
        get() {
            val elapsed = System.currentTimeMillis() - lastVerified
            return (elapsed / (24 * 60 * 60 * 1000)).toInt()
        }
    
    /**
     * Determines if verification is needed (>7 days since last check).
     */
    val needsVerification: Boolean
        get() = daysSinceLastVerification > 7
    
    /**
     * Gets the expiration date as a LocalDateTime in the current system timezone.
     */
    val expirationDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(expirationDate)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Gets the purchase date as a LocalDateTime in the current system timezone.
     */
    val purchaseDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(purchaseDate)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Gets the last verified date as a LocalDateTime in the current system timezone.
     */
    val lastVerifiedDateTime: kotlinx.datetime.LocalDateTime
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(lastVerified)
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
    
    /**
     * Creates a new subscription with updated verification timestamp.
     */
    fun withUpdatedVerification(): Subscription {
        return copy(lastVerified = kotlinx.datetime.Clock.System.now().toEpochMilliseconds())
    }
    
    /**
     * Creates a new subscription with updated active status.
     */
    fun withActiveStatus(active: Boolean): Subscription {
        return copy(
            isActive = active,
            lastVerified = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        )
    }
    
    companion object {
        /**
         * Creates a new subscription with the current timestamp.
         */
        fun create(
            userId: String,
            transactionId: String,
            purchaseToken: String,
            productId: String,
            expirationDate: Long
        ): Subscription {
            require(userId.isNotBlank()) { "User ID cannot be blank" }
            require(transactionId.isNotBlank()) { "Transaction ID cannot be blank" }
            require(purchaseToken.isNotBlank()) { "Purchase token cannot be blank" }
            require(productId.isNotBlank()) { "Product ID cannot be blank" }
            require(expirationDate > System.currentTimeMillis()) { "Expiration date must be in the future" }
            
            val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            
            return Subscription(
                userId = userId.trim(),
                transactionId = transactionId.trim(),
                purchaseToken = purchaseToken.trim(),
                productId = productId.trim(),
                isActive = true,
                expirationDate = expirationDate,
                purchaseDate = now,
                lastVerified = now
            )
        }
    }
}