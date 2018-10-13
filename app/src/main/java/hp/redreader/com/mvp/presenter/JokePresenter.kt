package hp.redreader.com.mvp.presenter

import android.app.Application
import android.text.TextUtils

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.JokeContract
import hp.redreader.com.mvp.model.entity.wanandroid.DuanZiBean
import hp.redreader.com.mvp.model.entity.wanandroid.QsbkListBean
import hp.redreader.com.mvp.ui.adapter.JokeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.isNetWork
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import java.util.*


@FragmentScope
class JokePresenter
@Inject
constructor(model: JokeContract.Model, rootView: JokeContract.View) :
        BasePresenter<JokeContract.Model, JokeContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager
    @Inject
    lateinit var mAdapter: JokeAdapter
    @Inject
    lateinit var mData: MutableList<DuanZiBean>

    // 开始请求的角标
    var mPage = 0

    override fun onDestroy() {
        super.onDestroy()
    }

    fun getQsbkList(pullToRefresh: Boolean) {
        if (pullToRefresh) mPage = getRandom()//下拉刷新默认只请求第一页

        var isEvictCache = pullToRefresh//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存


        if (isNetWork()) {//网络不可用，使用缓存
            if (pullToRefresh) {
                mRootView.showMessage("网络不可用")
            }
            isEvictCache = false
        }
        mModel.getQsbkList(mPage, isEvictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<QsbkListBean>(mErrorHandler) {
                    override fun onNext(bean: QsbkListBean) {
                        var lists = makeDatas(bean)
                        mRootView.showLoadSuccessView()

                        if (pullToRefresh) {
                            mData.clear()//如果是下拉刷新则清空列表
                            if (lists.isEmpty()) {
                                mRootView.hideLoading()
                                return
                            }
                        } else {
                            if (lists.isEmpty()) {
                                mRootView.showListNoMoreLoading()
                                return
                            }
                        }
                        mData.addAll(lists)
                        mAdapter.notifyDataSetChanged()
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
     * 重组数据
     */
    private fun makeDatas(bean: QsbkListBean): List<DuanZiBean> {
        val lists = ArrayList<DuanZiBean>()
        if (bean.items != null && bean.items.size > 0) {
            val items = bean.items
            for (bean1 in items) {
                val duanZiBean = DuanZiBean()
                duanZiBean.content = bean1.content
                duanZiBean.createTime = bean1.published_at
                val topic = bean1.topic
                val user = bean1.user
                if (topic != null) {
                    duanZiBean.categoryName = topic.content
                }
                if (user != null) {
                    duanZiBean.name = user.login
//                    val thumb = user.thumb
//                    if (!TextUtils.isEmpty(thumb)) {
//                        val stringBuffer = StringBuffer()
//                        stringBuffer.append("http:")
//                        stringBuffer.append(thumb)
//                        duanZiBean.avatarUrl = stringBuffer.toString()
//                    }
                    duanZiBean.avatarUrl = user.thumb
                }
                lists.add(duanZiBean)
            }
        }
        return lists
    }

    private fun getRandom(): Int {
        return Random().nextInt(100)
    }
}
