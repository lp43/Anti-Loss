package com.funtrigger.followdroid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.funtrigger.followdroid.R;

/**
 * This Class just design for check Internet.
 * @author simon
 *
 */
public class InternetInspector {
	private static String tag="tag";
	static ConnectivityManager conn;
	
	/**
	 * Inspect internet status
	 * @return return a String what is the internet using with.
	 */
	public static String checkInternet(Context context){

		if(conn==null) conn=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo net=conn.getActiveNetworkInfo();
		
		if(net!=null){
			Log.i(tag, "activitiy internet is: "+net.getTypeName());
		}else{
			Log.i(tag, "activitiy internet is null");
		}

		
		return (net==null?"null":net.getTypeName());
	}
	
	
	/**
	 * Inspect any internet ability or not
	 * @return return a String what is the internet using with.
	 */
	public static Boolean InternetOrNot(Context context){

		if(conn==null) conn=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo net=conn.getActiveNetworkInfo();
		
		if(net!=null){
			Log.i(tag, "activitiy internet is: "+net.getTypeName());
		}else{
			Log.i(tag, "activitiy internet is null");
		}

		
		return (net==null?false:true);
	}
}
