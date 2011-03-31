package com.funtrigger.followdroid;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.funtrigger.followdroid.R;

/**
 * 在後臺運行中偵測手機是否掉落的Gsensor的Service，
 * 會啟動這個類別。
 * 這個類別用來處理動畫、音效、和震動…，
 * 以提示使用者手機掉了。
 * 這個類別被設在KEYGUARD保護層以外，
 * 所以螢幕在休眠時，
 * 也能亮起來，並啟動該類別
 * 註︰這個類別只會在手勢啟動後才被執行
 * @author simon
 */
public class Fallen extends Activity{

	/**
	 * 測試期間，讓我可以快速更改音量的變數
	 * 不用每次都跑到程式碼裡去找設定
	 */
	public static int setVolumn = 6;
	/**
	 * 記錄原始音量
	 */
	private static int previousVolumn = 6;
	/**
	 * 機器人圖案的ImageView
	 */
	private ImageView imgfall;
	/**
	 * 控制音樂播放的變數
	 */
    private MediaPlayer mp;
    /**
     * 告知系統跌倒聲音還在播放的單元
     */
	private boolean mpplaying=false;
	/**
	 * 機器人暈眩的動畫變數
	 */
	private AnimationDrawable aniimg;
    private final String tag="tag";
    /**
     * 控制Gsensor的變數
     */
    private SensorManager sensormanager;

	/**
	 * 電源管理變數，該變數被拿來偵測螢幕是否有亮
	 */
	PowerManager pm;
	/**
	 * 該變數是重力感應的X,Y,Z值
	 * 程式將它們存在List裡
	 */
	List<Sensor> list;
	/**
	 * 用來調整音量Stream大小的啟始變數
	 */
	static AudioManager am;
	/**
	 * 專門用來處理震動的公用變數
	 */
	Vibrator myVibrator;
	Handler handler;
	Runnable reg_Gsensor;
    /**
     * 該變數被用在告知系統，該類別啟動時，手機不能休眠，
     * 但是因為和我後來的使用方向不符，就沒用到了
     */
    WakeLock wakeLock;
    MySensor mysensor;
	private BroadcastReceiver receiver_sensorchanged;
	/**
	 * 整個Fallen的layout變數
	 */
	RelativeLayout relativelayout;
	/**
	 * 關閉螢幕的控制變數
	 */
	WindowManager.LayoutParams lp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		Log.i(tag, "into Fallen.onCreate()");
		super.onCreate(savedInstanceState);
		
		SwitchService.stopService(Fallen.this,FallDetector.class);//進來時把Service關掉，避免重覆進入本畫面		
		
		lp=this.getWindow().getAttributes();
		
		//檢驗是否為螢幕鎖
		KeyguardManager km = (KeyguardManager) this.getSystemService(
                Context.KEYGUARD_SERVICE);
//		Log.i(tag, "keylock? "+km.inKeyguardRestrictedInputMode());
		
		 
		if(km.inKeyguardRestrictedInputMode()==true){
			//如果有螢幕鎖時要做的事
//			Log.i(tag, "into inKeyguardRestrictedInputMode()==true");
		}else{
			//沒有進入螢幕鎖時，正常啟動程式
//			Log.i(tag, "into inKeyguardRestrictedInputMode()==false");
		}
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		Log.i(tag, "power screen on? "+pm.isScreenOn());
	
				//讓該Activity能在鍵盤鎖以外，讓螢幕亮起的法寶
				final Window win = getWindow();
				 win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
			                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
			                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
		                 /* | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON*/);

		setContentView(R.layout.ahmyphone);
//		Log.i(tag, "setContentView finish");
		
		imgfall=(ImageView) findViewById(R.id.fall);
		relativelayout = (RelativeLayout) findViewById(R.id.ahmyphone_layout);
		imgfall.setVisibility(View.VISIBLE);
	
