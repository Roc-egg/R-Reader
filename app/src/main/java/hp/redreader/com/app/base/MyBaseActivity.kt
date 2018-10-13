package hp.redreader.com.app.base

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout

import com.jess.arms.base.delegate.IActivity
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.integration.lifecycle.ActivityLifecycleable
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.ArmsUtils
import com.trello.rxlifecycle2.android.ActivityEvent

import javax.inject.Inject

import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.statusbar.StatusBarUtil
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

import com.jess.arms.utils.ThirdViewUtil.convertAutoView

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/18/018 22:50
 * 修改人：  hp
 * 修改时间：2018/9/18/018 22:50
 * 修改备注：
 */
abstract class MyBaseActivity<P : IPresenter> : AppCompatActivity(), IActivity, ActivityLifecycleable {
    protected val TAG = this.javaClass.simpleName
    private val mLifecycleSubject = BehaviorSubject.create<ActivityEvent>()
    private var mCache: Cache<String, Any>? = null

    // 布局view
    protected var ContentView: View
    // 加载中
    private var loadingView: View? = null
    // 加载失败
    private var refresh: View? = null
    // 布局
    protected var mView: View
    // 动画
    private var mAnimationDrawable: AnimationDrawable? = null


    @Inject
    protected var mPresenter: P? = null//如果当前页面逻辑简单, Presenter 可以为 null

    @Synchronized
    override fun provideCache(): Cache<String, Any> {
        if (mCache == null) {
            mCache = ArmsUtils.obtainAppComponentFromContext(this).cacheFactory().build(CacheType.ACTIVITY_CACHE)
        }
        return mCache
    }

    override fun provideLifecycleSubject(): Subject<ActivityEvent> {
        return mLifecycleSubject
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val view = convertAutoView(name, context, attrs)
        return view ?: super.onCreateView(name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val layoutResID = initView(savedInstanceState)
            if (layoutResID != 0) {
                setContentView(layoutResID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initData(savedInstanceState)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        mView = layoutInflater.inflate(R.layout.activity_base, null, false)
        ContentView = layoutInflater.inflate(layoutResID, null, false)

        // content
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        ContentView.layoutParams = params
        val mContainer = mView.findViewById<RelativeLayout>(R.id.container)
        mContainer.addView(ContentView)
        window.setContentView(mView)

        // 设置透明状态栏，兼容4.4
        StatusBarUtil.setColor(this, ArmsUtils.getColor(this, R.color.colorTheme), 0)
        loadingView = (findViewById<View>(R.id.vs_loading) as ViewStub).inflate()
        refresh = getView(R.id.ll_error_refresh)
        val img = loadingView!!.findViewById<ImageView>(R.id.img_progress)

        // 加载动画
        mAnimationDrawable = img.drawable as AnimationDrawable
        // 默认进入页面就开启动画
        if (!mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.start()
        }

        setToolBar()
        // 点击加载失败布局
        refresh!!.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View) {
                showLoadings()
                onRefresh()
            }
        })
        ContentView.visibility = View.GONE
    }

    protected fun <T : View> getView(id: Int): T {
        return findViewById<View>(id) as T
    }

    /**
     * 设置titlebar
     */
    protected fun setToolBar() {
        val toolBar = getView<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        toolBar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun setTitle(text: CharSequence) {
        (getView<View>(R.id.tool_bar) as Toolbar).title = text
    }

    protected fun showLoadings() {
        if (loadingView != null && loadingView!!.visibility != View.VISIBLE) {
            loadingView!!.visibility = View.VISIBLE
        }
        // 开始动画
        if (!mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.start()
        }
        if (ContentView.visibility != View.GONE) {
            ContentView.visibility = View.GONE
        }
        if (refresh!!.visibility != View.GONE) {
            refresh!!.visibility = View.GONE
        }
    }

    protected fun showContentView() {
        if (loadingView != null && loadingView!!.visibility != View.GONE) {
            loadingView!!.visibility = View.GONE
        }
        // 停止动画
        if (mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.stop()
        }
        if (refresh!!.visibility != View.GONE) {
            refresh!!.visibility = View.GONE
        }
        if (ContentView.visibility != View.VISIBLE) {
            ContentView.visibility = View.VISIBLE
        }
    }

    protected fun showError() {
        if (loadingView != null && loadingView!!.visibility != View.GONE) {
            loadingView!!.visibility = View.GONE
        }
        // 停止动画
        if (mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.stop()
        }
        if (refresh!!.visibility != View.VISIBLE) {
            refresh!!.visibility = View.VISIBLE
        }
        if (ContentView.visibility != View.GONE) {
            ContentView.visibility = View.GONE
        }
    }

    /**
     * 失败后点击刷新
     */
    protected open fun onRefresh() {

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        ?: return super.dispatchTouchEvent(ev)
                imm.hideSoftInputFromWindow(v!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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

    override fun onDestroy() {
        super.onDestroy()
        if (mPresenter != null)
            mPresenter!!.onDestroy()//释放资源
        this.mPresenter = null
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return
     */
    override fun useEventBus(): Boolean {
        return true
    }

    /**
     * 这个Activity是否会使用Fragment,框架会根据这个属性判断是否注册[android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks]
     * 如果返回false,那意味着这个Activity不需要绑定Fragment,那你再在这个Activity中绑定继承于 [com.jess.arms.base.BaseFragment] 的Fragment将不起任何作用
     *
     * @return
     */
    override fun useFragment(): Boolean {
        return true
    }
}
