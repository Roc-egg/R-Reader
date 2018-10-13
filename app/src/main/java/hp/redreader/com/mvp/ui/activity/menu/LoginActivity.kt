package hp.redreader.com.mvp.ui.activity.menu

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerLoginComponent
import hp.redreader.com.di.module.LoginModule
import hp.redreader.com.mvp.contract.LoginContract
import hp.redreader.com.mvp.presenter.LoginPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseActivity
import kotlinx.android.synthetic.main.activity_login.*


/**
 * 类名：    LoginActivity.kt
 * 类描述：  登录页面
 * 创建人：  hp
 * 创建时间：2018/9/20/020 15:20
 * 修改人：  hp
 * 修改时间：2018/9/20/020 15:20
 * 修改备注：
 */
class LoginActivity : MyBaseActivity<LoginPresenter>(), LoginContract.View {

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerLoginComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .loginModule(LoginModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_login //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        title = "玩安卓登录"
        showContentView()
    }

    fun register(view: View) {
        mPresenter?.register(text_email.text, password.text)
    }

    fun login(view: View) {
        mPresenter?.login(text_email.text, password.text)
    }

    override fun loadSuccess() {
        killMyself()
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
