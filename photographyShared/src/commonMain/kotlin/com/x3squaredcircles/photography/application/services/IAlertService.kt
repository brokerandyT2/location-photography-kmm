// core/src/commonMain/kotlin/com/x3squaredcircles/photography/application/services/IAlertService.kt
package com.x3squaredcircles.photography.application.services

interface IAlertService {
    suspend fun showErrorAlertAsync(message: String, title: String)
    suspend fun showInfoAlertAsync(message: String, title: String)
    suspend fun showWarningAlertAsync(message: String, title: String)
    suspend fun showConfirmationAlertAsync(message: String, title: String): Boolean
}