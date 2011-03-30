package com.funtrigger.followdroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;
import com.funtrigger.followdroid.R;

/**
 * 該類別被拿來專門在背景更新geographic location
 * @author simon
 *
 */
public class LocationUpdateService extends Service implements LocationListener{

	static LocationManager lm;
	static ClipboardManager cbm;
	private static String tag="tag";
	/**
	 * longitude經度
	 */
	static double longitude=0.0;
	/**
	 * latitude緯度
	 */
	static double latitude=0.0;

	NotificationManager mNotificationManager;
	Notification notification;
	/**
	 * 最後經緯度更新時間
	 */
	private static String updated_time="";
	PendingIntent pIntent;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag, "LocationUpdateService.onCreate");
		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

			Log.i(tag, "into 59");

			if(getMyProvider()==null){
				stopSelf();
				Log.i(tag, "into 63");
//			}else if(getMyProvider().equals("network")){
//				
//				if(InternetInspector.InternetOrNot(this)==false){
//					Log.i(tag, "into 67");
//					stopSelf();
//					
//				}else{
//					Log.i(tag, "into 71");
//					updateLocation(LocationManager.NETWORK_PROVIDER);					
//				}	
				
			}else if(getMyProvider().equals("gps")){
				
				Log.i(tag, "into 77");
				updateLocation(LocationManager.GPS_PROVIDER);	
			}else{
				//目前先把AGPS功能關閉，下一版再釋出
				Log.i(tag, "into 80");
				stopSelf();
			}
	
		super.onCreate();
	}


	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(tag, "into LocationUpdateService.onStart");
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		try{
			lm.removeUpdates(this);
			Log.i(tag, "remove locationmanager : success!");
		}catch(NullPointerException e){
			Log.i(tag, "NullPointerException: "+e.getMessage());
		}
		
		Log.i(tag, "LocationUpdateService.onDestroy");
		Toast.makeText(this, getString(R.string.stop_location_update), Toast.LENGTH_SHORT).show();
		
		
		//==============關閉Notify欄視窗
		try{
			mNotificationManager.cancelAll();
			 stopForeground(true);
		}catch(NullPointerException e){
			Log.i(tag, "NullPointerException: "+e.getMessage());
		}
		//============================
	
		
		super.onDestroy();
	}

	/**
	 * 更新地理座標函式
	 */
	private void updateLocation(String provider){
		Toast.makeText(this, getString(R.string.start_location_update), Toast.LENGTH_SHORT).show();
		
		//====================================================
		//以下的程式不能寫在onStart(),因為被TaskManager清掉後，會自動onCreate()
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		notification = new Notification(R.drawable.location_icon,getString(R.string.location_updating), System.currentTimeMillis());
		
		pIntent = PendingIntent.getActivity(this,0,new Intent(this, Smsset.class),PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this,getString(R.string.location_name),getString(R.string.location_updating),pIntent);	
		
		if(notification!=null){
			Log.i(tag, "notification!=null");
			startForeground(R.string.location_name,notification);//將Service強制在前景執行
		}
		mNotificationManager.notify(R.string.location_name,notification);
		//============================================================
		
		
		
		lm.requestLocationUpdates(provider, 10000, 5,  this);

		Log.i("tag", "into 169");

//    	Toast.makeText(context,"Location: "+latitude+","+longitude , Toast.LENGTH_SHORT).show();
//    	Log.i(tag,latitude+","+longitude);//顯示緯度+經度以配合Google Map格式
	
	}
	/**
	 * 清除Location資料
	 */
	public void resetLocation(){
			
			latitude=0.0;
			longitude=0.0;
	}
	
	/**
	 * 讓程式去選取最好的定位方式標準<br/>
	 */
	private static Criteria getBestCriteria(){
		Criteria criteria= new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置为最大精度 
		criteria.setAltitudeRequired(false);//不要求海拔信息 
		criteria.setBearingRequired(false);//不要求方位信息 
		criteria.setCostAllowed(true);//是否允许付费 
		criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求

		return criteria;
	}
	
	/**
	 * 該函式是提供給對外的程式，取得依據Criteria所得到的最好的Provider
	 * @return String : Best Provider
	 */
	public static String getMyBestProvider(Context context){
		String return_value=null;
		
		try{
			if(lm==null)lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			Log.i(tag, "Best Provider: "+lm.getBestProvider(getBestCriteria(), true));
			
			return_value=lm.getBestProvider(getBestCriteria(), true);
		}catch(IllegalArgumentException e){
			Log.i(tag, "LocationUpdateService187: IllegalArgumentException: "+e.getMessage());
		}catch(NullPointerException e){
			Log.i(tag, "LocationUpdateService189: NullPointerException: "+e.getMessage());	
		}
		
//		Toast.makeText(LocationUpdateService.this, "bestProvider is: "+lm.getBestProvider(getBestCriteria(), true), Toast.LENGTH_SHORT).show();
		return return_value;
		
	}
	
	
	
	/**
	 * 取得依據Criteria所得到的最好的Provider
	 * @return String : Best Provider
	 */
	private String getMyProvider(){
		String return_value=null;
		
		try{
			
			Log.i(tag, "Best Provider: "+lm.getBestProvider(getBestCriteria(), true));
			
			return_value=lm.getBestProvider(getBestCriteria(), true);
		}catch(IllegalArgumentException e){
			Log.i(tag, "LocationUpdateService212: IllegalArgumentException: "+e.getMessage());
			
		}catch(NullPointerException e){
			Log.i(tag, "LocationUpdateService215: NullPointerException: "+e.getMessage());	
		}
		
//		Toast.makeText(LocationUpdateService.this, "bestProvider is: "+lm.getBestProvider(getBestCriteria(), true), Toast.LENGTH_SHORT).show();
		return return_value;
		
	}
	
	
	/**
	 * API文檔說︰如果要馬上取得經緯度，用getLastKnownLocation()
	 * @return 回傳String︰latitue,longitude
	 */
	public static String getRecordLocation(Context context){
		
		return latitude==0.0?context.getString(R.string.no_geolocation_data):latitude+","+longitude;
		
	}
	
	/**
	 * 取得經緯度最後更新的時間
	 */
	public static String getLastUpdatedTime(Context context){
		Log.i(tag, "updated_time.equals(): " + String.valueOf(updated_time.equals("")));
		return updated_time.equals("")?context.getString(R.string.no_geolocation_data):updated_time;
		
	}
	 
	
	@Override
	public void onLocationChanged(Location location) {
		Toast.makeText(LocationUpdateService.this,"LocationListener onLocationChanged" , Toast.LENGTH_SHORT).show();
		Log.i("tag", "Location Changed");
		latitude = location.getLatitude();//查詢緯度
		longitude=location.getLongitude();//查詢經度,並存進經度
		updated_time=MyTime.getHHMMSS1();
		
		//更新Notify Bar======
		notification.setLatestEventInfo(this,getString(R.string.location_name),getMyProvider() + ":" +getRecordLocation(this), pIntent);
		mNotificationManager.notify(R.string.location_name,notification);
		
		//寄出簡訊=============
		replyLocation(this);
		
		stopSelf();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(tag, "LocationListener onProviderDisabled");
		resetLocation();
		
//		Toast.makeText(LocationUpdateService.this,"LocationListener onProviderDisabled" , Toast.LENGTH_SHORT).show();
		
			
			stopSelf();
		
//			MySharedPreferences.addPreference(LocationUpdateService.this, "location", false);
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(tag, "LocationListener onProviderEnabled");
//		Toast.makeText(LocationUpdateService.this,"LocationListener onProviderEnabled" , Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(tag, "LocationListener onStatusChanged");
//		Toast.makeText(LocationUpdateService.this,"LocationListener onStatusChanged" , Toast.LENGTH_SHORT).show();
		
	}

	/**
	 * 發送含經緯度位置的SMS簡訊
	 * @param context 呼叫簡訊服務的主體
	 */
	private void replyLocation(Context context){
		
		String phoneNumber = MySharedPreferences.getPreference(context,"message_number","");
		Log.i("tag", "cellphone num is: "+ phoneNumber);
		
		String messageContext = 
			getMyProvider() + ":" +getRecordLocation(this)+"\n"+
			context.getString(R.string.location_update_time)+getLastUpdatedTime(this);
		Log.i("tag", "SMS context: "+ messageContext);
		
    	SmsManager smsmanager=SmsManager.getDefault();
		smsmanager.sendTextMessage(phoneNumber, null, messageContext, null, null);
		
		Toast.makeText(this, messageContext, Toast.LENGTH_SHORT).show();
		MyDialog.newToast(context, context.getString(R.string.message_response), R.drawable.message_pic);
		
		Log.i("tag", "message_send success!");
	}
	
	
}
