// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/infrastructure/services/ApplicationPerformanceMonitoringService.kt
package com.x3squaredcircles.photography.infrastructure.services

import com.x3squaredcircles.photography.application.common.behaviors.CachingBehavior
import com.x3squaredcircles.photography.application.common.behaviors.PerformanceMonitoring
import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Background service to monitor application performance and maintain caches
 */
class ApplicationPerformanceMonitoringService(
    private val logger: Logger
) {
    private var monitoringJob: Job? = null
    private val monitoringInterval = 10.minutes
    private var serviceStartTime: kotlinx.datetime.Instant? = null
    private var lastMaintenanceRun: kotlinx.datetime.Instant? = null

    companion object {
        private const val MEMORY_THRESHOLD_BYTES = 100 * 1024 * 1024L // 100MB
        private const val MEMORY_CLEANUP_THRESHOLD_BYTES = 10 * 1024 * 1024L // 10MB
    }

    /**
     * Start the performance monitoring service
     */
    fun start() {
        logger.i { "Starting application performance monitoring service" }

        serviceStartTime = Clock.System.now()

        monitoringJob = CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            while (isActive) {
                try {
                    delay(monitoringInterval)

                    if (!isActive) break

                    performMaintenanceTasks()
                } catch (ex: CancellationException) {
                    logger.d { "Performance monitoring cancelled" }
                    break
                } catch (ex: Exception) {
                    logger.e(ex) { "Error during performance monitoring" }
                    // Continue monitoring despite errors
                }
            }
        }
    }

    /**
     * Stop the performance monitoring service
     */
    fun stop() {
        logger.i { "Stopping application performance monitoring service" }
        monitoringJob?.cancel()
        monitoringJob = null
    }

    private suspend fun performMaintenanceTasks() {
        withContext(Dispatchers.Default) {
            try {
                logger.d { "Starting performance maintenance tasks" }

                lastMaintenanceRun = Clock.System.now()

                // Cleanup expired caches
                cleanupExpiredCaches()

                // Monitor memory usage
                monitorMemoryUsage()

                // Performance statistics
                logPerformanceStatistics()

                logger.d { "Performance maintenance tasks completed" }

            } catch (ex: Exception) {
                logger.w(ex) { "Error during performance maintenance tasks" }
            }
        }
    }

    private fun cleanupExpiredCaches() {
        try {
            logger.d { "Cleaning up expired caches" }

            // Cleanup CachingBehavior cache
            CachingBehavior.cleanupExpiredCache()

            // Cleanup performance monitoring history
            PerformanceMonitoring.clearHistory()

            logger.d { "Cache cleanup completed" }

        } catch (ex: Exception) {
            logger.w(ex) { "Error during cache cleanup" }
        }
    }

    private fun monitorMemoryUsage() {
        try {
            // Platform-specific memory monitoring would be implemented here
            // For now, we'll log basic cache statistics
            val cacheStats = CachingBehavior.getCacheStatistics()

            logger.d {
                "Cache statistics - Total: ${cacheStats.totalEntries}, " +
                        "Active: ${cacheStats.activeEntries}, " +
                        "Expired: ${cacheStats.expiredEntries}, " +
                        "Memory: ${cacheStats.memoryFootprint}KB"
            }

            // Check for potential memory issues
            if (cacheStats.expiredEntries > cacheStats.activeEntries && cacheStats.totalEntries > 100) {
                logger.w {
                    "Cache inefficiency detected - more expired entries (${cacheStats.expiredEntries}) " +
                            "than active (${cacheStats.activeEntries}). Consider adjusting cache TTL."
                }
            }

            // Force cleanup if cache is too large
            if (cacheStats.totalEntries > 5000) {
                logger.w { "Large cache detected (${cacheStats.totalEntries} entries), forcing cleanup" }
                CachingBehavior.cleanupExpiredCache()
            }

        } catch (ex: Exception) {
            logger.w(ex) { "Error during memory usage monitoring" }
        }
    }

    private fun logPerformanceStatistics() {
        try {
            val stats = PerformanceMonitoring.getPerformanceStatistics()

            if (stats.totalRequests > 0) {
                logger.i {
                    "Performance stats - Requests: ${stats.totalRequests}, " +
                            "Avg time: ${stats.averageExecutionTimeMs}ms, " +
                            "Slow requests: ${stats.slowRequestCount}"
                }

                stats.slowestRequest?.let { slowest ->
                    logger.i {
                        "Slowest request: ${slowest.requestName} " +
                                "(${slowest.executionTimeMs}ms)"
                    }
                }

                // Warning for high slow request ratio
                val slowRatio = stats.slowRequestCount.toDouble() / stats.totalRequests
                if (slowRatio > 0.1) { // More than 10% slow requests
                    logger.w {
                        "High slow request ratio detected: ${(slowRatio * 100).toInt()}% " +
                                "(${stats.slowRequestCount}/${stats.totalRequests})"
                    }
                }
            }

        } catch (ex: Exception) {
            logger.w(ex) { "Error during performance statistics logging" }
        }
    }

    /**
     * Force immediate maintenance tasks
     */
    suspend fun forceMaintenanceTasks() {
        logger.i { "Forcing immediate maintenance tasks" }
        performMaintenanceTasks()
    }

    /**
     * Get service status information
     */
    fun getServiceStatus(): ServiceStatus {
        val isRunning = monitoringJob?.isActive == true

        return ServiceStatus(
            isRunning = isRunning,
            startTime = serviceStartTime ?: Clock.System.now(),
            lastMaintenanceRun = lastMaintenanceRun ?: Clock.System.now(),
            maintenanceInterval = monitoringInterval
        )
    }

    /**
     * Configure monitoring settings
     */
    fun updateConfiguration(config: MonitoringConfiguration) {
        logger.i { "Updating monitoring configuration" }

        // Restart with new configuration if running
        if (monitoringJob?.isActive == true) {
            stop()
            start()
        }
    }
}

