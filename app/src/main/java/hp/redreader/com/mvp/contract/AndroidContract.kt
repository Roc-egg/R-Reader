package hp.redreader.com.mvp.contract

import com.jess.arms.mvp.IView
import com.jess.arms.mvp.IModel
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import io.reactivex.Observable


interface AndroidContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        /**
         * 请求成功
         */
        fun showLoadSuccessView()

        /**
         * 显示列表没有更多数据布局
         */
        fun showListNoMoreLoading()

    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun getGankIoData(mType: String, mPage: Int, preEndIndex: Int, evictCache: Boolean): Observable<GankIoDataBean>
    }

}
