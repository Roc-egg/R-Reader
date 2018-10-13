package hp.redreader.com.mvp.presenter

import android.app.Application
import android.util.Log

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.WelfareContract
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.ui.adapter.WelfareAdapter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.isNetWork
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import timber.log.Timber


@FragmentScope
class WelfarePresenter
@Inject
constructor(model: WelfareContract.Model, rootView: WelfareContract.View) :
        BasePresenter<WelfareContract.Model, WelfareContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager
    @Inject
    lateinit var mAdapter: WelfareAdapter
    @Inject
    lateinit var mData: MutableList<GankIoDataBean.ResultBean>

    // 开始请求的角标
    var mPage = 1
    // 一次请求的数量
    private var preEndIndex: Int = 20

    /**
     * 图片url集合
     */
    private var imgList: java.util.ArrayList<String> = java.util.ArrayList()
    /**
     * 图片标题集合，用于保存图片时使用
     */
    private var imageTitleList: java.util.ArrayList<String> = java.util.ArrayList()
    /**
     * 传递给Activity数据集合
     */
    private var allList: java.util.ArrayList<java.util.ArrayList<String>> = java.util.ArrayList()

    override fun onDestroy() {
        super.onDestroy()
    }

    fun loadWelfareData(pullToRefresh: Boolean) {
        if (pullToRefresh) mPage = 1//下拉刷新默认只请求第一页

        var isEvictCache = pullToRefresh//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存


        if (isNetWork()) {//网络不可用，使用缓存
            if (pullToRefresh) {
                mRootView.showMessage("网络不可用")
            }
            isEvictCache = false
        }
        Timber.e("页数==$mPage")
        mModel.getGankIoData("福利", mPage, preEndIndex, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<GankIoDataBean>(mErrorHandler) {
                    override fun onNext(bean: GankIoDataBean) {

                        mRootView.showLoadSuccessView()

                        if (pullToRefresh) {
                            mData.clear()//如果是下拉刷新则清空列表
                            if (bean.results == null || bean.results.size <= 0) {
                                mRootView.hideLoading()
                                return
                            }
                        } else {
                            if (bean.results == null || bean.results.size <= 0) {
                                mRootView.showListNoMoreLoading()
                                return
                            }
                        }
                        handleImageList(bean)
                        mData.addAll(bean.results)
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
     * 异步处理用于图片详情显示的数据
     *
     * @param gankIoDataBean 原数据
     */
    private fun handleImageList(gankIoDataBean: GankIoDataBean) {

        Observable.create(ObservableOnSubscribe<GankIoDataBean> { e ->
            e.onNext(gankIoDataBean)
        }).map(object : Function<GankIoDataBean, ArrayList<ArrayList<String>>> {
            override fun apply(t: GankIoDataBean): ArrayList<ArrayList<String>> {
                imgList.clear()
                imageTitleList.clear()
                for (i in 0 until gankIoDataBean.results.size) {
                    imgList.add(gankIoDataBean.results[i].url)
                    imageTitleList.add(gankIoDataBean.results[i].desc)
                }
                allList.clear()
                allList.add(imgList)
                allList.add(imageTitleList)
                return allList
            }

        }).subscribeOn(Schedulers.newThread()) //线程调度器,将发送者运行在子线程
                .observeOn(AndroidSchedulers.mainThread())//接受者运行在主线程
                .subscribe(object : Consumer<ArrayList<ArrayList<String>>> {
                    override fun accept(t: ArrayList<ArrayList<String>>?) {
                        mRootView.setImageList(t)
                    }

                })

    }
}
