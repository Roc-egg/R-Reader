package hp.redreader.com.mvp.ui.activity.movie;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.jess.arms.utils.ArmsUtils;

import java.util.ArrayList;
import java.util.List;

import hp.redreader.com.R;
import hp.redreader.com.app.MyImageConfigImpl;
import hp.redreader.com.app.utils.RxPermissionsUtlis;
import hp.redreader.com.app.utils.RxSaveImage;
import timber.log.Timber;

import static me.jessyan.mvparms.demo.app.ExtKt.isNetWork;


/**
 * 用于查看大图
 *
 * @author jingbin
 */
public class ViewBigImageActivity extends FragmentActivity implements OnPageChangeListener, OnPhotoTapListener {

    /**
     * 保存图片
     */
    private TextView tvSaveBigImage;
    /**
     * 用于管理图片的滑动
     */
    private ViewPager veryImageViewpager;
    /**
     * 显示当前图片的页数
     */
    private TextView veryImageViewpagerText;
    /**
     * 接收传过来的uri地址
     */
    private List<String> imageList;
    /**
     * 接收穿过来当前选择的图片的数量
     */
    int code;
    /**
     * 用于判断是头像还是文章图片 1:头像 2：文章大图
     */
    int selet;
    /**
     * 当前页数
     */
    private int page;
    /**
     * 用于判断是否是加载本地图片
     */
    private boolean isLocal;
    /**
     * 本应用图片的id
     */
    private int imageId;
    /**
     * 是否是本应用中的图片
     */
    private boolean isApp;
    /**
     * 标题
     */
    private ArrayList<String> imageTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_big_image);

        initView();
        getIntentData();
    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            code = bundle.getInt("code");
            selet = bundle.getInt("selet");
            isLocal = bundle.getBoolean("isLocal", false);
            imageList = bundle.getStringArrayList("imageList");
            imageTitles = bundle.getStringArrayList("imageTitles");
            isApp = bundle.getBoolean("isApp", false);
            imageId = bundle.getInt("id", 0);
        }

        if (isApp) {
            // 本地图片
            MyPageAdapter myPageAdapter = new MyPageAdapter();
            veryImageViewpager.setAdapter(myPageAdapter);
            veryImageViewpager.setEnabled(false);
        } else {
            ViewPagerAdapter adapter = new ViewPagerAdapter();
            veryImageViewpager.setAdapter(adapter);
            veryImageViewpager.setCurrentItem(code);
            page = code;
            veryImageViewpager.addOnPageChangeListener(this);
            veryImageViewpager.setEnabled(false);
            // 设定当前的页数和总页数
            if (selet == 2) {
                veryImageViewpagerText.setText((code + 1) + " / " + imageList.size());
            }
        }
    }

    private void initView() {
        veryImageViewpagerText = findViewById(R.id.very_image_viewpager_text);
        tvSaveBigImage = findViewById(R.id.tv_save_big_image);
        veryImageViewpager = findViewById(R.id.very_image_viewpager);

        tvSaveBigImage.setOnClickListener(view -> {
            if (isNetWork()) {
                ToastUtils.showShort("当前网络不可用，请检查你的网络设置");
                return;
            }

            new RxPermissionsUtlis(this, "存储", () -> {
                ToastUtils.showShort("开始下载图片");
                if (isApp) {
                    // 本地图片
                    ToastUtils.showShort("图片已存在");
                } else {
                    // 网络图片
                    RxSaveImage.saveImageToGallery(this, imageList.get(page), imageTitles.get(page));
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        });
    }


    /**
     * 本应用图片适配器
     */
    private class MyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.viewpager_very_image, container, false);
            PhotoView zoomImageView = view.findViewById(R.id.zoom_image_view);
            ProgressBar spinner = view.findViewById(R.id.loading);
            spinner.setVisibility(View.GONE);
            if (imageId != 0) {
                zoomImageView.setImageResource(imageId);
            }
            zoomImageView.setOnPhotoTapListener(ViewBigImageActivity.this);
            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    /**
     * ViewPager的适配器
     */
    private class ViewPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ViewPagerAdapter() {
            inflater = getLayoutInflater();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = inflater.inflate(R.layout.viewpager_very_image, container, false);
            final PhotoView zoomImageView = view.findViewById(R.id.zoom_image_view);
            final ProgressBar spinner = view.findViewById(R.id.loading);
            // 保存网络图片的路径
            String adapterImageEntity = (String) getItem(position);
            String imageUrl;
            if (isLocal) {
                imageUrl = "file://" + adapterImageEntity;
                tvSaveBigImage.setVisibility(View.GONE);
            } else {
                imageUrl = adapterImageEntity;
            }

            spinner.setVisibility(View.VISIBLE);
            spinner.setClickable(false);

            ArmsUtils.obtainAppComponentFromContext(ViewBigImageActivity.this)
                    //itemView 的 Context 就是 Activity, Glide 会自动处理并和该 Activity 的生命周期绑定
                    .imageLoader().loadImage(ViewBigImageActivity.this,
                    MyImageConfigImpl
                            .builder()
//                            .placeholder(R.drawable.stackblur_default)
                            .errorPic(R.drawable.img_default_meizi)
                            .isCrossFade(true, 700)
                            .setListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    ToastUtils.showShort("资源加载异常");
                                    spinner.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                    spinner.setVisibility(View.GONE);

                                    /**这里应该是加载成功后图片的高*/
                                    int height = zoomImageView.getHeight();

                                    int wHeight = getWindowManager().getDefaultDisplay().getHeight();
                                    if (height > wHeight) {
                                        zoomImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    } else {
                                        zoomImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    }


                                    return false;
                                }
                            })
                            .url(imageUrl)
                            .imageView(zoomImageView)
                            .build());


            zoomImageView.setOnPhotoTapListener(ViewBigImageActivity.this);
            container.addView(view, 0);
            return view;
        }

        @Override
        public int getCount() {
            if (imageList == null || imageList.size() == 0) {
                return 0;
            }
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        private Object getItem(int position) {
            return imageList.get(position);
        }
    }

    /**
     * 下面是对Viewpager的监听
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    /**
     * 本方法主要监听viewpager滑动的时候的操作
     * 每当页数发生改变时重新设定一遍当前的页数和总页数
     */
    @Override
    public void onPageSelected(int arg0) {
        veryImageViewpagerText.setText((arg0 + 1) + " / " + imageList.size());
        page = arg0;
    }

    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Timber.e("hasCapture==" + hasCapture);
    }

    @Override
    protected void onDestroy() {
        if (imageList != null) {
            imageList.clear();
            imageList = null;
        }
        if (imageTitles != null) {
            imageTitles.clear();
            imageTitles = null;
        }
        super.onDestroy();
    }

    /**
     * selet： 是什么类型的图片 2：大图显示当前页数，1：头像，不显示页数
     *
     * @param position    大图的话是第几张图片 从0开始
     * @param imageList   图片集合
     * @param imageTitles 图片标题集合
     */
    public static void startImageList(Context context, int position, ArrayList<String> imageList, ArrayList<String> imageTitles) {
        Bundle bundle = new Bundle();
        bundle.putInt("selet", 2);
        bundle.putInt("code", position);
        bundle.putStringArrayList("imageList", imageList);
        bundle.putStringArrayList("imageTitles", imageTitles);
        Intent intent = new Intent(context, ViewBigImageActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 查看头像/单张图片
     */
    public static void start(Context context, String imageUrl, String imageTitle) {
        ArrayList<String> imageList = new ArrayList<>();
        ArrayList<String> imageTitles = new ArrayList<>();
        imageList.add(imageUrl);
        imageTitles.add(imageUrl);
        Bundle bundle = new Bundle();
        bundle.putInt("selet", 1);
        bundle.putInt("code", 0);
        bundle.putStringArrayList("imageList", imageList);
        bundle.putStringArrayList("imageTitles", imageTitles);
        Intent intent = new Intent(context, ViewBigImageActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
