package com.funtrigger.followdroid;

import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;
import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.funtrigger.followdroid.R;

public class Ahmydroid extends Activity {
	/**
	 * 記錄當前的版本編號<br/>
	 * 這個編號會被放在[Menu]的[關於]裡
	 */
	private String softVersion="v2.0.2";
	Button btn1,btn2,btn3,btn4;
	AdView  adView = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        callAdView();

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        
        btn1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
        		intent.setClass(Ahmydroid.this.getApplicationContext(), Btn1set.class);
        		startActivity(intent);	
			}
        	
        });
        
        btn2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				
				if(MySharedPreferences.getPreference(Ahmydroid.this, "password_set", false)==true){
					typePassword();
				}else{
					
//					Ahmydroid.this.startActivity(new Intent(Ahmydroid.this, LocationUpdateService.getMyBestProvider(Ahmydroid.this)==null?TurnOnLocation.class:Smsset.class));
					checkProviderThenDoing();
				}
				
			}
        	
        });
        
        btn3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Ahmydroid.this, Settings.class);
				startActivity(intent);
			}
        	
        });
        
        btn4.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
        	
        });
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
	 * 檢查要開GPS還是直接進入關鍵字畫面
	 * 註︰2.0.1版先以GPS為抓取經緯度的要項
	 * 等下一版出來後，再釋出APGS版本
	 */
	private void checkProviderThenDoing(){
		String provider = null;
		provider = LocationUpdateService.getMyBestProvider(Ahmydroid.this);
		if(provider!=null && provider.equals("gps")){
		
			Intent intent = new Intent();
			intent.setClass(this, Smsset.class);
			startActivity(intent);
			
		}else{
			
			Intent intent = new Intent();
			intent.setClass(this, TurnOnLocation.class);
			startActivity(intent);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		if(hasFocus){
			  
			  //btn1和btn2會因為使用者的設定而改變按鈕
				if(MySystemManager.checkServiceExist(Ahmydroid.this,".FallDetector")==true){
					btn1.setBackgroundResource(R.drawable.btn1_yes);
				}else{
					btn1.setBackgroundResource(R.drawable.btn1);
				}
				
				if(MySharedPreferences.getPreference(this, "FindPhoneByKeyword", false) == true){
					btn2.setBackgroundResource(R.drawable.btn2_yes);
				}else{
					btn2.setBackgroundResource(R.drawable.btn2);
				}
		}
		super.onWindowFocusChanged(hasFocus);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, 0, 0, R.string.about);
//		menu.add(0, 1, 1, "查看中獎號");

		menu.getItem(0).setIcon(R.drawable.about);
	
		return super.onCreateOptionsMenu(menu);
	}
	



	/**描述 : 建立Menu清單的觸發事件*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case 0:
				new AlertDialog.Builder(this)
				.setTitle(R.string.author)
				.setIcon(R.drawable.icon)
				.setMessage(getResources().getText(R.string.app_name) +" "+softVersion+"\n"+getResources().getText(R.string.author) +" Funtrigger"+"\n\n"+getResources().getText(R.string.copyright)+" 2011")
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {	
					}
				})
				.setNegativeButton(R.string.report_problem, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent sendIntent = new Intent(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lp43simon@gmail.com"}); 
						sendIntent.putExtra(Intent.EXTRA_TEXT, "Comment here");
						sendIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name)+" "+softVersion+" 意見回報");
						sendIntent.setType("message/rfc822");
						startActivity(Intent.createChooser(sendIntent, "Title:"));
					}
				})
				
				.show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 如果之前有存密碼，使用者要開始設定關鍵字時，會要求先輸入密碼，
	 * 才能進入關鍵字頁面
	 * @param context 要做事情的呼叫主體
	 */
	
	public void typePassword(){
		 LayoutInflater factory = LayoutInflater.from(this);
         final View EntryView = factory.inflate(getResources().getLayout(R.layout.password_login), null);
         TextView text = (TextView) EntryView.findViewById(R.id.text);
         text.setText(R.string.password_first_title);
         new AlertDialog.Builder(this)
             .setTitle(R.string.password_protect)
             .setView(EntryView)
             .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 EditText password=(EditText) EntryView.findViewById(R.id.edit);
                	 if(password.getText().toString().equals(MySharedPreferences.getPreference(Ahmydroid.this, "password", ""))){

//                		 Ahmydroid.this.startActivity(new Intent(Ahmydroid.this, LocationUpdateService.getMyBestProvider(Ahmydroid.this)==null?TurnOnLocation.class:Smsset.class));
                		 checkProviderThenDoing();
                	 }else{
                		 Toast.makeText(Ahmydroid.this, R.string.password_wrong, Toast.LENGTH_SHORT).show();
                	 }
                   
                 }
             })
             .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                 }
             })
             .show();
	}
    
	/**
	 * 呼叫AdMob
	 */
	private void callAdView(){
//		Log.i("tag", "callAdView");
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
//	    Log.i("tag", "isAdReady? "+adView.isReady());
//	    Log.i("tag", "isAdRefresh? "+adView.isRefreshing());
	    adView.setAdListener(new AdListener(){

			@Override
			public void onDismissScreen(Ad arg0) {
//				Log.i("tag", "onDismissScreen, "+arg0.toString());
			}

			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
//				Log.i("tag", "onFailedToReceiveAd, "+arg1.toString());
				
			}

			@Override
			public void onLeaveApplication(Ad arg0) {
//				Log.i("tag", "onLeaveApplication, "+arg0.toString());
				
			}

			@Override
			public void onPresentScreen(Ad arg0) {
//				Log.i("tag", "onPresentScreen, "+arg0.toString());
				
			}

			@Override
			public void onReceiveAd(Ad arg0) {
//				Log.i("tag", "onReceiveAd, "+arg0.toString());
				
			}
	    	
	    });
	}
}