package com.funtrigger.followdroid;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import com.funtrigger.followdroid.R;

public class PasswordCreate extends Activity {
	Button ok,cancel;
	CheckBox showpass;
	EditText password_first,password_second;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_create);
		setTitle(R.string.password_title);
		
		ok = (Button) findViewById(R.id.btn_ok);
		cancel = (Button) findViewById(R.id.btn_cancel);
		showpass = (CheckBox) findViewById(R.id.check_box);
		password_first = (EditText) findViewById(R.id.password_first);
		password_second = (EditText) findViewById(R.id.password_second);
		password_first.setTransformationMethod(PasswordTransformationMethod.getInstance());
		password_second.setTransformationMethod(PasswordTransformationMethod.getInstance());
		
		ok.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(!password_first.getText().toString().equals("")){
					if(password_first.getText().toString().equals(password_second.getText().toString())){
						MySharedPreferences.addPreference(PasswordCreate.this, "password", password_first.getText().toString());
						Toast.makeText(PasswordCreate.this, getString(R.string.password_saved), Toast.LENGTH_SHORT).show();
						PasswordCreate.this.setResult(RESULT_OK);
					}else{
						Toast.makeText(PasswordCreate.this, getString(R.string.password_dismatch), Toast.LENGTH_SHORT).show();
						
					}
				}else{
					Toast.makeText(PasswordCreate.this, getString(R.string.cant_be_null), Toast.LENGTH_SHORT).show();
					
				}
				
				finish();
			}
			
		});
		
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		showpass.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if(isChecked == true){
					password_first.setTransformationMethod(SingleLineTransformationMethod.getInstance());
					password_second.setTransformationMethod(SingleLineTransformationMethod.getInstance());
				}else{
					password_first.setTransformationMethod(PasswordTransformationMethod.getInstance());
					password_second.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
			
		});
	}

}
