package hp.redreader.com.mvp.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

/**
 * 类名：    MyFragmentPagerAdapter.kt
 *
 * 类描述：  FragmentPagerAdapter
 *
 * 创建人：  hp
 * 创建时间：2018/8/24 17:59
 * 修改人：  hp
 * 修改时间：2018/8/24 17:59
 * 修改备注：
 */

class MyFragmentPagerAdapter : FragmentPagerAdapter {

//    private var mFragment: MutableList<*>? = null
//    private val mTitleList: List<String>? = null

    private var mFragment: MutableList<*>
    private lateinit var mTitleList: List<String>

    /**
     * 普通，主页使用
     */
    constructor(fm: FragmentManager, mFragment: MutableList<*>) : super(fm) {
        this.mFragment = mFragment
    }


    /**
     * 接收首页传递的标题
     */
    constructor(fm: FragmentManager, mFragment: MutableList<*>, mTitleList: List<String>) : super(fm) {
        this.mFragment = mFragment
        this.mTitleList = mTitleList
    }

    override fun getItem(position: Int): Fragment {
        return mFragment[position] as Fragment
    }

    override fun getCount(): Int {
        return mFragment.size
    }


    /**
     * 首页显示title，每日推荐等..
     * 若有问题，移到对应单独页面
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return if (mTitleList != null && position < mTitleList.size) {
            mTitleList[position]
        } else {
            ""
        }
    }

    fun addFragmentList(fragment: MutableList<*>) {
        this.mFragment!!.clear()
        this.mFragment = fragment
        notifyDataSetChanged()
    }

}
