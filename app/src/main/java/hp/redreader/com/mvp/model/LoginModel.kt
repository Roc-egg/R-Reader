package hp.redreader.com.mvp.model

import android.app.Application
import android.text.Editable
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.ActivityScope
import javax.inject.Inject

import hp.redreader.com.mvp.contract.LoginContract
import hp.redreader.com.mvp.model.entity.wanandroid.LoginBean
import io.reactivex.Observable
import me.jessyan.mvparms.demo.mvp.model.api.service.WanAndroidService


@ActivityScope
class LoginModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), LoginContract.Model {
    override fun login(username: Editable?, password: Editable?): Observable<LoginBean> {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService::class.java).login(username.toString(), password.toString())
    }

    override fun register(username: Editable?, password: Editable?): Observable<LoginBean> {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService::class.java).register(username.toString(), password.toString(), password.toString())
    }

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun onDestroy() {
        super.onDestroy()
    }
}
