package hp.redreader.com.mvp.ui.fragment.gankChild

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.blankj.utilcode.util.SPUtils
import com.bumptech.glide.Glide

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.youth.banner.BannerConfig
import com.youth.banner.loader.ImageLoader

import hp.redreader.com.di.component.DaggerEverydayComponent
import hp.redreader.com.di.module.EverydayModule
import hp.redreader.com.mvp.contract.EverydayContract
import hp.redreader.com.mvp.presenter.EverydayPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseFragment
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.displayRandom
import hp.redreader.com.mvp.model.entity.gank.AndroidBean
import hp.redreader.com.mvp.model.entity.gank.FrontpageBean
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import hp.redreader.com.mvp.ui.adapter.EverydayAdapter
import kotlinx.android.synthetic.main.fragment_everyday.view.*
import kotlinx.android.synthetic.main.header_item_everyday.view.*
import kotlinx.android.synthetic.main.home_middle_layout.view.*
import me.jessyan.mvparms.demo.app.getData
import me.jessyan.mvparms.demo.app.getLastTime
import me.jessyan.mvparms.demo.app.getTodayTime
import org.simple.eventbus.EventBus
import java.util.ArrayList
import javax.inject.Inject


/**
 * 类名：    EverydayFragment.kt
 * 类描述：  每日推荐
 *
 * 更新逻辑：判断是否是第二天(相对于2018-9-26)
 * 是：判断是否是大于12：30
 * *****     |是：刷新当天数据
 * *****     |否：使用缓存：|无：请求前一天数据,直到请求到数据为止
 * **********             |有：使用缓存
 * 否：使用缓存 ： |无：请求今天数据
 * **********    |有：使用缓存
 *
 * 创建人：  hp
 * 创建时间：2018/9/26/026 20:22
 * 修改人：  hp
 * 修改时间：2018/9/26/026 20:22
 * 修改备注：
 */
class EverydayFragment : MyBaseFragment<EverydayPresenter>(), EverydayContract.View {
    @Inject
    lateinit var mAdapter: EverydayAdapter
    @Inject
    lateinit var mData: MutableList<List<AndroidBean>>

    private var mIsPrepared: Boolean = false
    private var mIsFirst = true
    private lateinit var mHeaderView: View
    private lateinit var mFooterView: View
    private lateinit var animation: RotateAnimation
    // 是否是上一天的请求
    private var isOldDayRequest: Boolean = false


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerEverydayComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .everydayModule(EverydayModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_everyday, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        showContentView()
        initAnimation()

        mHeaderView = layoutInflater.inflate(R.layout.header_item_everyday, ContentView.xrv_everyday.parent as ViewGroup, false)
        initLocalSetting()
        initRecyclerView()
        mIsPrepared = true
        /**
         * 因为启动时先走loadData()再走onActivityCreated，
         * 所以此处要额外调用load(),不然最初不会加载内容
         */
        loadData()
    }

