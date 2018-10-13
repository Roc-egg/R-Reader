/*
 * Copyright (C) 2015 Drakeet <drakeet.me@gmail.com>
 *
 * This file is part of Meizhi
 *
 * Meizhi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Meizhi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Meizhi.  If not, see <http://www.gnu.org/licenses/>.
 */

package hp.redreader.com.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.jess.arms.utils.ArmsUtils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hp.redreader.com.R;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * 保存图片，重复插入图片提示已存在
 *
 * @author jingbin
 */
public class RxSaveImage {

    private static Observable<Uri> saveImageAndGetPathObservable(Activity context, String url, String title) {
        return Observable.unsafeCreate(new ObservableSource<Bitmap>() {
            @Override
            public void subscribe(Observer<? super Bitmap> observer) {
                // 检查路径
                if (TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
//                    observer.onError(new Exception("请检查图片路径"));
                    ToastUtils.showLong("请检查图片路径");
                }
                // 检查图片是否已存在
                File appDir = new File(Environment.getExternalStorageDirectory(), "R-阅相册");
                if (appDir.exists()) {
                    String fileName = title.replace('/', '-') + ".jpg";
                    File file = new File(appDir, fileName);
                    if (file.exists()) {
//                        observer.onError(new Exception("图片已存在"));
                        ToastUtils.showLong("图片已存在");
                    }
                }
                // 获得Bitmap
                Bitmap bitmap = null;
                try {
                    bitmap = Glide.with(context)
                            .asBitmap()
                            .load(url)
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();

                } catch (Exception e) {
                    observer.onError(e);
                }
                if (bitmap == null) {
//                    observer.onError(new Exception("无法下载到图片"));
                    ToastUtils.showLong("无法下载到图片");
                }
                observer.onNext(bitmap);
                observer.onComplete();
            }
//            @Override
//            public void call(Subscriber<? super Bitmap> subscriber) {
//                // 检查路径
//                if (TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
//                    subscriber.onError(new Exception("请检查图片路径"));
//                }
//                // 检查图片是否已存在
//                File appDir = new File(Environment.getExternalStorageDirectory(), "云阅相册");
//                if (appDir.exists()) {
//                    String fileName = title.replace('/', '-') + ".jpg";
//                    File file = new File(appDir, fileName);
//                    if (file.exists()) {
//                        subscriber.onError(new Exception("图片已存在"));
//                    }
//                }
//                // 获得Bitmap
//                Bitmap bitmap = null;
//                try {
//                    bitmap = Glide.with(context)
//                            .load(url)
//                            .asBitmap()
//                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                            .get();
//
//                } catch (Exception e) {
//                    subscriber.onError(e);
//                }
//                if (bitmap == null) {
//                    subscriber.onError(new Exception("无法下载到图片"));
//                }
//                subscriber.onNext(bitmap);
//                subscriber.onCompleted();
//            }
        }).flatMap(bitmap -> {
            File appDir = new File(Environment.getExternalStorageDirectory(), "R-阅相册");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = title.replace('/', '-') + ".jpg";
            File file = new File(appDir, fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri uri = Uri.fromFile(file);
            // 通知图库更新
            Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            context.sendBroadcast(scannerIntent);
            return Observable.just(uri);
        }).subscribeOn(Schedulers.io());
    }


    public static void saveImageToGallery(Activity context, String mImageUrl, String mImageTitle) {
        // @formatter:off
         RxSaveImage.saveImageAndGetPathObservable(context, mImageUrl, mImageTitle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uri -> {
                    File appDir = new File(Environment.getExternalStorageDirectory(), "R-阅相册");
                    String msg = String.format(ArmsUtils.getString(context,R.string.picture_has_save_to),
                            appDir.getAbsolutePath());
                    ToastUtils.showLong(msg);
                }, error -> ToastUtils.showLong(error.getMessage()));
        // @formatter:on
//        addSubscription(s);
    }

}
