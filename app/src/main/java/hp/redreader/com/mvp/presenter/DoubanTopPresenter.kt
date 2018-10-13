package hp.redreader.com.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.ActivityScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.DoubanTopContract
import hp.redreader.com.mvp.model.entity.movie.HotMovieBean
import hp.redreader.com.mvp.model.entity.movie.SubjectsBean
import hp.redreader.com.mvp.ui.adapter.DouBanTopAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.isNetWork
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay


@ActivityScope
class DoubanTopPresenter
@Inject
constructor(model: DoubanTopContract.Model, rootView: DoubanTopContract.View) :
        BasePresenter<DoubanTopContract.Model, DoubanTopContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager
    @Inject
    lateinit var mAdapter: DouBanTopAdapter
    @Inject
    lateinit var mData: MutableList<SubjectsBean>

    // 开始请求的角标
    var mPage = 0
    // 一次请求的数量
    private var preEndIndex: Int = 21

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getHotMovie(pullToRefresh: Boolean) {
        if (pullToRefresh) mPage = 0//下拉刷新默认只请求第一页

        var isEvictCache = pullToRefresh//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存


        if (isNetWork()) {//网络不可用，使用缓存
            if (pullToRefresh) {
                mRootView.showMessage("网络不可用")
            }
            isEvictCache = false
        }

        mModel.getHotMovie(mPage, preEndIndex, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<HotMovieBean>(mErrorHandler) {
                    override fun onNext(bean: HotMovieBean) {

                        mRootView.showLoadSuccessView()


                        if (pullToRefresh) {
                            mData.clear()//如果是下拉刷新则清空列表
                            if (bean.subjects == null || bean.subjects.size <= 0) {
                                mRootView.hideLoading()
                                return
                            }
                        } else {
                            if (bean.subjects == null || bean.subjects.size <= 0) {
                                mRootView.showListNoMoreLoading()
                                return
                            }
                        }
                        mData.addAll(bean.subjects)
//                        if (pullToRefresh)
                        mAdapter.notifyDataSetChanged()
//                        else
//                            mAdapter.notifyItemRangeInserted(preEndIndex, bean.data.datas.size)
                        mRootView.showLoading()//更新列表加载状态
                        mPage += preEndIndex
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        mRootView.hideLoading()
                    }
                })
    }
}
