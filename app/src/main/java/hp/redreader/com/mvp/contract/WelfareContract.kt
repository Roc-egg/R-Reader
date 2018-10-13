package hp.redreader.com.mvp.contract

import com.jess.arms.mvp.IView
import com.jess.arms.mvp.IModel
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import io.reactivex.Observable


interface WelfareContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView{
        /**
         * 显示加载成功页面
         */
        fun showLoadSuccessView()

        /**
         * 没有更多了
         */
        fun showListNoMoreLoading()

        fun setImageList(t: ArrayList<ArrayList<String>>?)
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getGankIoData(s: String, mPage: Int, preEndIndex: Int, evictCache: Boolean): Observable<GankIoDataBean>
    }

}
