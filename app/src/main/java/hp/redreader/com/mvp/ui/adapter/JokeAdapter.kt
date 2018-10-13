package hp.redreader.com.mvp.ui.adapter

import android.widget.ImageView
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.dialog.showItems
import hp.redreader.com.app.utils.displayCircle
import hp.redreader.com.mvp.model.entity.wanandroid.DuanZiBean

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/23/023 0:13
 * 修改人：  hp
 * 修改时间：2018/9/23/023 0:13
 * 修改备注：
 */
class JokeAdapter constructor(mDatas: MutableList<DuanZiBean>) : BaseQuickAdapter<DuanZiBean
        , BaseViewHolder>(R.layout.item_joke, mDatas) {

    override fun convert(helper: BaseViewHolder?, item: DuanZiBean?) {
        val imageView = helper?.getView<ImageView>(R.id.joke_avatarUrl)
        displayCircle(imageView, item?.avatarUrl)

        helper?.setText(R.id.joke_name,
                if (StringUtils.isEmpty(item?.name))
                    ArmsUtils.getString(mContext, R.string.no_name_joke)
                else item?.name)
                ?.setText(R.id.joke_time, TimeUtils.millis2String(java.lang.Long.valueOf(item?.createTime.toString() + "000")))
                ?.setText(R.id.joke_content, item?.content)
                ?.setText(R.id.joke_categoryName, ArmsUtils.getString(mContext, R.string.string_joke) + item?.categoryName)
                ?.setGone(R.id.joke_categoryName, !StringUtils.isEmpty(item?.categoryName))


    }

}