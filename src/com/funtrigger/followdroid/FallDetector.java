package com.funtrigger.followdroid;

import java.util.List;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.view.Window;
import com.funtrigger.followdroid.R;

/**
 * 手機掉落時的後臺Service，目的是開啟Gsensor來偵測使用者手機掉落，
 * 當掉落時，會開啟Fallen.java檔播放所有相關事件。如︰音效、震動、動畫…等
 * @author simon
 */
public class FallDetector extends Service{



	private String tag="tag";
	
	NotificationManager mNotificationManager;
	Notification notification;
//    /**
//     * 控制Gsensor的變數
//     */
//    private SensorManager sensormanager;
    /**
     * 該變數被用在告知系統，該類別啟動時，手機不能休眠，
     * 但是因為和我後來的使用方向不符，就沒用到了
     */
    WakeLock wakeLock;
	/**
	 * 電源管理變數，該變數被拿來偵測螢幕是否有亮
	 */
    PowerManager pm;
	/**
	 * 控制音樂播放的變數
	 */
    private MediaPlayer mp;
    /**
     * 告知系統跌倒聲音還在播放的單元
     */
	private boolean mpplaying=false;
	/**
	 * 用來調整音量Stream大小的啟始變數
	 */
	static AudioManager am;
	/**
	 * 宣告MySensor實體變數
	 */
	MySensor mysensor;
	BroadcastReceiver broadcastreceiver;

	
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(tag,"into FallDetector.onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(tag,"into FallDetector.onCreate");
		
		
		
		
//		Debug.startMethodTracing("methodtrace");
		
		this.registerReceiver(broadcastreceiver=new DropReceiver(), new IntentFilter("STARTFALLEN"));
		acquireWakeLock();
		
		//以下的程式不能寫在onStart(),因為被TaskManager清掉後，會自動onCreate()
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		notification = new Notification(R.drawable.icon,getString(R.string.startfallprotect), System.currentTimeMillis());
		
		PendingIntent pIntent = PendingIntent.getActivity(this,0,new Intent(this, Btn1set.class),PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this,getString(R.string.app_name),getString(R.string.startingfallprotect),pIntent);
		
//		notification.defaults=Notification.DEFAULT_SOUND;//開啟音效
		
		//啟動震動
//		long[] vibrate = {0,100,200,400};
//		notification.vibrate = vibrate;
		
		if(notification!=null){
			Log.i(tag, "notification!=null");
			startForeground(R.string.app_name,notification);//將Service強制在前景執行
		}
		mNotificationManager.notify(R.string.app_name,notification);
		
		
		mysensor=new MySensor();
		mysensor.startSensor(FallDetector.this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL );
		
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.i(tag,"into FallDetector.onDestroy");
//		Debug.stopMethodTracing();
		
		mysensor.stopSensor();
		mNotificationManager.cancelAll();
		  stopForeground(true);
		if(wakeLock.isHeld()){
			wakeLock.release();
			Log.i(tag, "wakeLock release");
			wakeLock=null;
		}
		if(broadcastreceiver!=null){
			this.unregisterReceiver(broadcastreceiver);
		}
	
		
		super.onDestroy();
	}

	
	/**
	 * 當Service打開時，請求CPU不要進入Sleep
	 * 
	 */
	private void acquireWakeLock() {
        if (wakeLock == null) {
               Log.i(tag,"Acquiring wake lock");
//               Log.i(tag, "canonical: "+this.getClass().getCanonicalName());
               pm = (PowerManager) getSystemService(POWER_SERVICE);
               wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                       PowerManager.ACQUIRE_CAUSES_WAKEUP |
                       PowerManager.ON_AFTER_RELEASE, this.getClass().getCanonicalName());
               wakeLock.acquire();
           }
       
   }
	
	/**
	 * 啟動Fallen.java並開啟震動和音效
	 */
	public void startFallen(){
		Log.i(tag, "into StartFallen");
		
		
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		Log.i(tag, "power screen on? "+pm.isScreenOn());
		//針測到螢幕是關的，才需要先播放一聲音效告知使用者
		if(pm.isScreenOn()==false){
			//先叫一聲，表示Gsensor有針測到掉下去，免得使用者以為沒有產生事件
			if(mpplaying==false){
				Log.i(tag, "into mp player");			
				am=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
				am.setStreamVolume(AudioManager.STREAM_MUSIC, Fallen.setVolumn, 0);
				mp=MediaPlayer.create(this, this.getResources().getIdentifier("dizzy", "raw", this.getPackageName()));
				
				mp.start();
				mpplaying=true;
			
			mp.setOnCompletionListener(new OnCompletionListener(){

				@Override
				public void onCompletion(MediaPlayer arg0) {
//					Log.i(tag, "into onCompletion");
					if(mp!=null){
						mp.release();					
					}		
					mpplaying=false;
				}
				
			});
			mp.setOnErrorListener(new OnErrorListener(){

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if(mp!=null){
						mp.release();
					}
					return false;
				}
				
			});
			}
		}	
		
		Intent intent = new Intent();
		intent.setClass(this, Fallen.class);
		//如果沒有在新的Task開啟程式，會Error
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	/**
	 * 該內部廣播接收用在當力道指定力道時，啟動Fallen.java
	 * @author simon
	 *
	 */
	public class DropReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("STARTFALLEN")){
				
				Log.i(tag, intent.getExtras().getString("filter"));
				startFallen();
			}
			
		}
		
	}

}
