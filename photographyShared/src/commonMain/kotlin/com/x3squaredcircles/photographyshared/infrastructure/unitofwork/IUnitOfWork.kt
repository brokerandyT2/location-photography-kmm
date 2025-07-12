// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photographyshared/infrastructure/unitofwork/IUnitOfWork.kt
package com.x3squaredcircles.photographyshared.infrastructure.unitofwork

import com.x3squaredcircles.core.infrastructure.repositories.ILocationRepository
import com.x3squaredcircles.core.infrastructure.repositories.IWeatherRepository


/**
 * Unit of Work pattern implementation for coordinating repositories and transactions.
 * Provides access to all repositories and ensures transactional consistency.
 */
interface IUnitOfWork {
    
    // Core repositories (universal entities)
    val locations: ILocationRepository
    val weather: IWeatherRepository
    
    // Weather forecast repositories
    val hourlyForecasts: IHourlyForecastRepository
    val dailyForecasts: IDailyForecastRepository
    
    // Photography-specific repositories
    val tipTypes: ITipTypeRepository
    val tips: ITipRepository
    val settings: ISettingRepository
    val cameraBodies: ICameraBodyRepository
    val lenses: ILensRepository
    val lensCameraCompatibility: ILensCameraCompatibilityRepository
    val subscriptions: ISubscriptionRepository
    
    /**
     * Begins a database transaction.
     * All repository operations within the transaction will be atomic.
     */
    suspend fun beginTransactionAsync()
    
    /**
     * Commits the current transaction.
     * Makes all changes permanent.
     */
    suspend fun commitTransactionAsync()
    
    /**
     * Rolls back the current transaction.
     * Reverts all changes made within the transaction.
     */
    suspend fun rollbackTransactionAsync()
    
    /**
     * Executes the given block within a transaction.
     * Automatically commits on success or rolls back on failure.
     */
    suspend fun <T> withTransactionAsync(block: suspend () -> T): T
    
    /**
     * Disposes resources and closes database connections.
     */
    suspend fun dispose()
}