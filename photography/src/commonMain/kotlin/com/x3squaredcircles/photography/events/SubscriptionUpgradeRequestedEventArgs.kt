// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/events/SubscriptionUpgradeRequestedEventArgs.kt
package com.x3squaredcircles.photography.events

data class SubscriptionUpgradeRequestedEventArgs(
    val requiredSubscription: String = "",
    val message: String = "",
    val featureName: String = ""
)