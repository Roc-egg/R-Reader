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


import hp.redreader.com.mvp.model.api.Api.*
import hp.redreader.com.mvp.model.entity.book.BookBean
import hp.redreader.com.mvp.model.entity.book.BookDetailBean
import hp.redreader.com.mvp.model.entity.gank.GankIoDataBean
import hp.redreader.com.mvp.model.entity.gank.GankIoDayBean
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
interface GankService {
    /**
     * 如果不需要多个 BaseUrl, 继续使用初始化时传入 Retrofit 中的默认 BaseUrl, 就不要加上 DOMAIN_NAME_HEADER 这个 Header
     * 也可以通过在注解里给全路径达到使用不同的 BaseUrl, 但是这样无法在 App 运行时动态切换 BaseUrl
     */
    @Headers(DOMAIN_NAME_HEADER + GANK_DOMAIN_NAME)
    /**
     * 分类数据: http://gank.io/api/data/数据类型/请求个数/第几页
     * 数据类型： 福利 | Android | iOS | 休息视频 | 拓展资源 | 前端 | all
     * 请求个数： 数字，大于0
     * 第几页：数字，大于0
     * eg: http://gank.io/api/data/Android/10/1
     */
    @GET("data/{type}/{pre_page}/{page}")
    fun getGankIoData(@Path("type") id: String, @Path("page") page: Int, @Path("pre_page") pre_page: Int): Observable<GankIoDataBean>

    @Headers(DOMAIN_NAME_HEADER + GANK_DOMAIN_NAME)
    /**
     * 每日数据： http://gank.io/api/day/年/月/日
     * eg:http://gank.io/api/day/2015/08/06
     */
    @GET("day/{year}/{month}/{day}")
    fun getGankIoDay(@Path("year") year: String, @Path("month") month: String, @Path("day") day: String): Observable<GankIoDayBean>

}
