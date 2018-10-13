package hp.redreader.com.app.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.ArcMotion;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jess.arms.base.delegate.IActivity;
import com.jess.arms.integration.cache.Cache;
import com.jess.arms.integration.cache.CacheType;
import com.jess.arms.integration.lifecycle.ActivityLifecycleable;
import com.jess.arms.mvp.IPresenter;
import com.jess.arms.utils.ArmsUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.reflect.Method;

import javax.inject.Inject;

import hp.redreader.com.R;
import hp.redreader.com.app.utils.ImgLoadUtilKt;
import hp.redreader.com.app.utils.PerfectClickListener;
import hp.redreader.com.app.utils.statusbar.StatusBarUtil;
import hp.redreader.com.mvp.ui.widegt.CustomChangeBounds;
import hp.redreader.com.mvp.ui.widegt.MyNestedScrollView;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;

import static com.jess.arms.utils.ThirdViewUtil.convertAutoView;

/**
 * 类名：    RedReader
 * 类描述：  电影、书籍等详情页
 * 创建人：  hp
 * 创建时间：2018/9/23/023 21:41
 * 修改人：  hp
 * 修改时间：2018/9/23/023 21:41
 * 修改备注：
 */
public abstract class BaseHeaderActivity<P extends IPresenter> extends AppCompatActivity implements IActivity, ActivityLifecycleable {
    protected final String TAG = this.getClass().getSimpleName();
    private final BehaviorSubject<ActivityEvent> mLifecycleSubject = BehaviorSubject.create();
    private Cache<String, Object> mCache;

    @Inject
    @Nullable
    protected P mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null

    // 内容布局view
    protected View ContentView;
    // 头部布局view
    protected View HeaderView;
    // 标题布局view
    protected View TitleView;
    // 加载中
    private View loadingView;
    // 加载失败
    private View refresh;
    // 布局
    protected View mView;
    // 动画
    private AnimationDrawable mAnimationDrawable;
    // 滑动多少距离后标题不透明
    private int slidingDistance;
    // 这个是高斯图背景的高度
    private int imageBgHeight;
    // 清除动画，防止内存泄漏
    private CustomChangeBounds changeBounds;
    private Toolbar tbBaseTitle;
    private ImageView ivBaseTitlebarBg;

    @NonNull
    @Override
    public synchronized Cache<String, Object> provideCache() {
        if (mCache == null) {
            mCache = ArmsUtils.obtainAppComponentFromContext(this).cacheFactory().build(CacheType.ACTIVITY_CACHE);
        }
        return mCache;
    }

    @NonNull
    @Override
    public final Subject<ActivityEvent> provideLifecycleSubject() {
        return mLifecycleSubject;
    }

