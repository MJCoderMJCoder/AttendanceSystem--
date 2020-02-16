package com.lzf.attendancesystem.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Bitmap的处理 解决out of memory的一些问题：
 * <p>
 * 先使用软引用：把图片保存在内存之中 用到时候先去找，找不到再从路径加载
 * <p>
 * 有个独立的释放函数 如果不用了 请记得调用释放函数
 */
public class BitmapUtil {
    private static Map<String, SoftReference<Bitmap>> bitmapCache = new HashMap<String, SoftReference<Bitmap>>();

    public static Bitmap loadBitmap(String path) {
        if (bitmapCache.containsKey(path)) {
            SoftReference<Bitmap> softReference = bitmapCache.get(path);
            Bitmap bitmap = softReference.get();
            if (null != bitmap) {
                return bitmap;
            }
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 3;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        bitmapCache.put(path, new SoftReference<Bitmap>(bitmap));
        return bitmap;
    }

    public static BitmapDrawable decodeImage(String path) {
        return new BitmapDrawable(loadBitmap(path));
    }

    public static void releaseBitmap(String path) {
        if (bitmapCache.containsKey(path)) {
            SoftReference<Bitmap> softReference = bitmapCache.get(path);
            Bitmap bitmap = softReference.get();
            if (null != bitmap) {
                bitmap.recycle();   // 回收bitmap的内存
                bitmap = null;
            }
            bitmapCache.remove(path);
        }
    }

}
