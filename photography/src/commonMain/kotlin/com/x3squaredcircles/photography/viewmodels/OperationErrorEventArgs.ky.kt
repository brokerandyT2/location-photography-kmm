package com.x3squaredcircles.photography.viewmodels

data class OperationErrorEventArgs(
    private var _message: String = ""
) {
    val message: String get() = _message
    // Internal method for pool reuse
    internal fun updateMessage(message: String) {
        _message = message
    }
}