    protected <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = convertAutoView(name, context, attrs);
        return view == null ? super.onCreateView(name, context, attrs) : view;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            int layoutResID = initView(savedInstanceState);
            if (layoutResID != 0) {
                setContentView(layoutResID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mView = getLayoutInflater().inflate(R.layout.activity_header_base, null, false);
        // 内容
        ContentView = getLayoutInflater().inflate(layoutResID, null, false);
        // 头部
        HeaderView = getLayoutInflater().inflate(setHeaderLayout(), null, false);
        // 标题
        TitleView = getLayoutInflater().inflate(R.layout.base_header_title_bar, null, false);

        // title (如自定义很强可以拿出去)
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TitleView.setLayoutParams(titleParams);
        RelativeLayout mTitleContainer = mView.findViewById(R.id.title_container);
        mTitleContainer.addView(TitleView);
        getWindow().setContentView(mView);

        // header
        RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        HeaderView.setLayoutParams(headerParams);
        RelativeLayout mHeaderContainer = mView.findViewById(R.id.header_container);
        mHeaderContainer.addView(HeaderView);
        getWindow().setContentView(mView);

        // content
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ContentView.setLayoutParams(params);
        RelativeLayout mContainer = mView.findViewById(R.id.container);
        mContainer.addView(ContentView);
        getWindow().setContentView(mView);

        loadingView = ((ViewStub) findViewById(R.id.vs_loading)).inflate();
        refresh = ((ViewStub) getView(R.id.vs_error_refresh)).inflate();
        refresh.setVisibility(View.GONE);

        ImageView img = loadingView.findViewById(R.id.img_progress);

        // 加载动画
        mAnimationDrawable = (AnimationDrawable) img.getDrawable();
        // 默认进入页面就开启动画
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }

        // 设置自定义元素共享切换动画
//        setMotion(setHeaderPicView(), false);

        // 初始化滑动渐变
//        initSlideShapeTheme(setHeaderImgUrl(), setHeaderImageView());

        // 设置toolbar
        setToolBar();

        // 点击加载失败布局
        refresh.setOnClickListener(new PerfectClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showLoadings();
                onRefresh();
            }
        });
        ContentView.setVisibility(View.GONE);
    }

    /**
     * a. 布局高斯透明图 header布局
     */
    protected abstract int setHeaderLayout();

    /**
     * b. 设置头部header高斯背景 imgUrl
     */
    protected abstract String setHeaderImgUrl();

    /**
     * c. 设置头部header布局 高斯背景ImageView控件
     */
    protected abstract ImageView setHeaderImageView();

    /**
     * 设置头部header布局 左侧的图片(需要设置曲线路径切换动画时重写)
     */
    protected ImageView setHeaderPicView() {
        return new ImageView(this);
    }

    /**
     * 1. 标题
     */
    @Override
    public void setTitle(CharSequence text) {
        tbBaseTitle.setTitle(text);
    }

    /**
     * 2. 副标题
     */
    protected void setSubTitle(CharSequence text) {
        tbBaseTitle.setSubtitle(text);
    }

    /**
     * 3. toolbar 单击"更多信息"
     */
    protected void setTitleClickMore() {
    }

    /**
     * 设置自定义 Shared Element切换动画
     * 默认不开启曲线路径切换动画，
     * 开启需要重写setHeaderPicView()，和调用此方法并将isShow值设为true
     *
     * @param imageView 共享的图片
     * @param isShow    是否显示曲线动画
     */
    protected void setMotion(ImageView imageView, boolean isShow) {
        if (!isShow) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 定义ArcMotion
            ArcMotion arcMotion = new ArcMotion();
            // 设置曲线幅度
            arcMotion.setMinimumHorizontalAngle(10f);
            arcMotion.setMinimumVerticalAngle(10f);
            //插值器，控制速度
            Interpolator interpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

            // 实例化自定义的ChangeBounds
            changeBounds = new CustomChangeBounds();
            changeBounds.setPathMotion(arcMotion);
            changeBounds.setInterpolator(interpolator);
            changeBounds.addTarget(imageView);
            // 将切换动画应用到当前的Activity的进入和返回
            getWindow().setSharedElementEnterTransition(changeBounds);
            getWindow().setSharedElementReturnTransition(changeBounds);
        }
    }

    /**
     * 设置toolbar
     */
    protected void setToolBar() {
        tbBaseTitle = getView(R.id.tb_base_title);
        ivBaseTitlebarBg = getView(R.id.iv_base_titlebar_bg);
        setSupportActionBar(tbBaseTitle);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
        }
        // 手动设置才有效果
