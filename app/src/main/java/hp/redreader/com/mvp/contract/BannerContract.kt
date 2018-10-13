package hp.redreader.com.mvp.contract

import com.jess.arms.mvp.IView
import com.jess.arms.mvp.IModel
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.model.entity.wanandroid.WanAndroidBannerBean
import io.reactivex.Observable


interface BannerContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {

        /**
         * 显示加载成功页面
         */
        fun showLoadSuccessView()

        /**
         * 没有更多了
         */
        fun showListNoMoreLoading()

        /**
         * 显示banner图
         *
         * @param bannerImages 图片链接集合
         * @param mBannerTitle 文章标题集合
         * @param result       全部数据
         */
        fun showBannerView(mBannerImages: ArrayList<String>, mBannerTitles: ArrayList<String>, result: List<WanAndroidBannerBean.DataBean>)

        /**
         * 加载banner图失败
         */
        fun loadBannerFailure()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getHomeList(mPage: Int, cid: Int?, evictCache: Boolean): Observable<HomeListBean>
        fun getWanAndroidBanner(evictCache: Boolean): Observable<WanAndroidBannerBean>
        fun collect(id: Int?): Observable<HomeListBean>
        fun unCollect(id: Int?, originId: Int?): Observable<HomeListBean>
        fun unCollectOrigin(id: Int?): Observable<HomeListBean>
        fun getCollectList(mPage: Int, evictCache: Boolean): Observable<HomeListBean>
    }

}
