package com.example.xrecyclerview

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class LoadingMoreFooter : LinearLayout {
    private var mText: TextView? = null
    private var mAnimationDrawable: AnimationDrawable? = null
    private var mIvProgress: ImageView? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    /**
     * @param context
     * @param attrs
     */
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context)
    }

    fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.yun_refresh_footer, this)
        mText = findViewById<View>(R.id.msg) as TextView
        mIvProgress = findViewById<View>(R.id.iv_progress) as ImageView
        mAnimationDrawable = mIvProgress!!.drawable as AnimationDrawable
        if (!mAnimationDrawable!!.isRunning) {
            mAnimationDrawable!!.start()
        }
        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setState(state: Int) {
        when (state) {
            STATE_LOADING -> {
                if (!mAnimationDrawable!!.isRunning) {
                    mAnimationDrawable!!.start()
                }
                mIvProgress!!.visibility = View.VISIBLE
                mText!!.text = context.getText(R.string.listview_loading)
                this.visibility = View.VISIBLE
            }
            STATE_COMPLETE -> {
                if (mAnimationDrawable!!.isRunning) {
                    mAnimationDrawable!!.stop()
                }
                mText!!.text = context.getText(R.string.listview_loading)
                this.visibility = View.GONE
            }
            STATE_NOMORE -> {
                if (mAnimationDrawable!!.isRunning) {
                    mAnimationDrawable!!.stop()
                }
                mText!!.text = context.getText(R.string.nomore_loading)
                mIvProgress!!.visibility = View.GONE
                this.visibility = View.VISIBLE
            }
        }
    }

    fun reSet() {
        this.visibility = View.GONE
    }

    companion object {

        val STATE_LOADING = 0
        val STATE_COMPLETE = 1
        val STATE_NOMORE = 2
    }
}
