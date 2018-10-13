package com.example.xrecyclerview

/**
 * Created by jianghejie on 15/11/22.
 */
internal interface BaseRefreshHeader {

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
