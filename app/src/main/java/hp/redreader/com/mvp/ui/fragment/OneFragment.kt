package hp.redreader.com.mvp.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerOneComponent
import hp.redreader.com.di.module.OneModule
import hp.redreader.com.mvp.contract.OneContract
import hp.redreader.com.mvp.presenter.OnePresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseFragment
import hp.redreader.com.app.utils.displayRandom
import hp.redreader.com.mvp.model.api.ConstantsImageUrl
import hp.redreader.com.mvp.ui.activity.movie.DoubanTopActivity
import hp.redreader.com.mvp.ui.adapter.OneAdapter
import kotlinx.android.synthetic.main.fragment_one.view.*
import kotlinx.android.synthetic.main.header_item_one.view.*
import me.jessyan.mvparms.demo.app.onClick
import javax.inject.Inject


/**
 * 类名：    OneFragment.kt
 * 类描述：  电影相关资讯的Fragment
 * 创建人：  hp
 * 创建时间：2018/8/27 9:02
 * 修改人：  hp
 * 修改时间：2018/8/27 9:02
 * 修改备注：
 */

class OneFragment : MyBaseFragment<OnePresenter>(), OneContract.View {

    @Inject
    lateinit var mAdapter: OneAdapter

    private var mIsPrepared: Boolean = false
    private var mIsFirst = true
    private lateinit var headerView: View

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerOneComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .oneModule(OneModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {

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
        ContentView.list_one.layoutManager = LinearLayoutManager(activity)
//        ContentView.list_one.setPullRefreshEnabled(false)
//        ContentView.list_one.setLoadingMoreEnabled(false)
//        ContentView.list_one.clearHeader()
        // 需加，不然滑动不流畅
        ContentView.list_one.isNestedScrollingEnabled = false
        ContentView.list_one.setHasFixedSize(false)

        ContentView.list_one.adapter = mAdapter.apply {
            //onItemClickListener = this@OneFragment
            isFirstOnly(false)
            openLoadAnimation(BaseQuickAdapter.SCALEIN)

        }

        headerView = layoutInflater.inflate(R.layout.header_item_one, ContentView.list_one.parent as ViewGroup, false)
        //两种为recyclerview添加header,postion索引有区别
//        ContentView.list_one.addHeaderView(headerView)
        mAdapter.addHeaderView(headerView)

        headerView.onClick {
            DoubanTopActivity.start(activity as Activity)
        }
        setHeaderViewDatas()
    }

    /**
     * 设置头部数据
     */
    private fun setHeaderViewDatas() {
        displayRandom(3, ConstantsImageUrl.ONE_URL_01, headerView.iv_img)
    }

    override fun loadData() {
        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
            return
        }
        mIsFirst = false
        loadCustomData()
    }

    private fun loadCustomData() {

        mPresenter?.getHotMovie()// 请求电影资讯数据
    }

    override fun onRefresh() {
        loadCustomData()
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
        showContentView()
    }

    override fun hideLoading() {
        showContentView()
        showError()
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
