package hp.redreader.com.mvp.ui.activity

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerMainComponent
import hp.redreader.com.di.module.MainModule
import hp.redreader.com.mvp.contract.MainContract
import hp.redreader.com.mvp.presenter.MainPresenter

import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.dialog.showItems
import hp.redreader.com.app.utils.displayCircle
import hp.redreader.com.app.utils.listener.OnLoginListener
import hp.redreader.com.app.utils.statusbar.StatusBarUtil
import hp.redreader.com.mvp.model.api.ConstantsImageUrl
import hp.redreader.com.mvp.ui.activity.menu.LoginActivity
import hp.redreader.com.mvp.ui.activity.menu.NavHomePageActivity
import hp.redreader.com.mvp.ui.activity.wan.ArticleListActivity
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import hp.redreader.com.mvp.ui.adapter.MyFragmentPagerAdapter
import hp.redreader.com.mvp.ui.fragment.GankFragment
import hp.redreader.com.mvp.ui.fragment.OneFragment
import hp.redreader.com.mvp.ui.fragment.WanFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import me.jessyan.mvparms.demo.app.isLogin
import org.jetbrains.anko.startActivity
import org.simple.eventbus.Subscriber
import java.util.ArrayList
import javax.inject.Inject

/**
 * 类名：    MainActivity.kt
 * 类描述：  主MainActivity
 * 创建人：  hp
 * 创建时间：2018/8/27 9:01
 * 修改人：  hp
 * 修改时间：2018/8/27 9:01
 * 修改备注：
 */


class MainActivity : BaseActivity<MainPresenter>(), MainContract.View, ViewPager.OnPageChangeListener, View.OnClickListener {
    @Inject
    lateinit var mApplication: Application

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(MainModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {


        StatusBarUtil.setColorNoTranslucentForDrawerLayout(this@MainActivity, drawer_layout,
                ArmsUtils.getColor(this, R.color.colorTheme))

        initContentFragment()
        initDrawerLayout()
        initListener()
    }

    private fun initListener() {
        ll_title_menu.setOnClickListener(this)
        iv_title_one.setOnClickListener(this)
        iv_title_two.setOnClickListener(this)
        iv_title_three.setOnClickListener(this)
        fab.setOnClickListener(this)
    }

    /**
     * 初始化DrawerLayout
     */
    private fun initDrawerLayout() {
        val inflateHeaderView = nav_view.inflateHeaderView(R.layout.nav_header_main)
        displayCircle(inflateHeaderView.iv_avatar, ConstantsImageUrl.IC_AVATAR)
        inflateHeaderView.ll_nav_exit.setOnClickListener(this@MainActivity)
        inflateHeaderView.iv_avatar.setOnClickListener(this@MainActivity)

        inflateHeaderView.ll_nav_homepage.setOnClickListener(listener)
        inflateHeaderView.ll_nav_scan_download.setOnClickListener(listener)
        inflateHeaderView.ll_nav_deedback.setOnClickListener(listener)
        inflateHeaderView.ll_nav_about.setOnClickListener(listener)
        inflateHeaderView.ll_nav_login.setOnClickListener(listener)
        inflateHeaderView.ll_nav_collect.setOnClickListener(listener)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ll_title_menu ->
                // 开启菜单
                drawer_layout.openDrawer(GravityCompat.START)
            R.id.iv_title_two ->
                // 不然cpu会有损耗
                if (include.vp_content.currentItem != 1) {
                    setCurrentItem(1)
                }
            R.id.iv_title_one -> if (include.vp_content.currentItem != 0) {
                setCurrentItem(0)
            }
            R.id.iv_title_three -> if (include.vp_content.currentItem != 2) {
                setCurrentItem(2)
            }
            R.id.iv_avatar ->
                // 头像进入GitHub
                WebViewActivity.loadUrl(this, getString(R.string.string_url_cloudreader), "CloudReader")
            R.id.ll_nav_exit ->
                // 退出应用
                finish()
            else -> {
            }
        }
    }

    private val listener = object : PerfectClickListener() {
        override fun onNoDoubleClick(v: View?) {
            drawer_layout.closeDrawer(GravityCompat.START)
            drawer_layout.postDelayed({
                when (v?.id) {
                    R.id.ll_nav_homepage// 主页
                    -> startActivity<NavHomePageActivity>()
                    R.id.ll_nav_scan_download//扫码下载
                    ->
//                        startActivity<NavDownloadActivity>()
                        showMessage("扫码下载")
                    R.id.ll_nav_deedback// 问题反馈
                    ->
//                        startActivity<NavDeedBackActivity>()
                        showMessage("问题反馈")
                    R.id.ll_nav_about// 关于R-阅
                    ->
//                        startActivity<NavAboutActivity>()
                        showMessage("关于R-阅")
                    R.id.ll_nav_collect// 玩安卓收藏
                    -> if (isLogin()) {
                        startActivity<ArticleListActivity>()
                    }
                    R.id.ll_nav_login// 玩安卓登录
                    -> showItems(v,mApplication, object : OnLoginListener {
                        override fun loginWanAndroid() {
                            startActivity<LoginActivity>()
                        }

                        override fun loginGitHub() {
                            WebViewActivity.loadUrl(v.context, "https://github.com/login", "登录GitHub账号")
                        }
                    })
                    else -> {
                    }
                }
            }, 260)
        }
    }

    /**
     * 初始化fragment
     */
    private fun initContentFragment() {
        val mFragmentList = ArrayList<Fragment>()
        mFragmentList.add(WanFragment())
        mFragmentList.add(GankFragment())
        mFragmentList.add(OneFragment())
        // 注意使用的是：getSupportFragmentManager
        val adapter = MyFragmentPagerAdapter(supportFragmentManager, mFragmentList)
        include.vp_content.adapter = adapter
        // 设置ViewPager最大缓存的页面个数(cpu消耗少)
        include.vp_content.offscreenPageLimit = 2
        include.vp_content.addOnPageChangeListener(this)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayShowTitleEnabled(false)
        setCurrentItem(0)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        when (position) {
            0 -> setCurrentItem(0)
            1 -> setCurrentItem(1)
            2 -> setCurrentItem(2)
            else -> {
            }
        }
    }

    private fun setCurrentItem(position: Int) {
        var isOne = false
        var isTwo = false
        var isThree = false
        when (position) {
            0 -> isOne = true
            1 -> isTwo = true
            2 -> isThree = true
            else -> isTwo = true
        }
        include.vp_content.currentItem = position
        include.iv_title_one.isSelected = isOne
        include.iv_title_two.isSelected = isTwo
        include.iv_title_three.isSelected = isThree
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search ->
                //                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                return true
            else -> return super.onOptionsItemSelected(item)
        }
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
        finish()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            } else {
                // 不退出程序，进入后台
                moveTaskToBack(true)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

    /**
     * 首页跳转到电影栏
     */
    @Subscriber(tag = "jump_type_to_one")
    private fun updata(s: String) {
        setCurrentItem(2)
    }
}
