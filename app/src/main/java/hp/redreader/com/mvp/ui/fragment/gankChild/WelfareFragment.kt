package hp.redreader.com.mvp.ui.fragment.gankChild

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.xrecyclerview.XRecyclerView

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerWelfareComponent
import hp.redreader.com.di.module.WelfareModule
import hp.redreader.com.mvp.contract.WelfareContract
import hp.redreader.com.mvp.presenter.WelfarePresenter

import hp.redreader.com.R
import hp.redreader.com.mvp.ui.activity.movie.ViewBigImageActivity
import hp.redreader.com.mvp.ui.adapter.WelfareAdapter
import timber.log.Timber
import javax.inject.Inject


/**
 * 类名：    WelfareFragment.kt
 * 类描述：  福利
 * 创建人：  hp
 * 创建时间：2018/9/26/026 20:25
 * 修改人：  hp
 * 修改时间：2018/9/26/026 20:25
 * 修改备注：
 */
class WelfareFragment : MyBaseFragment<WelfarePresenter>(), WelfareContract.View, BaseQuickAdapter.OnItemClickListener {

    @Inject
    lateinit var mAdapter: WelfareAdapter

    private var mIsPrepared: Boolean = false
    private var mIsFirst = true
    private val imgList = ArrayList<String>()
    private val imgTitleList = ArrayList<String>()

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerWelfareComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .welfareModule(WelfareModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_welfare, container, false);
    }

    override fun initData(savedInstanceState: Bundle?) {
        initRecycleView()
        mIsPrepared = true
    }

    private fun initRecycleView() {
        ContentView.xrv_welfare.setPullRefreshEnabled(false)
        ContentView.xrv_welfare.clearHeader()
        //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
        ContentView.xrv_welfare.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        ContentView.xrv_welfare.adapter = mAdapter.apply {
            onItemClickListener = this@WelfareFragment
        }
        ContentView.xrv_welfare.setLoadingListener(object : XRecyclerView.LoadingListener {
            override fun onRefresh() {

            }

            override fun onLoadMore() {
                loadWelfareData(false)
            }
        })
    }

    /**
     * item点击
     */
    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        ViewBigImageActivity.startImageList(context, position - 1, imgList, imgTitleList)
    }

    override fun loadData() {
        if (!mIsVisible || !mIsPrepared || !mIsFirst) {
            return
        }
        mIsFirst = false
        loadWelfareData(true)
    }

    private fun loadWelfareData(b: Boolean) {
        mPresenter?.loadWelfareData(b)
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

    override fun setImageList(arrayLists: ArrayList<ArrayList<String>>?) {
        if (arrayLists != null && arrayLists.size == 2) {
            imgList.addAll(arrayLists[0])
            imgTitleList.addAll(arrayLists[1])
        }
    }

    override fun onRefresh() {
        loadWelfareData(true)
    }

    override fun showLoading() {
        Timber.tag(TAG).w("showLoading")
        ContentView.xrv_welfare.refreshComplete()
    }

    override fun hideLoading() {
        Timber.tag(TAG).w("hideLoading")
        showContentView()
        if (mPresenter?.mPage == 1) {
            showError()
        } else {
            ContentView.xrv_welfare.refreshComplete()
        }
    }

    override fun showLoadSuccessView() {
        showContentView()
    }

    override fun showListNoMoreLoading() {
        Timber.e("showListNoMoreLoading...")
        ContentView.xrv_welfare.noMoreLoading()
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
