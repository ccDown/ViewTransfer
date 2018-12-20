package com.soullistener.viewtransfer.utils;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * @author kuan
 * Created on 2018/12/19.
 * @description
 */
public class ViewUnitConventUtils {
    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dpValue) {
        return dp2px(Utils.getApp(), dpValue);
    }

    /**
     * Value of dp to value of px.
     *
     * @param context The context.
     * @param dpValue The value of dp.
     * @return value of px
     */
    public static int dp2px(@NonNull final Context context, final float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Value of px to value of dp.
     *
     * @param pxValue The value of px.
     * @return value of dp
     */
    public static int px2dp(final float pxValue) {
        return px2dp(Utils.getApp(), pxValue);
    }

    /**
     * Value of px to value of dp.
     *
     * @param context The context.
     * @param pxValue The value of px.
     * @return value of dp
     */
    public static int px2dp(@NonNull final Context context, final float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Value of sp to value of px.
     *
     * @param spValue The value of sp.
     * @return value of px
     */
    public static int sp2px(final float spValue) {
        return sp2px(Utils.getApp(), spValue);
    }

    /**
     * Value of sp to value of px.
     *
     * @param context The context.
     * @param spValue The value of sp.
     * @return value of px
     */
    public static int sp2px(@NonNull final Context context, final float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * Value of px to value of sp.
     *
     * @param pxValue The value of px.
     * @return value of sp
     */
    public static int px2sp(final float pxValue) {
        return px2sp(Utils.getApp(), pxValue);
    }

    /**
     * Value of px to value of sp.
     *
     * @param context The context.
     * @param pxValue The value of px.
     * @return value of sp
     */
    public static int px2sp(@NonNull final Context context, final float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
