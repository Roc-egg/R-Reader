package hp.redreader.com.mvp.ui.fragment.wanChild

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.xrecyclerview.XRecyclerView

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerJokeComponent
import hp.redreader.com.di.module.JokeModule
import hp.redreader.com.mvp.contract.JokeContract
import hp.redreader.com.mvp.presenter.JokePresenter

import hp.redreader.com.R
import hp.redreader.com.app.utils.dialog.showItems
import hp.redreader.com.mvp.model.entity.wanandroid.DuanZiBean
import hp.redreader.com.mvp.ui.adapter.JokeAdapter
import timber.log.Timber
import javax.inject.Inject


/**
 * 类名：    JokeFragment.kt
 * 类描述：  段子
 * 创建人：  hp
 * 创建时间：2018/9/25/025 0:42
 * 修改人：  hp
 * 修改时间：2018/9/25/025 0:42
 * 修改备注：
 */
class JokeFragment : MyBaseFragment<JokePresenter>(), JokeContract.View, BaseQuickAdapter.OnItemLongClickListener {

    @Inject
    lateinit var mAdapter: JokeAdapter

    private var mIsPrepared: Boolean = false
    private var mIsFirst = true

    companion object {
        private val TYPE = "param1"
        private var mType = "综合"
        fun newInstance(s: String): JokeFragment {
            val fragment = JokeFragment()
            val args = Bundle()
            args.putString(TYPE, s)
            fragment.arguments = args
            return fragment
        }
    }


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerJokeComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .jokeModule(JokeModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_banner, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
//        showContentView()
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
        ContentView.srl_book.setOnRefreshListener {
            ContentView.srl_book.postDelayed({
                ContentView.xrv_book.reset()
                loadCustomData(true)
            }, 100)
        }
        ContentView.xrv_book.layoutManager = LinearLayoutManager(activity)
        ContentView.xrv_book.setPullRefreshEnabled(false)
        ContentView.xrv_book.clearHeader()

        ContentView.xrv_book.adapter = mAdapter.apply {
            onItemLongClickListener = this@JokeFragment
        }

        ContentView.xrv_book.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                loadCustomData(false)
            }
        })
    }

    /**
     * item长按点击
     */
    override fun onItemLongClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int): Boolean {
        var bean = adapter!!.data[position - 1] as DuanZiBean
        showItems(view!!, bean.content)
        return false
    }

    override fun loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return
        }
        mIsFirst = false
        ContentView.srl_book.isRefreshing = true
        ContentView.srl_book.postDelayed({ loadCustomData(true) }, 500)
    }

    private fun loadCustomData(b: Boolean) {
        mPresenter?.getQsbkList(b)// 请求段子数据
    }

    override fun onRefresh() {
        ContentView.srl_book.isRefreshing = true
        loadCustomData(true)
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
