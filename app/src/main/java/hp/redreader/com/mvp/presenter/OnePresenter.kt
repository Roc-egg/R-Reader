package hp.redreader.com.mvp.presenter

import android.app.Application
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.OneContract
import hp.redreader.com.mvp.model.entity.movie.HotMovieBean
import hp.redreader.com.mvp.model.entity.movie.SubjectsBean
import hp.redreader.com.mvp.model.entity.wanandroid.DuanZiBean
import hp.redreader.com.mvp.ui.adapter.JokeAdapter
import hp.redreader.com.mvp.ui.adapter.OneAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.isNetWork
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay


@FragmentScope
class OnePresenter
@Inject
constructor(model: OneContract.Model, rootView: OneContract.View) :
        BasePresenter<OneContract.Model, OneContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    @Inject
    lateinit var mAdapter: OneAdapter
    @Inject
    lateinit var mData: MutableList<SubjectsBean>

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getHotMovie() {

        if (isNetWork()) {//网络不可用，使用缓存
            mRootView.showMessage("网络不可用")
        }
        /**
         * 设置一直使用缓存
         * 缓存过期时间设置为1天，超过后清除本缓存
         */
        mModel.getHotMovie(false)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<HotMovieBean>(mErrorHandler) {
                    override fun onNext(bean: HotMovieBean) {
                        mRootView.showLoading()
                        mData.clear()
                        if (bean.subjects.size <= 0) {
                            mRootView.hideLoading()
                            return
                        }
                        mData.addAll(bean.subjects)
                        mAdapter.notifyDataSetChanged()

                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        mRootView.hideLoading()
                    }
                })

    }
}
