package com.drewzillawood.customprogressbar.domain

import com.drewzillawood.customprogressbar.data.PersistentDemoConfigService
import com.drewzillawood.customprogressbar.data.model.PersistentDemoConfigs
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class SaveDemoConfigUseCase {

  operator fun invoke(configs: PersistentDemoConfigs) = configService().save(configs)

  companion object {
    @JvmStatic
    fun configService() = service<PersistentDemoConfigService>()
  }
}