package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "PersistentConfigs", storages = [Storage("PersistentConfigs.xml")])
class PersistentConfigsComponent : SimplePersistentStateComponent<PersistentConfigs>(PersistentConfigs())