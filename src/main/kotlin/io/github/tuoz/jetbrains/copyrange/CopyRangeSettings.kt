package io.github.tuoz.jetbrains.copyrange

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.*

enum class PathFormat {
    FILENAME, RELATIVE, ABSOLUTE
}

@Service
@State(name = "CopyRangeSettings", storages = [Storage("copyRangeSettings.xml")])
class CopyRangeSettings : PersistentStateComponent<CopyRangeSettings.State> {

    data class State(
        var pathFormat: String = "FILENAME",
        var showToast: Boolean = false
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    fun getPathFormat(): PathFormat = try {
        PathFormat.valueOf(myState.pathFormat)
    } catch (_: Exception) {
        PathFormat.FILENAME
    }

    fun setPathFormat(format: PathFormat) {
        myState.pathFormat = format.name
    }

    fun isShowToast(): Boolean = myState.showToast

    fun setShowToast(show: Boolean) {
        myState.showToast = show
    }

    companion object {
        fun getInstance(): CopyRangeSettings =
            ApplicationManager.getApplication().getService(CopyRangeSettings::class.java)
    }
}
