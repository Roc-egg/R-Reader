package hp.redreader.com.mvp.contract

import android.text.Editable
import com.jess.arms.mvp.IView
import com.jess.arms.mvp.IModel
import hp.redreader.com.mvp.model.entity.wanandroid.LoginBean
import io.reactivex.Observable


interface LoginContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View : IView {
        /**
         * 登录成功
         */
        fun loadSuccess()
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model : IModel {
        fun register(username: Editable?, password: Editable?): Observable<LoginBean>
        fun login(username: Editable?, password: Editable?): Observable<LoginBean>
    }

}
