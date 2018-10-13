package hp.redreader.com.mvp.ui.adapter

import android.text.Html
import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.utils.PerfectClickListener
import hp.redreader.com.app.utils.listener.WanNavigator
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.presenter.BannerPresenter
import hp.redreader.com.mvp.ui.activity.wan.ArticleListActivity
import me.jessyan.mvparms.demo.app.isLogin
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
class WanAndroidAdapter constructor(mDatas: MutableList<HomeListBean.DatasBean>) : BaseQuickAdapter<HomeListBean.DatasBean, BaseViewHolder>(R.layout.item_wan_android, mDatas) {

    /**
     * 是我的收藏页进来的，全部是收藏状态。bean里面没有返回isCollect信息
     */
    var isCollectList = false
    /**
     * 不显示类别信息
     */
    var isNoShowChapterName = false

    var mPresenter: BannerPresenter? = null


    override fun convert(helper: BaseViewHolder?, item: HomeListBean.DatasBean?) {
        helper?.setText(R.id.textView3, item?.author)
                ?.setText(R.id.textView4, item?.chapterName)
                ?.setVisible(R.id.textView4, !isNoShowChapterName)
                ?.setText(R.id.textView, Html.fromHtml(item?.title))
                ?.setChecked(R.id.vb_collect, if (isCollectList) true else item?.isCollect!!)
                ?.setText(R.id.textView2, item?.niceDate)


        helper?.setOnClickListener(R.id.textView4) {
            ArmsUtils.snackbarText("类型点击了${helper.adapterPosition}")
            ArticleListActivity.start(mContext, item?.chapterId!!, item.chapterName)
        }?.setOnClickListener(R.id.vb_collect
                , object : PerfectClickListener() {
            override fun onNoDoubleClick(v: View?) {

                val isChecked = helper.getView<CheckBox>(R.id.vb_collect).isChecked

                if (isLogin()) {
                    // 为什么状态值相反？因为点了之后控件已改变状态
                    Timber.e("-----binding.vbCollect.isChecked():" + (helper?.getView(R.id.vb_collect) as CheckBox).isChecked)
                    if (!isChecked) {
                        mPresenter?.unCollect(isCollectList, item?.id, item?.originId, object : WanNavigator.OnCollectNavigator {
                            override fun onSuccess() {
                                if (isCollectList) {

                                    val indexOf = data.indexOf(item)
                                    // 角标始终加一
                                    val adapterPosition = helper.adapterPosition

                                    Timber.e("getAdapterPosition():$adapterPosition")
                                    Timber.e("indexOf:$indexOf")
                                    // 移除数据增加删除动画
                                    data.removeAt(indexOf)
                                    notifyItemRemoved(adapterPosition)
                                } else {
                                    item?.isCollect = isChecked
                                }
                            }

                            override fun onFailure() {
                                item?.isCollect = true
                                notifyItemChanged(helper.adapterPosition)
                                ArmsUtils.snackbarTextWithLong("取消收藏失败")
                            }
                        })
                    } else {
                        mPresenter?.collect(item?.id, object : WanNavigator.OnCollectNavigator {
                            override fun onSuccess() {
                                item?.isCollect = true
                            }

                            override fun onFailure() {
                                item?.isCollect = false
                                notifyItemChanged(helper.adapterPosition)
                                ArmsUtils.snackbarTextWithLong("收藏失败")
                            }
                        })
                    }
                } else {
                    item?.isCollect = false
                    notifyItemChanged(helper.adapterPosition)
                }

            }

        })
    }

    fun setCollectList() {
        this.isCollectList = true
    }

    fun setNoShowChapterName() {
        this.isNoShowChapterName = true
    }

    /**
     * 将关联的Presenter添加进来在adapter中做请求逻辑
     */
    fun setPresenter(mPresenter: BannerPresenter?) {
        this.mPresenter = mPresenter
    }

}