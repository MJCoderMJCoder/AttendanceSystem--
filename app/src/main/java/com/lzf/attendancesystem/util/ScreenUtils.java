package com.lzf.attendancesystem.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * 手机屏幕工具类：可用于多机型适配
 *
 * @author MJCoder
 */
public class ScreenUtils {
    /**
     * 手机屏幕的宽（widthPixels-像素）
     */
    private static int screenW;
    /**
     * 手机屏幕的高（heightPixels-像素）
     */
    private static int screenH;
    /**
     * 手机屏幕的密度/分辨率
     */
    private static float screenDensity;

    /**
     * 获取手机屏幕的宽（widthPixels-像素）
     *
     * @param context 环境/上下文
     * @return 手机屏幕的宽（widthPixels-像素）
     */
    public static int getScreenW(Context context) {
        if (screenW == 0) {
            initScreen(context);
        }
        return screenW;
    }

    /**
     * 获取手机屏幕的高（heightPixels-像素）
     *
     * @param context 环境/上下文
     * @return 手机屏幕的高（heightPixels-像素）
     */
    public static int getScreenH(Context context) {
        if (screenH == 0) {
            initScreen(context);
        }
        return screenH;
    }

    /**
     * 获取手机屏幕的密度/分辨率
     *
     * @param context 环境/上下文
     * @return 手机屏幕的密度/分辨率
     */
    public static float getScreenDensity(Context context) {
        if (screenDensity == 0) {
            initScreen(context);
        }
        return screenDensity;
    }

    /**
     * 初始化屏幕信息；并初始化赋值屏幕的宽（widthPixels-像素）、高（heightPixels-像素）、密度/分辨率
     *
     * @param context 环境/上下文
     */
    private static void initScreen(Context context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        screenW = metric.widthPixels;
        screenH = metric.heightPixels;
        screenDensity = metric.density;
    }

    /**
     * 根据手机的密度/分辨率将以 dp 为单位的值转换为以 px-像素 为单位的值
     *
     * @param context 环境/上下文
     * @param dpValue 以 dp 为单位的值
     * @return 以 px-像素 为单位的值
     */
    public static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * getScreenDensity(context) + 0.5f);
    }

    /**
     * 根据手机的密度/分辨率将以 px-像素 为单位的值转换为以 dp 为单位的值
     *
     * @param context 环境/上下文
     * @param pxValue 以 px-像素 为单位的值
     * @return 以 dp 为单位的值
     */
    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / getScreenDensity(context) + 0.5f);
    }

    /**
     * 根据手机的密度/分辨率将以 sp 为单位的值转换为以 px-像素 为单位的值
     *
     * @param context 环境/上下文
     * @param spValue 以 sp 为单位的值
     * @return 以 px-像素 为单位的值
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 计算状态栏高度
     *
     * @param activity 当前界面的Activity
     * @return 状态栏高度
     * @see Activity
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }
}
