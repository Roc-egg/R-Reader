/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.jessyan.mvparms.demo.app

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.WindowManager
import android.widget.TextView
import hp.redreader.com.R
import hp.redreader.com.mvp.ui.activity.MainActivity
import kotlinx.android.synthetic.main.include_title.*
import timber.log.Timber

/**
 * ================================================
 * 展示 [Application.ActivityLifecycleCallbacks] 的用法
 *
 *
 * Created by JessYan on 04/09/2017 17:14
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
class ActivityLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        Timber.w(activity.toString() + " - onActivityCreated")

//        activity?.let {
//            when (it) {
//                is MainActivity,is SplashActivity -> {
//                    ImmersionBar.with(activity)
//                            .keyboardEnable(true)
//                            .navigationBarWithKitkatEnable(false)
//                            .statusBarDarkFont(true, 0.2f)
//                            .init()
//                }
//                else -> {
//                    ImmersionBar.with(activity)
//                            .statusBarColor(R.color.white)
//                            .keyboardEnable(true)  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
//                            .navigationBarEnable(false)//是否可以修改导航栏颜色，默认为true
//                            .keyboardMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)  //单独指定软键盘模式
//                            .fitsSystemWindows(true)
//                            .statusBarDarkFont(true, 0.2f)
//                            .init()
//                }
//            }
//        }

    }

    override fun onActivityStarted(activity: Activity?) {
        if (activity != null) {
            Timber.w(activity.toString() + " - onActivityStarted")
//            if (!activity.intent.getBooleanExtra("isInitToolbar", false)) {
//                //由于加强框架的兼容性,故将 setContentView 放到 onActivityCreated 之后,onActivityStarted 之前执行
//                //而 findViewById 必须在 Activity setContentView() 后才有效,所以将以下代码从之前的 onActivityCreated 中移动到 onActivityStarted 中执行
//                activity.intent.putExtra("isInitToolbar", true)
//                //这里全局给Activity设置toolbar和title,你想象力有多丰富,这里就有多强大,以前放到BaseActivity的操作都可以放到这里
//                if (activity.toolbar != null) {
//                    if (activity is AppCompatActivity) {
//                        activity.setSupportActionBar(activity.toolbar as Toolbar)
//                        activity.supportActionBar!!.setDisplayShowTitleEnabled(false)
//                    } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            activity.setActionBar(activity.toolbar as android.widget.Toolbar)
//                            activity.actionBar!!.setDisplayShowTitleEnabled(false)
//                        }
//                    }
//                }
//                if (activity.toolbar_title != null) {
//                    (activity.toolbar_title as TextView).text = activity.title
//                }
//                if (activity.toolbar_back != null) {
//                    activity.toolbar_back.setOnClickListener({ activity.onBackPressed() })
//                }
//            }
        }
    }

    override fun onActivityResumed(activity: Activity?) {
        Timber.w(activity.toString() + " - onActivityResumed")
    }

    override fun onActivityPaused(activity: Activity?) {
        Timber.w(activity.toString() + " - onActivityPaused")
    }

    override fun onActivityStopped(activity: Activity?) {
        Timber.w(activity.toString() + " - onActivityStopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        Timber.w(activity.toString() + " - onActivitySaveInstanceState")
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if (activity != null) {
            Timber.w(activity.toString() + " - onActivityDestroyed")
            //横竖屏切换或配置改变时, Activity 会被重新创建实例, 但 Bundle 中的基础数据会被保存下来,移除该数据是为了保证重新创建的实例可以正常工作
            activity.intent.removeExtra("isInitToolbar")
        }
    }
}
