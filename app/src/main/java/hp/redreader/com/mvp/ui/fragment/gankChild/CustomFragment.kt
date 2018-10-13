package hp.redreader.com.mvp.ui.fragment.gankChild

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.cocosw.bottomsheet.BottomSheet
import com.example.xrecyclerview.XRecyclerView

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerCustomComponent
import hp.redreader.com.di.module.CustomModule
import hp.redreader.com.mvp.contract.CustomContract
import hp.redreader.com.mvp.presenter.CustomPresenter

import hp.redreader.com.R
import hp.redreader.com.mvp.model.api.Constants.GANK_CALA
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import hp.redreader.com.mvp.ui.adapter.GankAndroidAdapter
import org.simple.eventbus.Subscriber
import timber.log.Timber
import javax.inject.Inject


/**
 * 类名：    CustomFragment.kt
 * 类描述：  干货订制
 * 创建人：  hp
 * 创建时间：2018/9/26/026 20:25
 * 修改人：  hp
 * 修改时间：2018/9/26/026 20:25
 * 修改备注：
 */
class CustomFragment : MyBaseFragment<CustomPresenter>(), CustomContract.View, BaseQuickAdapter.OnItemClickListener {

    @Inject
    lateinit var mAdapter: GankAndroidAdapter
    @Inject
    lateinit var mData: MutableList<GankIoDataBean.ResultBean>

    private var mType = "all"
    private var mIsPrepared: Boolean = false
    private var mIsFirst = true
    private var builder: BottomSheet.Builder? = null
    private var mHeaderView: View? = null

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerCustomComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .customModule(CustomModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_custom, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        initDatas()
        initRecyclerView()
        // 准备就绪
        mIsPrepared = true
    }

    private fun initDatas() {
        val type = SPUtils.getInstance().getString(GANK_CALA, "全部")
        mType = when (type) {
            "全部" -> "all"
            "IOS" -> "iOS"
            else -> type
        }
    }

    private fun initRecyclerView() {
        // 禁止下拉刷新
        ContentView.xrv_custom.setPullRefreshEnabled(false)
        // 去掉刷新头
        ContentView.xrv_custom.clearHeader()
        if (mHeaderView == null) {
            mHeaderView = View.inflate(context, R.layout.header_item_gank_custom, null)
            ContentView.xrv_custom.addHeaderView(mHeaderView)
        }
        mHeaderView?.let { initHeader(it) }
        ContentView.xrv_custom.layoutManager = LinearLayoutManager(activity)
        ContentView.xrv_custom.adapter = mAdapter.apply {
            onItemClickListener = this@CustomFragment
        }
        ContentView.xrv_custom.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {}

            override fun onLoadMore() {
                loadCustomData(false)
            }
        })
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        var bean = adapter!!.data[position - 2] as GankIoDataBean.ResultBean
        WebViewActivity.loadUrl(context, bean.url, bean.desc)
    }

    override fun loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return
        }
        loadCustomData(true)
    }

    /**
     * 每日推荐跳转对应type下
     */
    @Subscriber(tag = "jump_type_code")
    private fun updata(s: String) {
        mHeaderView?.let {
            val txName = it.findViewById<TextView>(R.id.tx_name)
            when (s) {
                "IOS" -> {
                    txName.text = "IOS"
                    mType = "iOS"// 这里有严格大小写
                    mData.clear()
                    SPUtils.getInstance().put(GANK_CALA, "IOS")
                    showLoadings()
                    loadCustomData(true)
                }
                "休息视频",
                "拓展资源",
                "瞎推荐",
                "前端",
                "App" -> changeContent(txName, s)
            }
        }

    }

    private fun initHeader(mHeaderView: View) {
        val txName = mHeaderView.findViewById<TextView>(R.id.tx_name)
        val gankCala = SPUtils.getInstance().getString(GANK_CALA, "全部")
        txName.text = gankCala
        try {
            builder = BottomSheet.Builder(activity, R.style.BottomSheet_StyleDialog)
                    .title("选择分类")
                    .sheet(R.menu.gank_bottomsheet)
                    .listener { dialog, which ->
                        when (which) {
                            R.id.gank_all -> if (isOtherType("全部")) {
                                txName.text = "全部"
                                mType = "all"// 全部传 all
                                mData.clear()
                                SPUtils.getInstance().put(GANK_CALA, "全部")
                                showLoadings()
                                loadCustomData(true)
                            }
                            R.id.gank_ios -> if (isOtherType("IOS")) {
                                txName.text = "IOS"
                                mType = "iOS"// 这里有严格大小写
                                mData.clear()
                                SPUtils.getInstance().put(GANK_CALA, "IOS")
                                showLoadings()
                                loadCustomData(true)
                            }
                            R.id.gank_qian -> if (isOtherType("前端")) {
                                changeContent(txName, "前端")
                            }
                            R.id.gank_app -> if (isOtherType("App")) {
                                changeContent(txName, "App")
                            }
                            R.id.gank_xtj -> if (isOtherType("瞎推荐")) {
                                changeContent(txName, "瞎推荐")
                            }
                            R.id.gank_movie -> if (isOtherType("休息视频")) {
                                changeContent(txName, "休息视频")
                            }
                            R.id.gank_resouce -> if (isOtherType("拓展资源")) {
                                changeContent(txName, "拓展资源")
                            }
                            else -> {
                            }
                        }
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val view = mHeaderView.findViewById<View>(R.id.ll_choose_catalogue)
        view.setOnClickListener {
            if (builder != null) {
                builder?.show()
            }
        }
    }

    private fun changeContent(textView: TextView, content: String) {
        textView.text = content
        mType = content
        mData.clear()
        SPUtils.getInstance().put(GANK_CALA, content)
        showLoadings()
        loadCustomData(true)
    }

    private fun isOtherType(selectType: String): Boolean {
        val clickText = SPUtils.getInstance().getString(GANK_CALA, "全部")
        if (clickText == selectType) {
            showMessage("当前已经是" + selectType + "分类")
            return false
        } else {
            // 重置XRecyclerView状态，解决 如出现刷新到底无内容再切换其他类别后，无法上拉加载的情况
            ContentView.xrv_custom.reset()
            return true
        }
    }

    /**
     * 请求干货数据
     */
    private fun loadCustomData(b: Boolean) {
        Timber.e("mType111==$mType")
        mPresenter?.getGankIoData(b, mType)
    }

    /**
     * 加载失败后点击后的操作
     */
    override fun onRefresh() {
        loadCustomData(true)
    }

    override fun showLoadSuccessView() {
        showContentView()
    }

    override fun showAdapterView(gankIoDataBean: GankIoDataBean) {
        setAdapter(gankIoDataBean)
    }

    private fun setAdapter(mCustomBean: GankIoDataBean) {
        if (mPresenter?.mPage == 1) {
            val isAll = SPUtils.getInstance().getString(GANK_CALA, "全部") == "全部"
            mData.clear()
            mAdapter.setAllType(isAll)
        }

        mData.addAll(mCustomBean.results)
        mAdapter.notifyDataSetChanged()
        ContentView.xrv_custom.refreshComplete()
        if (mIsFirst) {
            mIsFirst = false
        }
    }

    override fun showListNoMoreLoading() {
        ContentView.xrv_custom.noMoreLoading()
    }

    override fun showLoadFailedView() {
        ContentView.xrv_custom.refreshComplete()
        if (mAdapter.itemCount == 0) {
            showError()
        }
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
