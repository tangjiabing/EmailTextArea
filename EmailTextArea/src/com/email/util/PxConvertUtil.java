package com.email.util;

import android.content.Context;

/**
 * 
 * @author tangjiabing
 * 
 * @see 开源时间：2016年03月31日
 * 
 *      记得给我个star哦~
 * 
 */
public class PxConvertUtil {

	public static int dp2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * scale + 0.5f);
	}

	public static int px2dp(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / scale + 0.5f);
	}

}
