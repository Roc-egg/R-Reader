package hp.redreader.com.app.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.request.RequestListener
import com.jess.arms.utils.ArmsUtils
import hp.redreader.com.R
import hp.redreader.com.app.MyImageConfigImpl


/**
 * 显示随机的图片(每日推荐)
 *
 * @param imgNumber 有几张图片要显示,对应默认图
 * @param imageUrl  显示图片的url
 * @param imageView 对应图片控件
 */
fun displayRandom(imgNumber: Int, imageUrl: String, imageView: ImageView) {

    ArmsUtils.obtainAppComponentFromContext(imageView.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(getMusicDefaultPic(imgNumber))
                            .errorPic(getMusicDefaultPic(imgNumber))
                            .isCrossFade(true, 1500)
                            .url(imageUrl)
                            .imageView(imageView)
                            .build())

}

private fun getMusicDefaultPic(imgNumber: Int): Int {
    when (imgNumber) {
        1 -> return R.drawable.img_two_bi_one
        2 -> return R.drawable.img_four_bi_three
        3 -> return R.drawable.img_one_bi_one
        else -> {
        }
    }
    return R.drawable.img_four_bi_three
}

//--------------------------------------

/**
 * 用于干货item
 */
fun displayGif(url: String, imageView: ImageView) {

    ArmsUtils.obtainAppComponentFromContext(imageView.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(R.drawable.img_one_bi_one)
                            .errorPic(R.drawable.img_one_bi_one)
                            .url(url)
                            .imageView(imageView)
                            .build())

}

/**
 * 书籍、妹子图、电影列表图
 * 默认图区别
 */
fun displayEspImage(url: String?, imageView: ImageView?, type: Int) {

    ArmsUtils.obtainAppComponentFromContext(imageView?.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView?.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(getMusicDefaultPic(type))
                            .errorPic(getMusicDefaultPic(type))
//                            .isCrossFade(true, 500)//和isCenterCrop同时使用会导致变形
                            .isCenterCrop(true)
                            .url(url)
                            .imageView(imageView)
                            .build())
}

private fun getDefaultPic(type: Int): Int {
    when (type) {
        0// 电影
        -> return R.drawable.img_default_movie
        1// 妹子
        -> return R.drawable.img_default_meizi
        2// 书籍
        -> return R.drawable.img_default_book
    }
    return R.drawable.img_default_meizi
}


/**
 * 加载圆角图,暂时用到显示头像
 */
fun displayCircle(imageView: ImageView?, imageUrl: String?) {

    ArmsUtils.obtainAppComponentFromContext(imageView?.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView?.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(R.drawable.ic_avatar_default)
                            .errorPic(R.drawable.ic_avatar_default)
                            .fallback(R.drawable.ic_avatar_default)
//                            .isCrossFade(true, 500)
                            .isCenterCrop(true)
                            .isCircle(true)
                            .url(imageUrl)
                            .imageView(imageView)
                            .build())

}


/**
 * 电影列表图片
 */
fun showMovieImg(imageView: ImageView?, url: String?) {
//    Glide.with(imageView.context)
//            .load(url)
//            .crossFade(500)
//            .override(CommonUtils.getDimens(R.dimen.movie_detail_width) as Int, CommonUtils.getDimens(R.dimen.movie_detail_height) as Int)
//            .placeholder(getDefaultPic(0))
//            .error(getDefaultPic(0))
//            .into(imageView)

    ArmsUtils.obtainAppComponentFromContext(imageView?.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView?.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(getDefaultPic(0))
                            .errorPic(getDefaultPic(0))
                            .resize(ArmsUtils.getDimens(imageView?.context, R.dimen.movie_detail_width),
                                    ArmsUtils.getDimens(imageView?.context, R.dimen.movie_detail_height))
                            .isCrossFade(true, 500)
                            .url(url)
                            .imageView(imageView)
                            .build())

}

/**
 * 书籍列表图片
 */
fun showBookImg(imageView: ImageView?, url: String?) {
//    Glide.with(imageView.context)
//            .load(url)
//            .crossFade(500)
//            .override(CommonUtils.getDimens(R.dimen.book_detail_width) as Int, CommonUtils.getDimens(R.dimen.book_detail_height) as Int)
//            .placeholder(getDefaultPic(2))
//            .error(getDefaultPic(2))
//            .into(imageView)

    ArmsUtils.obtainAppComponentFromContext(imageView!!.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(getDefaultPic(2))
                            .errorPic(getDefaultPic(2))
                            .resize(ArmsUtils.getDimens(imageView.context, R.dimen.book_detail_width),
                                    ArmsUtils.getDimens(imageView.context, R.dimen.book_detail_height))
                            .isCrossFade(true, 500)
                            .url(url)
                            .imageView(imageView)
                            .build())

}

/**
 * 电影详情页显示高斯背景图
 */
fun showImgBg(imageView: ImageView, url: String) {
    // "23":模糊度；"4":图片缩放4倍后再进行模糊

    ArmsUtils.obtainAppComponentFromContext(imageView.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(R.drawable.stackblur_default)
                            .errorPic(R.drawable.stackblur_default)
                            .isCrossFade(true, 500)
                            .blurValue(60, 4)
                            .url(url)
                            .imageView(imageView)
                            .build())
}

/**
 * 电影详情页显示高斯背景图
 *  添加加载监听
 */
fun showImgBg(imageView: ImageView, url: String, listener: RequestListener<Drawable>) {
    // 模糊度60 ；貌似和glide的版本有些区别

    ArmsUtils.obtainAppComponentFromContext(imageView.context)
            //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
            .imageLoader().loadImage(imageView.context,
                    MyImageConfigImpl
                            .builder()
                            .placeholder(R.drawable.stackblur_default)
                            .errorPic(R.drawable.stackblur_default)
//                            .isCrossFade(true, 500)//有坑:用于标题栏设置alpha时，动画执行完会将执行前设置的alpha覆盖。此处标题可以不使用更好 ☺
                            .blurValue(60, 4)
                            .setListener(listener)
                            .url(url)
                            .imageView(imageView)
                            .build())
}