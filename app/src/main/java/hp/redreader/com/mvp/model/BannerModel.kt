package hp.redreader.com.mvp.model

import android.app.Application
import com.google.gson.Gson
import com.jess.arms.integration.IRepositoryManager
import com.jess.arms.mvp.BaseModel

import com.jess.arms.di.scope.FragmentScope
import javax.inject.Inject

import hp.redreader.com.mvp.contract.BannerContract
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.model.entity.wanandroid.WanAndroidBannerBean
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictDynamicKey
import io.rx_cache2.Reply
import io.rx_cache2.Source
import me.jessyan.mvparms.demo.mvp.model.api.cache.CommonCache
import me.jessyan.mvparms.demo.mvp.model.api.service.WanAndroidService
import timber.log.Timber


@FragmentScope
class BannerModel
@Inject
constructor(repositoryManager: IRepositoryManager) : BaseModel(repositoryManager), BannerContract.Model {
    override fun getCollectList(mPage: Int, evictCache: Boolean): Observable<HomeListBean> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(WanAndroidService::class.java)
                .getCollectList(mPage))
                .flatMap<HomeListBean> { listObservable ->
                    mRepositoryManager.obtainCacheService(CommonCache::class.java)
                            .getCollectList(listObservable,
                                    DynamicKey(mPage)//RxCache2的使用
                                    , EvictDynamicKey(evictCache))
//                            .map { listReply -> listReply.data }
                            .map { reply: Reply<HomeListBean> ->
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

    override fun collect(id: Int?): Observable<HomeListBean> {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService::class.java).collect(id)
    }

    override fun unCollect(id: Int?, originId: Int?): Observable<HomeListBean> {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService::class.java).unCollect(id, originId)
    }

    override fun unCollectOrigin(id: Int?): Observable<HomeListBean> {
        return mRepositoryManager.obtainRetrofitService(WanAndroidService::class.java).unCollectOrigin(id)
    }

    override fun getWanAndroidBanner(evictCache: Boolean): Observable<WanAndroidBannerBean> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(WanAndroidService::class.java)
                .getWanAndroidBanner())
                .flatMap<WanAndroidBannerBean> { listObservable ->
                    mRepositoryManager.obtainCacheService(CommonCache::class.java)
                            .getWanAndroidBanner(listObservable,
                                    DynamicKey(0),
                                    EvictDynamicKey(evictCache))
                            .map { listReply -> listReply.data }
                }
    }

    override fun getHomeList(mPage: Int, cid: Int?, evictCache: Boolean): Observable<HomeListBean> {
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(WanAndroidService::class.java)
                .getHomeList(mPage, cid))
                .flatMap<HomeListBean> { listObservable ->
                    mRepositoryManager.obtainCacheService(CommonCache::class.java)
                            .getHomeList(listObservable,
                                    DynamicKey(cid.toString() + "_" + mPage)//RxCache2的使用
                                    , EvictDynamicKey(evictCache))
//                            .map { listReply -> listReply.data }
                            .map { reply: Reply<HomeListBean> ->
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
