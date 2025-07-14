// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/viewmodels/BaseViewModel.kt
package com.x3squaredcircles.photography.viewmodels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import java.lang.ref.WeakReference
abstract class BaseViewModel {
    // Thread-safe boolean flags
    private val _isBusy = AtomicBoolean(false)
    private val _isError = AtomicBoolean(false)
    private val _hasActiveErrors = AtomicBoolean(false)
    private val _isDisposed = AtomicBoolean(false)

    // Error message state
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    // Cache command references to avoid reflection
    private var lastCommandRef: WeakReference<suspend () -> Unit>? = null
    private var _lastCommandParameter: Any? = null

    // Add the ErrorOccurred event for system errors (MediatR failures)
    private val _errorOccurred = MutableStateFlow<OperationErrorEventArgs?>(null)
    val errorOccurred: StateFlow<OperationErrorEventArgs?> = _errorOccurred.asStateFlow()

    val isBusy: Boolean
        get() = _isBusy.get()

    val isError: Boolean
        get() = _isError.get()

    val hasActiveErrors: Boolean
        get() = _hasActiveErrors.get()

    // Last command tracking for retry capability
    val lastCommand: (suspend () -> Unit)?
        get() = lastCommandRef?.get()

    val lastCommandParameter: Any?
        get() = _lastCommandParameter

    protected fun setBusy(value: Boolean) {
        _isBusy.set(value)
    }

    protected fun setError(value: Boolean) {
        _isError.set(value)
        if (value && _errorMessage.value.isNotEmpty()) {
            // ViewModel validation errors stay in UI - no event needed
        }
    }

    protected fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    protected fun setHasActiveErrors(value: Boolean) {
        _hasActiveErrors.set(value)
    }

    /**
     * Optimized command tracking with weak references
     */
    protected fun trackCommand(command: suspend () -> Unit, parameter: Any? = null) {
        if (_isDisposed.get()) return

        lastCommandRef = WeakReference(command)
        _lastCommandParameter = parameter
    }

    /**
     * Executes a command and tracks it for retry capability
     */
    suspend fun executeAndTrackAsync(command: suspend () -> Unit, parameter: Any? = null) {
        if (_isDisposed.get()) return

        trackCommand(command, parameter)
        command()
    }

    /**
     * Optimized retry with null checks and weak reference handling
     */
    suspend fun retryLastCommandAsync() {
        if (_isDisposed.get() || lastCommandRef == null) return

        lastCommandRef?.get()?.let { command ->
            command()
        }
    }

    /**
     * System error handling with event pooling
     */
    open fun onSystemError(message: String) {
        if (_isDisposed.get()) return

        val args = OperationErrorEventArgsPool.get(message)
        _errorOccurred.value = args
        OperationErrorEventArgsPool.`return`(args)
    }

    /**
     * Validation error setter
     */
    protected fun setValidationError(message: String) {
        setErrorMessage(message)
        setError(true)
    }

    /**
     * Optimized error clearing with batch updates
     */
    protected fun clearErrors() {
        val wasError = _isError.get()
        val hadActiveErrors = _hasActiveErrors.get()
        val hadErrorMessage = _errorMessage.value.isNotEmpty()

        if (wasError || hadActiveErrors || hadErrorMessage) {
            _isError.set(false)
            _errorMessage.value = ""
            _hasActiveErrors.set(false)
        }
    }

    open fun dispose() {
        if (_isDisposed.getAndSet(true)) return

        // Clear weak references
        lastCommandRef = null
        _lastCommandParameter = null
    }
}