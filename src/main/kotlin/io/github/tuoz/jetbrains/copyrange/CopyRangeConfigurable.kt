package io.github.tuoz.jetbrains.copyrange

import com.intellij.openapi.options.Configurable
import javax.swing.*

class CopyRangeConfigurable : Configurable {
    private var panel: JPanel? = null
    private var pathFormatCombo: JComboBox<String>? = null
    private var toastCheckbox: JCheckBox? = null

    override fun createComponent(): JComponent {
        val formats = arrayOf("Filename", "Relative", "Absolute")
        pathFormatCombo = JComboBox(formats)
        toastCheckbox = JCheckBox("Show toast notification")

        val layout = GroupLayout(JPanel())
        panel = JPanel(layout)
        layout.setAutoCreateGaps(true)
        layout.setAutoCreateContainerGaps(true)

        val pathLabel = JLabel("Path format:")
        val toastLabel = JLabel("Notification:")

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(pathLabel)
                        .addComponent(toastLabel)
                )
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(pathFormatCombo)
                        .addComponent(toastCheckbox)
                )
        )

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(pathLabel)
                        .addComponent(pathFormatCombo)
                )
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(toastLabel)
                        .addComponent(toastCheckbox)
                )
        )

        reset()
        return panel!!
    }

    override fun isModified(): Boolean {
        val settings = CopyRangeSettings.getInstance()
        val format = pathFormatCombo?.selectedIndex ?: 0
        return format != settings.getPathFormat().ordinal
                || toastCheckbox?.isSelected != settings.isShowToast()
    }

    override fun apply() {
        val settings = CopyRangeSettings.getInstance()
        settings.setPathFormat(PathFormat.entries[pathFormatCombo?.selectedIndex ?: 0])
        settings.setShowToast(toastCheckbox?.isSelected ?: false)
    }

    override fun reset() {
        val settings = CopyRangeSettings.getInstance()
        pathFormatCombo?.selectedIndex = settings.getPathFormat().ordinal
        toastCheckbox?.isSelected = settings.isShowToast()
    }

    override fun getDisplayName(): String = "Copy Reference with Range"

    override fun disposeUIResources() {
        panel = null
        pathFormatCombo = null
        toastCheckbox = null
    }
}
