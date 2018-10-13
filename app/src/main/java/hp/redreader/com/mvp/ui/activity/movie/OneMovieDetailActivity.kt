package hp.redreader.com.mvp.ui.activity.movie

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerOneMovieDetailComponent
import hp.redreader.com.di.module.OneMovieDetailModule
import hp.redreader.com.mvp.contract.OneMovieDetailContract
import hp.redreader.com.mvp.presenter.OneMovieDetailPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.BaseHeaderActivity
import hp.redreader.com.app.utils.StringFormatUtil
import hp.redreader.com.app.utils.showBookImg
import hp.redreader.com.app.utils.showImgBg
import hp.redreader.com.mvp.model.entity.movie.MovieDetailBean
import hp.redreader.com.mvp.model.entity.movie.PersonBean
import hp.redreader.com.mvp.model.entity.movie.SubjectsBean
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity
import hp.redreader.com.mvp.ui.adapter.MovieDetailAdapter
import kotlinx.android.synthetic.main.activity_one_movie_detail.view.*
import kotlinx.android.synthetic.main.header_slide_shape.view.*
import javax.inject.Inject


/**
 * 类名：    OneMovieDetailActivity.kt
 * 类描述：  电影资讯详情
 * 创建人：  hp
 * 创建时间：2018/9/26/026 0:46
 * 修改人：  hp
 * 修改时间：2018/9/26/026 0:46
 * 修改备注：
 */
class OneMovieDetailActivity : BaseHeaderActivity<OneMovieDetailPresenter>(), OneMovieDetailContract.View {
    @Inject
    lateinit var mAdapter: MovieDetailAdapter
    @Inject
    lateinit var mData: MutableList<PersonBean>

    private var subjectsBean: SubjectsBean? = null
    private var mMoreUrl: String? = null
    private var mMovieName: String? = null

    companion object {
        /**
         * @param context      activity
         * @param positionData bean
         * @param imageView    imageView
         */
        fun start(context: Activity, positionData: SubjectsBean, imageView: ImageView) {
            val intent = Intent(context, OneMovieDetailActivity::class.java)
            intent.putExtra("bean", positionData)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                    imageView, ArmsUtils.getString(context, R.string.transition_movie_img))//与xml文件对应
            ActivityCompat.startActivity(context, intent, options.toBundle())
        }
    }

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerOneMovieDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .oneMovieDetailModule(OneMovieDetailModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_one_movie_detail //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        subjectsBean = intent?.getSerializableExtra("bean") as SubjectsBean?

        initSlideShapeTheme(setHeaderImgUrl(), setHeaderImageView())
        title = subjectsBean?.title
        setSubTitle(String.format("主演：%s", StringFormatUtil.formatName(subjectsBean?.casts)))

        setHeaderLayoutDatas()


        loadMovieDetail()
    }

    override fun setDatas(movieDetailBean: MovieDetailBean) {
        // 上映日期
        HeaderView.tv_one_day.text = String.format("上映日期：%s", movieDetailBean.year)
        // 制片国家
        HeaderView.tv_one_city.text = String.format("制片国家/地区：%s", StringFormatUtil.formatGenres(movieDetailBean.countries))

        ContentView.tv_one_title.text = StringFormatUtil.formatGenres(movieDetailBean.aka)
        ContentView.tv_one_summary.text = movieDetailBean.summary

        mMoreUrl = movieDetailBean.getAlt()
        mMovieName = movieDetailBean.getTitle()

        transformData(movieDetailBean)

    }

    /**
     * 异步线程转换数据
     */
    private fun transformData(movieDetailBean: MovieDetailBean) {
        Thread(Runnable {
            for (i in 0 until movieDetailBean.directors.size) {
                movieDetailBean.directors[i].type = "导演"
            }
            for (i in 0 until movieDetailBean.casts.size) {
                movieDetailBean.casts[i].type = "演员"
            }

            this@OneMovieDetailActivity.runOnUiThread { setAdapter(movieDetailBean) }
        }).start()
    }


    private fun setAdapter(movieDetailBean: MovieDetailBean) {

        ContentView.xrv_cast.layoutManager = LinearLayoutManager(this)
        // 需加，不然滑动不流畅
        ContentView.xrv_cast.isNestedScrollingEnabled = false
        ContentView.xrv_cast.setHasFixedSize(false)

        ContentView.xrv_cast.adapter = mAdapter.apply {
            //onItemClickListener = this@OneFragment
            openLoadAnimation()

        }

        mData.addAll(movieDetailBean.directors)
        mData.addAll(movieDetailBean.casts)

    }

    private fun loadMovieDetail() {
        mPresenter?.getMovieDetail(subjectsBean?.id)
    }

    private fun setHeaderLayoutDatas() {
        showImgBg(HeaderView.img_item_bg, subjectsBean?.images?.medium!!)
        showBookImg(HeaderView.iv_one_photo, subjectsBean?.images?.large)
        HeaderView.tv_one_directors.text = StringFormatUtil.formatName(subjectsBean?.directors)
        HeaderView.tv_one_rating_rate.text = resources.getString(R.string.string_rating) + subjectsBean?.rating?.average
        HeaderView.tv_one_rating_number.text = subjectsBean?.collect_count.toString() + resources.getString(R.string.string_rating_num)
        HeaderView.tv_one_casts.text = StringFormatUtil.formatName(subjectsBean?.casts)
        HeaderView.tv_one_genres.text = resources.getString(R.string.string_type) + StringFormatUtil.formatGenres(subjectsBean?.genres)
    }

    override fun setTitleClickMore() {
        WebViewActivity.loadUrl(this@OneMovieDetailActivity, mMoreUrl, mMovieName)
    }

    override fun setHeaderLayout(): Int {
        return R.layout.header_slide_shape
    }

    override fun setHeaderImgUrl(): String? {
        return if (subjectsBean == null) {
            ""
        } else subjectsBean?.images?.medium
    }

    override fun setHeaderImageView(): ImageView {
        return HeaderView.img_item_bg
    }

    override fun onRefresh() {
        loadMovieDetail()
    }

    override fun showLoading() {
        //请求完成
        showContentView()
    }

    override fun hideLoading() {
        //请求失败
        showError()
    }

    override fun showMessage(message: String) {
        ArmsUtils.snackbarText(message)
    }

    override fun launchActivity(intent: Intent) {
        ArmsUtils.startActivity(intent)
    }

    override fun killMyself() {
        finish()
    }
}
