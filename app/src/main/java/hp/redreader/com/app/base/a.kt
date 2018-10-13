package hp.redreader.com.app.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.transition.ArcMotion
import android.util.AttributeSet
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.RelativeLayout

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jess.arms.base.delegate.IActivity
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.integration.lifecycle.ActivityLifecycleable
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.ArmsUtils
import com.trello.rxlifecycle2.android.ActivityEvent

import java.lang.reflect.Method

import javax.inject.Inject

import hp.redreader.com.R
import hp.redreader.com.app.utils.*
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.statusbar.StatusBarUtil
import hp.redreader.com.mvp.ui.widegt.CustomChangeBounds
import hp.redreader.com.mvp.ui.widegt.MyNestedScrollView
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

import com.jess.arms.utils.ThirdViewUtil.convertAutoView

/**
 * 两个没用的文件为了github上面显示一下kotlin ☺
 */
abstract class a<P : IPresenter> : AppCompatActivity(), IActivity, ActivityLifecycleable {
    protected val TAG = this.javaClass.simpleName
    private val mLifecycleSubject = BehaviorSubject.create<ActivityEvent>()
    private var mCache: Cache<String, Any>? = null


    // 加载中
    private var loadingView: View? = null
    // 加载失败
    private var refresh: View? = null
    // 动画
    private var mAnimationDrawable: AnimationDrawable? = null
    // 滑动多少距离后标题不透明
    private var slidingDistance: Int = 0
    // 这个是高斯图背景的高度
    private var imageBgHeight: Int = 0
    // 清除动画，防止内存泄漏
    private var changeBounds: CustomChangeBounds? = null
    private var tbBaseTitle: Toolbar? = null
    private var ivBaseTitlebarBg: ImageView? = null

    private//加载完成
    //                tbBaseTitle.setBackgroundColor(Color.TRANSPARENT);
    //                ivBaseTitlebarBg.setVisibility(View.VISIBLE);
    //            @Override
    //            public void onProgress(ProgressInfo progressInfo) {
    //                int progress = progressInfo.getPercent();
    //                Timber.e("--Glide-- %s", +progress + " %  " + progressInfo.getSpeed() + " byte/s  " + progressInfo.toString());
    //                if (progressInfo.isFinish()) {
    //                    //说明已经加载完成
    //                    Timber.e("--Glide-- finish");
    //
    //                    tbBaseTitle.setBackgroundColor(Color.TRANSPARENT);
    //                    ivBaseTitlebarBg.setImageAlpha(0);
    //                    ivBaseTitlebarBg.setVisibility(View.GONE);
    //                }
    //            }
    //
    //            @Override
    //            public void onError(long id, Exception e) {
    //                Timber.e("--Glide-- onError");
    //            }
    val glideListener: RequestListener<Drawable>
        get() = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                Timber.e("--Glide-- finish")
                ivBaseTitlebarBg!!.imageAlpha = 0

