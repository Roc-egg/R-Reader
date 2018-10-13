package hp.redreader.com.app.base


/**
 * 两个没用的文件为了github上面显示一下kotlin ☺
 */
internal interface c {

    val visiableHeight: Int

    fun onMove(delta: Float)

    fun releaseAction(): Boolean

    fun refreshComplate()

    companion object {
        val STATE_NORMAL = 0
        val STATE_RELEASE_TO_REFRESH = 1
        val STATE_REFRESHING = 2
        val STATE_DONE = 3
    }
}