/**
 * Service status information
 */
data class ServiceStatus(
    val isRunning: Boolean,
    val startTime: kotlinx.datetime.Instant,
    val lastMaintenanceRun: kotlinx.datetime.Instant,
    val maintenanceInterval: kotlin.time.Duration
)

/**
 * Monitoring configuration options
 */
data class MonitoringConfiguration(
    val maintenanceIntervalMinutes: Int = 10,
    val memoryThresholdMB: Int = 100,
    val enableDetailedLogging: Boolean = false,
    val enableCacheCleanup: Boolean = true,
    val enableMemoryMonitoring: Boolean = true,
    val enablePerformanceStats: Boolean = true
) {
    companion object {
        val default = MonitoringConfiguration()

        val aggressive = MonitoringConfiguration(
            maintenanceIntervalMinutes = 5,
            memoryThresholdMB = 50,
            enableDetailedLogging = true
        )

        val conservative = MonitoringConfiguration(
            maintenanceIntervalMinutes = 30,
            memoryThresholdMB = 200,
            enableDetailedLogging = false
        )
    }
}

/**
 * Singleton manager for the performance monitoring service
 */
object PerformanceMonitoringManager {
    private var service: ApplicationPerformanceMonitoringService? = null

    /**
     * Initialize and start the monitoring service
     */
    fun initialize(logger: Logger) {
        if (service == null) {
            service = ApplicationPerformanceMonitoringService(logger)
            service?.start()
        }
    }

    /**
     * Stop the monitoring service
     */
    fun shutdown() {
        service?.stop()
        service = null
    }

    /**
     * Get the current service instance
     */
    fun getInstance(): ApplicationPerformanceMonitoringService? = service

    /**
     * Force maintenance tasks if service is running
     */
    suspend fun forceMaintenance() {
        service?.forceMaintenanceTasks()
    }
}