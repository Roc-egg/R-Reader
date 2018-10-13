package hp.redreader.com.mvp.presenter

import android.app.Application

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import hp.redreader.com.app.utils.listener.WanNavigator
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.BannerContract
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.model.entity.wanandroid.WanAndroidBannerBean
import hp.redreader.com.mvp.ui.adapter.WanAndroidAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.isNetWork
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import java.util.ArrayList


@FragmentScope
class BannerPresenter
@Inject
constructor(model: BannerContract.Model, rootView: BannerContract.View) :
        BasePresenter<BannerContract.Model, BannerContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager
    @Inject
    lateinit var mAdapter: WanAndroidAdapter
    @Inject
    lateinit var mData: MutableList<HomeListBean.DatasBean>

    var mPage = 0
    private var preEndIndex: Int = 0

    private var mBannerImages: ArrayList<String>? = null
    private var mBannerTitles: ArrayList<String>? = null

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * 获取轮播数据
     */
    fun getWanAndroidBanner() {

        var isEvictCache = true//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存


        if (isNetWork()) {//网络不可用，使用缓存
            isEvictCache = false
        }

        mModel.getWanAndroidBanner(isEvictCache)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<WanAndroidBannerBean>(mErrorHandler) {
                    override fun onNext(wanAndroidBannerBean: WanAndroidBannerBean) {
                        if (mBannerImages == null) {
                            mBannerImages = ArrayList()
                        } else {
                            mBannerImages!!.clear()
                        }
                        if (mBannerTitles == null) {
                            mBannerTitles = ArrayList()
                        } else {
                            mBannerTitles!!.clear()
                        }
                        var result: List<WanAndroidBannerBean.DataBean>? = null
                        if (wanAndroidBannerBean.data != null) {
                            result = wanAndroidBannerBean.data
                            if (result != null && result.isNotEmpty()) {
                                for (i in result.indices) {
                                    //获取所有图片
                                    mBannerImages!!.add(result[i].imagePath)
                                    mBannerTitles!!.add(result[i].title)
                                }
                                mRootView.showBannerView(mBannerImages!!, mBannerTitles!!, result)
                            }
                        }
                        if (result == null) {
                            mRootView.loadBannerFailure()
                        }
                    }
                })
    }

    /**
     * @param pullToRefresh true=下拉
     * @param cid 体系id
     */
    fun getHomeList(pullToRefresh: Boolean, cid: Int?) {

        if (pullToRefresh) mPage = 0//下拉刷新默认只请求第一页

        var isEvictCache = pullToRefresh//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存


        if (isNetWork()) {//网络不可用，使用缓存
            if (pullToRefresh) {
                mRootView.showMessage("网络不可用")
            }
            isEvictCache = false
        }

        mModel.getHomeList(mPage, cid, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .doOnSubscribe { disposable ->
                    //请求开始
                }.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    //完成
                }
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<HomeListBean>(mErrorHandler) {
                    override fun onNext(bean: HomeListBean) {

                        mRootView.showLoadSuccessView()

                        if (pullToRefresh) {
                            mData.clear()//如果是下拉刷新则清空列表
                            if (bean.data == null || bean.data.datas == null || bean.data.datas.size <= 0) {
                                mRootView.hideLoading()
                                return
                            }
                        } else {
                            if (bean.data == null || bean.data.datas == null || bean.data.datas.size <= 0) {
                                mRootView.showListNoMoreLoading()
                                return
                            }
                        }
                        preEndIndex = mData.size//更新之前列表总长度,用于确定加载更多的起始位置
                        mData.addAll(bean.data.datas)
//                        if (pullToRefresh)
                        mAdapter.notifyDataSetChanged()
//                        else
//                            mAdapter.notifyItemRangeInserted(preEndIndex, bean.data.datas.size)
                        mRootView.showLoading()//更新列表加载状态
                        mPage++
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        mRootView.hideLoading()
                    }
                })
    }

    /**
     * 我的收藏
     */
    fun getCollectList(pullToRefresh: Boolean) {
        if (pullToRefresh) mPage = 0//下拉刷新默认只请求第一页

        var isEvictCache = pullToRefresh//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存


        if (isNetWork()) {//网络不可用，使用缓存
            if (pullToRefresh) {
                mRootView.showMessage("网络不可用")
            }
            isEvictCache = false
        }

        mModel.getCollectList(mPage, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<HomeListBean>(mErrorHandler) {
                    override fun onNext(bean: HomeListBean) {

                        mRootView.showLoadSuccessView()

                        if (pullToRefresh) {
                            mData.clear()//如果是下拉刷新则清空列表
                            if (bean.data == null || bean.data.datas == null || bean.data.datas.size <= 0) {
                                mRootView.hideLoading()
                                return
                            }
                        } else {
                            if (bean.data == null || bean.data.datas == null || bean.data.datas.size <= 0) {
                                mRootView.showListNoMoreLoading()
                                return
                            }
                        }
                        preEndIndex = mData.size//更新之前列表总长度,用于确定加载更多的起始位置
                        mData.addAll(bean.data.datas)
//                        if (pullToRefresh)
                        mAdapter.notifyDataSetChanged()
//                        else
//                            mAdapter.notifyItemRangeInserted(preEndIndex, bean.data.datas.size)
                        mRootView.showLoading()//更新列表加载状态
                        mPage++
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        mRootView.hideLoading()
                    }
                })
    }

    /**
     * 收藏
     */
    fun collect(id: Int?, navigator: WanNavigator.OnCollectNavigator) {

        if (isNetWork()) {//网络不可用
            mRootView.showMessage("网络不可用")
            return
        }

        mModel.collect(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<HomeListBean>(mErrorHandler) {
                    override fun onNext(bean: HomeListBean) {

                        if (bean.isSuccess) {
                            navigator.onSuccess()
                        } else {
                            navigator.onFailure()
                        }
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        navigator.onFailure()
                    }
                })
    }

    /**
     * @param isCollectList 是否是收藏列表
     * @param originId      如果是收藏列表的话就是原始文章的id，如果是站外文章就是-1
     * @param id            bean里的id
     */
    fun unCollect(isCollectList: Boolean, id: Int?, originId: Int?, navigator: WanNavigator.OnCollectNavigator) {
        if (isCollectList) {
            unCollect(id, originId, navigator)
        } else {
            unCollect(id, navigator)
        }
    }

    /**
     * 取消收藏，我的收藏页
     */
    private fun unCollect(id: Int?, originId: Int?, navigator: WanNavigator.OnCollectNavigator) {
        if (isNetWork()) {//网络不可用
            mRootView.showMessage("网络不可用")
            return
        }

        mModel.unCollect(id, originId)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<HomeListBean>(mErrorHandler) {
                    override fun onNext(bean: HomeListBean) {

                        if (bean.isSuccess) {
                            navigator.onSuccess()
                        } else {
                            navigator.onFailure()
                        }
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        navigator.onFailure()
                    }
                })
    }

    /**
     * 取消收藏
     */
    private fun unCollect(id: Int?, navigator: WanNavigator.OnCollectNavigator) {
        if (isNetWork()) {//网络不可用
            mRootView.showMessage("网络不可用")
            return
        }

        mModel.unCollectOrigin(id)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<HomeListBean>(mErrorHandler) {
                    override fun onNext(bean: HomeListBean) {

                        if (bean.isSuccess) {
                            navigator.onSuccess()
                        } else {
                            navigator.onFailure()
                        }
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        navigator.onFailure()
                    }
                })
    }

}
