package me.jessyan.mvparms.demo.app.utils

import android.support.v4.app.FragmentManager

/**
 * 项目名称：MVPArms-master
 * 类描述：
 * 创建人：hp
 * 创建时间： 2018/7/16 16:14
 * 修改人：hp
 * 修改时间： 2018/7/16 16:14
 * 修改备注：
 */
object FragmentBackHelper {
    fun HandleBack(fragmentManager: FragmentManager): Boolean {
        val fragments = fragmentManager.fragments
        fragments?.forEach {
            if (it is onActivityBackCallback) {
                return it.onBackPressed()
            }
        }

        return false
    }
}