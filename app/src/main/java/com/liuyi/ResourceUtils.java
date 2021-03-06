package com.liuyi;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.liuyi.loadingview.R;

import java.lang.reflect.Field;

public class ResourceUtils {

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @return
     */
    public static int getResourceId(Context ctx, String imageName) {
        int resId = ctx.getResources().getIdentifier(imageName, "drawable", ctx.getPackageName());
        return resId;
    }

    /**
     * 获取图片名称获取图片的资源id的方法
     *
     * @param imageName
     * @param defaultId 获取失败时默认的资源id
     * @return
     */
    public static int getResourceIdByReflect(String imageName, int defaultId) {
        Class drawable = R.drawable.class;
        Field field = null;
        int r_id;
        try {
            field = drawable.getField(imageName);
            r_id = field.getInt(field.getName());
        } catch (Exception e) {
//            r_id = R.drawable.b_nothing;
            r_id = defaultId;
            Log.e("ERROR", "PICTURE NOT　FOUND！");
        }
        return r_id;
    }


    /**
     * 根据资源id获取文件名
     *
     * @param context
     * @param resid
     * @return
     */
    public static String getResourceName(Context context, int resid) {
        return context.getResources().getResourceName(resid);
    }

    public static int getScreenWidth(Context context) {
//        WindowManager manager = context.getWindowManager();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        manager.getDefaultDisplay().getMetrics(outMetrics);
//        int width = outMetrics.widthPixels;
//        int height = outMetrics.heightPixels;
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return width;
    }

    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return height;
    }

}