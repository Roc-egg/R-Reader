package com.example.xrecyclerview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by yangcai on 2016/1/27.
 */
class YunRefreshHeader constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(mContext, attrs, defStyleAttr), BaseRefreshHeader {
    private var animationDrawable: AnimationDrawable? = null
    private var msg: TextView? = null
    var state = BaseRefreshHeader.Companion.STATE_NORMAL
        private set(state) {
            if (state == this.state) return
            when (state) {
                BaseRefreshHeader.Companion.STATE_NORMAL -> {
                    if (animationDrawable!!.isRunning) {
                        animationDrawable!!.stop()
                    }
                    msg!!.setText(R.string.listview_header_hint_normal)
                }
                BaseRefreshHeader.Companion.STATE_RELEASE_TO_REFRESH -> if (this.state != BaseRefreshHeader.Companion.STATE_RELEASE_TO_REFRESH) {
                    if (!animationDrawable!!.isRunning) {
                        animationDrawable!!.start()
                    }
                    msg!!.setText(R.string.listview_header_hint_release)
                }
                BaseRefreshHeader.STATE_REFRESHING -> msg!!.setText(R.string.refreshing)
                BaseRefreshHeader.STATE_DONE -> msg!!.setText(R.string.refresh_done)
            }
            field = state
        }
    private var mMeasuredHeight: Int = 0
    private var mContainer: LinearLayout? = null

    override//       `
    var visiableHeight: Int
        get() = mContainer!!.height
        private set(height) {
            var height = height
            if (height < 0)
                height = 0
            val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
            lp.height = height
            mContainer!!.layoutParams = lp
        }

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(mContext).inflate(R.layout.kaws_refresh_header, this)
        val img = findViewById<View>(R.id.img) as ImageView

        animationDrawable = img.drawable as AnimationDrawable
        if (animationDrawable!!.isRunning) {
            animationDrawable!!.stop()
        }
        msg = findViewById<View>(R.id.msg) as TextView?
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mMeasuredHeight = measuredHeight
        gravity = Gravity.CENTER_HORIZONTAL
        mContainer = findViewById<View>(R.id.container) as LinearLayout?
        mContainer!!.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
        this.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }


    override fun onMove(delta: Float) {
        if (visiableHeight > 0 || delta > 0) {
            visiableHeight = delta.toInt() + visiableHeight
            if (state <= BaseRefreshHeader.Companion.STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (visiableHeight > mMeasuredHeight) {
                    state = BaseRefreshHeader.Companion.STATE_RELEASE_TO_REFRESH
                } else {
                    state = BaseRefreshHeader.Companion.STATE_NORMAL
                }
            }
        }
    }

    override fun releaseAction(): Boolean {
        var isOnRefresh = false
        val height = visiableHeight
        if (height == 0)
        // not visible.
            isOnRefresh = false

        if (visiableHeight > mMeasuredHeight && state < BaseRefreshHeader.STATE_REFRESHING) {
            state = BaseRefreshHeader.STATE_REFRESHING
            isOnRefresh = true
        }
        // refreshing and header isn't shown fully. do nothing.
        if (state == BaseRefreshHeader.STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        var destHeight = 0 // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (state == BaseRefreshHeader.STATE_REFRESHING) {
            destHeight = mMeasuredHeight
        }
        smoothScrollTo(destHeight)

        return isOnRefresh
    }

    override fun refreshComplate() {
        state = BaseRefreshHeader.Companion.STATE_DONE
        Handler().postDelayed({ reset() }, 500)
    }

    fun reset() {
        smoothScrollTo(0)
        state = BaseRefreshHeader.Companion.STATE_NORMAL
    }

    private fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(visiableHeight, destHeight)
        animator.setDuration(300).start()
        animator.addUpdateListener { animation -> visiableHeight = animation.animatedValue as Int }
        animator.start()
    }
}
