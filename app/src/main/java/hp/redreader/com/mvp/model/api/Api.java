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
package hp.redreader.com.mvp.model.api;

/**
 * ================================================
 * 存放一些与 API 有关的东西,如请求地址,请求码等
 * <p>
 * Created by JessYan on 08/05/2016 11:25
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public interface Api {
    // gankio、豆瓣、（轮播图）
    String APP_DEFAULT_DOMAIN = "http://www.wanandroid.com/";

    String APP_GANK_DOMAIN = "https://gank.io/api/";
    String APP_DOUBAN_DOMAIN = "https://api.douban.com/";
    String API_TING = "https://tingapi.ting.baidu.com/v1/restserver/";
    String API_FIR = "http://api.fir.im/apps/";
    String API_NHDZ = "https://ic.snssdk.com/";
    String API_QSBK = "http://m2.qiushibaike.com/";

    //domain_name
    String GANK_DOMAIN_NAME = "gank";
    String DOUBAN_DOMAIN_NAME = "douban";
    String QSBK_DOMAIN_NAME = "qsbk";
    String TING_DOMAIN_NAME = "ting";
    /**
     * 成功
     */
    int RequestSuccess = 0;
    /**
     * 未登录
     */
    String RequestNotLogin = "200001";
}
