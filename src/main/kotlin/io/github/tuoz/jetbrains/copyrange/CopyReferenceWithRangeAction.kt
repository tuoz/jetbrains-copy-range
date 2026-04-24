package io.github.tuoz.jetbrains.copyrange

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vfs.VirtualFile
import java.awt.datatransfer.StringSelection

class CopyReferenceWithRangeAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val document = editor.document

        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

        val selectionModel = editor.selectionModel
        val hasSelection = selectionModel.hasSelection()

        val line: Int
        val endLine: Int

        if (hasSelection) {
            val startOffset = selectionModel.selectionStart
            val endOffset = selectionModel.selectionEnd

            var startLine = document.getLineNumber(startOffset) + 1
            var endLineTemp = document.getLineNumber(endOffset) + 1

            if (endOffset > 0 && endOffset == document.getLineStartOffset(endLineTemp - 1)) {
                endLineTemp -= 1
            }

            if (endLineTemp < startLine) {
                endLineTemp = startLine
            }

            line = startLine
            endLine = endLineTemp
        } else {
            val offset = editor.caretModel.offset
            line = document.getLineNumber(offset) + 1
            endLine = line
        }

        val filePath = formatPath(virtualFile, project)
        val result = if (line == endLine) {
            "$filePath:$line"
        } else {
            "$filePath:$line-$endLine"
        }

        CopyPasteManager.getInstance().setContents(StringSelection(result))

        val settings = CopyRangeSettings.getInstance()
        if (settings.isShowToast()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Copy Reference with Range")
                .createNotification("Copied: $result", NotificationType.INFORMATION)
                .notify(project)
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible =
            project != null && editor != null && virtualFile != null
    }

    private fun formatPath(virtualFile: VirtualFile, project: com.intellij.openapi.project.Project): String {
        return when (CopyRangeSettings.getInstance().getPathFormat()) {
            PathFormat.FILENAME -> virtualFile.name
            PathFormat.RELATIVE -> {
                val projectBase = project.basePath
                if (projectBase != null) {
                    val filePath = virtualFile.path
                    if (filePath.startsWith(projectBase)) {
                        filePath.removePrefix(projectBase).trimStart('/')
                    } else {
                        virtualFile.name
                    }
                } else {
                    virtualFile.name
                }
            }
            PathFormat.ABSOLUTE -> virtualFile.path
        }
    }
}
