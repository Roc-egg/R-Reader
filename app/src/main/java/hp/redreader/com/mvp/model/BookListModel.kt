package hp.redreader.com.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import javax.inject.Inject

import hp.redreader.com.mvp.contract.BookListContract
import hp.redreader.com.mvp.model.api.service.DouBanService
import hp.redreader.com.mvp.model.entity.book.BookBean
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.Reply
import io.rx_cache2.Source
import me.jessyan.mvparms.demo.mvp.model.api.cache.CommonCache
import me.jessyan.mvparms.demo.mvp.model.api.service.WanAndroidService
import timber.log.Timber


@FragmentScope
class BookListModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), BookListContract.Model {

    override fun getBookListDatas(mType: String, mPage: Int, preEndIndex: Int, evictCache: Boolean): Observable<BookBean> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(DouBanService::class.java)
                .getBook(mType, mPage, preEndIndex))
                .flatMap<BookBean> { listObservable ->
                    mRepositoryManager.obtainCacheService(CommonCache::class.java)
                            .getBook(listObservable,
                                    DynamicKey(mPage)
                                    , EvictDynamicKey(evictCache))
//                            .map { listReply -> listReply.data }
                            .map { reply: Reply<BookBean> ->
                                reply.data.apply {
                                    when (reply.source) {
                                        Source.CLOUD -> {
                                            Timber.i("數據來自網諾")
                                        }
                                        Source.MEMORY -> {
                                            Timber.i("數據來自內存")
                                        }
                                        Source.PERSISTENCE -> {
                                            Timber.i("數據來自磁盤")
                                        }
                                        else -> {
                                        }
                                    }
                                }
                            }

                }
    }

    @Inject
    lateinit var mGson: Gson
    @Inject
    lateinit var mApplication: Application

    override fun onDestroy() {
        super.onDestroy()
    }
}
