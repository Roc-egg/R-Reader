package hp.redreader.com.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.ActivityScope
import javax.inject.Inject

import hp.redreader.com.mvp.contract.OneMovieDetailContract
import hp.redreader.com.mvp.model.api.service.DouBanService
import hp.redreader.com.mvp.model.entity.movie.MovieDetailBean
import io.reactivex.Observable


@ActivityScope
class OneMovieDetailModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), OneMovieDetailContract.Model {
    override fun getMovieDetail(id: String?): Observable<MovieDetailBean> {

        return mRepositoryManager.obtainRetrofitService(DouBanService::class.java).getMovieDetail(id)
    }

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun onDestroy() {
        super.onDestroy()
    }
}
