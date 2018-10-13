package hp.redreader.com.mvp.ui.fragment.wanChild

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.xrecyclerview.XRecyclerView

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils
import com.youth.banner.BannerConfig
import com.youth.banner.loader.ImageLoader

import hp.redreader.com.di.component.DaggerBannerComponent
import hp.redreader.com.di.module.BannerModule
import hp.redreader.com.mvp.contract.BannerContract
import hp.redreader.com.mvp.presenter.BannerPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseFragment
import hp.redreader.com.app.utils.displayRandom
import hp.redreader.com.mvp.model.entity.data.User
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.model.entity.wanandroid.WanAndroidBannerBean
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import hp.redreader.com.mvp.ui.adapter.WanAndroidAdapter
import kotlinx.android.synthetic.main.fragment_banner.view.*
import kotlinx.android.synthetic.main.fragment_banner.view.srl_book
import kotlinx.android.synthetic.main.header_wan_android.view.*
import org.simple.eventbus.Subscriber
import timber.log.Timber
import javax.inject.Inject


/**
 * 类名：    BannerFragment.kt
 * 类描述：  玩安卓Fragment
 * 创建人：  hp
 * 创建时间：2018/9/20/020 15:13
 * 修改人：  hp
 * 修改时间：2018/9/20/020 15:13
 * 修改备注：
 */
class BannerFragment : MyBaseFragment<BannerPresenter>(), BannerContract.View, BaseQuickAdapter.OnItemClickListener {

    @Inject
    lateinit var mAdapter: WanAndroidAdapter

    private var mIsPrepared: Boolean = false
    private var mIsFirst = true
    private lateinit var headerView: View
    private lateinit var mList: List<WanAndroidBannerBean.DataBean>

    companion object {
        private val TYPE = "param1"
        private var mType = "综合"

        fun newInstance(s: String): BannerFragment {
            val fragment = BannerFragment()
            val args = Bundle()
            args.putString(TYPE, s)
            fragment.arguments = args
            return fragment
        }
    }


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerBannerComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .bannerModule(BannerModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_banner, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        showContentView()
        initRefreshView()

        // 准备就绪
        mIsPrepared = true
        /**
         * 因为启动时先走loadData()再走onActivityCreated，
         * 所以此处要额外调用load(),不然最初不会加载内容
         */
        loadData()
    }

    private fun initRefreshView() {
        ContentView.srl_book.setColorSchemeColors(ArmsUtils.getColor(activity, R.color.colorTheme))
        ContentView.srl_book.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            ContentView.srl_book.postDelayed({
                loadCustomData(true)
            }, 1000)
        })
        ContentView.xrv_book.layoutManager = LinearLayoutManager(activity)
        ContentView.xrv_book.setPullRefreshEnabled(false)
        ContentView.xrv_book.clearHeader()

        mAdapter.setPresenter(mPresenter)
        ContentView.xrv_book.adapter = mAdapter.apply {
            onItemClickListener = this@BannerFragment
        }

        headerView = layoutInflater.inflate(R.layout.header_wan_android, ContentView.xrv_book.parent as ViewGroup, false)
        //两种为recyclerview添加header,postion索引有区别
//        ContentView.xrv_book.addHeaderView(headerView)
        mAdapter.addHeaderView(headerView)

        headerView.banner.setDelayTime(3000)
        headerView.banner.setIndicatorGravity(BannerConfig.CENTER)
        headerView.banner.setImageLoader(object : ImageLoader() {
            override fun displayImage(context: Context, path: Any, imageView: ImageView) {
                if (path is String) {
                    displayRandom(1, path, imageView)
                }
            }
        })
        headerView.banner.setOnBannerListener { position ->
            if (mList[position] != null && !TextUtils.isEmpty(mList[position].url)) {
                WebViewActivity.loadUrl(context, mList[position].url, mList[position].title)
            }
        }

        ContentView.xrv_book.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                loadCustomData(false)
            }
        })
    }

    private fun loadCustomData(b: Boolean) {
        if (b)
            mPresenter?.getWanAndroidBanner()// 请求banner数据
        mPresenter?.getHomeList(b, null)
    }

    /**
     * item内部点击
     */
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        var bean = adapter!!.data[position - 1] as HomeListBean.DatasBean
        WebViewActivity.loadUrl(activity, bean.link, bean.title)
    }

    //    /**
//     * item点击事件监听
//     */
//    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
//        var bean = adapter!!.data[position - 1] as HomeListBean.DatasBean
//        when (view!!.id) {
//            R.id.textView4 -> {//类型点击
//                ArmsUtils.snackbarText("类型点击了$position")
//                ArticleListActivity.start(activity as Activity, bean.chapterId, bean.chapterName)
//            }
//            R.id.vb_collect -> {//收藏点击
//                ArmsUtils.snackbarText("收藏点击了$position")
//                if (isLogin(activity as Activity)){
//                    Timber.e("-----binding.vbCollect.isChecked():${}")
//                }
//            }
//        }
//    }
    override fun onRefresh() {
        ContentView.srl_book.isRefreshing = true
        loadCustomData(true)
    }

    /**
     * 登录成功后刷新
     */
    @Subscriber(tag = "LoginSuccess")
    private fun updata(user: User) {
        ContentView.srl_book.isRefreshing = true
        loadCustomData(true)
    }

    override fun loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return
        }
        mIsFirst = false
        ContentView.srl_book.isRefreshing = true
        ContentView.srl_book.postDelayed({ loadCustomData(true) }, 500)
    }


    override fun showBannerView(mBannerImages: ArrayList<String>, mBannerTitles: ArrayList<String>, result: List<WanAndroidBannerBean.DataBean>) {
        headerView.banner.visibility = View.VISIBLE
        headerView.banner.setBannerTitles(mBannerTitles)
        headerView.banner.setImages(mBannerImages)
        headerView.banner.isAutoPlay(true)
        headerView.banner.start()
        mList = result
    }

    override fun loadBannerFailure() {
        headerView.banner.visibility = View.GONE
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
        Timber.tag(TAG).w("showLoading")
        ContentView.xrv_book.refreshComplete()
    }

    override fun hideLoading() {
        Timber.tag(TAG).w("hideLoading")
        showContentView()
        if (ContentView.srl_book.isRefreshing) {
            ContentView.srl_book.isRefreshing = false
        }
        if (mPresenter?.mPage == 0) {
            showError()
        } else {
            ContentView.xrv_book.refreshComplete()
        }
    }

    override fun showLoadSuccessView() {
        showContentView()
        ContentView.srl_book.isRefreshing = false
    }

    override fun showListNoMoreLoading() {
        Timber.e("showListNoMoreLoading...")
        ContentView.xrv_book.noMoreLoading()
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
