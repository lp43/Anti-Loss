package com.funtrigger.followdroid;

import android.content.Context;
import android.content.Intent;
import com.funtrigger.followdroid.R;

/**
 * 用來簡易的開關Service
 * @author simon
 *
 */
public class SwitchService {

	/**
	 * 啟動指定的Service
	 * @param context 呼叫Service的主體
	 * @param c 欲啟動的Service名稱，如FallDetector.class
	 */
	public static void startService(Context context,Class<?> c){
		Intent intent = new Intent();
		intent.setClass(context,c);
		context.startService(intent);
	}
	
	/**
	 * 關閉指定的Service
	 * @param context 呼叫Service的主體
	 * @param c 欲停止的Service名稱，如FallDetector.class
	 */
	public static void stopService(Context context,Class<?> c){
		Intent intent = new Intent();
		intent.setClass(context, c);
		context.stopService(intent);
	}
}
