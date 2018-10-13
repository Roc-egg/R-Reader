package com.example.xrecyclerview

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup

/**
 * Created by yangcai on 2016/1/28.
 */
class WrapAdapter(private val mHeaderViews: SparseArray<View>, private val mFootViews: SparseArray<View>, private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var headerPosition = 1

    val headersCount: Int
        get() = mHeaderViews.size()

    val footersCount: Int
        get() = mFootViews.size()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isHeader(position) || isFooter(position))
                        manager.spanCount
                    else
                        1
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp != null
                && lp is StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.layoutPosition) || isFooter(holder.layoutPosition))) {
            lp.isFullSpan = true
        }
    }

    fun isHeader(position: Int): Boolean {
        return position >= 0 && position < mHeaderViews.size()
    }

    fun isFooter(position: Int): Boolean {
        return position < itemCount && position >= itemCount - mFootViews.size()
    }

    fun isRefreshHeader(position: Int): Boolean {
        return position == 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_REFRESH_HEADER) {
            return SimpleViewHolder(mHeaderViews.get(0))
        } else if (viewType == TYPE_HEADER) {
            return SimpleViewHolder(mHeaderViews.get(headerPosition++))
        } else if (viewType == TYPE_FOOTER) {
            return SimpleViewHolder(mFootViews.get(0))
        }
        return adapter!!.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeader(position)) {
            return
        }
        val adjPosition = position - headersCount
        val adapterCount: Int
        if (adapter != null) {
            adapterCount = adapter.itemCount
            if (adjPosition < adapterCount) {
                adapter.onBindViewHolder(holder, adjPosition)
                return
            }
        }
    }

    override fun getItemCount(): Int {
        return if (adapter != null) {
            headersCount + footersCount + adapter.itemCount
        } else {
            headersCount + footersCount
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isRefreshHeader(position)) {
            return TYPE_REFRESH_HEADER
        }
        if (isHeader(position)) {
            return TYPE_HEADER
        }
        if (isFooter(position)) {
            return TYPE_FOOTER
        }
        val adjPosition = position - headersCount
        val adapterCount: Int
        if (adapter != null) {
            adapterCount = adapter.itemCount
            if (adjPosition < adapterCount) {
                return adapter.getItemViewType(adjPosition)
            }
        }
        return TYPE_NORMAL
    }

    override fun getItemId(position: Int): Long {
        if (adapter != null && position >= headersCount) {
            val adjPosition = position - headersCount
            val adapterCount = adapter.itemCount
            if (adjPosition < adapterCount) {
                return adapter.getItemId(adjPosition)
            }
        }
        return -1
    }

    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        adapter?.unregisterAdapterDataObserver(observer)
    }

    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        adapter?.registerAdapterDataObserver(observer)
    }

    private inner class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        private val TYPE_REFRESH_HEADER = -5
        private val TYPE_HEADER = -4
        private val TYPE_NORMAL = 0
        private val TYPE_FOOTER = -3
    }
}