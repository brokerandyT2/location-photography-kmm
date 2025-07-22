// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/models/CachedFeatureAccessResult.kt
package com.x3squaredcircles.photography.models

import kotlinx.datetime.Instant

data class FeatureAccessResult(
    val hasAccess: Boolean,
    val action: FeatureAccessAction,
    val requiredSubscription: String = "",
    val message: String = ""
)

enum class FeatureAccessAction {
    ShowUpgradePrompt,
    ShowError
}

data class CachedFeatureAccessResult(
    val accessResult: FeatureAccessResult,
    val hasAccess: Boolean,
    val timestamp: Instant
)