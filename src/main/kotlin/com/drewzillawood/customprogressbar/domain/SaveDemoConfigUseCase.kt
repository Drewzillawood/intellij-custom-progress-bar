package com.drewzillawood.customprogressbar.domain

import com.drewzillawood.customprogressbar.data.PersistentDemoConfigService
import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class SaveDemoConfigUseCase {

  operator fun invoke(configs: PersistentConfigs) = configService().save(configs)

  companion object {
    @JvmStatic
    fun configService() = service<PersistentDemoConfigService>()
  }
}