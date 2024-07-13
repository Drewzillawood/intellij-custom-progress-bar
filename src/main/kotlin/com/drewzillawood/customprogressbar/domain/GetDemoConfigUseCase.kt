package com.drewzillawood.customprogressbar.domain

import com.drewzillawood.customprogressbar.data.PersistentDemoConfigService
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class GetDemoConfigUseCase {

  operator fun invoke() = configService().read()

  companion object {
    @JvmStatic
    fun configService() = service<PersistentDemoConfigService>()
  }
}