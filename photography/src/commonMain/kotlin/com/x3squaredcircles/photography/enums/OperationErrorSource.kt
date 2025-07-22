// photography/src/commonMain/kotlin/com/x3squaredcircles/photography/enums/OperationErrorSource.kt
package com.x3squaredcircles.photography.enums

enum class OperationErrorSource {
    Unknown,
    Validation,
    Database,
    Network,
    Sensor,
    Permission,
    Device,
    MediaService,
    Navigation,
    Calculation
}