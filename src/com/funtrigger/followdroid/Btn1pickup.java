package com.funtrigger.followdroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.funtrigger.followdroid.R;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

public class Btn1pickup extends Activity {
	Button previous,next,setting;
	protected String tag="tag";
	TextView pickup_text;
	TextView desc = null;
	ToggleButton toogle_btn = null;
	AdView  adView = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		setContentView(R.layout.page_layout);
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.pickup_set);
		
		callAdView();
		
		desc=(TextView)findViewById(R.id.desc);
		desc.setText(R.string.btn1_pick_up_des);
		pickup_text=(TextView)findViewById(R.id.describe_text);	
		pickup_text.setVisibility(View.VISIBLE);
		pickup_text.setText(MySharedPreferences.getPreference(this, "pick_context", getString(R.string.pickup_default_text)));
		
		previous = (Button) findViewById(R.id.previous);
		next = (Button) findViewById(R.id.next);
		setting = (Button) findViewById(R.id.setting);
		toogle_btn = (ToggleButton) findViewById(R.id.switch_btn);
		
		ImageView pic = (ImageView) findViewById(R.id.iv);
		pic.setBackgroundResource(R.drawable.background_demo);
		
		previous.setText(R.string.previous);
		previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent();
				intent.setClass(Btn1pickup.this, Btn1set.class);
				
				startActivity(intent);
			}
			
		});
		

		
		next.setText(R.string.exit);
		next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent();
				intent.setClass(Btn1pickup.this, Ahmydroid.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
			}
			
		});
		
		setting.setVisibility(View.VISIBLE);
		setting.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				modifyPickUP();
				
			}
			
		});
		
		toogle_btn.setOnClickListener(new OnClickListener(){


			@Override
			public void onClick(View v) {
				if(toogle_btn.isChecked() == true){
					MySharedPreferences.addPreference(Btn1pickup.this, "pick", true);
				}else{
					MySharedPreferences.addPreference(Btn1pickup.this, "pick", false);
				}
			}
			
		});
	}


	@Override
	protected void onResume() {
//		Log.i("tag", "Btn1pickup.onResume");
		super.onPause();
		
		if(MySharedPreferences.getPreference(this, "pick", false)==true){
			toogle_btn.setChecked(true);
		}

	}

	@Override
	protected void onDestroy() {
		if(adView != null)adView.stopLoading();
		super.onDestroy();
	}
	
	/**
	 * 修改拾獲者告知的訊息視窗
	 * @param context 呼叫的主體
	 */
	public void modifyPickUP(){
//		final Context context = context.getApplicationContext();
		LayoutInflater factory = LayoutInflater.from(this);
        final View EntryView = factory.inflate(R.layout.pickup_dialog, null);
        final EditText text=(EditText) EntryView.findViewById(R.id.pickup_type_area);
        text.setText(MySharedPreferences.getPreference(this, "pick_context", getString(R.string.pickup_default_text)));
		new AlertDialog.Builder(this)
        .setTitle(R.string.pickup_set)
	    .setIcon(R.drawable.pickup_spic)
	    .setView(EntryView)
	    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
	    		   
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				MySharedPreferences.addPreference(Btn1pickup.this, "pick_context", text.getText().toString());
				MySharedPreferences.addPreference(Btn1pickup.this, "pick", true);
				toogle_btn.setChecked(true);
				pickup_text.setText(MySharedPreferences.getPreference(Btn1pickup.this, "pick_context", getString(R.string.pickup_default_text)));
				
			}
		})
	    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
	    	
	    })
	    	.show();

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
