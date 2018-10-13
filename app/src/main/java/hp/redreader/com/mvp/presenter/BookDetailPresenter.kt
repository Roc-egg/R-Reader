package hp.redreader.com.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.BookDetailContract
import hp.redreader.com.mvp.model.entity.book.BookDetailBean
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.isNetWork
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber


@ActivityScope
class BookDetailPresenter
@Inject
constructor(model: BookDetailContract.Model, rootView: BookDetailContract.View) :
        BasePresenter<BookDetailContract.Model, BookDetailContract.View>(model, rootView) {
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

    fun getBookDetail(id: String?) {

        mModel.getBookDetail(id)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<BookDetailBean>(mErrorHandler) {
                    override fun onComplete() {
                        mRootView.showLoading()
                    }
                    override fun onNext(bookDetailBean: BookDetailBean) {
                        mRootView.setDatas(bookDetailBean)
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        mRootView.hideLoading()
                    }

                })
    }
}
