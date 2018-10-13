package hp.redreader.com.mvp.ui.adapter

import android.animation.Animator
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.StringFormatUtil
import hp.redreader.com.app.utils.displayEspImage
import hp.redreader.com.mvp.model.entity.movie.PersonBean
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
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
class MovieDetailAdapter constructor(mDatas: MutableList<PersonBean>) : BaseQuickAdapter<PersonBean
        , BaseViewHolder>(R.layout.item_movie_detail_person, mDatas) {

    override fun convert(helper: BaseViewHolder?, item: PersonBean?) {
        val imageView = helper?.getView<ImageView>(R.id.iv_avatar)
        displayEspImage(item?.avatars?.large, imageView, 0)

        helper?.setText(R.id.iv_name, item?.name)
                ?.setText(R.id.iv_type, item?.type)

        helper?.getView<LinearLayout>(R.id.ll_item)?.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View) {
                if (item != null && !TextUtils.isEmpty(item.alt)) {
                    WebViewActivity.loadUrl(v.context, item.alt, item.name)
                }
            }
        })
    }

    override fun startAnim(anim: Animator?, index: Int) {
        super.startAnim(anim, index)
        if (index < 4)
            anim?.startDelay = (index * 150).toLong()
    }

}