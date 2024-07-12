package com.drewzillawood.customprogressbar.settings

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.Converter
import com.intellij.util.xmlb.annotations.Attribute
import java.awt.Color

@Service
@State(name = "CustomProgressBarSettings", storages = [(Storage("Custom-ProgressBar-Settings.xml"))])
class CustomProgressBarSettings : SimplePersistentStateComponent<CustomProgressBarSettings.State>(State()) {

    class State : BaseState() {
        var isCustomProgressBarEnabled: Boolean by property(true)

        private val DEFAULT_PRIMARY_BLUE = Color(64, 115, 213)
        private val DEFAULT_SECONDARY_BLUE = Color(100, 148, 243)

        @get:Attribute(converter = ColorConverter::class)
        var myIndeterminatePrimaryColor: Color by property(DEFAULT_PRIMARY_BLUE) { it == DEFAULT_PRIMARY_BLUE }
        @get:Attribute(converter = ColorConverter::class)
        var myIndeterminateSecondaryColor: Color by property(DEFAULT_SECONDARY_BLUE) { it == DEFAULT_SECONDARY_BLUE }
        @get:Attribute(converter = ColorConverter::class)
        var myIndeterminatePrimaryDemoColor: Color by property(DEFAULT_PRIMARY_BLUE) { it == DEFAULT_PRIMARY_BLUE }
        @get:Attribute(converter = ColorConverter::class)
        var myIndeterminateSecondaryDemoColor: Color by property(DEFAULT_SECONDARY_BLUE) { it == DEFAULT_SECONDARY_BLUE }
        @get:Attribute(converter = ColorConverter::class)
        var myDeterminatePrimaryColor: Color by property(DEFAULT_PRIMARY_BLUE) { it == DEFAULT_PRIMARY_BLUE }
        @get:Attribute(converter = ColorConverter::class)
        var myDeterminateSecondaryColor: Color by property(JBColor.GRAY) { it == JBColor.GRAY }
        @get:Attribute(converter = ColorConverter::class)
        var myDeterminatePrimaryDemoColor: Color by property(DEFAULT_PRIMARY_BLUE) { it == DEFAULT_PRIMARY_BLUE }
        @get:Attribute(converter = ColorConverter::class)
        var myDeterminateSecondaryDemoColor: Color by property(JBColor.GRAY) { it == JBColor.GRAY }

        var version: String? by string()

        var isAdvancedOptionsEnabled: Boolean by property(false)
        var cycleTime: Int by property(800)
        var cycleDemoTime: Int by property(800)
        var repaintInterval: Int by property(50)
        var repaintDemoInterval: Int by property(50)
    }

    companion object {
        @JvmStatic
        fun getInstance() = service<CustomProgressBarSettings>()
    }
}

private class ColorConverter : Converter<Color>() {
    override fun toString(value: Color): String = value.rgb.toString()

    override fun fromString(value: String): Color = Color(value.toInt())
}