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
package hp.redreader.com.mvp.model.api.service


import hp.redreader.com.mvp.model.api.Api.DOUBAN_DOMAIN_NAME
import hp.redreader.com.mvp.model.api.Api.QSBK_DOMAIN_NAME
import hp.redreader.com.mvp.model.entity.book.BookBean
import hp.redreader.com.mvp.model.entity.book.BookDetailBean
import hp.redreader.com.mvp.model.entity.movie.HotMovieBean
import hp.redreader.com.mvp.model.entity.movie.MovieDetailBean
import hp.redreader.com.mvp.model.entity.wanandroid.QsbkListBean
import io.reactivex.Observable
import me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * ================================================
 * 展示 [Retrofit.create] 中需要传入的 ApiService 的使用方式
 * 存放关于用户的一些 API
 *
 *
 * Created by JessYan on 08/05/2016 12:05
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface DouBanService {
    /**
     * 如果不需要多个 BaseUrl, 继续使用初始化时传入 Retrofit 中的默认 BaseUrl, 就不要加上 DOMAIN_NAME_HEADER 这个 Header
     * 也可以通过在注解里给全路径达到使用不同的 BaseUrl, 但是这样无法在 App 运行时动态切换 BaseUrl
     */
    @Headers(DOMAIN_NAME_HEADER + DOUBAN_DOMAIN_NAME)
    /**
     * 根据tag获取图书
     *
     * @param tag   搜索关键字
     * @param count 一次请求的数目 最多100
     */
    @GET("v2/book/search")
    fun getBook(@Query("tag") tag: String, @Query("start") start: Int, @Query("count") count: Int): Observable<BookBean>

    /**
     * 书籍详情
     */
    @Headers(DOMAIN_NAME_HEADER + DOUBAN_DOMAIN_NAME)
    @GET("v2/book/{id}")
    fun getBookDetail(@Path("id") id: String?): Observable<BookDetailBean>

    /**
     * 糗事百科
     *
     * @param page 页码，从1开始
     */
    @Headers(DOMAIN_NAME_HEADER + QSBK_DOMAIN_NAME)
    @GET("article/list/text")
    fun getQsbkList(@Query("page") page: Int): Observable<QsbkListBean>

    /**
     * 豆瓣热映电影，每日更新
     */
    @Headers(DOMAIN_NAME_HEADER + DOUBAN_DOMAIN_NAME)
    @GET("v2/movie/in_theaters")
    fun getHotMovie(): Observable<HotMovieBean>

    /**
     * 获取豆瓣电影top250
     *
     * @param start 从多少开始，如从"0"开始
     * @param count 一次请求的数目，如"10"条，最多100
     */
    @Headers(DOMAIN_NAME_HEADER + DOUBAN_DOMAIN_NAME)
    @GET("v2/movie/top250")
    fun getMovieTop250(@Query("start") start: Int, @Query("count") count: Int): Observable<HotMovieBean>

    /**
     * 获取电影详情
     *
     * @param id 电影bean里的id
     */
    @Headers(DOMAIN_NAME_HEADER + DOUBAN_DOMAIN_NAME)
    @GET("v2/movie/subject/{id}")
    fun getMovieDetail(@Path("id") id: String?): Observable<MovieDetailBean>
}
