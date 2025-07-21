// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/application/common/behaviors/PerformanceBehavior.kt
package com.x3squaredcircles.photography.application.common.behaviors

import co.touchlab.kermit.Logger
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Pipeline behavior to monitor and log slow-running requests for performance optimization
 */
class PerformanceBehavior<TRequest, TResponse>(
    private val logger: Logger
) : IPipelineBehavior<TRequest, TResponse>
        where TRequest : Any,
              TResponse : Any {

    companion object {
        internal const val SLOW_REQUEST_THRESHOLD_MS = 5000L // 5 seconds
        internal const val MODERATE_REQUEST_THRESHOLD_MS = 1000L // 1 second
        internal const val DEBUG_THRESHOLD_MS = 500L // 500 milliseconds
    }

    override suspend fun handle(
        request: TRequest,
        next: suspend () -> TResponse
    ): TResponse {
        val requestName = request::class.simpleName ?: "Unknown"
        val startTime = Clock.System.now()

        val response: TResponse = try {
            next()
        } catch (exception: Exception) {
            val elapsedMs = Clock.System.now().toEpochMilliseconds() - startTime.toEpochMilliseconds()
            // Log performance even for failed requests
            logger.e(exception) { "Request $requestName failed after ${elapsedMs}ms" }
            throw exception
        }

        val elapsedMs = Clock.System.now().toEpochMilliseconds() - startTime.toEpochMilliseconds()

        // Log performance metrics based on execution time
        when {
            elapsedMs >= SLOW_REQUEST_THRESHOLD_MS -> {
                logger.w { "Slow Request: $requestName took ${elapsedMs}ms" }

                // Log additional context for very slow requests
                logSlowRequestDetails(requestName, elapsedMs, request)
            }

            elapsedMs >= MODERATE_REQUEST_THRESHOLD_MS -> {
                logger.i { "Request: $requestName took ${elapsedMs}ms" }
            }

            elapsedMs >= DEBUG_THRESHOLD_MS -> {
                logger.d { "Request: $requestName completed in ${elapsedMs}ms" }
            }

            else -> {
                logger.v { "Request: $requestName completed quickly (${elapsedMs}ms)" }
            }
        }

        // Log memory-intensive operations
        if (elapsedMs >= MODERATE_REQUEST_THRESHOLD_MS) {
            logMemoryUsage(requestName)
        }

        return response
    }

    private fun logSlowRequestDetails(
        requestName: String,
        elapsedMs: Long,
        request: TRequest
    ) {
        logger.w {
            buildString {
                appendLine("Performance Alert - Slow Request Detected:")
                appendLine("  Request: $requestName")
                appendLine("  Duration: ${elapsedMs}ms")
                appendLine("  Thread: ${getCurrentThreadInfo()}")

                // Add request-specific details for debugging
                when {
                    requestName.contains("Image", ignoreCase = true) -> {
                        appendLine("  Type: Image processing operation")
                        appendLine("  Recommendation: Consider image size optimization or background processing")
                    }

                    requestName.contains("Camera", ignoreCase = true) -> {
                        appendLine("  Type: Camera operation")
                        appendLine("  Recommendation: Verify camera permissions and hardware availability")
                    }

                    requestName.contains("Calculate", ignoreCase = true) -> {
                        appendLine("  Type: Calculation operation")
                        appendLine("  Recommendation: Consider caching results or algorithm optimization")
                    }

                    requestName.contains("Query", ignoreCase = true) -> {
                        appendLine("  Type: Data query operation")
                        appendLine("  Recommendation: Check database indexes and query optimization")
                    }

                    else -> {
                        appendLine("  Type: General operation")
                        appendLine("  Recommendation: Review operation complexity and consider optimization")
                    }
                }
            }
        }
    }

    private fun logMemoryUsage(requestName: String) {
        try {
            // Platform-specific memory monitoring would be implemented here
            logger.d { "Request $requestName completed - memory monitoring enabled" }

            // Note: Kotlin/Native and platform-specific memory monitoring
            // would require platform-specific implementations
        } catch (ex: Exception) {
            logger.v(ex) { "Could not retrieve memory usage for $requestName" }
        }
    }

    private fun getCurrentThreadInfo(): String {
        return try {
            // This would be platform-specific in a real implementation
            "Main-Thread"
        } catch (ex: Exception) {
            "Unknown-Thread"
        }
    }
}

/**
 * Performance monitoring configuration and utilities
 */
object PerformanceMonitoring {

    /**
     * Tracks performance metrics for requests
     */
    data class PerformanceMetrics(
        val requestName: String,
        val executionTimeMs: Long,
        val timestamp: Instant,
        val threadInfo: String,
        val memoryUsage: Long? = null
    )

    private val performanceHistory = mutableListOf<PerformanceMetrics>()
    private const val MAX_HISTORY_SIZE = 1000

    /**
     * Records performance metrics for analysis
     */
    fun recordMetrics(metrics: PerformanceMetrics) {
        synchronized(performanceHistory) {
            performanceHistory.add(metrics)

            // Keep only recent metrics to prevent memory leaks
            if (performanceHistory.size > MAX_HISTORY_SIZE) {
                performanceHistory.removeFirst()
            }
        }
    }

    /**
     * Gets performance statistics for analysis
     */
    fun getPerformanceStatistics(): PerformanceStatistics {
        synchronized(performanceHistory) {
            if (performanceHistory.isEmpty()) {
                return PerformanceStatistics(
                    totalRequests = 0,
                    averageExecutionTimeMs = 0L,
                    slowRequestCount = 0,
                    slowestRequest = null
                )
            }

            val totalRequests = performanceHistory.size
            val averageTimeMs = performanceHistory.sumOf { it.executionTimeMs } / totalRequests

            val slowRequests = performanceHistory.count {
                it.executionTimeMs >= PerformanceBehavior.SLOW_REQUEST_THRESHOLD_MS
            }

            val slowestRequest = performanceHistory.maxByOrNull { it.executionTimeMs }

            return PerformanceStatistics(
                totalRequests = totalRequests,
                averageExecutionTimeMs = averageTimeMs,
                slowRequestCount = slowRequests,
                slowestRequest = slowestRequest
            )
        }
    }

    /**
     * Clears performance history to free memory
     */
    fun clearHistory() {
        synchronized(performanceHistory) {
            performanceHistory.clear()
        }
    }
}

/**
 * Performance statistics summary
 */
data class PerformanceStatistics(
    val totalRequests: Int,
    val averageExecutionTimeMs: Long,
    val slowRequestCount: Int,
    val slowestRequest: PerformanceMonitoring.PerformanceMetrics?
)