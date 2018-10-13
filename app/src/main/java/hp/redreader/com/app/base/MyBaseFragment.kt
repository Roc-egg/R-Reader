package hp.redreader.com.app.base

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout

import com.jess.arms.base.delegate.IFragment
import com.jess.arms.integration.cache.Cache
import com.jess.arms.integration.cache.CacheType
import com.jess.arms.integration.lifecycle.FragmentLifecycleable
import com.jess.arms.mvp.IPresenter
import com.jess.arms.utils.ArmsUtils
import com.trello.rxlifecycle2.android.FragmentEvent

import javax.inject.Inject

import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import timber.log.Timber

/**
 * 类名：    MyBaseFragment
 * 类描述：  没有title懒加载的Fragment
 * 创建人：  hp
 * 创建时间：2018/9/9/009 18:43
 * 修改人：  hp
 * 修改时间：2018/9/9/009 18:43
 * 修改备注：
 */
abstract class MyBaseFragment<P : IPresenter> : Fragment(), IFragment, FragmentLifecycleable {
    protected val TAG = this.javaClass.simpleName
    private val mLifecycleSubject = BehaviorSubject.create<FragmentEvent>()
    private var mCache: Cache<String, Any>? = null

    // 布局view
    protected var ContentView: View
    // fragment是否显示了
    protected var mIsVisible = false
    // 加载中
    private var loadingView: View? = null
    // 加载失败
    private var mRefresh: LinearLayout? = null
    // 内容布局
    protected var mContainer: RelativeLayout
    // 动画
    private var mAnimationDrawable: AnimationDrawable? = null
    @Inject
    protected var mPresenter: P? = null//如果当前页面逻辑简单, Presenter 可以为 null

    @Synchronized
    override fun provideCache(): Cache<String, Any> {
        if (mCache == null) {
            mCache = ArmsUtils.obtainAppComponentFromContext(activity!!).cacheFactory().build(CacheType.FRAGMENT_CACHE)
        }
        return mCache
    }


    override fun provideLifecycleSubject(): Subject<FragmentEvent> {
        return mLifecycleSubject
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ContentView = initView(inflater, container, savedInstanceState)
        val ll = inflater.inflate(R.layout.fragment_base, null)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        ContentView.layoutParams = params
        mContainer = ll.findViewById(R.id.container)
        mContainer.addView(ContentView)
        return ll
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (userVisibleHint) {
            mIsVisible = true
            onVisible()
        } else {
            mIsVisible = false
            onInvisible()
        }
    }

    protected fun onInvisible() {}

    /**
     * 显示时加载数据,需要这样的使用
     * 注意声明 isPrepared，先初始化
     * 生命周期会先执行 setUserVisibleHint 再执行onActivityCreated
     * 在 onActivityCreated 之后第一次显示加载数据，只加载一次
     */
    protected open fun loadData() {}

    protected fun onVisible() {
        loadData()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadingView = (getView<View>(R.id.vs_loading) as ViewStub).inflate()
        val img = loadingView!!.findViewById<ImageView>(R.id.img_progress)

        // 加载动画
        mAnimationDrawable = img.drawable as AnimationDrawable
        // 默认进入页面就开启动画
        if (!mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.start()
        }
        mRefresh = getView(R.id.ll_error_refresh)
        // 点击加载失败布局
        mRefresh!!.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View) {
                showLoadings()
                onRefresh()
            }
        })
        ContentView.visibility = View.GONE

    }

    protected fun <T : View> getView(id: Int): T {
        return view!!.findViewById<View>(id) as T
    }

    /**
     * 加载失败后点击后的操作
     */
    protected open fun onRefresh() {

    }

    /**
     * 显示加载中状态
     */
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
        if (mRefresh!!.visibility != View.GONE) {
            mRefresh!!.visibility = View.GONE
        }
    }

    /**
     * 加载完成的状态
     */
    protected fun showContentView() {
        if (loadingView != null && loadingView!!.visibility != View.GONE) {
            loadingView!!.visibility = View.GONE
        }
        // 停止动画
        if (mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.stop()
        }
        if (mRefresh!!.visibility != View.GONE) {
            mRefresh!!.visibility = View.GONE
        }
        if (ContentView.visibility != View.VISIBLE) {
            ContentView.visibility = View.VISIBLE
        }
    }

    /**
     * 加载失败点击重新加载的状态
     */
    protected fun showError() {
        if (loadingView != null && loadingView!!.visibility != View.GONE) {
            loadingView!!.visibility = View.GONE
        }
        // 停止动画
        if (mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.stop()
        }
        if (mRefresh!!.visibility != View.VISIBLE) {
            mRefresh!!.visibility = View.VISIBLE
        }
        if (ContentView.visibility != View.GONE) {
            ContentView.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPresenter != null) mPresenter!!.onDestroy()//释放资源
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

}
