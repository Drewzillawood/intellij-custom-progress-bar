package com.drewzillawood.customprogressbar.domain

import com.drewzillawood.customprogressbar.data.PersistentConfigService
import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class SaveConfigUseCase {

  operator fun invoke(configs: PersistentConfigs) = configService().save(configs)

  companion object {
    @JvmStatic
    fun configService() = service<PersistentConfigService>()
  }
}