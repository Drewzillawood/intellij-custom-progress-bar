package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service
@State(name = "PersistentConfigs", storages = [Storage("PersistentConfigs.xml")])
class PersistentConfigServiceImpl : PersistentStateComponent<PersistentConfigs>, PersistentConfigService {

  private var state: PersistentConfigs = PersistentConfigs()

  override fun getState(): PersistentConfigs = state

  override fun loadState(state: PersistentConfigs) {
    this.state = state
  }

  override fun save(configs: PersistentConfigs) {
    state.map.entries.zip(configs.map.entries).forEach {
      it.first.setValue(it.second.value)
    }
  }

  override fun read(): PersistentConfigs = state
}

