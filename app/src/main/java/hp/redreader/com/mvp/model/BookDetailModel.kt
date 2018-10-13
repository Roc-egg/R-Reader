package hp.redreader.com.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.ActivityScope
import javax.inject.Inject

import hp.redreader.com.mvp.contract.BookDetailContract
import hp.redreader.com.mvp.model.api.service.DouBanService
import hp.redreader.com.mvp.model.entity.book.BookDetailBean
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.Reply
import io.rx_cache2.Source
import me.jessyan.mvparms.demo.mvp.model.api.cache.CommonCache
import timber.log.Timber


@ActivityScope
class BookDetailModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), BookDetailContract.Model {
    override fun getBookDetail(id: String?): Observable<BookDetailBean> {
        return mRepositoryManager.obtainRetrofitService(DouBanService::class.java).getBookDetail(id)
    }

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun onDestroy() {
        super.onDestroy()
    }
}
