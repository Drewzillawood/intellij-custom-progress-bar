package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "PersistentConfigs", storages = [Storage("PersistentConfigs.xml")])
class PersistentConfigServiceImpl : PersistentStateComponent<PersistentConfigs>, PersistentConfigService, Disposable {

  private var state: PersistentConfigs = PersistentConfigs()

  init {
//    ApplicationManager.getApplication().messageBus.connect(this)
//      .subscribe()
  }

  override fun getState(): PersistentConfigs = state

  override fun loadState(state: PersistentConfigs) {
    this.state = state
  }

  override fun save(configs: PersistentConfigs) {
    with(state) {
      myIndeterminatePrimaryColor = configs.myIndeterminatePrimaryColor
      myIndeterminateSecondaryColor = configs.myIndeterminateSecondaryColor
      myDeterminatePrimaryColor = configs.myDeterminatePrimaryColor
      myDeterminateSecondaryColor = configs.myDeterminateSecondaryColor
      isCustomProgressBarEnabled = configs.isCustomProgressBarEnabled
      isAdvancedOptionsEnabled = configs.isAdvancedOptionsEnabled
      cycleTime = configs.cycleTime
      repaintInterval = configs.repaintInterval
    }
  }

  override fun read(): PersistentConfigs = PersistentConfigs(state)

  override fun dispose() {}
}

