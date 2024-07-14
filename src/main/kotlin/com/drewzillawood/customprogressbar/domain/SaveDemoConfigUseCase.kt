package com.drewzillawood.customprogressbar.domain

import com.drewzillawood.customprogressbar.data.PersistentDemoConfigService
import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class SaveDemoConfigUseCase {

  private val configService = service<PersistentDemoConfigService>()

  operator fun invoke(configs: PersistentConfigs) = configService.save(configs)
}