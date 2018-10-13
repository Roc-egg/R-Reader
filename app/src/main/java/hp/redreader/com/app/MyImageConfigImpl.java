package hp.redreader.com.app;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.jess.arms.http.imageloader.BaseImageLoaderStrategy;
import com.jess.arms.http.imageloader.ImageConfig;
import com.jess.arms.http.imageloader.ImageLoader;

/**
 * 类名：    RedReader
 * 类描述：
 * 创建人：  hp
 * 创建时间：2018/9/24/024 3:05
 * 修改人：  hp
 * 修改时间：2018/9/24/024 3:05
 * 修改备注：
 */

/**
 * ================================================
 * 这里存放图片请求的配置信息,可以一直扩展字段,如果外部调用时想让图片加载框架
 * 做一些操作,比如清除缓存或者切换缓存策略,则可以定义一个 int 类型的变量,内部根据 switch(int) 做不同的操作
 * 其他操作同理
 * <p>
 * Created by JessYan on 8/5/16 15:19
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class MyImageConfigImpl extends ImageConfig {
    private int cacheStrategy;//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
    private int fallback; //请求 url 为空,则使用此图片作为占位符
    private int imageRadius;//图片每个圆角的大小
    private int blurValue;//高斯模糊值, 值越大模糊效果越大
    private int mSampling;//缩放比例值
    /**
     * @see {@link Builder#transformation(BitmapTransformation)}
     */
    @Deprecated
    private BitmapTransformation transformation;//glide用它来改变图形的形状
    private ImageView[] imageViews;
    private boolean isCrossFade;//是否使用淡入淡出过渡动画
    private int crossFade;//淡入淡出过渡动画时间
    private boolean isCenterCrop;//是否将图片剪切为 CenterCrop
    private boolean isCircle;//是否将图片剪切为圆形
    private boolean isClearMemory;//清理内存缓存
    private boolean isClearDiskCache;//清理本地缓存

    private int resizeX;//宽
    private int resizeY;//高

    private RequestListener listener;//图片加载监听

    private MyImageConfigImpl(Builder builder) {
        this.url = builder.url;
        this.imageView = builder.imageView;
        this.placeholder = builder.placeholder;
        this.errorPic = builder.errorPic;
        this.fallback = builder.fallback;
        this.cacheStrategy = builder.cacheStrategy;
        this.imageRadius = builder.imageRadius;
        this.blurValue = builder.blurValue;
        this.mSampling = builder.mSampling;
        this.transformation = builder.transformation;
        this.imageViews = builder.imageViews;
        this.isCrossFade = builder.isCrossFade;
        this.crossFade = builder.crossFade;
        this.isCenterCrop = builder.isCenterCrop;
        this.isCircle = builder.isCircle;
        this.isClearMemory = builder.isClearMemory;
        this.isClearDiskCache = builder.isClearDiskCache;
        this.resizeX = builder.resizeX;
        this.resizeY = builder.resizeY;
        this.listener = builder.listener;
    }

    public int getCacheStrategy() {
        return cacheStrategy;
    }

    public BitmapTransformation getTransformation() {
        return transformation;
    }

    public ImageView[] getImageViews() {
        return imageViews;
    }

    public boolean isClearMemory() {
        return isClearMemory;
    }

    public boolean isClearDiskCache() {
        return isClearDiskCache;
    }

    public int getFallback() {
        return fallback;
    }

    public int getBlurValue() {
        return blurValue;
    }

    public int getSampling() {
        return mSampling;
    }

    public int getResizeX() {
        return resizeX;
    }

    public int getResizeY() {
        return resizeY;
    }

    public RequestListener getListener() {
        return listener;
    }

    public boolean isBlurImage() {
        return blurValue > 0;
    }

    public int getImageRadius() {
        return imageRadius;
    }

    public boolean isImageRadius() {
        return imageRadius > 0;
    }

    public boolean isCrossFade() {
        return isCrossFade;
    }

    public int getCrossFade() {
        return crossFade;
    }

    public boolean isCenterCrop() {
        return isCenterCrop;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private String url;
        private ImageView imageView;
        private int placeholder;
        private int errorPic;
        private int fallback; //请求 url 为空,则使用此图片作为占位符
        private int cacheStrategy;//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
        private int imageRadius;//图片每个圆角的大小
        private int blurValue;//高斯模糊值, 值越大模糊效果越大
        private int mSampling;//缩放比例值
        /**
         * @see {@link Builder#transformation(BitmapTransformation)}
         */
        @Deprecated
        private BitmapTransformation transformation;//glide用它来改变图形的形状
        private ImageView[] imageViews;
        private boolean isCrossFade;//是否使用淡入淡出过渡动画
        private int crossFade;//淡入淡出过渡动画时间
        private boolean isCenterCrop;//是否将图片剪切为 CenterCrop
        private boolean isCircle;//是否将图片剪切为圆形
        private boolean isClearMemory;//清理内存缓存
        private boolean isClearDiskCache;//清理本地缓存

        private int resizeX;//宽
        private int resizeY;//高
        private RequestListener listener;//图片加载监听

        private Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder placeholder(int placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder errorPic(int errorPic) {
            this.errorPic = errorPic;
            return this;
        }

        public Builder fallback(int fallback) {
            this.fallback = fallback;
            return this;
        }

        public Builder imageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder cacheStrategy(int cacheStrategy) {
            this.cacheStrategy = cacheStrategy;
            return this;
        }

        public Builder imageRadius(int imageRadius) {
            this.imageRadius = imageRadius;
            return this;
        }

        /**
         * 高斯模糊
         *
         * @param blurValue 默认为 25
         * @return
         */
        public Builder blurValue(int blurValue) {
            this.blurValue = blurValue;
            return this;
        }

        /**
         * 高斯模糊 + 缩放
         *
         * @param blurValue 默认为 25
         * @param mSampling 默认为 1
         * @return
         */
        public Builder blurValue(int blurValue, int mSampling) { //blurValue 建议设置为 25
            this.blurValue = blurValue;
            this.mSampling = mSampling;
            return this;
        }


        /**
         * 给图片添加 Glide 独有的 BitmapTransformation
         * <p>
         * 因为 BitmapTransformation 是 Glide 独有的类, 所以如果 BitmapTransformation 出现在 {@link MyImageConfigImpl} 中
         * 会使 {@link ImageLoader} 难以切换为其他图片加载框架, 在 {@link MyImageConfigImpl} 中只能配置基础类型和 Android 包里的类
         * 此 API 会在后面的版本中被删除, 请使用其他 API 替代
         *
         * @param transformation {@link BitmapTransformation}
         * @deprecated 请使用 {@link #isCircle()}, {@link #isCenterCrop()}, {@link #isImageRadius()} 替代
         * 如果有其他自定义 BitmapTransformation 的需求, 请自行扩展 {@link BaseImageLoaderStrategy}
         */
        @Deprecated
        public Builder transformation(BitmapTransformation transformation) {
            this.transformation = transformation;
            return this;
        }

        public Builder imageViews(ImageView... imageViews) {
            this.imageViews = imageViews;
            return this;
        }

        /**
         * 设置是否淡入淡出动画和时间
         *
         * @param isCrossFade
         * @param crossFade
         * @return
         */
        public Builder isCrossFade(boolean isCrossFade, int crossFade) {
            this.isCrossFade = isCrossFade;
            this.crossFade = crossFade;
            return this;
        }

        public Builder isCenterCrop(boolean isCenterCrop) {
            this.isCenterCrop = isCenterCrop;
            return this;
        }

        public Builder isCircle(boolean isCircle) {
            this.isCircle = isCircle;
            return this;
        }

        public Builder isClearMemory(boolean isClearMemory) {
            this.isClearMemory = isClearMemory;
            return this;
        }

        public Builder isClearDiskCache(boolean isClearDiskCache) {
            this.isClearDiskCache = isClearDiskCache;
            return this;
        }

        /**
         * 设置图片宽高
         *
         * @param resizeX
         * @param resizeY
         * @return
         */
        public Builder resize(int resizeX, int resizeY) {
            this.resizeX = resizeX;
            this.resizeY = resizeY;
            return this;
        }

        /**
         * 设置加载图片监听
         *
         * @param listener
         * @return
         */
        public Builder setListener(RequestListener listener) {
            this.listener = listener;
            return this;
        }


        public MyImageConfigImpl build() {
            return new MyImageConfigImpl(this);
        }
    }
}

