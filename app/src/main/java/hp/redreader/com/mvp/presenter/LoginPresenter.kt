package hp.redreader.com.mvp.presenter

import android.app.Application
import android.text.Editable
import android.text.TextUtils

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.ArmsUtils
import com.jess.arms.utils.RxLifecycleUtils
import hp.redreader.com.app.utils.data.TaskLocalDataSource
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.LoginContract
import hp.redreader.com.mvp.model.entity.wanandroid.LoginBean
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import hp.redreader.com.mvp.model.entity.data.User
import me.jessyan.mvparms.demo.app.handleLoginSuccess


@ActivityScope
class LoginPresenter
@Inject
constructor(model: LoginContract.Model, rootView: LoginContract.View) :
        BasePresenter<LoginContract.Model, LoginContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager


    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 注册
     */
    fun register(username: Editable?, password: Editable?) {
        if (!verifyData(username, password)) {
            return
        }
        mModel.register(username, password)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<LoginBean>(mErrorHandler) {
                    override fun onNext(bean: LoginBean) {

                        if (null != bean.data) {
                            TaskLocalDataSource.getInstance(mApplication).deleteAll(User::class.java)
                            TaskLocalDataSource.getInstance(mApplication).insert(bean.data)
                            handleLoginSuccess()
                            mRootView.loadSuccess()
                        } else {
                            ArmsUtils.snackbarTextWithLong(bean.errorMsg)
                        }
                    }
                })
    }

    /**
     * 登录
     */
    fun login(username: Editable?, password: Editable?) {
        if (!verifyData(username, password)) {
            return
        }

        mModel.login(username, password)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<LoginBean>(mErrorHandler) {
                    override fun onNext(bean: LoginBean) {

                        if (null != bean.data) {
                            TaskLocalDataSource.getInstance(mApplication).deleteAll(User::class.java)
                            TaskLocalDataSource.getInstance(mApplication).insert(bean.data)
                            handleLoginSuccess()
                            mRootView.loadSuccess()
                        } else {
                            ArmsUtils.snackbarTextWithLong(bean.errorMsg)
                        }
                    }
                })

    }

    private fun verifyData(username: Editable?, password: Editable?): Boolean {
        if (TextUtils.isEmpty(username)) {
            mRootView.showMessage("请输入用户名")
            return false
        }
        if (TextUtils.isEmpty(password)) {
            mRootView.showMessage("请输入密码")
            return false
        }
        return true
    }

}
