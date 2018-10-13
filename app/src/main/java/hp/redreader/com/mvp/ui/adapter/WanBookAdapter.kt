package hp.redreader.com.mvp.ui.adapter

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.showBookImg
import hp.redreader.com.mvp.model.entity.book.BookBean
import hp.redreader.com.mvp.ui.activity.wan.BookDetailActivity

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/23/023 0:13
 * 修改人：  hp
 * 修改时间：2018/9/23/023 0:13
 * 修改备注：
 */
class WanBookAdapter constructor(mDatas: MutableList<BookBean.BooksBean>) : BaseQuickAdapter<BookBean.BooksBean
        , BaseViewHolder>(R.layout.item_book, mDatas) {

    override fun convert(helper: BaseViewHolder?, item: BookBean.BooksBean?) {
        helper?.setText(R.id.tv_name, item?.title)
                ?.setText(R.id.tv_rate, ArmsUtils.getString(mContext, R.string.string_rating) + item?.rating?.average)

        val imageView = helper?.getView<ImageView>(R.id.iv_top_photo)
        val mLinearLayout = helper?.getView<LinearLayout>(R.id.ll_item_top)
        showBookImg(imageView, item?.images?.large)

        mLinearLayout?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View) {
                BookDetailActivity.start(mContext as Activity, item!!, imageView!!)
            }
        })

    }

}