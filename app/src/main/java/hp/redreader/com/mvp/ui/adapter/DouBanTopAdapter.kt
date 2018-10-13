package hp.redreader.com.mvp.ui.adapter

import android.app.Activity
import android.content.DialogInterface
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.dialog.show
import hp.redreader.com.app.utils.displayCircle
import hp.redreader.com.app.utils.showMovieImg
import hp.redreader.com.mvp.model.entity.movie.SubjectsBean
import hp.redreader.com.mvp.ui.activity.movie.OneMovieDetailActivity

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/23/023 0:13
 * 修改人：  hp
 * 修改时间：2018/9/23/023 0:13
 * 修改备注：
 */
class DouBanTopAdapter constructor(mDatas: MutableList<SubjectsBean>) : BaseQuickAdapter<SubjectsBean
        , BaseViewHolder>(R.layout.item_douban_top, mDatas) {

    override fun convert(helper: BaseViewHolder?, item: SubjectsBean?) {
        val imageView = helper?.getView<ImageView>(R.id.iv_top_photo)
        showMovieImg(imageView, item?.images?.large)

        helper?.setText(R.id.tv_name, item?.title)
                ?.setText(R.id.tv_rate, ArmsUtils.getString(mContext, R.string.string_rating) + item?.rating?.average)

        helper?.getView<LinearLayout>(R.id.ll_item_top)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {
                OneMovieDetailActivity.start(mContext as Activity, item!!, imageView!!)
            }
        })
        helper?.getView<LinearLayout>(R.id.ll_item_top)?.setOnLongClickListener { v ->
            val title = "Top" + helper?.adapterPosition + ": " + item?.title
            show(v, title, DialogInterface.OnClickListener { _, _ ->
                OneMovieDetailActivity.start(mContext as Activity, item!!, imageView!!)
            })
            false
        }

    }

}