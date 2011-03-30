package com.funtrigger.followdroid;

import java.util.List;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;
import com.funtrigger.followdroid.R;

public class MySystemManager {

	
	private static String tag="tag";
	public static String now_activity="";
	
	/**
	 * 專門檢查摔落告知的Service是否有開啟,
	 * 預設為false;
	 * @return 若有開啟回傳為true,否則為false
	 */
	public static boolean checkServiceExist(Context context,String checkServiceName){
		boolean return_field=false;
		ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE); 
		List<ActivityManager.RunningServiceInfo> service=activityManager.getRunningServices(100);
//		Log.i(tag, "packagename: "+this.getPackageName());
		
		for(RunningServiceInfo service_name:service){
//			Log.i(tag, "exist service: "+service_name.service.getShortClassName());
			if(service_name.service.getShortClassName().equals(checkServiceName)){
				return_field=true;
				break;
			}
		}
		
		Log.i(tag, "service exist: "+String.valueOf(return_field));
		return return_field;
		
	}
	
	/**
	 * 專門檢查某App是否有開啟,
	 * 預設為false;
	 * @return 若有開啟回傳為true,否則為false
	 */
	public static boolean checkAppExist(Context context,String checkAppName){
		boolean return_field=false;
		ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE); 
		List<RunningAppProcessInfo> app=activityManager.getRunningAppProcesses();
//		Log.i(tag, "packagename: "+this.getPackageName());
		
		for(RunningAppProcessInfo app_name:app){
			Log.i(tag, "exist app: "+app_name.processName);
			if(app_name.processName.equals(checkAppName)){
				return_field=true;
				break;
			}
		}
		
		Log.i(tag, "app exist: "+String.valueOf(return_field));
		return return_field;
		
	}
	
	
	/**
	 * 專門檢查某Task是否有開啟,
	 * 預設為false;
	 * @return 若有開啟回傳為true,否則為false
	 */
	public static boolean checkTaskExist(Context context,String checkTaskName){
		boolean return_field=false;
		ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE); 
		List<RunningTaskInfo> task=activityManager.getRunningTasks(10);
//		Log.i(tag, "packagename: "+this.getPackageName());
		
		for(RunningTaskInfo task_name:task){
			Log.i(tag, "exist task: "+task_name.topActivity.getShortClassName());
			if(task_name.topActivity.getShortClassName().equals(checkTaskName)|
					task_name.topActivity.getShortClassName().equals("com.funtrigger.tuition"+checkTaskName)){
				return_field=true;
				break;
			}
		}
		
		Log.i(tag, "app exist: "+String.valueOf(return_field));
		return return_field;
		
	}
	
//	/**
//	 * 檢查記憶體是否超載
//	 * @param context
//	 * @return 若超載，回傳true
//	 */
//	public static boolean checkMemory(Context context){
//		//檢查緩衝記憶體是否過量
//		int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
////		String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
//
//		if(usedMegs>11){
//			Log.i(tag, "Memory is: "+usedMegs+"MB, cancel into Welcome.class");
//			
//			MySharedPreferences.addPreference(context, "into_ahmydroid", true);
//			
////			new AlertDialog.Builder(context)
////            .setTitle(context.getResources().getString(R.string.attention))
//////		    .setIcon(icon)
////		    .setMessage(context.getResources().getString(R.string.memory_overload))
////		    .setPositiveButton(context.getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
////				
////				@Override
////				public void onClick(DialogInterface dialog, int which) {
////					
////				}
////			})
////
////			.show();
//			
////			Toast.makeText(context, context.getResources().getString(R.string.memory_overload), Toast.LENGTH_SHORT).show();
//		}
//		return usedMegs>11?true:false;
//	}
}
