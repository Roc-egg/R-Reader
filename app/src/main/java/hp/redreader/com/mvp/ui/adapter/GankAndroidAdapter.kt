package hp.redreader.com.mvp.ui.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.displayEspImage
import hp.redreader.com.app.utils.displayGif
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import timber.log.Timber
import java.text.SimpleDateFormat

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/13/013 19:11
 * 修改人：  hp
 * 修改时间：2018/9/13/013 19:11
 * 修改备注：
 */
class GankAndroidAdapter constructor(mDatas: MutableList<GankIoDataBean.ResultBean>) : BaseQuickAdapter<GankIoDataBean.ResultBean, BaseViewHolder>(R.layout.item_android, mDatas) {

    private var isAll = false
    private var sdf1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    fun setAllType(isAll: Boolean) {
        this.isAll = isAll
    }

    override fun convert(helper: BaseViewHolder?, item: GankIoDataBean.ResultBean?) {
        Timber.e("isAll==$isAll")
        helper?.setText(R.id.tv_android_des, item?.desc)
                ?.setText(R.id.tv_android_who, if (TextUtils.isEmpty(item?.who))
                    ArmsUtils.getString(mContext, R.string.no_name)
                else
                    item?.who
                )
                ?.setText(R.id.tv_android_time, TimeUtils.getFriendlyTimeSpanByNow(item?.publishedAt, sdf1))

        if (isAll && "福利" == item?.type) {
            displayEspImage(item.url, helper?.getView(R.id.iv_all_welfare), 1)
        }
        helper?.setGone(R.id.iv_all_welfare, isAll && "福利" == item?.type)
        helper?.setGone(R.id.ll_welfare_other, !(isAll && "福利" == item?.type))

        if (isAll) {
            helper?.setText(R.id.tv_content_type, " · " + item?.type)
        }
        helper?.setGone(R.id.tv_content_type, isAll)

        // 显示gif图片会很耗内存
        val imageView = helper?.getView<ImageView>(R.id.iv_android_pic)
        if (item?.images != null
                && item.images.size > 0
                && !TextUtils.isEmpty(item.images[0])) {
            imageView?.visibility = View.VISIBLE
            displayGif(item.images[0], imageView!!)
        } else {
            imageView?.visibility = View.GONE
        }

    }

}