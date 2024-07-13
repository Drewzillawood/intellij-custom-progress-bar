package com.drewzillawood.customprogressbar.domain

import com.drewzillawood.customprogressbar.data.PersistentConfigService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class GetConfigUseCase {

  operator fun invoke() = configService().read()

  companion object {
    @JvmStatic
    fun configService() = service<PersistentConfigService>()
  }
}