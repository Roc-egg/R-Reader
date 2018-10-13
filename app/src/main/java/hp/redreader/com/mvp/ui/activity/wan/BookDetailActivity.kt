package hp.redreader.com.mvp.ui.activity.wan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.widget.ImageView

import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.ArmsUtils

import hp.redreader.com.di.component.DaggerBookDetailComponent
import hp.redreader.com.di.module.BookDetailModule
import hp.redreader.com.mvp.contract.BookDetailContract
import hp.redreader.com.mvp.presenter.BookDetailPresenter

import hp.redreader.com.R
import hp.redreader.com.app.base.BaseHeaderActivity
import hp.redreader.com.app.utils.StringFormatUtil
import hp.redreader.com.app.utils.showBookImg
import hp.redreader.com.app.utils.showImgBg
import hp.redreader.com.mvp.model.entity.book.BookBean
import hp.redreader.com.mvp.model.entity.book.BookDetailBean
import hp.redreader.com.mvp.ui.activity.webview.WebViewActivity


/**
 * 类名：    BookDetailActivity.kt
 * 类描述：  书籍详情
 * 创建人：  hp
 * 创建时间：2018/9/24/024 0:47
 * 修改人：  hp
 * 修改时间：2018/9/24/024 0:47
 * 修改备注：
 */
class BookDetailActivity : BaseHeaderActivity<BookDetailPresenter>(), BookDetailContract.View {
    private var booksBean: BookBean.BooksBean? = null
    private var mBookDetailUrl: String? = null
    private var mBookDetailName: String? = null


    companion object {
        val EXTRA_PARAM = "bookBean"
        /**
         * @param context      activity
         * @param positionData bean
         * @param imageView    imageView
         */
        fun start(context: Activity, positionData: BookBean.BooksBean, imageView: ImageView) {
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra(EXTRA_PARAM, positionData)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                    imageView, ArmsUtils.getString(context, R.string.transition_book_img))//与xml文件对应
            ActivityCompat.startActivity(context, intent, options.toBundle())
        }
    }

    override fun setupActivityComponent(appComponent: AppComponent) {
        DaggerBookDetailComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .bookDetailModule(BookDetailModule(this))
                .build()
                .inject(this)
    }


    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_book_detail //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }


    override fun initData(savedInstanceState: Bundle?) {
        booksBean = intent?.getSerializableExtra(EXTRA_PARAM) as BookBean.BooksBean?

        setMotion(setHeaderPicView(), true)
        initSlideShapeTheme(setHeaderImgUrl(), setHeaderImageView())

        title = booksBean?.title
        setSubTitle("作者：" + booksBean?.author)

        setHeaderLayoutDatas()

        loadBookDetail()
    }

    private fun loadBookDetail() {
        mPresenter?.getBookDetail(booksBean?.id)
    }

    private fun setHeaderLayoutDatas() {
        showImgBg(HeaderView.img_item_bg, booksBean?.images?.medium!!)
        showBookImg(HeaderView.iv_one_photo, booksBean?.images?.large)
        HeaderView.tv_one_directors.text = StringFormatUtil.formatGenres(booksBean?.author)
        HeaderView.tv_one_rating_rate.text = resources.getString(R.string.string_rating) + booksBean?.rating?.average
        HeaderView.tv_one_rating_number.text = booksBean?.rating?.numRaters.toString() + resources.getString(R.string.string_rating)
        HeaderView.tv_one_casts.text = booksBean?.pubdate
        HeaderView.tv_one_genres.text = resources.getString(R.string.string_publisher) + booksBean?.publisher
    }

    override fun setHeaderLayout(): Int {
        return R.layout.header_book_detail
    }

    override fun setTitleClickMore() {
        WebViewActivity.loadUrl(this, mBookDetailUrl, mBookDetailName)
    }

    override fun setHeaderImgUrl(): String? {
        return if (booksBean == null) {
            ""
        } else booksBean?.images?.medium
    }

    override fun setHeaderImageView(): ImageView {
        return HeaderView.img_item_bg
    }

    override fun setHeaderPicView(): ImageView {
        return HeaderView.iv_one_photo
    }

    override fun onRefresh() {
        loadBookDetail()
    }

    override fun setDatas(bookDetailBean: BookDetailBean) {
        mBookDetailUrl = bookDetailBean.alt
        mBookDetailName = bookDetailBean.title
        ContentView.tv_book_summary.text = bookDetailBean.summary
        ContentView.tv_book_author_intro.text = bookDetailBean.author_intro
        ContentView.tv_book_catalog.text = bookDetailBean.catalog
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
