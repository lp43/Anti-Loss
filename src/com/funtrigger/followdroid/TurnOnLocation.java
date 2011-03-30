package com.funtrigger.followdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.funtrigger.followdroid.R;


public class TurnOnLocation extends Activity{
	Button previous,next,gotoset;
	

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.page_layout);
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.GPS_title);
		ImageView pic=(ImageView)findViewById(R.id.iv);
		TextView desc=(TextView)findViewById(R.id.desc);
		previous=(Button)findViewById(R.id.previous);
		next=(Button)findViewById(R.id.next);
		gotoset=(Button)findViewById(R.id.setting);
		ToggleButton tbn = (ToggleButton) findViewById(R.id.switch_btn);
		tbn.setVisibility(View.INVISIBLE);
		
		next.setEnabled(false);
		gotoset.setVisibility(View.VISIBLE);
		gotoset.setText(R.string.go_to);
		pic.setImageResource(R.drawable.mlocation_en);
		desc.setText(getResources().getText(R.string.GPS_des1)+"\n\n"+
				getResources().getText(R.string.GPS_des2)/*+"\n\n"+
				getResources().getText(R.string.please_location)*/);
		
		
		
		gotoset.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
				
			}
			
		});
		
		next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				TurnOnLocation.this.finish();
				TurnOnLocation.this.startActivity(new Intent(TurnOnLocation.this, Smsset.class));
				
			}
			
		});
		
		previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				finish();
				Intent intent = new Intent();
				intent.setClass(TurnOnLocation.this, Ahmydroid.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				
			}
			
		});
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus){
			if(LocationUpdateService.getMyBestProvider(this)==null){
				next.setEnabled(false);
			}else if(LocationUpdateService.getMyBestProvider(this).equals("network")){
				next.setEnabled(false);
			}else if(LocationUpdateService.getMyBestProvider(this).equals("gps")){
				next.setEnabled(true);
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}




}
