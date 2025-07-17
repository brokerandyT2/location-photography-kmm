// core/src/commonMain/kotlin/com/x3squaredcircles/core/application/common/interfaces/IUnitOfWork.kt
package com.x3squaredcircles.photography.application.common.interfaces

import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ISettingRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipTypeRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ITipRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ILocationRepository
import com.x3squaredcircles.photography.infrastructure.repositories.interfaces.ICameraBodyRepository

interface IUnitOfWork {
    val settings: ISettingRepository
    val tipTypes: ITipTypeRepository
    val tips: ITipRepository
    val locations: ILocationRepository
    val cameraBodies: ICameraBodyRepository

    suspend fun saveChangesAsync(): Int
    suspend fun beginTransactionAsync()
    suspend fun commitTransactionAsync()
    suspend fun rollbackTransactionAsync()
}