    override fun loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return
        }
        mIsFirst = false
        // 显示时轮播图滚动
        if (mHeaderView.banner != null) {
            mHeaderView.banner.startAutoPlay()
            mHeaderView.banner.setDelayTime(4000)
        }

        mPresenter?.loadData()
    }

    private fun initRecyclerView() {
        ContentView.xrv_everyday.setPullRefreshEnabled(false)
        ContentView.xrv_everyday.setLoadingMoreEnabled(false)

        ContentView.xrv_everyday.addHeaderView(mHeaderView)

        mFooterView = layoutInflater.inflate(R.layout.footer_item_everyday, ContentView.xrv_everyday.parent as ViewGroup, false)

        ContentView.xrv_everyday.addFootView(mFooterView, true)
        ContentView.xrv_everyday.noMoreLoading()
        ContentView.xrv_everyday.layoutManager = LinearLayoutManager(context)
        // 需加，不然滑动不流畅
        ContentView.xrv_everyday.isNestedScrollingEnabled = false
        ContentView.xrv_everyday.setHasFixedSize(false)
        ContentView.xrv_everyday.itemAnimator = DefaultItemAnimator()
    }

    /**
     * 设置本地数据点击事件等
     */
    private fun initLocalSetting() {
        // 显示日期,去掉第一位的"0"
        mHeaderView.include_everyday.tv_daily_text.text =
                if (getTodayTime()[2].indexOf("0") === 0)
                    getTodayTime()[2].replace("0", "")
                else
                    getTodayTime()[2]
        mHeaderView.include_everyday.ib_xiandu.setOnClickListener(listener)
        mHeaderView.include_everyday.ib_wan_android.setOnClickListener(listener)
        mHeaderView.include_everyday.ib_movie_hot.setOnClickListener(listener)
        mHeaderView.include_everyday.fl_everyday.setOnClickListener(listener)
    }

    private val listener = object : PerfectClickListener() {
        override fun onNoDoubleClick(v: View?) {
            when (v?.id) {
                R.id.ib_xiandu -> WebViewActivity.loadUrl(v.context, getString(R.string.string_url_xiandu), "闲读")
                R.id.ib_wan_android -> WebViewActivity.loadUrl(v.context, getString(R.string.string_url_wanandroid), "玩Android")
                R.id.ib_movie_hot -> EventBus.getDefault().post("", "jump_type_to_one")
                R.id.fl_everyday -> WebViewActivity.loadUrl(v.context, getString(R.string.string_url_trending), "Trending")
                else -> {
                }
            }
        }
    }

    private fun initAnimation() {
        ContentView.ll_loading.visibility = View.VISIBLE
        animation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 3000//设置动画持续时间
        animation.interpolator = LinearInterpolator()//不停顿
        animation.repeatMode = ValueAnimator.RESTART//重新从头执行
        animation.repeatCount = ValueAnimator.INFINITE//设置重复次数
        ContentView.iv_loading.animation = animation
        animation.startNow()
    }

    override fun showRotaLoading() {
        showRotaLoading(true)
    }

    /**
     * 设置是否是上一天的请求
     */
    override fun setIsOldDayRequest(isOldDayRequest: Boolean) {
        this.isOldDayRequest = isOldDayRequest
    }

    /**
     * 显示旋转动画
     */
    private fun showRotaLoading(isLoading: Boolean) {
        if (isLoading) {
            ContentView.ll_loading.visibility = View.VISIBLE
            ContentView.xrv_everyday.visibility = View.GONE
            animation.startNow()
        } else {
            ContentView.ll_loading.visibility = View.GONE
            ContentView.xrv_everyday.visibility = View.VISIBLE
            animation.cancel()
        }
    }

    /**
     * 加载获取的轮播数据
     */
    override fun showBannerView(mBannerImages: ArrayList<String>, result: List<FrontpageBean.ResultBannerBean.FocusBean.ResultBeanX>) {
        mHeaderView.banner.setDelayTime(3000)
        mHeaderView.banner.setIndicatorGravity(BannerConfig.CENTER)
        mHeaderView.banner.setImageLoader(object : ImageLoader() {
            override fun displayImage(context: Context, path: Any, imageView: ImageView) {
                if (path is String) {
                    displayRandom(1, path, imageView)
                }
            }
        })
        mHeaderView.banner.setOnBannerListener { position ->
            if (result[position].code != null && result[position].code.startsWith("http")) {
                WebViewActivity.loadUrl(context, result[position].code, "加载中...")
            }
        }

        mHeaderView.banner.setImages(mBannerImages)
        mHeaderView.banner.isAutoPlay(true)
        mHeaderView.banner.start()

    }

    /**
     * 显示错页面
     */
    override fun showErrorView() {
        showError()
    }

    /**
     * 显示列表内容
     */
    override fun showListView(mLists: ArrayList<List<AndroidBean>>) {
        showRotaLoading(false)
        mData.clear()
        mData.addAll(mLists)

        if (isOldDayRequest) {
            val lastTime = getLastTime(getTodayTime()[0], getTodayTime()[1], getTodayTime()[2])
            SPUtils.getInstance().put("everyday_data", lastTime[0] + "-" + lastTime[1] + "-" + lastTime[2])
        } else {
            // 保存请求的日期
            SPUtils.getInstance().put("everyday_data", getData())
        }

        mIsFirst = false

        ContentView.xrv_everyday.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        showContentView()
        showRotaLoading(true)
        loadData()
    }

//    override fun onInvisible() {
//        // 不可见时轮播图停止滚动
//        if (mHeaderView != null && mHeaderView.banner != null) {
//            mHeaderView.banner.stopAutoPlay()
//        }
//    }

    override fun onResume() {
        super.onResume()
        // 失去焦点，否则RecyclerView第一个item会回到顶部
        ContentView.xrv_everyday.isFocusable = false
    }

    /**
     * 通过此方法可以使 Fragment 能够与外界做一些交互和通信, 比如说外部的 Activity 想让自己持有的某个 Fragment 对象执行一些方法,
     * 建议在有多个需要与外界交互的方法时, 统一传 {@link Message}, 通过 what 字段来区分不同的方法, 在 {@link #setData(Object)}
     * 方法中就可以 {@code switch} 做不同的操作, 这样就可以用统一的入口方法做多个不同的操作, 可以起到分发的作用
     * <p>
     * 调用此方法时请注意调用时 Fragment 的生命周期, 如果调用 {@link #setData(Object)} 方法时 {@link Fragment#onCreate(Bundle)} 还没执行
     * 但在 {@link #setData(Object)} 里却调用了 Presenter 的方法, 是会报空的, 因为 Dagger 注入是在 {@link Fragment#onCreate(Bundle)} 方法中执行的
     * 然后才创建的 Presenter, 如果要做一些初始化操作,可以不必让外部调用 {@link #setData(Object)}, 在 {@link #initData(Bundle)} 中初始化就可以了
     * <p>
     * Example usage:
     * <pre>
     *fun setData(data:Any) {
     *   if(data is Message){
     *       when (data.what) {
     *           0-> {
     *               //根据what 做你想做的事情
     *           }
     *           else -> {
     *           }
     *       }
     *   }
     *}
     *
     *
     *
     *
     *
     * // call setData(Object):
     * val data = Message();
     * data.what = 0;
     * data.arg1 = 1;
     * fragment.setData(data);
     * </pre>
     *
     * @param data 当不需要参数时 {@code data} 可以为 {@code null}
     */
    override fun setData(data: Any?) {

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

    }
}
