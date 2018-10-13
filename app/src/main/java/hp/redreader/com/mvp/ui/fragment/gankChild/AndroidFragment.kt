package hp.redreader.com.mvp.ui.fragment.gankChild

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

import hp.redreader.com.di.component.DaggerAndroidComponent
import hp.redreader.com.di.module.AndroidModule
import hp.redreader.com.mvp.contract.AndroidContract
import hp.redreader.com.mvp.presenter.AndroidPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseFragment
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import hp.redreader.com.mvp.ui.adapter.GankAndroidAdapter
import kotlinx.android.synthetic.main.fragment_android.view.*
import javax.inject.Inject


/**
 * 类名：    AndroidFragment.kt
 * 类描述：  大安卓 fragment
 * 创建人：  hp
 * 创建时间：2018/9/26/026 20:25
 * 修改人：  hp
 * 修改时间：2018/9/26/026 20:25
 * 修改备注：
 */
class AndroidFragment : MyBaseFragment<AndroidPresenter>(), AndroidContract.View, BaseQuickAdapter.OnItemClickListener {
    @Inject
    lateinit var mAdapter: GankAndroidAdapter

    private var mIsPrepared: Boolean = false
    private var mIsFirst = true

    companion object {
        private val TYPE = "mType"
        private var mType = "Android"

        fun newInstance(type: String): AndroidFragment {
            val fragment = AndroidFragment()
            val args = Bundle()
            args.putString(TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerAndroidComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .androidModule(AndroidModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_android, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        mType = arguments?.getString(TYPE)!!

        initRecyclerView()
        // 准备就绪
        mIsPrepared = true
    }

    override fun loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return
        }
        mIsFirst = false
        loadCustomData(true)
    }

    private fun initRecyclerView() {
        ContentView.xrv_android.layoutManager = LinearLayoutManager(activity)
        ContentView.xrv_android.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {
                loadCustomData(true)
            }

            override fun onLoadMore() {
                loadCustomData(false)
            }
        })
        ContentView.xrv_android.adapter = mAdapter.apply {
            onItemClickListener = this@AndroidFragment
        }
    }

    /**
     * item点击
     */
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        var bean = adapter!!.data[position - 1] as GankIoDataBean.ResultBean
        WebViewActivity.loadUrl(context, bean.url, bean.desc)
    }

    /**
     * 请求大安卓数据
     */
    private fun loadCustomData(b: Boolean) {
        mPresenter?.loadAndroidData(b, mType)
    }

    override fun onRefresh() {
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

    override fun showListNoMoreLoading() {
        ContentView.xrv_android.noMoreLoading()
    }

    override fun showLoadSuccessView() {
        showContentView()
    }

    override fun showLoading() {
        ContentView.xrv_android.refreshComplete()
    }

    override fun hideLoading() {
        ContentView.xrv_android.refreshComplete()
        if (mAdapter.itemCount == 0) {
            showError()
        }
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
