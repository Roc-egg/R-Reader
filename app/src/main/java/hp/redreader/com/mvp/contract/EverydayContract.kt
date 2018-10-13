package hp.redreader.com.mvp.contract

import com.jess.arms.mvp.IView
import com.jess.arms.mvp.IModel
import hp.redreader.com.mvp.model.entity.gank.AndroidBean
import hp.redreader.com.mvp.model.entity.gank.FrontpageBean
import hp.redreader.com.mvp.model.entity.gank.GankIoDayBean
import io.reactivex.Observable
import java.util.ArrayList


interface EverydayContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        /**
         * 显示旋转动画
         */
        fun showRotaLoading()

        /**
         * 显示错误页面
         */
         fun showErrorView()

        /**
         * 显示轮播图
         */
        fun showBannerView(mBannerImages: ArrayList<String>, result: List<FrontpageBean.ResultBannerBean.FocusBean.ResultBeanX>)

        /**
         * 显示列表数据
         */
         fun showListView(mLists: ArrayList<List<AndroidBean>>)

        /**
         * 有一个变量需要单独设置
         */
         fun setIsOldDayRequest(isOldDayRequest: Boolean)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun showBannerPage(evictCache: Boolean): Observable<FrontpageBean>
        fun getGankIoDay(s: String, s1: String, s2: String, evictCache: Boolean): Observable<GankIoDayBean>
    }

}
