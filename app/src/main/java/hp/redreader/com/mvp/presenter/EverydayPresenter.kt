package hp.redreader.com.mvp.presenter

import android.app.Application
import android.text.TextUtils
import com.blankj.utilcode.util.SPUtils

import com.jess.arms.integration.AppManager
import com.jess.arms.di.scope.FragmentScope
import com.jess.arms.mvp.BasePresenter
import com.jess.arms.http.imageloader.ImageLoader
import com.jess.arms.utils.RxLifecycleUtils
import me.jessyan.rxerrorhandler.core.RxErrorHandler
import javax.inject.Inject

import hp.redreader.com.mvp.contract.EverydayContract
import hp.redreader.com.mvp.model.api.ConstantsImageUrl
import hp.redreader.com.mvp.model.entity.gank.AndroidBean
import hp.redreader.com.mvp.model.entity.gank.FrontpageBean
import hp.redreader.com.mvp.model.entity.gank.GankIoDayBean
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import me.jessyan.mvparms.demo.app.*
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber
import me.jessyan.rxerrorhandler.handler.RetryWithDelay
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


@FragmentScope
class EverydayPresenter
@Inject
constructor(model: EverydayContract.Model, rootView: EverydayContract.View) :
        BasePresenter<EverydayContract.Model, EverydayContract.View>(model, rootView) {
    @Inject
    lateinit var mErrorHandler: RxErrorHandler
    @Inject
    lateinit var mApplication: Application
    @Inject
    lateinit var mImageLoader: ImageLoader
    @Inject
    lateinit var mAppManager: AppManager

    private var year = "2018"
    private var month = "10"
    private var day = "11"
    private val HOME_ONE = "home_one"
    private val HOME_TWO = "home_two"
    private val HOME_SIX = "home_six"

    override fun onDestroy() {
        super.onDestroy()
    }

    private var mBannerImages: ArrayList<String>? = ArrayList()
    private var mLists: ArrayList<List<AndroidBean>>? = ArrayList()

    fun setData(year: String, month: String, day: String) {
        this.year = year
        this.month = month
        this.day = day
    }

    fun loadData() {
        var isEvictCache = false//是否驱逐缓存,为ture即不使用缓存,每次下拉刷新即需要最新数据,则不使用缓存

        val oneData = SPUtils.getInstance().getString("everyday_data", "2018-10-12")

        if (oneData != getData()) {//不是当天请求
            if (isRightTime()) {
                //大于12：30,请求
                mRootView.setIsOldDayRequest(false)
                isEvictCache = true
            } else {
                // 小于，使用缓存
                mRootView.setIsOldDayRequest(true)

                val lastTime = getLastTime(getTodayTime()[0], getTodayTime()[1], getTodayTime()[2])
                setData(lastTime[0], lastTime[1], lastTime[2])

            }
        } else {//是当天请求,使用缓存
            mRootView.setIsOldDayRequest(false)
        }

        if (isNetWork()) {//网络不可用，使用缓存
            mRootView.showMessage("网络不可用")
        }
        showBannerPage(isEvictCache)
        showRecyclerViewData(isEvictCache)


    }

    /**
     * 求情banner数据
     */
    private fun showBannerPage(evictCache: Boolean) {
        /**
         * 设置一直使用缓存
         * 缓存过期时间设置为1天，超过后清除本缓存
         */
        mModel.showBannerPage(evictCache)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<FrontpageBean>(mErrorHandler) {
                    override fun onNext(bean: FrontpageBean) {

                        mBannerImages?.clear()
                        if (bean.result != null && bean.result.focus != null && bean.result.focus.result != null) {
                            val result = bean.result.focus.result as ArrayList<FrontpageBean.ResultBannerBean.FocusBean.ResultBeanX>
                            if (result.size > 0) {
                                for (i in result.indices) {
                                    //获取所有图片
                                    mBannerImages?.add(result[i].randpic)
                                    Timber.e("getGankIoDay=222=${result[i].randpic}")
                                }
                                mRootView.showBannerView(mBannerImages!!, result)
                            }
                        }
                    }

                })
    }

    /**
     * 请求列表数据
     */
    private fun showRecyclerViewData(evictCache: Boolean) {
        SPUtils.getInstance().put(HOME_ONE, "")
        SPUtils.getInstance().put(HOME_TWO, "")
        SPUtils.getInstance().put(HOME_SIX, "")
        val func1 = object : Function<GankIoDayBean, Observable<ArrayList<List<AndroidBean>>>> {
            override fun apply(gankIoDayBean: GankIoDayBean): Observable<ArrayList<List<AndroidBean>>> {
                val lists = ArrayList<List<AndroidBean>>()
                val results = gankIoDayBean.results

                if (results.android != null && results.android.size > 0) {
                    addUrlList(lists, results.android, "Android")
                }
                if (results.welfare != null && results.welfare.size > 0) {
                    addUrlList(lists, results.welfare, "福利")
                }
                if (results.getiOS() != null && results.getiOS().size > 0) {
                    addUrlList(lists, results.getiOS(), "IOS")
                }
                if (results.restMovie != null && results.restMovie.size > 0) {
                    addUrlList(lists, results.restMovie, "休息视频")
                }
                if (results.resource != null && results.resource.size > 0) {
                    addUrlList(lists, results.resource, "拓展资源")
                }
                if (results.recommend != null && results.recommend.size > 0) {
                    addUrlList(lists, results.recommend, "瞎推荐")
                }
                if (results.front != null && results.front.size > 0) {
                    addUrlList(lists, results.front, "前端")
                }
                if (results.getApp() != null && results.app.size > 0) {
                    addUrlList(lists, results.app, "App")
                }

                return Observable.just(lists)
            }
        }

        mModel.getGankIoDay(year, month, day, evictCache)
                .flatMap(func1)
                .subscribeOn(Schedulers.io())
                .retryWhen(RetryWithDelay(3, 2))//遇到错误时重试,第一个参数为重试几次,第二个参数为重试的间隔
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))//使用 Rxlifecycle,使 Disposable 和 Activity 一起销毁
                .subscribe(object : ErrorHandleSubscriber<ArrayList<List<AndroidBean>>>(mErrorHandler) {
                    override fun onNext(bean: ArrayList<List<AndroidBean>>) {

                        mLists = bean
                        if (mLists!!.size > 0 && mLists!![0].isNotEmpty()) {
                            mRootView.showListView(mLists!!)
                        } else {
                            //有网但是没请求道数据， 一直请求，直到请求到数据为止
                            val lastTime = getLastTime(year, month, day)
                            setData(lastTime[0], lastTime[1], lastTime[2])
                            showRecyclerViewData(true)
                        }
                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        if (mLists!!.size > 0) {
                            return
                        }
                        mRootView.showErrorView()
                    }
                })
    }

    private fun addUrlList(lists: ArrayList<List<AndroidBean>>, arrayList: List<AndroidBean>, typeTitle: String) {
        // title
        val bean = AndroidBean()
        bean.type_title = typeTitle
        val androidBeen = ArrayList<AndroidBean>()
        androidBeen.add(bean)
        lists.add(androidBeen)

        val androidSize = arrayList.size

        if (androidSize > 0 && androidSize < 4) {

            lists.add(addUrlList(arrayList, androidSize))
        } else if (androidSize >= 4) {

            val list1 = ArrayList<AndroidBean>()
            val list2 = ArrayList<AndroidBean>()

            for (i in 0 until androidSize) {
                if (i < 3) {
                    list1.add(getAndroidBean(arrayList, i, androidSize))
                } else if (i < 6) {
                    list2.add(getAndroidBean(arrayList, i, androidSize))
                }
            }
            lists.add(list1)
            lists.add(list2)
        }
    }

    private fun getAndroidBean(arrayList: List<AndroidBean>, i: Int, androidSize: Int): AndroidBean {

        val androidBean = AndroidBean()
        // 标题
        androidBean.desc = arrayList[i].desc
        // 类型
        androidBean.type = arrayList[i].type
        // 跳转链接
        androidBean.url = arrayList[i].url
        // 随机图的url
        when {
            i < 3 -> androidBean.image_url = ConstantsImageUrl.HOME_SIX_URLS[getRandom(3)]//三小图
            androidSize == 4 -> androidBean.image_url = ConstantsImageUrl.HOME_ONE_URLS[getRandom(1)]//一图
            androidSize == 5 -> androidBean.image_url = ConstantsImageUrl.HOME_TWO_URLS[getRandom(2)]//两图
            androidSize >= 6 -> androidBean.image_url = ConstantsImageUrl.HOME_SIX_URLS[getRandom(3)]//三小图
        }
        return androidBean
    }


    private fun addUrlList(arrayList: List<AndroidBean>, androidSize: Int): List<AndroidBean> {
        val tempList = ArrayList<AndroidBean>()
        for (i in 0 until androidSize) {
            val androidBean = AndroidBean()
            // 标题
            androidBean.desc = arrayList[i].desc
            // 类型
            androidBean.type = arrayList[i].type
            // 跳转链接
            androidBean.url = arrayList[i].url
            //            DebugUtil.error("---androidSize:  " + androidSize);
            // 随机图的url
            when (androidSize) {
                1 -> androidBean.image_url = ConstantsImageUrl.HOME_ONE_URLS[getRandom(1)]//一图
                2 -> androidBean.image_url = ConstantsImageUrl.HOME_TWO_URLS[getRandom(2)]//两图
                3 -> androidBean.image_url = ConstantsImageUrl.HOME_SIX_URLS[getRandom(3)]//三图
            }
            tempList.add(androidBean)
        }
        return tempList
    }

    /**
     * 取不同的随机图，在每次网络请求时重置
     */
    private fun getRandom(type: Int): Int {
        var saveWhere = ""
        var urlLength = 0
        // 已取到的值
        when (type) {
            1 -> {
                saveWhere = HOME_ONE
                urlLength = ConstantsImageUrl.HOME_ONE_URLS.size
            }
            2 -> {
                saveWhere = HOME_TWO
                urlLength = ConstantsImageUrl.HOME_TWO_URLS.size
            }
            3 -> {
                saveWhere = HOME_SIX
                urlLength = ConstantsImageUrl.HOME_SIX_URLS.size
            }
        }

        val homeSix = SPUtils.getInstance().getString(saveWhere, "")
        if (!TextUtils.isEmpty(homeSix)) {
            // 已取到的值
            val split = homeSix.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

            val random = Random()
            for (j in 0 until urlLength) {
                val randomInt = random.nextInt(urlLength)

                var isUse = false
                for (aSplit in split) {
                    if (!TextUtils.isEmpty(aSplit) && randomInt.toString() == aSplit) {
                        isUse = true
                        break
                    }
                }
                if (!isUse) {
                    val sb = StringBuilder(homeSix)
                    sb.insert(0, randomInt.toString() + ",")
                    SPUtils.getInstance().put(saveWhere, sb.toString())
                    return randomInt
                }
            }

        } else {
            val random = Random()
            val randomInt = random.nextInt(urlLength)
            SPUtils.getInstance().put(saveWhere, randomInt.toString() + ",")
            return randomInt
        }
        return 0
    }
}
