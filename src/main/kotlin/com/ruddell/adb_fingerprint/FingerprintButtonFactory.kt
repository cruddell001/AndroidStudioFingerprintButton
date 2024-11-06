package com.ruddell.adb_fingerprint

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import com.intellij.util.ui.UIUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.JLabel
import javax.swing.JPanel

private const val TOOL_WINDOW_ID = "Fingerprint"
class FingerprintButtonFactory: ToolWindowFactory {
    private var isActive = false

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("FingerprintButtonFactory: ${toolWindow.id}")
        val myToolWindow = MyToolWindowUI(toolWindow)
        val content = toolWindow.contentManager.factory.createContent(myToolWindow.component, "", false)
        toolWindow.contentManager.addContent(content)
        setupListener(project)
        setIcon(toolWindow)
    }

    private fun setIcon(toolWindow: ToolWindow) {
        val icon = when (UIUtil.isUnderDarcula()) {
            true -> "/icons/fingerprint_dark.png"
            false -> "/icons/fingerprint.png"
        }
        toolWindow.setIcon(IconLoader.getIcon(icon, javaClass))
    }

    private fun setupListener(project: Project) {
        project.messageBus.connect().subscribe(
            ToolWindowManagerListener.TOPIC,
            object : ToolWindowManagerListener {
                override fun stateChanged(toolWindowManager: ToolWindowManager) {
                    println("FingerprintButtonFactory: stateChanged: ${toolWindowManager.activeToolWindowId}: ${toolWindowManager.toolWindowIdSet}")

                    if (isActive) return println("FingerprintButtonFactory: skipping b/c isActive is true")
                    isActive = true

                    val fingerprintButton = toolWindowManager.getToolWindow(TOOL_WINDOW_ID) ?: return println("FingerprintButtonFactory: skipping b/c fingerprintButton is null")
                    if (fingerprintButton.isVisible) {
                        println("FingerprintButtonFactory: Hiding $TOOL_WINDOW_ID and running adb command")
                        runAdbCommand("-e", "emu", "finger", "touch", "2")
                        fingerprintButton.hide()
                    } else {
                        println("FingerprintButtonFactory: $TOOL_WINDOW_ID is not visible")
                    }
                    isActive = false
                }
            }
        )
    }
}

class MyToolWindowUI(val toolWindow: ToolWindow) {
    val component: JPanel = JPanel().apply {
        add(JLabel("You clicked on: ${toolWindow.id}"))
    }
}

fun runAdbCommand(vararg commands: String) {
    try {
        val process = ProcessBuilder("adb", *commands).start()
        val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
        println("ADB Command Result: $output")
    } catch (ex: Exception) {
        Messages.showMessageDialog(ex.message, "Error", Messages.getErrorIcon())
    }
}
