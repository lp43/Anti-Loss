package com.funtrigger.followdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import com.funtrigger.followdroid.R;

public class SMSReceiver extends BroadcastReceiver {
	SmsMessage[] resSMS;
	MySqlHelper mysql = null;
	Context c;
	@Override
	public void onReceive(Context context, Intent intent) {
		c = context;
		Log.i("tag", "SMSReceiver.onReceive");
		if(MySharedPreferences.getPreference(context, "FindPhoneByKeyword", false)==true){
			
			pdusToSmsMessage(context,intent.getExtras());
			
			mysql = new MySqlHelper(context);
			Cursor cursor = mysql.getAll();
			
			for(int i=0;i<cursor.getCount();i++){
				cursor.moveToPosition(i);
				Log.i("tag", "cursor.getString("+i+")"+cursor.getString(1));
				if(getSMScontext().equals(cursor.getString(1))){
//					
					//將電話存進MySharedPreferences
					MySharedPreferences.addPreference(context,"message_number",getPhoneNum());
					String provider = null;
					provider = LocationUpdateService.getMyBestProvider(context);
					if(provider==null ||
							provider.equals("network")){
						sendNoGpsSMS(context);
					}else{
						sendFirstSMS(context);
						//開啟定位功能
						startLocation();
					}
					
				}
			}	
			mysql.close();
		}
		
	}
	
	private void pdusToSmsMessage(Context context,Bundle bundle){
		
		
		Object[] pdus=(Object[]) bundle.get("pdus");
		
		resSMS=null;
		resSMS=new SmsMessage[pdus.length];
		
		for(int i=0;i<pdus.length;i++){
			byte[] buffer=(byte[]) pdus[i];
			resSMS[i]=SmsMessage.createFromPdu(buffer);
		}
		
		
//		Toast.makeText(context, "phone is: "+resSMS[0].getDisplayOriginatingAddress(), Toast.LENGTH_SHORT).show();
//		Toast.makeText(context, "context is: "+resSMS[0].getMessageBody(), Toast.LENGTH_SHORT).show();
		
	}
	
	private String getPhoneNum(){
		String num="";
		if(resSMS.length==0){
			num="";
		}else{
			num=resSMS[0].getDisplayOriginatingAddress();
		}
		return num;
	}

	private String getSMScontext(){
		String context="";
		if(resSMS.length==0){
			context="";
		}else{
			context=resSMS[0].getMessageBody();
		}
		return context;
	}
	
	/**
	 * 啟動GPS/AGPS定位
	 */
	private void startLocation(){
		Log.i("tag", "SMSReceiver.startLocation()");
		SwitchService.startService(c, LocationUpdateService.class);
	}
	
	/**
	 * 發送手機沒有開啟GPS的簡訊
	 * @param context 呼叫簡訊服務的主體
	 * 註︰這封簡訊目的是讓使用者知道因為先前沒有開啟系統GPS，
	 * 所以無法獲知手機位置
	 */
	private void sendNoGpsSMS(Context context){
		
		String phoneNumber = MySharedPreferences.getPreference(context,"message_number","");
		Log.i("tag", "cellphone num is: "+ phoneNumber+"NoGPS");
		
		String messageContext = 
			context.getString(R.string.message_nogps);
		
		Log.i("tag", "SMS context: "+ messageContext);
		
    	SmsManager smsmanager=SmsManager.getDefault();
		smsmanager.sendTextMessage(phoneNumber, null, messageContext, null, null);
		
		Toast.makeText(context, messageContext, Toast.LENGTH_SHORT).show();
		
		MyDialog.newToast(context, context.getString(R.string.message_response), R.drawable.message_pic);
		Log.i("tag", "message_send success!");
	}
	
	
	/**
	 * 發送第1封SMS簡訊
	 * @param context 呼叫簡訊服務的主體
	 * 註︰這封簡訊目的是讓使用者知道程式收到訊息了，
	 * 等等更新到經緯度後會回傳手機位置
	 */
	private void sendFirstSMS(Context context){
		
		String phoneNumber = MySharedPreferences.getPreference(context,"message_number","");
		Log.i("tag", "cellphone num is: "+ phoneNumber + "SendFirstSMS");
		
		String messageContext = 
			context.getString(R.string.message_please_wait);
		
		Log.i("tag", "SMS context: "+ messageContext);
		
    	SmsManager smsmanager=SmsManager.getDefault();
		smsmanager.sendTextMessage(phoneNumber, null, messageContext, null, null);
		
		Toast.makeText(context, messageContext, Toast.LENGTH_SHORT).show();
		
		MyDialog.newToast(context, context.getString(R.string.message_response), R.drawable.message_pic);
		Log.i("tag", "message_send success!");
	}
}
