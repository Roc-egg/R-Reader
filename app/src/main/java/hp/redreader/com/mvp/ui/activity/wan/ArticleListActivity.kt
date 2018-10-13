package hp.redreader.com.mvp.ui.activity.wan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.xrecyclerview.XRecyclerView

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils


import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseActivity
import hp.redreader.com.app.base.MyBasesActivity
import hp.redreader.com.di.component.DaggerBannerComponent
import hp.redreader.com.di.module.BannerModule
import hp.redreader.com.mvp.contract.BannerContract
import hp.redreader.com.mvp.model.entity.data.User
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.model.entity.wanandroid.WanAndroidBannerBean
import hp.redreader.com.mvp.presenter.BannerPresenter
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import hp.redreader.com.mvp.ui.adapter.WanAndroidAdapter
import kotlinx.android.synthetic.main.fragment_banner.view.*
import org.simple.eventbus.Subscriber
import timber.log.Timber
import javax.inject.Inject


/**
 * 类名：    ArticleListActivity.kt
 * 类描述：  玩安卓文章列表
 * 创建人：  hp
 * 创建时间：2018/9/20/020 15:12
 * 修改人：  hp
 * 修改时间：2018/9/20/020 15:12
 * 修改备注：
 */
class ArticleListActivity : MyBasesActivity<BannerPresenter>(), BannerContract.View, BaseQuickAdapter.OnItemClickListener {
    @Inject
    lateinit var mAdapter: WanAndroidAdapter

    private var cid = 0

    companion object {
        fun start(mContext: Context) {
            val intent = Intent(mContext, ArticleListActivity::class.java)
            mContext.startActivity(intent)
        }

        fun start(mContext: Context, cid: Int, chapterName: String) {
            val intent = Intent(mContext, ArticleListActivity::class.java)
            intent.putExtra("cid", cid)
            intent.putExtra("chapterName", chapterName)
            mContext.startActivity(intent)
        }
    }

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerBannerComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .bannerModule(BannerModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.fragment_banner //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        initRefreshView()
        getIntentData()
        loadData(true)
    }

    private fun getIntentData() {
        cid = intent.getIntExtra("cid", 0)
        val chapterName = intent.getStringExtra("chapterName")

        if (cid != 0) {
            title = chapterName
            mAdapter.setNoShowChapterName()
        } else {
            title = "我的收藏"
            mAdapter.setCollectList()
        }
    }

    private fun loadData(b: Boolean) {
        if (cid != 0) {
            mPresenter?.getHomeList(b, cid)
        } else {
            mPresenter?.getCollectList(b)
        }
    }

    private fun initRefreshView() {
        ContentView.srl_book.setColorSchemeColors(ArmsUtils.getColor(this, R.color.colorTheme))
        ContentView.srl_book.setOnRefreshListener {
            ContentView.srl_book.postDelayed({
                loadData(true)
            }, 1000)
        }
        ContentView.xrv_book.layoutManager = LinearLayoutManager(this)
        ContentView.xrv_book.setPullRefreshEnabled(false)
        ContentView.xrv_book.clearHeader()
        ContentView.xrv_book.adapter = mAdapter.apply {
            onItemClickListener = this@ArticleListActivity
        }


        ContentView.xrv_book.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                loadData(false)
            }
        })
    }


    /**
     * item内部点击
     */
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        var bean = adapter!!.data[position - 1] as HomeListBean.DatasBean
        ArmsUtils.snackbarText("点击了$position")
        WebViewActivity.loadUrl(this, bean.link, bean.title)
    }

    override fun onRefresh() {
        ContentView.srl_book.isRefreshing = true
        loadData(true)
    }
    /**
     * 登录成功后刷新
     */
    @Subscriber(tag = "LoginSuccess")
    private fun updata(user: User) {
        ContentView.srl_book.isRefreshing = true
        loadData(true)
    }
    override fun showBannerView(mBannerImages: ArrayList<String>, mBannerTitles: ArrayList<String>, result: List<WanAndroidBannerBean.DataBean>) {
    }

    override fun loadBannerFailure() {
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
        finish()
    }
}
