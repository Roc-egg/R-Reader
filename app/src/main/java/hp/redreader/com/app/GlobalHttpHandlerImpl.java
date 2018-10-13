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
package hp.redreader.com.app;

import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.jess.arms.http.GlobalHttpHandler;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link GlobalHttpHandler} 的用法
 * <p>
 * Created by JessYan on 04/09/2017 17:06
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class GlobalHttpHandlerImpl implements GlobalHttpHandler {
    private Context context;

    public GlobalHttpHandlerImpl(Context context) {
        this.context = context;
    }

    /**
     * 这里可以先客户端一步拿到每一次 Http 请求的结果, 可以先解析成 Json, 再做一些操作, 如检测到 token 过期后
     * 重新请求 token, 并重新执行请求
     *
     * @param httpResult 服务器返回的结果 (已被框架自动转换为字符串)
     * @param chain      {@link Interceptor.Chain}
     * @param response   {@link Response}
     * @return
     */
    @Override
    public Response onHttpResultResponse(String httpResult, Interceptor.Chain chain, Response response) {
//        if (!TextUtils.isEmpty(httpResult) && RequestInterceptor.isJson(response.body().contentType())) {
//            try {
//                List<User> list = ArmsUtils.obtainAppComponentFromContext(context).gson().fromJson(httpResult, new TypeToken<List<User>>() {
//                }.getType());
//                User user = list.get(0);
//                Timber.w("Result ------> " + user.getLogin() + "    ||   Avatar_url------> " + user.getAvatarUrl());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return response;
//            }
//        }
        Timber.e("httpResult==" + httpResult);
        /* 这里如果发现 token 过期, 可以先请求最新的 token, 然后在拿新的 token 放入 Request 里去重新请求
        注意在这个回调之前已经调用过 proceed(), 所以这里必须自己去建立网络请求, 如使用 Okhttp 使用新的 Request 去请求
        create a new request and modify it accordingly using the new token
        Request newRequest = chain.request().newBuilder().header("token", newToken)
                             .build();
        retry the request

        response.body().close();
        如果使用 Okhttp 将新的请求, 请求成功后, 再将 Okhttp 返回的 Response return 出去即可
        如果不需要返回新的结果, 则直接把参数 response 返回出去即可*/

        /**
         * 获取Cookie
         */
        if (!response.headers("Set-Cookie").isEmpty()) {
            List<String> d = response.headers("Set-Cookie");
            Timber.tag("Set-Cookie").e("------------得到的 cookies:%s", d.toString());

            // 返回cookie
            if (!TextUtils.isEmpty(d.toString())) {

                String oldCookie = SPUtils.getInstance().getString("cookie", "");

                HashMap<String, String> stringStringHashMap = new HashMap<>();

                // 之前存过cookie
                if (!TextUtils.isEmpty(oldCookie)) {
                    String[] substring = oldCookie.split(";");
                    for (String aSubstring : substring) {
                        if (aSubstring.contains("=")) {
                            String[] split = aSubstring.split("=");
                            stringStringHashMap.put(split[0], split[1]);
                        } else {
                            stringStringHashMap.put(aSubstring, "");
                        }
                    }
                }
                String join = StringUtils.join(d, ";");
                String[] split = join.split(";");

                // 存到Map里
                for (String aSplit : split) {
                    String[] split1 = aSplit.split("=");
                    if (split1.length == 2) {
                        stringStringHashMap.put(split1[0], split1[1]);
                    } else {
                        stringStringHashMap.put(split1[0], "");
                    }
                }

                // 取出来
                StringBuilder stringBuilder = new StringBuilder();
                if (stringStringHashMap.size() > 0) {
                    for (String key : stringStringHashMap.keySet()) {
                        stringBuilder.append(key);
                        String value = stringStringHashMap.get(key);
                        if (!TextUtils.isEmpty(value)) {
                            stringBuilder.append("=");
                            stringBuilder.append(value);
                        }
                        stringBuilder.append(";");
                    }
                }

                SPUtils.getInstance().put("cookie", stringBuilder.toString());
                Timber.tag("Set-Cookie").e("------------处理后的 cookies:%s", stringBuilder.toString());
            }
        }


        return response;
    }

    /**
     * 这里可以在请求服务器之前拿到 {@link Request}, 做一些操作比如给 {@link Request} 统一添加 token 或者 header 以及参数加密等操作
     *
     * @param chain   {@link Interceptor.Chain}
     * @param request {@link Request}
     * @return
     */
    @Override
    public Request onHttpRequestBefore(Interceptor.Chain chain, Request request) {
        /* 如果需要再请求服务器之前做一些操作, 则重新返回一个做过操作的的 Request 如增加 Header, 不做操作则直接返回参数 request*/

//        Request.Builder requestBuilder = request.newBuilder();
//        //读取请求的body数据（此处为提交的json数据，key Value键值对是其他方法）
//        String postBodyString = bodyToString(request.body());
//
//        if (TextUtils.isEmpty(postBodyString)) {
//            //没有body时添加公共参数
//            postBodyString = "{\"opSource\":\"3\",\"version\":\"" + BuildConfig.VERSION_NAME + "\"}";
//        } else {
//            //有body时添加公共参数
//            try {
//                JSONObject jsonObject = new JSONObject(postBodyString);
//
//                jsonObject.put("opSource", "3");
//
//                jsonObject.put("version", BuildConfig.VERSION_NAME);
//                postBodyString = jsonObject.toString();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        //获取body的MediaType
//        MediaType mediaType;
//        if (!ArmsUtils.isEmpty(request.body())){
//             mediaType = request.body().contentType();
//            if (ArmsUtils.isEmpty(mediaType)){
//                mediaType=MediaType.parse("application/json");
//            }
//        }else {
//            mediaType=MediaType.parse("application/json");
//        }
//        /**
//         * 1.没有MediaType手动添加，不添加则不能正常请求
//         * 2.重新添加请求体
//         */
//        requestBuilder.post(RequestBody.create(mediaType, postBodyString));
//        //header添加token
//        if (!ArmsUtils.isEmpty(DataHelper.getStringSF(context, "token")))
//            requestBuilder.header("token", DataHelper.getStringSF(context, "token"));
//        request = requestBuilder.build();
//
//        return request;
        return request.newBuilder().addHeader("Cookie",SPUtils.getInstance().getString("cookie", "")).build();
//        return request;
    }

    /**
     * 解析body数据
     *
     * @param request
     * @return
     */
    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
