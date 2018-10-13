package hp.redreader.com.mvp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerWanComponent
import hp.redreader.com.di.module.WanModule
import hp.redreader.com.mvp.contract.WanContract
import hp.redreader.com.mvp.presenter.WanPresenter

import hp.redreader.com.R
import hp.redreader.com.mvp.ui.adapter.MyFragmentPagerAdapter
import hp.redreader.com.mvp.ui.fragment.wanChild.BannerFragment
import hp.redreader.com.mvp.ui.fragment.wanChild.BookListFragment
import hp.redreader.com.mvp.ui.fragment.wanChild.JokeFragment
import java.util.ArrayList


/**
 * 类名：    WanFragment.kt
 * 类描述：  展示玩安卓的页面
 * 创建人：  hp
 * 创建时间：2018/8/27 9:02
 * 修改人：  hp
 * 修改时间：2018/8/27 9:02
 * 修改备注：
 */

class WanFragment : MyBaseFragment<WanPresenter>(), WanContract.View {

    private val mTitleList = ArrayList<String>(3)
    private val mFragments = ArrayList<Fragment>(3)


    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerWanComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .wanModule(WanModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_wan, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        showLoading()
        initFragmentList()
        /**
         * 注意使用的是：getChildFragmentManager，
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻2个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */
        val myAdapter = MyFragmentPagerAdapter(childFragmentManager, mFragments, mTitleList)
        ContentView.vp_book.adapter = myAdapter
        // 左右预加载页面的个数
        ContentView.vp_book.offscreenPageLimit = 2
        myAdapter.notifyDataSetChanged()
        ContentView.tab_book.tabMode = TabLayout.MODE_FIXED
        ContentView.tab_book.setupWithViewPager(ContentView.vp_book)
        showContentView()
    }

    private fun initFragmentList() {
        mTitleList.clear()
        mTitleList.add("玩安卓")
        mTitleList.add("书籍")
        mTitleList.add("段子")
        mFragments.add(BannerFragment.newInstance("玩安卓"))
        mFragments.add(BookListFragment.newInstance("心理学"))
        mFragments.add(JokeFragment.newInstance("段子"))
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
