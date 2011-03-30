package com.funtrigger.followdroid;


import com.funtrigger.followdroid.R;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class Btn1set extends Activity{

	Button previous,next;
	static AnimationDrawable aniimg;
	protected String tag="tag";
	static TextView tuition_pick_up;
	ImageView pic = null;
	TextView desc = null;
	ToggleButton toogle_btn = null;
	AdView  adView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		setContentView(R.layout.page_layout);
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.fall_notify);
		
		callAdView();
		
			pic=(ImageView)findViewById(R.id.iv);
			desc=(TextView)findViewById(R.id.desc);

			pic.setBackgroundResource(R.anim.btn1_anim);
			desc.setText(R.string.btn1_set_des);
		
			aniimg=(AnimationDrawable) pic.getBackground();
			
		previous = (Button) findViewById(R.id.previous);
		next = (Button) findViewById(R.id.next);
		toogle_btn = (ToggleButton) findViewById(R.id.switch_btn);
		
		previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent();
				intent.setClass(Btn1set.this, Ahmydroid.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
			
		});
		next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(aniimg !=null && aniimg.isRunning()==true){
					aniimg.stop();
				}
				finish();
				Intent intent = new Intent();
				intent.setClass(Btn1set.this, Btn1pickup.class);
				startActivity(intent);
			}
			
		});
		
		toogle_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(toogle_btn.isChecked() == true){
//					if(MySystemManager.checkServiceExist(Btn1set.this.getApplicationContext(),".FallDetector")==false){
						SwitchService.startService(Btn1set.this,FallDetector.class);
						Log.i("tag", "start");
//						}
						//用在讓重新開機時，能夠知道要不要啟動防摔小安
						MySharedPreferences.addPreference(Btn1set.this, "falldetector_status", true);

						MyDialog.newToast(Btn1set.this, getString(R.string.startfallprotect), R.drawable.icon);
				}else{
					Log.i("tag", "stop");
//					if(MySystemManager.checkServiceExist(Btn1set.this.getApplicationContext(),".FallDetector")==true){
						
						SwitchService.stopService(Btn1set.this,FallDetector.class);
						//用在讓重新開機時，能夠知道要不要啟動防摔小安
						MySharedPreferences.addPreference(Btn1set.this, "falldetector_status", false);
						MyDialog.newToast(Btn1set.this, getString(R.string.stopfallprotect), R.drawable.icon);
//					}
					
				}
			}
			
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		if(MySystemManager.checkServiceExist(Btn1set.this,".FallDetector")==true){
			toogle_btn.setChecked(true);
		}
		
		if(hasFocus==true){
			aniimg.start();
		}else{
			aniimg.stop();
		
		}
		super.onWindowFocusChanged(hasFocus);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(adView != null)adView.stopLoading();
		super.onDestroy();
	}

	/**
	 * 呼叫AdMob
	 */
	private void callAdView(){
		Log.i("tag", "callAdView");
	    // Create the adView
	    adView = new AdView(this, AdSize.BANNER, "a14d8fff48e3815");
	    // Lookup your LinearLayout assuming it’s been given
	    // the attribute android:id="@+id/mainLayout"
	    LinearLayout layout = (LinearLayout)findViewById(R.id.adView);
	    
//	    TextView tv = new TextView(this);
//	    tv.setText("test");
//	    layout.addView(tv);
	    // Add the adView to it
	    layout.addView(adView);
	    
	    // Initiate a generic request to load it with an ad
	    AdRequest ar = new AdRequest();
//	    ar.setTesting(true);
	    adView.loadAd(ar);
	    Log.i("tag", "isAdReady? "+adView.isReady());
	    Log.i("tag", "isAdRefresh? "+adView.isRefreshing());
	    adView.setAdListener(new AdListener(){

			@Override
			public void onDismissScreen(Ad arg0) {
				Log.i("tag", "onDismissScreen, "+arg0.toString());
			}

			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				Log.i("tag", "onFailedToReceiveAd, "+arg1.toString());
				
			}

			@Override
			public void onLeaveApplication(Ad arg0) {
				Log.i("tag", "onLeaveApplication, "+arg0.toString());
				
			}

			@Override
			public void onPresentScreen(Ad arg0) {
				Log.i("tag", "onPresentScreen, "+arg0.toString());
				
			}

			@Override
			public void onReceiveAd(Ad arg0) {
				Log.i("tag", "onReceiveAd, "+arg0.toString());
				
			}
	    	
	    });
	}
	
}
