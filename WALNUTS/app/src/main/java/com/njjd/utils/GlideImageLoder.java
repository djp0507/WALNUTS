package com.njjd.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.njjd.http.HttpManager;
import com.njjd.walnuts.R;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by mrwim on 17/7/26.
 */

public class GlideImageLoder extends ImageLoader {
    private static GlideImageLoder instance;

    @Override
    public void displayImage(final Context context, Object path, ImageView imageView) {
        Glide.with(context).load(path.toString().replace("\\","/")).centerCrop()
                .thumbnail(0.5f)
                .placeholder(R.drawable.error)
                .error(R.drawable.error)
                .dontAnimate().into(imageView);
    }

    //获取单例
    public static GlideImageLoder getInstance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new GlideImageLoder();
                }
            }
        }
        return instance;
    }
}
