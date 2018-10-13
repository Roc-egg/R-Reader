package hp.redreader.com.app.base

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.xrecyclerview.LoadingMoreFooter
import com.example.xrecyclerview.WrapAdapter
import com.example.xrecyclerview.YunRefreshHeader


/**
 * 两个没用的文件为了github上面显示一下kotlin ☺
 */
class c  constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {
    private var mLoadingListener: LoadingListener? = null
    private var mWrapAdapter: WrapAdapter? = null
    private val mHeaderViews = SparseArray<View>()
    private val mFootViews = SparseArray<View>()
    private var pullRefreshEnabled = true
    private var loadingMoreEnabled = true
    private var mRefreshHeader: YunRefreshHeader? = null
    private var isLoadingData: Boolean = false
    //    public int previousTotal;
    var isnomore: Boolean = false
    private var mLastY = -1f
    // 是否是额外添加FooterView
    private var isOther = false

    val isOnTop: Boolean
        get() {
            if (mHeaderViews == null || mHeaderViews.size() == 0) {
                return false
            }

            val view = mHeaderViews.get(0)
            return if (view.parent != null) {
                true
            } else {
                false
            }
        }

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            mWrapAdapter!!.notifyDataSetChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter!!.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter!!.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mWrapAdapter!!.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter!!.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mWrapAdapter!!.notifyItemMoved(fromPosition, toPosition)
        }
    }

    init {
        init(context)
    }

    private fun init(context: Context) {
        if (pullRefreshEnabled) {
            val refreshHeader = YunRefreshHeader(context)
            mHeaderViews.put(0, refreshHeader)
            mRefreshHeader = refreshHeader
        }
        val footView = LoadingMoreFooter(context)
        addFootView(footView, false)
        mFootViews.get(0).visibility = View.GONE
    }

    /**
     * 改为公有。供外添加view使用,使用标识
     * 注意：使用后不能使用 上拉加载，否则添加无效
     * 使用时 isOther 传入 true，然后调用 noMoreLoading即可。
     */
    fun addFootView(view: View, isOther: Boolean) {
        mFootViews.clear()
        mFootViews.put(0, view)
        this.isOther = isOther
    }

    /**
     * 相当于加一个空白头布局：
     * 只有一个目的：为了滚动条显示在最顶端
     * 因为默认加了刷新头布局，不处理滚动条会下移。
     * 和 setPullRefreshEnabled(false) 一块儿使用
     * 使用下拉头时，此方法不应被使用！
     */
    fun clearHeader() {
        mHeaderViews.clear()
        val scale = context.resources.displayMetrics.density
        val height = (1.0f * scale + 0.5f).toInt()
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        val view = View(context)
        view.layoutParams = params
        mHeaderViews.put(0, view)
    }

    fun addHeaderView(view: View) {
        if (pullRefreshEnabled && mHeaderViews.get(0) !is YunRefreshHeader) {
            val refreshHeader = YunRefreshHeader(context)
            mHeaderViews.put(0, refreshHeader)
            mRefreshHeader = refreshHeader
        }
        mHeaderViews.put(mHeaderViews.size(), view)
    }

    private fun loadMoreComplete() {
        isLoadingData = false
        val footView = mFootViews.get(0)
        /**
         * 备注此处注释掉ItemCount()，原判断有误易导致上啦多次后再下拉刷新多次导致显示“没有更多内容了”
         * 2018年9月17日22:53:37
         */
        //        if (previousTotal <= getLayoutManager().getItemCount()) {
        if (footView is LoadingMoreFooter) {
            footView.setState(LoadingMoreFooter.STATE_COMPLETE)
        } else {
            footView.visibility = View.GONE
        }
        //        } else {
        //            if (footView instanceof LoadingMoreFooter) {
        //                Log.e("noMoreLoading","noMoreLoading++++++");
        //                ((LoadingMoreFooter) footView).setState(LoadingMoreFooter.STATE_NOMORE);
        //            } else {
        //                footView.setVisibility(View.GONE);
        //            }
        //            isnomore = true;
        //        }
        //        previousTotal = getLayoutManager().getItemCount();
    }

    fun noMoreLoading() {
        isLoadingData = false
        val footView = mFootViews.get(0)
        isnomore = true
        if (footView is LoadingMoreFooter) {
            footView.setState(LoadingMoreFooter.STATE_NOMORE)
        } else {
            footView.visibility = View.GONE
        }
        // 额外添加的footView
        if (isOther) {
            footView.visibility = View.VISIBLE
        }
    }

    fun refreshComplete() {
        //  mRefreshHeader.refreshComplate();
        if (isLoadingData) {
            loadMoreComplete()
        } else {
            mRefreshHeader!!.refreshComplate()
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mWrapAdapter = WrapAdapter(mHeaderViews, mFootViews, adapter)
        super.setAdapter(mWrapAdapter)
        adapter.registerAdapterDataObserver(mDataObserver)
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)

        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingListener != null && !isLoadingData && loadingMoreEnabled) {
            val layoutManager = layoutManager
            val lastVisibleItemPosition: Int
            if (layoutManager is GridLayoutManager) {
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            } else if (layoutManager is StaggeredGridLayoutManager) {
                val into = IntArray(layoutManager.spanCount)
                layoutManager.findLastVisibleItemPositions(into)
                lastVisibleItemPosition = findMax(into)
            } else {
                lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
            if (layoutManager.childCount > 0
                    && lastVisibleItemPosition >= layoutManager.itemCount - 1
                    && layoutManager.itemCount > layoutManager.childCount
                    && !isnomore
                    && mRefreshHeader!!.state < YunRefreshHeader.STATE_REFRESHING) {

                val footView = mFootViews.get(0)
                isLoadingData = true
                if (footView != null) {
                    if (footView is LoadingMoreFooter) {
                        footView.setState(LoadingMoreFooter.STATE_LOADING)
                    } else {
                        footView.visibility = View.VISIBLE
                    }
                }
                if (isNetWorkConnected(context)) {
                    mLoadingListener!!.onLoadMore()
                } else {
                    postDelayed({ mLoadingListener!!.onLoadMore() }, 1000)
                }
            }
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mLastY == -1f) {
            mLastY = ev.rawY
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> mLastY = ev.rawY
            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.rawY - mLastY
                mLastY = ev.rawY
                if (isOnTop && pullRefreshEnabled) {
                    mRefreshHeader!!.onMove(deltaY / DRAG_RATE)
                    if (mRefreshHeader!!.visiableHeight > 0 && mRefreshHeader!!.state < YunRefreshHeader.STATE_REFRESHING) {
                        return false
                    }
                }
            }
            else -> {
                mLastY = -1f // reset
                if (isOnTop && pullRefreshEnabled) {
                    if (mRefreshHeader!!.releaseAction()) {
                        if (mLoadingListener != null) {
                            mLoadingListener!!.onRefresh()
                            isnomore = false
                            //                            previousTotal = 0;
                            val footView = mFootViews.get(0)
                            if (footView is LoadingMoreFooter) {
                                if (footView.getVisibility() != View.GONE) {
                                    footView.setVisibility(View.GONE)
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) {
                max = value
            }
        }
        return max
    }

    private fun findMin(firstPositions: IntArray): Int {
        var min = firstPositions[0]
        for (value in firstPositions) {
            if (value < min) {
                min = value
            }
        }
        return min
    }


    fun setLoadingListener(listener: LoadingListener) {
        mLoadingListener = listener
    }

    fun setPullRefreshEnabled(pullRefreshEnabled: Boolean) {
        this.pullRefreshEnabled = pullRefreshEnabled
    }

    fun setLoadingMoreEnabled(loadingMoreEnabled: Boolean) {
        this.loadingMoreEnabled = loadingMoreEnabled
        if (!loadingMoreEnabled) {
            mFootViews?.remove(0)
        } else {
            if (mFootViews != null) {
                val footView = LoadingMoreFooter(context)
                addFootView(footView, false)
            }
        }
    }


    fun setLoadMoreGone() {
        if (mFootViews == null) {
            return
        }
        val footView = mFootViews.get(0)
        if (footView != null && footView is LoadingMoreFooter) {
            mFootViews.remove(0)
        }
    }

    interface LoadingListener {

        fun onRefresh()

        fun onLoadMore()
    }

    fun reset() {
        isnomore = false
        //        previousTotal = 0;
        val footView = mFootViews.get(0)
        if (footView is LoadingMoreFooter) {
            footView.reSet()
        }
    }

    companion object {
        private val DRAG_RATE = 1.75f

        /**
         * 检测网络是否可用
         *
         * @param context
         * @return
         */
        fun isNetWorkConnected(context: Context?): Boolean {
            if (context != null) {
                val mConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable
                }
            }
            return false
        }
    }
}

