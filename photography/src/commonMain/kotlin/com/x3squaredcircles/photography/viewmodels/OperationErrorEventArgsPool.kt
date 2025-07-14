// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/OperationErrorEventArgsPool.kt
package com.x3squaredcircles.photography.viewmodels
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
// Object pool for OperationErrorEventArgs to reduce allocations
internal object OperationErrorEventArgsPool {
    private val pool = ConcurrentHashMap<Int, OperationErrorEventArgs>()
    private val poolCount = AtomicBoolean(false)
    private const val MAX_POOL_SIZE = 10
    fun get(message: String): OperationErrorEventArgs {
        // Simple pool implementation - return new instance for now
        return OperationErrorEventArgs(message)
    }

    fun `return`(args: OperationErrorEventArgs) {
        // Simple implementation - could be enhanced with actual pooling
    }
}