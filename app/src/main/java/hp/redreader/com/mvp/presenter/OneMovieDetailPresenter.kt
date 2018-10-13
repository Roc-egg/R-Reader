package hp.redreader.com.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.OneMovieDetailContract
import hp.redreader.com.mvp.model.entity.movie.MovieDetailBean
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import timber.log.Timber


@ActivityScope
class OneMovieDetailPresenter
@Inject
constructor(model: OneMovieDetailContract.Model, rootView: OneMovieDetailContract.View) :
        BasePresenter<OneMovieDetailContract.Model, OneMovieDetailContract.View>(model, rootView) {
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

    fun getMovieDetail(id: String?) {

        mModel.getMovieDetail(id)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<MovieDetailBean>(mErrorHandler) {
                    override fun onComplete() {
                        Timber.e("onComplete---来了啊")
                        mRootView.showLoading()
                    }

                    override fun onNext(bean: MovieDetailBean) {
                        mRootView.setDatas(bean)
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        mRootView.hideLoading()
                    }

                })
    }
}
