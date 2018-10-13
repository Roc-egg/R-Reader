package hp.redreader.com.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import javax.inject.Inject

import hp.redreader.com.mvp.contract.WanContract


@FragmentScope
class WanModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), WanContract.Model {
    @Inject
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun onDestroy() {
        super.onDestroy();
    }
}
