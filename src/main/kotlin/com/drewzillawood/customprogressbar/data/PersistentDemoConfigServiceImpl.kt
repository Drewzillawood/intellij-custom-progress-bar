package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentDemoConfigs
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "PersistentDemoConfigs", storages = [Storage("PersistentDemoConfigs.xml")])
class PersistentDemoConfigServiceImpl : PersistentStateComponent<PersistentDemoConfigs>, PersistentDemoConfigService {

  private var state: PersistentDemoConfigs = PersistentDemoConfigs()

  override fun getState(): PersistentDemoConfigs = state

  override fun loadState(state: PersistentDemoConfigs) {
    this.state = state
  }

  override fun save(configs: PersistentDemoConfigs) {
    with(state) {
      myIndeterminatePrimaryColor = configs.myIndeterminatePrimaryColor
      myIndeterminateSecondaryColor = configs.myIndeterminateSecondaryColor
      myDeterminatePrimaryColor = configs.myDeterminatePrimaryColor
      myDeterminateSecondaryColor = configs.myDeterminateSecondaryColor
    }
  }

  override fun read(): PersistentDemoConfigs = state
}