                return false
            }
        }


    override fun provideLifecycleSubject(): Subject<ActivityEvent> {
        return mLifecycleSubject
    }

    protected fun <T : View> getView(id: Int): T {
        return findViewById<View>(id) as T
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

    }

    /**
     * a. 布局高斯透明图 header布局
     */
    protected abstract fun setHeaderLayout(): Int

    /**
     * b. 设置头部header高斯背景 imgUrl
     */
    protected abstract fun setHeaderImgUrl(): String

    /**
     * c. 设置头部header布局 高斯背景ImageView控件
     */
    protected abstract fun setHeaderImageView(): ImageView

    /**
     * 设置头部header布局 左侧的图片(需要设置曲线路径切换动画时重写)
     */
    protected fun setHeaderPicView(): ImageView {
        return ImageView(this)
    }

    /**
     * 1. 标题
     */
    override fun setTitle(text: CharSequence) {
        tbBaseTitle!!.title = text
    }

    /**
     * 2. 副标题
     */
    protected fun setSubTitle(text: CharSequence) {
        tbBaseTitle!!.subtitle = text
    }

    /**
     * 3. toolbar 单击"更多信息"
     */
    protected fun setTitleClickMore() {}

    /**
     * 设置自定义 Shared Element切换动画
     * 默认不开启曲线路径切换动画，
     * 开启需要重写setHeaderPicView()，和调用此方法并将isShow值设为true
     *
     * @param imageView 共享的图片
     * @param isShow    是否显示曲线动画
     */
    protected fun setMotion(imageView: ImageView, isShow: Boolean) {
        if (!isShow) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 定义ArcMotion
            val arcMotion = ArcMotion()
            // 设置曲线幅度
            arcMotion.minimumHorizontalAngle = 10f
            arcMotion.minimumVerticalAngle = 10f
            //插值器，控制速度
            val interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in)

            // 实例化自定义的ChangeBounds
            changeBounds = CustomChangeBounds()
            changeBounds!!.pathMotion = arcMotion
            changeBounds!!.interpolator = interpolator
            changeBounds!!.addTarget(imageView)
            // 将切换动画应用到当前的Activity的进入和返回
            window.sharedElementEnterTransition = changeBounds
            window.sharedElementReturnTransition = changeBounds
        }
    }

    /**
     * 设置toolbar
     */
    protected fun setToolBar() {
        tbBaseTitle = getView(R.id.tb_base_title)
        ivBaseTitlebarBg = getView(R.id.iv_base_titlebar_bg)
        setSupportActionBar(tbBaseTitle)
        val actionBar = supportActionBar
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back)
        }
        // 手动设置才有效果
        //        bindingTitleView.tbBaseTitle.setTitleTextAppearance(this, R.style.ToolBar_Title);
        //        bindingTitleView.tbBaseTitle.setSubtitleTextAppearance(this, R.style.Toolbar_SubTitle);
        tbBaseTitle!!.overflowIcon = ContextCompat.getDrawable(this, R.drawable.actionbar_more)
        tbBaseTitle!!.setNavigationOnClickListener { v -> onBackPressed() }
        tbBaseTitle!!.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.actionbar_more// 更多信息
                -> setTitleClickMore()
                else -> {
                }
            }
            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.base_header_menu, menu)
        return true
    }

    /**
     * 显示popu内的图片
     */
    @SuppressLint("RestrictedApi")
    override fun onPrepareOptionsPanel(view: View, menu: Menu?): Boolean {
        if (menu != null) {
            if (menu.javaClass.simpleName == "MenuBuilder") {
                try {
                    val m = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
                    m.isAccessible = true
                    m.invoke(menu, true)
                } catch (e: Exception) {
                    Timber.e(e, javaClass.simpleName, "onMenuOpened...unable to set icons for overflow menu")
                }

            }
        }
        return super.onPrepareOptionsPanel(view, menu)
    }

    /**
     * *** 初始化滑动渐变 一定要实现 ******
     *
     * @param imgUrl    header头部的高斯背景imageUrl
     * @param mHeaderBg header头部高斯背景ImageView控件
     */
    protected fun initSlideShapeTheme(imgUrl: String, mHeaderBg: ImageView?) {
        setImgHeaderBg(imgUrl)

        // toolbar 的高
        val toolbarHeight = tbBaseTitle!!.layoutParams.height
        val headerBgHeight = toolbarHeight + StatusBarUtil.getStatusBarHeight(this)

        // 使背景图向上移动到图片的最低端，保留（titlebar+statusbar）的高度
        val params = ivBaseTitlebarBg!!.layoutParams
        val ivTitleHeadBgParams = ivBaseTitlebarBg!!.layoutParams as ViewGroup.MarginLayoutParams
        val marginTop = params.height - headerBgHeight
        ivTitleHeadBgParams.setMargins(0, -marginTop, 0, 0)

        //        ivBaseTitlebarBg.setImageAlpha(0);
        StatusBarUtil.setTranslucentImageHeader(this, 0, tbBaseTitle)

        // 上移背景图片，使空白状态栏消失(这样下方就空了状态栏的高度)
        if (mHeaderBg != null) {
            val layoutParams = mHeaderBg.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(0, -StatusBarUtil.getStatusBarHeight(this), 0, 0)

            val imgItemBgparams = mHeaderBg.layoutParams
            // 获得高斯图背景的高度
            imageBgHeight = imgItemBgparams.height
        }
        // 变色
        initScrollViewListener()
        initNewSlidingParams()
    }

    /**
     * 加载titlebar背景
     */
    private fun setImgHeaderBg(imgUrl: String) {
        if (!TextUtils.isEmpty(imgUrl)) {
            //高斯模糊背景
            showImgBg(ivBaseTitlebarBg!!, imgUrl, glideListener)

            /**
             * 坑备注：arms框架的ProgressManager监听addResponseListener仅“地址存在下载的动作时,此监听器将被调用”
             * 加了缓存之后是不会再次调用，修改为使用Glide的加载监听
             */
            //            ProgressManager.getInstance().addResponseListener(mImageUrl, getGlideListener());

        }
    }

    private fun initScrollViewListener() {
        // 为了兼容23以下
        (findViewById<View>(R.id.mns_base) as MyNestedScrollView).setOnScrollChangeListener { scrollX, scrollY, oldScrollX, oldScrollY -> scrollChangeHeader(scrollY) }
    }

    private fun initNewSlidingParams() {
        val titleBarAndStatusHeight = ArmsUtils.getDimens(applicationContext, R.dimen.nav_bar_height) + StatusBarUtil.getStatusBarHeight(this)
        // 减掉后，没到顶部就不透明了
        slidingDistance = imageBgHeight - titleBarAndStatusHeight - ArmsUtils.getDimens(applicationContext, R.dimen.base_header_activity_slide_more)
    }

    /**
     * 根据页面滑动距离改变Header方法
     */
    private fun scrollChangeHeader(scrolledY: Int) {
        var scrolledY = scrolledY

        Timber.tag(TAG).e("scrolledY==%s", scrolledY)
        Timber.tag(TAG).e("slidingDistance==%s", slidingDistance)
        if (scrolledY < 0) {
            scrolledY = 0
        }
        val alpha = Math.abs(scrolledY) * 1.0f / slidingDistance
        Timber.tag(TAG).e("alpha==%s", alpha)
        val drawable = ivBaseTitlebarBg!!.drawable ?: return

        if (scrolledY <= slidingDistance) {
            // title部分的渐变
            drawable.mutate().alpha = (alpha * 255).toInt()
            ivBaseTitlebarBg!!.setImageDrawable(drawable)
        } else {
            drawable.mutate().alpha = 255
            ivBaseTitlebarBg!!.setImageDrawable(drawable)
        }
    }

    protected fun showLoadings() {
        if (loadingView != null && loadingView!!.visibility != View.VISIBLE) {
            loadingView!!.visibility = View.VISIBLE
        }
        // 开始动画
        if (!mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.start()
        }
        if (refresh != null && refresh!!.visibility != View.GONE) {
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
        if (refresh != null && refresh!!.visibility != View.GONE) {
            refresh!!.visibility = View.GONE
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
        if (refresh != null && refresh!!.visibility != View.VISIBLE) {
            refresh!!.visibility = View.VISIBLE
        }
    }

    /**
     * 失败后点击刷新
     */
    protected fun onRefresh() {

    }

    override fun onDestroy() {
        super.onDestroy()

        if (changeBounds != null) {
            changeBounds!!.removeListener(null)
            changeBounds!!.removeTarget(setHeaderPicView())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.sharedElementEnterTransition = null
                window.sharedElementReturnTransition = null
            }
        }
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