//		Log.i(tag, "pick status: "+String.valueOf(MySharedPreferences.getPreference(this, "pick", false)));
//		Log.i(tag, "pick status: "+String.valueOf(PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getBoolean("pick", false)));
		//如果拾獲者告知有啟動，則要顯示該訊息畫面
		if(MySharedPreferences.getPreference(this, "pick", false) == true ){
			TextView pick_context=new TextView(this);
			pick_context.setText(MySharedPreferences.getPreference(this, "pick_context", ""));
			pick_context.setBackgroundResource(R.drawable.pick_message_background);
			pick_context.setPadding(30, 20, 30, 60);//文字與背景的邊距
			pick_context.setTextSize(20);
			pick_context.setTextColor(Color.BLACK);
			RelativeLayout.LayoutParams params_pick_context=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
			params_pick_context.setMargins(0, 20, 0, 0);//距離主螢幕的位置
			pick_context.setLayoutParams(params_pick_context);
			relativelayout.addView(pick_context);
		}
		
		
		imgfall.setBackgroundResource(R.anim.falling_animation);
		imgfall.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Log.i(tag, "annimation pressed");
				am.setStreamVolume(AudioManager.STREAM_MUSIC,previousVolumn, 0);
				finish();
				SwitchService.startService(Fallen.this,FallDetector.class);
			}
			
		});
		aniimg=(AnimationDrawable) imgfall.getBackground();			
		
		am=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		
		this.registerReceiver(receiver_sensorchanged=new SensorChangedReceiver(), new IntentFilter("FALLENSENSORCHANGED"));
		
		
		
		//啟動一個執行緒，負責偵測Screen是否有On。
		//這個檢查迴圈不能寫在主程式裡，否則會干擾主程序喚醒螢幕
		new Thread(){
			public void run(){
				while(pm.isScreenOn()==false){
//					Log.i(tag, "into isScreenOn==false");
					if(pm.isScreenOn()==true){
//						Log.i(tag, "into isScreenOn==true");
						break;
					}
				}
			}
		}.start();
		
		//使用Handler的方式，註冊Fallen.Sensor。
		//目的也是為了預防該段程式干擾主程式喚醒螢幕
		handler =new Handler();
		reg_Gsensor=new Runnable(){

			@Override
			public void run() {
				mysensor=new MySensor();
				mysensor.startSensor(Fallen.this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_NORMAL);
//				boolean sensormanager_register =sensormanager.registerListener(Fallen.this,list.get(0), SensorManager.SENSOR_DELAY_NORMAL);	
//				Log.i(tag, "sensormanager_register "+sensormanager_register);		
			}};
		handler.post(reg_Gsensor);
		
		//調整媒體音量
		previousVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		setVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		am.setStreamVolume(AudioManager.STREAM_MUSIC,setVolumn, 0);
		
	}

	@Override//程式在按[Back鍵]或[電源鍵]，都會執行到該Method
	protected void onPause() {
		Log.i(tag, "into Fallen.onPause()");
		
		//程式在按[Back鍵]或[電源鍵]，都會執行到該Method,
		//所以要偵測，如果螢幕是亮著，才能真的做以下變數的釋放。
		//否則會產生下次啟動該程式時的干擾
		if(pm.isScreenOn()==true){
			if(mp!=null){
				mp.release();//釋放掉音樂資源
			}
			if(aniimg!=null){
				aniimg.stop();//動畫關掉
			}
			
			if(myVibrator!=null){
				myVibrator.cancel();//震動關掉
			}
			
			if(receiver_sensorchanged!=null){
				this.unregisterReceiver(receiver_sensorchanged);
			}
			
			if(mysensor!=null){
				mysensor.stopSensor();
			}
			
			if(handler!=null){
				handler.removeCallbacks(reg_Gsensor);
			}
			finish();//結束掉，下次再感測時，才能從onCreate()再執行
			SwitchService.startService(Fallen.this,FallDetector.class);//離開時再把Service開回去
			Log.i(tag, "finish Fallen.onPause()");
		}
		
		super.onPause();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.i(tag, "Fallen.onDestroy");
		super.onDestroy();
	}

	
	
	/**
	 * 該內部廣播接收在Fallen.java裡不斷的播動畫和音效
	 * @author simon
	 *
	 */
	public class SensorChangedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("FALLENSENSORCHANGED")){
				
//				Log.i(tag, intent.getExtras().getString("filter"));
				if(aniimg.isRunning()==false){
					aniimg.start();
				}
				
				if(mpplaying==false){
//					Log.i(tag, "into mp player");
					mp=MediaPlayer.create(Fallen.this, Fallen.this.getResources().getIdentifier("dizzy", "raw", Fallen.this.getPackageName()));
					mp.start();
					mpplaying=true;

				
				mp.setOnCompletionListener(new OnCompletionListener(){

					@Override
					public void onCompletion(MediaPlayer arg0) {
//						Log.i(tag, "into onCompletion");
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
				
				//取得震動服務
				myVibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
				myVibrator.vibrate(1000);//震動1000秒
				
			}
			
		}
		
	}
	

}
