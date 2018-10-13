/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.jessyan.mvparms.demo.mvp.model.api.cache

import hp.redreader.com.mvp.model.entity.book.BookBean
import hp.redreader.com.mvp.model.entity.book.BookDetailBean
import hp.redreader.com.mvp.model.entity.gank.FrontpageBean
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.model.entity.gank.GankIoDayBean
import hp.redreader.com.mvp.model.entity.movie.HotMovieBean
import hp.redreader.com.mvp.model.entity.movie.MovieDetailBean
import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.model.entity.wanandroid.QsbkListBean
import hp.redreader.com.mvp.model.entity.wanandroid.WanAndroidBannerBean
import io.reactivex.Observable
import io.rx_cache2.DynamicKey
import io.rx_cache2.EvictProvider
import io.rx_cache2.LifeCache
import io.rx_cache2.Reply
import io.rx_cache2.internal.RxCache
import java.util.concurrent.TimeUnit

/**
 * ================================================
 * 展示 [RxCache.using] 中需要传入的 Providers 的使用方式
 *
 *
 * Created by JessYan on 08/30/2016 13:53
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface CommonCache {
    companion object {
        /**
         * 緩存有效時間  30分鐘
         */
        const val CACHE_LONG = 30L
    }

    @LifeCache(duration = CACHE_LONG, timeUnit = TimeUnit.MINUTES)
    fun getHomeList(newAtlasList: Observable<HomeListBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<HomeListBean>>

    @LifeCache(duration = CACHE_LONG, timeUnit = TimeUnit.MINUTES)
    fun getCollectList(newAtlasList: Observable<HomeListBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<HomeListBean>>

    @LifeCache(duration = CACHE_LONG, timeUnit = TimeUnit.MINUTES)
    fun getWanAndroidBanner(newAtlasList: Observable<WanAndroidBannerBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<WanAndroidBannerBean>>

    @LifeCache(duration = CACHE_LONG, timeUnit = TimeUnit.MINUTES)
    fun getBook(newAtlasList: Observable<BookBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<BookBean>>

    @LifeCache(duration = CACHE_LONG, timeUnit = TimeUnit.MINUTES)
    fun getQsbkList(newAtlasList: Observable<QsbkListBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<QsbkListBean>>

    @LifeCache(duration = 1L, timeUnit = TimeUnit.DAYS)
    fun getHotMovie(newAtlasList: Observable<HotMovieBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<HotMovieBean>>

    @LifeCache(duration = CACHE_LONG, timeUnit = TimeUnit.MINUTES)
    fun getMovieTop250(newAtlasList: Observable<HotMovieBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<HotMovieBean>>

    @LifeCache(duration = CACHE_LONG, timeUnit = TimeUnit.MINUTES)
    fun getGankIoData(newAtlasList: Observable<GankIoDataBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<GankIoDataBean>>

    @LifeCache(duration = 1L, timeUnit = TimeUnit.DAYS)
    fun getFrontpage(newAtlasList: Observable<FrontpageBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<FrontpageBean>>

    @LifeCache(duration = 1L, timeUnit = TimeUnit.DAYS)
    fun getGankIoDay(newAtlasList: Observable<GankIoDayBean>, idLastUserQueried: DynamicKey, evictProvider: EvictProvider): Observable<Reply<GankIoDayBean>>


}
