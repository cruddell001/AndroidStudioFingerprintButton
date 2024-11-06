package com.ruddell.adb_buttons

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.JFrame
import javax.swing.JLabel

class SettingsScreen: AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // Open the settings screen
        SettingsScreenWindow().isVisible = true
    }
}

private class SettingsScreenWindow: JFrame() {
    init {
        title = "Custom ADB Commands"
        setSize(400, 300)
        setLocationRelativeTo(null)
        defaultCloseOperation = DISPOSE_ON_CLOSE
        val label = JLabel("Settings screen")
        add(label)
    }
}
