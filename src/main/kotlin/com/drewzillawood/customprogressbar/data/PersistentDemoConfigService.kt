package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentConfigs

interface PersistentDemoConfigService {
  fun save(configs: PersistentConfigs)
  fun read(): PersistentConfigs
}