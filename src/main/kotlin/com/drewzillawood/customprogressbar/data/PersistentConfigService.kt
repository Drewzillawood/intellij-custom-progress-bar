package com.drewzillawood.customprogressbar.data

import com.drewzillawood.customprogressbar.data.model.PersistentConfigs

interface PersistentConfigService {
  fun save(configs: PersistentConfigs)
  fun read(): PersistentConfigs
}