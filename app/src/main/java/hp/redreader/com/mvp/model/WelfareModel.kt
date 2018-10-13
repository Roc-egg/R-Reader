package hp.redreader.com.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import javax.inject.Inject

import hp.redreader.com.mvp.contract.WelfareContract
import hp.redreader.com.mvp.model.api.service.DouBanService
import hp.redreader.com.mvp.model.api.service.GankService
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.Reply
import io.rx_cache2.Source
import me.jessyan.mvparms.demo.mvp.model.api.cache.CommonCache
import timber.log.Timber


@FragmentScope
class WelfareModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), WelfareContract.Model {
    override fun getGankIoData(s: String, mPage: Int, preEndIndex: Int, evictCache: Boolean): Observable<GankIoDataBean> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(GankService::class.java)
                .getGankIoData(s, mPage, preEndIndex))
                .flatMap<GankIoDataBean> { listObservable ->
                    mRepositoryManager.obtainCacheService(CommonCache::class.java)
                            .getGankIoData(listObservable,
                                    DynamicKey(s + "_" + mPage)//同一个接口缓存的区分
                                    , EvictDynamicKey(evictCache))
//                            .map { listReply -> listReply.data }
                            .map { reply: Reply<GankIoDataBean> ->
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
    lateinit var mGson: Gson;
    @Inject
    lateinit var mApplication: Application;

    override fun onDestroy() {
        super.onDestroy();
    }
}
