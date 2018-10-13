package hp.redreader.com.mvp.ui.activity

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ActivityUtils

import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerSplashComponent
import hp.redreader.com.di.module.SplashModule
import hp.redreader.com.mvp.contract.SplashContract
import hp.redreader.com.mvp.presenter.SplashPresenter

import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity


/**
 * 如果没presenter
 * 你可以这样写
 *
 * @ActivityScope(請注意命名空間) class NullObjectPresenterByActivity
 * @Inject constructor() : IPresenter {
 * override fun onStart() {
 * }
 *
 * override fun onDestroy() {
 * }
 * }
 */
class SplashActivity : BaseActivity<SplashPresenter>(), SplashContract.View, Animator.AnimatorListener {

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerSplashComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .splashModule(SplashModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_splash //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        animation_view.addAnimatorListener(this)

        tv_jump.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                animation_view.pauseAnimation()
                toMainActivity()
            }
        })
    }

    override fun onAnimationRepeat(p0: Animator?) {
    }

    override fun onAnimationEnd(p0: Animator?) {
        toMainActivity()
    }

    fun toMainActivity() {
        startActivity<MainActivity>()
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out)
        killMyself()
    }

    override fun onAnimationCancel(p0: Animator?) {
    }

    override fun onAnimationStart(p0: Animator?) {
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }
}
