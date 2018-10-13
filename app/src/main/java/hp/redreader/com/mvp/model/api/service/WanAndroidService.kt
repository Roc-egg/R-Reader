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
package me.jessyan.mvparms.demo.mvp.model.api.service

import hp.redreader.com.mvp.model.entity.wanandroid.HomeListBean
import hp.redreader.com.mvp.model.entity.wanandroid.LoginBean
import hp.redreader.com.mvp.model.entity.wanandroid.WanAndroidBannerBean
import io.reactivex.Observable
import retrofit2.http.*


/**
 * ================================================
 * 存放通用的一些 API
 *
 *
 * Created by JessYan on 08/05/2016 12:05
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface WanAndroidService {
    /**
     * 玩安卓轮播图
     */
    @GET("banner/json")
    fun getWanAndroidBanner(): Observable<WanAndroidBannerBean>

    /**
     * 玩安卓，文章列表、知识体系下的文章
     *
     * @param page 页码，从0开始
     * @param cid  体系id
     */
    @GET("/article/list/{page}/json")
    fun getHomeList(@Path("page") page: Int, @Query("cid") cid: Int?): Observable<HomeListBean>


    /**
     * 收藏本站文章，errorCode返回0为成功
     *
     * @param id 文章id
     */
    @POST("lg/collect/{id}/json")
    fun collect(@Path("id") id: Int?): Observable<HomeListBean>

    /**
     * 取消收藏(首页文章列表)
     *
     * @param id 文章id
     */
    @POST("lg/uncollect_originId/{id}/json")
    fun unCollectOrigin(@Path("id") id: Int?): Observable<HomeListBean>

    /**
     * 取消收藏，我的收藏页面（该页面包含自己录入的内容）
     *
     * @param id       文章id
     * @param originId 列表页下发，无则为-1
     * (代表的是你收藏之前的那篇文章本身的id；
     * 但是收藏支持主动添加，这种情况下，没有originId则为-1)
     */
    @FormUrlEncoded
    @POST("lg/uncollect/{id}/json")
    fun unCollect(@Path("id") id: Int?, @Field("originId") originId: Int?): Observable<HomeListBean>

    /**
     * 收藏文章列表
     *
     * @param page 页码
     */
    @GET("lg/collect/list/{page}/json")
    fun getCollectList(@Path("page") page: Int): Observable<HomeListBean>

    /**
     * 玩安卓登录
     *
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("user/login")
    fun login(@Field("username") username: String?, @Field("password") password: String?): Observable<LoginBean>

    /**
     * 玩安卓注册
     */
    @FormUrlEncoded
    @POST("user/register")
    fun register(@Field("username") username: String?, @Field("password") password: String?, @Field("repassword") repassword: String?): Observable<LoginBean>

}
