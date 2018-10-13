package hp.redreader.com.mvp.ui.activity.movie

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import com.example.xrecyclerview.XRecyclerView

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerDoubanTopComponent
import hp.redreader.com.di.module.DoubanTopModule
import hp.redreader.com.mvp.contract.DoubanTopContract
import hp.redreader.com.mvp.presenter.DoubanTopPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseActivity
import hp.redreader.com.app.base.MyBasesActivity
import hp.redreader.com.mvp.ui.adapter.DouBanTopAdapter
import kotlinx.android.synthetic.main.activity_douban_top.view.*
import timber.log.Timber
import javax.inject.Inject


/**
 * 类名：    DoubanTopActivity.kt
 * 类描述：  电影Top250
 * 创建人：  hp
 * 创建时间：2018/9/25/025 23:49
 * 修改人：  hp
 * 修改时间：2018/9/25/025 23:49
 * 修改备注：
 */
class DoubanTopActivity : MyBasesActivity<DoubanTopPresenter>(), DoubanTopContract.View {

    @Inject
    lateinit var mLayoutManager: StaggeredGridLayoutManager

    @Inject
    lateinit var mAdapter: DouBanTopAdapter

    companion object {
        fun start(mContext: Context) {
            val intent = Intent(mContext, DoubanTopActivity::class.java)
            mContext.startActivity(intent)
        }

    }

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerDoubanTopComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .doubanTopModule(DoubanTopModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_douban_top //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        title = "豆瓣电影Top250"

        initRefreshView()
        loadDouBanTop250(true)
    }

    private fun loadDouBanTop250(b: Boolean) {
        mPresenter?.getHotMovie(b)
    }

    private fun initRefreshView() {

        ContentView.xrv_top.layoutManager = mLayoutManager
        ContentView.xrv_top.setPullRefreshEnabled(false)
        ContentView.xrv_top.clearHeader()

        ContentView.xrv_top.adapter = mAdapter.apply {
            //            onItemLongClickListener = this@JokeFragment
        }

        ContentView.xrv_top.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                loadDouBanTop250(false)
            }
        })
    }

    override fun onRefresh() {
        loadDouBanTop250(true)
    }

    override fun showLoading() {
        Timber.tag(TAG).w("showLoading")
        ContentView.xrv_top.refreshComplete()
    }

    override fun hideLoading() {
        Timber.tag(TAG).w("hideLoading")
        showContentView()
        if (mPresenter?.mPage == 0) {
            showError()
        } else {
            ContentView.xrv_top.refreshComplete()
        }
    }

    override fun showLoadSuccessView() {
        showContentView()
    }

    override fun showListNoMoreLoading() {
        Timber.e("showListNoMoreLoading...")
        ContentView.xrv_top.noMoreLoading()
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
