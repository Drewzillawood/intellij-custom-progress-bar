package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentConfigs
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service
@State(name = "PersistentDemoConfigs", storages = [Storage("PersistentDemoConfigs.xml")])
class PersistentDemoConfigServiceImpl : SimplePersistentStateComponent<PersistentConfigs>(PersistentConfigs())