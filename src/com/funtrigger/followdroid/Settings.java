package com.funtrigger.followdroid;

import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.funtrigger.followdroid.R;

public class Settings extends PreferenceActivity {
	private PreferenceScreen preferenceScreen;
	private CheckBoxPreference password_set;
	private Preference password_modify;
	private static final int CREATEPASSWORD=1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("tag", "Settings.onCreate");
        addPreferencesFromResource(R.xml.settings);
        
        preferenceScreen=this.getPreferenceScreen();  
        password_set=(CheckBoxPreference)preferenceScreen.findPreference("password_set");
        password_modify = (Preference) preferenceScreen.findPreference("password_modify");
        
        password_set.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean return_value = true;
				if((Boolean)newValue == true){
					if(MySharedPreferences.getPreference(Settings.this, "password", "").equals("")){
						return_value = false;
						Intent intent = new Intent();
						intent.setClass(Settings.this, PasswordCreate.class);
						startActivityForResult(intent, CREATEPASSWORD);
					}
				}else{
					
					if(!MySharedPreferences.getPreference(Settings.this, "password", "").equals("")){
						return_value = false;
						typePassword();
					}
				}
				//讓原本切了就開和關的功能選擇性失效(如果有密碼時要取消就失效，強制要輸入完密碼才能取消)
				return return_value;
			}
		});
        
     
        
        password_modify.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {

				passwordToChangePass();
				//讓原本按了就出現視窗的原視窗失效
				return false;
			}
		});
    }
  

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
			case CREATEPASSWORD:
				if(resultCode == Activity.RESULT_OK){
					Log.i("tag", "into Result ok");
					password_set.setChecked(true);
				}
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
 
    
	/**
	 * 如果之前有存密碼，當使用者要改密碼時，會進來這裡要求先輸入之前的密碼，
	 * 才能重新設定密碼
	 * @param context 要做事情的呼叫主體
	 */
	public void passwordToChangePass(){
		 LayoutInflater factory = LayoutInflater.from(Settings.this);
         final View EntryView = factory.inflate(getResources().getLayout(R.layout.password_login), null);
         new AlertDialog.Builder(this)
             .setTitle(R.string.password_modify)
             .setView(EntryView)
             .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 EditText password=(EditText) EntryView.findViewById(R.id.edit);
                	 if(password.getText().toString().equals(MySharedPreferences.getPreference(Settings.this, "password", ""))){
                		 Intent intent = new Intent();
     					 intent.setClass(Settings.this, PasswordCreate.class);
     					 Settings.this.startActivity(intent);
                	 }else{
                		 Toast.makeText(Settings.this, R.string.password_wrong, Toast.LENGTH_SHORT).show();
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
	 * 如果之前有存密碼，使用者要關閉密碼保護時，會要求先輸入密碼，
	 * 才能關閉密碼保護
	 * @param context 要做事情的呼叫主體
	 */
	
	public void typePassword(){

		 LayoutInflater factory = LayoutInflater.from(this);
         final View EntryView = factory.inflate(getResources().getLayout(R.layout.password_login), null);
         TextView text = (TextView) EntryView.findViewById(R.id.text);
         text.setText(R.string.password_first_title);
         new AlertDialog.Builder(this)
             .setTitle(R.string.password_close)
             .setView(EntryView)
             .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                	 EditText password=(EditText) EntryView.findViewById(R.id.edit);
                	 if(password.getText().toString().equals(MySharedPreferences.getPreference(Settings.this, "password", ""))){

                		 password_set.setChecked(false);
                	 }else{
                		 Toast.makeText(Settings.this, R.string.password_wrong, Toast.LENGTH_SHORT).show();

                	 }
                   
                 }
             })
             .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int whichButton) {
                 }
             })
             .show();
	}


	
	
}
