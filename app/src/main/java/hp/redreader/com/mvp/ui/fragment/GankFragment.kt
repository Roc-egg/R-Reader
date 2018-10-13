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

import hp.redreader.com.di.component.DaggerGankComponent
import hp.redreader.com.di.module.GankModule
import hp.redreader.com.mvp.contract.GankContract
import hp.redreader.com.mvp.presenter.GankPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.MyBaseFragment
import hp.redreader.com.mvp.ui.adapter.MyFragmentPagerAdapter
import hp.redreader.com.mvp.ui.fragment.gankChild.AndroidFragment
import hp.redreader.com.mvp.ui.fragment.gankChild.CustomFragment
import hp.redreader.com.mvp.ui.fragment.gankChild.EverydayFragment
import hp.redreader.com.mvp.ui.fragment.gankChild.WelfareFragment
import kotlinx.android.synthetic.main.fragment_gank.view.*
import org.simple.eventbus.Subscriber
import java.util.ArrayList


/**
 * 类名：    GankFragment.kt
 * 类描述：  干货
 * 创建人：  hp
 * 创建时间：2018/8/27 9:02
 * 修改人：  hp
 * 修改时间：2018/8/27 9:02
 * 修改备注：
 */

class GankFragment : MyBaseFragment<GankPresenter>(), GankContract.View {

    private val mTitleList = ArrayList<String>(4)
    private val mFragments = ArrayList<Fragment>(4)

    override fun setupFragmentComponent(appComponent: AppComponent) {
        DaggerGankComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .gankModule(GankModule(this))
                .build()
                .inject(this)
    }

    override fun initView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_gank, container, false)
    }

    override fun initData(savedInstanceState: Bundle?) {
        showLoading()
        initFragmentList()
        /**
         * 注意使用的是：getChildFragmentManager，
         * 这样setOffscreenPageLimit()就可以添加上，保留相邻3个实例，切换时不会卡
         * 但会内存溢出，在显示时加载数据
         */
        val myAdapter = MyFragmentPagerAdapter(childFragmentManager, mFragments, mTitleList)
        ContentView.vp_gank.adapter = myAdapter
        // 左右预加载页面的个数
        ContentView.vp_gank.offscreenPageLimit = 3
        myAdapter.notifyDataSetChanged()
        ContentView.tab_gank.tabMode = TabLayout.MODE_FIXED
        ContentView.tab_gank.setupWithViewPager(ContentView.vp_gank)
        showContentView()
    }

    private fun initFragmentList() {
        mTitleList.clear()
        mTitleList.add("每日推荐")
        mTitleList.add("福利")
        mTitleList.add("干货订制")
        mTitleList.add("大安卓")
        mFragments.add(EverydayFragment())
        mFragments.add(WelfareFragment())
        mFragments.add(CustomFragment())
        mFragments.add(AndroidFragment.newInstance("Android"))
    }

    /**
     * 每日推荐跳转对应type下
     */
    @Subscriber(tag = "jump_type")
    private fun updata(int: Int) {
        when (int) {
            0 -> ContentView.vp_gank.currentItem = 3
            1 -> ContentView.vp_gank.currentItem = 1
            2 -> ContentView.vp_gank.currentItem = 2
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
