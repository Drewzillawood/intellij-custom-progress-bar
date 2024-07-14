package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentDemoConfigs

interface PersistentDemoConfigService {
  fun save(configs: PersistentDemoConfigs)
  fun read(): PersistentDemoConfigs
}