//        bindingTitleView.tbBaseTitle.setTitleTextAppearance(this, R.style.ToolBar_Title);
//        bindingTitleView.tbBaseTitle.setSubtitleTextAppearance(this, R.style.Toolbar_SubTitle);
        tbBaseTitle.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.actionbar_more));
        tbBaseTitle.setNavigationOnClickListener(v -> onBackPressed());
        tbBaseTitle.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.actionbar_more:// 更多信息
                    setTitleClickMore();
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_header_menu, menu);
        return true;
    }

    /**
     * 显示popu内的图片
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Timber.e(e, getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu");
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    /**
     * *** 初始化滑动渐变 一定要实现 ******
     *
     * @param imgUrl    header头部的高斯背景imageUrl
     * @param mHeaderBg header头部高斯背景ImageView控件
     */
    protected void initSlideShapeTheme(String imgUrl, ImageView mHeaderBg) {
        setImgHeaderBg(imgUrl);

        // toolbar 的高
        int toolbarHeight = tbBaseTitle.getLayoutParams().height;
        final int headerBgHeight = toolbarHeight + StatusBarUtil.getStatusBarHeight(this);

        // 使背景图向上移动到图片的最低端，保留（titlebar+statusbar）的高度
        ViewGroup.LayoutParams params = ivBaseTitlebarBg.getLayoutParams();
        ViewGroup.MarginLayoutParams ivTitleHeadBgParams = (ViewGroup.MarginLayoutParams) ivBaseTitlebarBg.getLayoutParams();
        int marginTop = params.height - headerBgHeight;
        ivTitleHeadBgParams.setMargins(0, -marginTop, 0, 0);

//        ivBaseTitlebarBg.setImageAlpha(0);
        StatusBarUtil.setTranslucentImageHeader(this, 0, tbBaseTitle);

        // 上移背景图片，使空白状态栏消失(这样下方就空了状态栏的高度)
        if (mHeaderBg != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mHeaderBg.getLayoutParams();
            layoutParams.setMargins(0, -StatusBarUtil.getStatusBarHeight(this), 0, 0);

            ViewGroup.LayoutParams imgItemBgparams = mHeaderBg.getLayoutParams();
            // 获得高斯图背景的高度
            imageBgHeight = imgItemBgparams.height;
        }
        // 变色
        initScrollViewListener();
        initNewSlidingParams();
    }

    /**
     * 加载titlebar背景
     */
    private void setImgHeaderBg(String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            //高斯模糊背景
            ImgLoadUtilKt.showImgBg(ivBaseTitlebarBg, imgUrl, getGlideListener());

            /**
             * 坑备注：arms框架的ProgressManager监听addResponseListener仅“地址存在下载的动作时,此监听器将被调用”
             * 加了缓存之后是不会再次调用，修改为使用Glide的加载监听
             */
//            ProgressManager.getInstance().addResponseListener(mImageUrl, getGlideListener());

        }
    }

    private RequestListener<Drawable> getGlideListener() {
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                //加载完成
                Timber.e("--Glide-- finish");

//                tbBaseTitle.setBackgroundColor(Color.TRANSPARENT);
                ivBaseTitlebarBg.setImageAlpha(0);
//                ivBaseTitlebarBg.setVisibility(View.VISIBLE);

                return false;
            }
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
        };
    }

    private void initScrollViewListener() {
        // 为了兼容23以下
        ((MyNestedScrollView) findViewById(R.id.mns_base)).setOnScrollChangeListener(new MyNestedScrollView.ScrollInterface() {
            @Override
            public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollChangeHeader(scrollY);
            }
        });
    }

    private void initNewSlidingParams() {
        int titleBarAndStatusHeight = ArmsUtils.getDimens(getApplicationContext(), R.dimen.nav_bar_height) + StatusBarUtil.getStatusBarHeight(this);
        // 减掉后，没到顶部就不透明了
        slidingDistance = imageBgHeight - titleBarAndStatusHeight - ArmsUtils.getDimens(getApplicationContext(), R.dimen.base_header_activity_slide_more);
    }

    /**
     * 根据页面滑动距离改变Header方法
     */
    private void scrollChangeHeader(int scrolledY) {

        Timber.tag(TAG).e("scrolledY==%s", scrolledY);
        Timber.tag(TAG).e("slidingDistance==%s", slidingDistance);
        if (scrolledY < 0) {
            scrolledY = 0;
        }
        float alpha = Math.abs(scrolledY) * 1.0f / (slidingDistance);
        Timber.tag(TAG).e("alpha==%s", alpha);
        Drawable drawable = ivBaseTitlebarBg.getDrawable();

        if (drawable == null) {
            return;
        }
        if (scrolledY <= slidingDistance) {
            // title部分的渐变
            drawable.mutate().setAlpha((int) (alpha * 255));
            ivBaseTitlebarBg.setImageDrawable(drawable);
        } else {
            drawable.mutate().setAlpha(255);
            ivBaseTitlebarBg.setImageDrawable(drawable);
        }
    }

    protected void showLoadings() {
        if (loadingView != null && loadingView.getVisibility() != View.VISIBLE) {
            loadingView.setVisibility(View.VISIBLE);
        }
        // 开始动画
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        if (ContentView.getVisibility() != View.GONE) {
            ContentView.setVisibility(View.GONE);
        }
        if (refresh != null && refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
    }

    protected void showContentView() {
        if (loadingView != null && loadingView.getVisibility() != View.GONE) {
            loadingView.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (refresh != null && refresh.getVisibility() != View.GONE) {
            refresh.setVisibility(View.GONE);
        }
        if (ContentView.getVisibility() != View.VISIBLE) {
            ContentView.setVisibility(View.VISIBLE);
        }
    }

    protected void showError() {
        if (loadingView != null && loadingView.getVisibility() != View.GONE) {
            loadingView.setVisibility(View.GONE);
        }
        // 停止动画
        if (mAnimationDrawable.isRunning()) {
            mAnimationDrawable.stop();
        }
        if (refresh != null && refresh.getVisibility() != View.VISIBLE) {
            refresh.setVisibility(View.VISIBLE);
        }
        if (ContentView.getVisibility() != View.GONE) {
            ContentView.setVisibility(View.GONE);
        }
    }

    /**
     * 失败后点击刷新
     */
    protected void onRefresh() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();//释放资源
        this.mPresenter = null;

        if (changeBounds != null) {
            changeBounds.removeListener(null);
            changeBounds.removeTarget(setHeaderPicView());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setSharedElementEnterTransition(null);
                getWindow().setSharedElementReturnTransition(null);
            }
        }
    }

    /**
     * 是否使用eventBus,默认为使用(true)，
     *
     * @return
     */
    @Override
    public boolean useEventBus() {
        return true;
    }

    /**
     * 这个Activity是否会使用Fragment,框架会根据这个属性判断是否注册{@link android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks}
     * 如果返回false,那意味着这个Activity不需要绑定Fragment,那你再在这个Activity中绑定继承于 {@link com.jess.arms.base.BaseFragment} 的Fragment将不起任何作用
     *
     * @return
     */
    @Override
    public boolean useFragment() {
        return true;
    }
}
