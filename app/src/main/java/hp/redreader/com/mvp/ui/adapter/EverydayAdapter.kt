package hp.redreader.com.mvp.ui.adapter

import android.content.DialogInterface
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import hp.redreader.com.R
import hp.redreader.com.mvp.model.entity.gank.AndroidBean
import com.chad.library.adapter.base.util.MultiTypeDelegate
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.dialog.show
import hp.redreader.com.app.utils.displayEspImage
import hp.redreader.com.app.utils.displayRandom
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import me.jessyan.mvparms.demo.app.formartHight
import org.simple.eventbus.EventBus
import timber.log.Timber


/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/13/013 19:11
 * 修改人：  hp
 * 修改时间：2018/9/13/013 19:11
 * 修改备注：
 */
class EverydayAdapter constructor(mDatas: MutableList<List<AndroidBean>>) : BaseQuickAdapter<List<AndroidBean>, BaseViewHolder>(mDatas) {
    private val TYPE_TITLE = 1 // title
    private val TYPE_ONE = 2// 一张图
    private val TYPE_TWO = 3// 二张图
    private val TYPE_THREE = 4// 三张图
    private var width: Int = 0

    init {
        width = ScreenUtils.getScreenWidth()
        multiTypeDelegate = object : MultiTypeDelegate<List<AndroidBean>>() {
            override fun getItemType(entity: List<AndroidBean>): Int {
                //根据你的实体类来判断布局类型
                if (!TextUtils.isEmpty(entity[0].type_title)) {
                    return TYPE_TITLE
                } else if (entity.size == 1) {
                    return TYPE_ONE
                } else if (entity.size == 2) {
                    return TYPE_TWO
                } else if (entity.size == 3) {
                    return TYPE_THREE
                }
                return 0
            }
        }
        multiTypeDelegate
                .registerItemType(TYPE_TITLE, R.layout.item_everyday_title)
                .registerItemType(TYPE_ONE, R.layout.item_everyday_one)
                .registerItemType(TYPE_TWO, R.layout.item_everyday_two)
                .registerItemType(TYPE_THREE, R.layout.item_everyday_three)
    }

    override fun convert(helper: BaseViewHolder?, item: List<AndroidBean>?) {
        when (helper?.itemViewType) {
            TYPE_TITLE -> {
                TitleSet(helper, item)
            }
            TYPE_ONE -> {
                OneSet(helper, item)

            }
            TYPE_TWO -> {
                TwoSet(helper, item)

            }
            TYPE_THREE -> {
                TherrSet(helper, item)
            }
        }

    }

    private fun TherrSet(helper: BaseViewHolder, item: List<AndroidBean>?) {
        val imageWidth = (width - SizeUtils.dp2px(6f)) / 3
        formartHight(helper.getView(R.id.iv_three_one_one), imageWidth, 1f, 1)
        formartHight(helper.getView(R.id.iv_three_one_two), imageWidth, 1f, 1)
        formartHight(helper.getView(R.id.iv_three_one_three), imageWidth, 1f, 1)
        displayRandomImg(3, 0, helper.getView(R.id.iv_three_one_one), item!!)
        displayRandomImg(3, 1, helper.getView(R.id.iv_three_one_two), item)
        displayRandomImg(3, 2, helper.getView(R.id.iv_three_one_three), item)
        setOnClick(helper.getView(R.id.ll_three_one_one), item[0])
        setOnClick(helper.getView(R.id.ll_three_one_two), item[1])
        setOnClick(helper.getView(R.id.ll_three_one_three), item[2])
        setDes(item, 0, helper.getView(R.id.tv_three_one_one_title))
        setDes(item, 1, helper.getView(R.id.tv_three_one_two_title))
        setDes(item, 2, helper.getView(R.id.tv_three_one_three_title))
    }

    private fun TwoSet(helper: BaseViewHolder, item: List<AndroidBean>?) {
        val imageWidth = (width - SizeUtils.dp2px(3f)) / 2
        formartHight(helper.getView(R.id.iv_two_one_one), imageWidth, 1.75f, 1)
        formartHight(helper.getView(R.id.iv_two_one_two), imageWidth, 1.75f, 1)
        displayRandomImg(2, 0, helper.getView(R.id.iv_two_one_one), item!!)
        displayRandomImg(2, 1, helper.getView(R.id.iv_two_one_two), item)
        setDes(item!!, 0, helper.getView(R.id.tv_two_one_one_title))
        setDes(item!!, 1, helper.getView(R.id.tv_two_one_two_title))
        setOnClick(helper.getView(R.id.ll_two_one_one), item[0])
        setOnClick(helper.getView(R.id.ll_two_one_two), item[1])
    }

    private fun OneSet(helper: BaseViewHolder, item: List<AndroidBean>?) {
        var image: ImageView = helper.getView(R.id.iv_one_photo)
        formartHight(image, width, 2.6f, 1)
        if ("福利" == item?.get(0)?.type) {
            helper.setGone(R.id.tv_one_photo_title, false)
            image.scaleType = ImageView.ScaleType.CENTER_CROP
            displayRandom(1, item[0].url, image)

        } else {
            helper.setGone(R.id.tv_one_photo_title, true)
            setDes(item!!, 0, helper.getView(R.id.tv_one_photo_title))
            displayRandomImg(1, 0, image, item)
        }
        setOnClick(helper.getView(R.id.ll_one_photo), item[0])

    }

    private fun displayRandomImg(imgNumber: Int, position: Int, imageView: ImageView, item: List<AndroidBean>) {
        displayRandom(imgNumber, item[position].image_url, imageView)
    }

    private fun setDes(item: List<AndroidBean>, position: Int, textView: TextView) {
        textView.text = item[position].desc
    }

    private fun TitleSet(helper: BaseViewHolder, item: List<AndroidBean>?) {
        var index = 0
        val title = item?.get(0)?.type_title

        when (title) {
            "Android" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_android));
                index = 0
            "福利" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_meizi));
                index = 1
            "IOS" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_ios));
                index = 2
            "休息视频" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_movie));
                index = 2
            "拓展资源" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_source));
                index = 2
            "瞎推荐" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_xia));
                index = 2
            "前端" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_qian));
                index = 2
            "App" ->
                //                binding.ivTitleType.setImageDrawable(CommonUtils.getDrawable(R.drawable.home_title_app));
                index = 2
        }
        val finalIndex = index
        Timber.e("索引为==${helper.adapterPosition}")
        helper.setText(R.id.tv_title_type, title)
                .setGone(R.id.view_line, helper.adapterPosition != 0)
                .setOnClickListener(R.id.ll_title_more) {
                    Timber.e("索引为==${helper.adapterPosition}")
                    EventBus.getDefault().post(finalIndex, "jump_type")//发起通知GankFragment切换fragment
                    EventBus.getDefault().post(title, "jump_type_code")//发起通知CustomFragment界面变更
                }

    }

    private fun setOnClick(linearLayout: LinearLayout, bean: AndroidBean) {
        linearLayout.setOnClickListener(object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View) {
                WebViewActivity.loadUrl(v.context, bean.url, bean.desc)
            }
        })

        linearLayout.setOnLongClickListener { v ->
            val title = if (TextUtils.isEmpty(bean.type)) bean.desc else bean.type + "：  " + bean.desc
            show(v, title, DialogInterface.OnClickListener { dialog, which ->
                WebViewActivity.loadUrl(linearLayout.context, bean.url, bean.desc)
            })
            false
        }

    }
}