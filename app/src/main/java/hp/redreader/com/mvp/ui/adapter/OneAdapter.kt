package hp.redreader.com.mvp.ui.adapter

import android.animation.Animator
import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.StringFormatUtil
import hp.redreader.com.app.utils.showMovieImg
import hp.redreader.com.mvp.model.entity.movie.SubjectsBean
import hp.redreader.com.mvp.ui.activity.movie.OneMovieDetailActivity
import me.jessyan.mvparms.demo.app.randomColor

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/23/023 0:13
 * 修改人：  hp
 * 修改时间：2018/9/23/023 0:13
 * 修改备注：
 */
class OneAdapter constructor(mDatas: MutableList<SubjectsBean>) : BaseQuickAdapter<SubjectsBean
        , BaseViewHolder>(R.layout.item_one, mDatas) {

    override fun convert(helper: BaseViewHolder?, item: SubjectsBean?) {
        val imageView = helper?.getView<ImageView>(R.id.iv_one_photo)
        showMovieImg(imageView, item?.images?.large)

        helper?.setText(R.id.tv_one_title, item?.title)
                ?.setText(R.id.tv_one_directors, StringFormatUtil.formatName(item?.directors))
                ?.setText(R.id.tv_one_casts, StringFormatUtil.formatName(item?.casts))
                ?.setText(R.id.tv_one_genres, ArmsUtils.getString(mContext, R.string.string_type)
                        + StringFormatUtil.formatGenres(item?.genres))
                ?.setText(R.id.tv_one_rating_rate, ArmsUtils.getString(mContext, R.string.string_rating)
                        + item?.rating?.average)
                ?.setBackgroundColor(R.id.view_color, randomColor())

        val mLinearLayout = helper?.getView<LinearLayout>(R.id.ll_one_item)
        mLinearLayout?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View) {
                OneMovieDetailActivity.start(mContext as Activity, item!!, imageView!!)
            }
        })
    }

    override fun startAnim(anim: Animator?, index: Int) {
        super.startAnim(anim, index)
        if (index < 4)
            anim?.startDelay = (index * 150).toLong()
    }

}