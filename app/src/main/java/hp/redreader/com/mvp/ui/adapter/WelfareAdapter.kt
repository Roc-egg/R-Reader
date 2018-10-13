package hp.redreader.com.mvp.ui.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.BaseTools
import hp.redreader.com.app.utils.displayEspImage
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/23/023 0:13
 * 修改人：  hp
 * 修改时间：2018/9/23/023 0:13
 * 修改备注：
 */
class WelfareAdapter constructor(mDatas: MutableList<GankIoDataBean.ResultBean>) : BaseQuickAdapter<GankIoDataBean.ResultBean
        , BaseViewHolder>(R.layout.item_welfare, mDatas) {

    override fun convert(helper: BaseViewHolder?, item: GankIoDataBean.ResultBean?) {
        val imageView = helper?.getView<ImageView>(R.id.iv_welfare)
        displayEspImage(item?.url, imageView, 1)
        /**
         * 注意：DensityUtil.setViewMargin(itemView,true,5,3,5,0);
         * 如果这样使用，则每个item的左右边距是不一样的，
         * 这样item不能复用，所以下拉刷新成功后显示会闪一下
         * 换成每个item设置上下左右边距是一样的话，系统就会复用，就消除了图片不能复用 闪跳的情况
         */
        if ((helper?.adapterPosition!! - 1) % 2 == 0) {
            BaseTools.setViewMargin(helper.itemView, false, 12, 6, 12, 0)
        } else {
            BaseTools.setViewMargin(helper.itemView, false, 6, 12, 12, 0)
        }
    }

}