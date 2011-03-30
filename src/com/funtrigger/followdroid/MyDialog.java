package com.funtrigger.followdroid;

import java.util.regex.Pattern;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.funtrigger.followdroid.R;

/**
 * 這個類別專用來產生和管理各種中獎的Dialog視窗
 * @author simon
 *
 */
public class MyDialog {

	private static String tag="tag"; 
	public static Toast toast;
	
	/**
	 * 顯示訊息視窗，並接收傳來的參數<br/>
	 * @param context 產生此視窗的主體
     * @param title 想要顯示的視窗標題
	 * @param message 想要顯示的訊息內容
	 * @param icon 想要顯示的icon
	 */
	public static void newDialog(Context context,String title,String message,String icon){
		Log.i(tag, "into MyDialog.newDialog");
        new AlertDialog.Builder(context)
            .setTitle(title)
		    .setIcon(context.getResources().getIdentifier(icon,"drawable",context.getPackageName()))
		    .setMessage(message)
		    .setPositiveButton("確認", new DialogInterface.OnClickListener() {
		
		    @Override
		    public void onClick(DialogInterface dialog, int which) {

		    }})

   .show();
   }
	
	/**
	 * 新增一個訊息視窗，OK鍵的名稱、listener皆自訂
	 * @param context 產生此視窗的主體
	 * @param icon 想要顯示的icon
	 * @param title 想要顯示的視窗標題
	 * @param message 想要顯示的訊息內容
	 * @param positiveText 原本OK位置的命名
	 * @param positivelistener OK按鈕的監聽事件
	 */
	public static void newOneBtnDialog(Context context,int icon,String title,String message,String positiveText,DialogInterface.OnClickListener positivelistener){
		Log.i(tag, "into MyDialog.newOneBtnDialog");
        new AlertDialog.Builder(context)
            .setTitle(title)
		    .setIcon(icon)
		    .setMessage(message)
		    .setPositiveButton(positiveText, positivelistener)

   .show();
   }
	
	
	public static void newTwoBtnDialog(Context context,int icon,String title,String message,String positiveText,DialogInterface.OnClickListener positivelistener,String negativeText,DialogInterface.OnClickListener negativelistener){
		Log.i(tag, "into MyDialog.newTwoBtnDialog");
        new AlertDialog.Builder(context)
            .setTitle(title)
		    .setIcon(icon)
		    .setMessage(message)
		    .setPositiveButton(positiveText, positivelistener)
		    .setNegativeButton(negativeText, negativelistener)
   .show();
   }
	/**
	 * 該視窗是Toast視窗
	 * @param context 產生此視窗的主體
	 * @param message 想要顯示的訊息內容
	 * @param icon 想要產生的icon
	 */
	public static void newToast(Context context,String message,int icon){

			cancelToast();
			
			toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		    View originView=toast.getView();
		    LinearLayout layout= new LinearLayout(context);
		    layout.setOrientation(LinearLayout.VERTICAL);
		    ImageView view = new ImageView(context);
//		    view.setImageResource(R.drawable.again);
		    view.setImageResource(icon);
		    layout.addView(view);
		    layout.addView(originView);
		    toast.setView(layout);
		    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		    toast.show();
		
	}
//	
//	/**
//	 * 當畫面進入Fallen時，呼叫該視窗，並輸入密碼，才能完全解密並關閉Fallen模式
//	 * @param context 要做事情的呼叫主體
//	 */
//	public static void passwordToExit(final Context context){
//		
//		 LayoutInflater factory = LayoutInflater.from(context);
//         final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.password_to_exit), null);
//         new AlertDialog.Builder(context)
//             .setIcon(context.getResources().getDrawable(com.funtrigger.ahmydroid.R.drawable.verify))
//             .setTitle(context.getResources().getText(com.funtrigger.ahmydroid.R.string.exit))
//             .setView(EntryView)
//             .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
//                 public void onClick(DialogInterface dialog, int whichButton) {
//                	 EditText password=(EditText) EntryView.findViewById(R.id.password_to_exit);
//                	 if(password.getText().toString().equals(MySharedPreferences.getPreference(context, "unlock_password", ""))){
//                		  final Activity fallen=(Activity)context;
//                		  SwitchService.stopService(context, TimeService.class);
//                		  
//                		  MyDialog.newOneBtnDialog(context, R.drawable.verify, context.getString(R.string.exit),context.getString(R.string.unlock_success),context.getString(R.string.ok),new DialogInterface.OnClickListener(){
//
//      						@Override
//      						public void onClick(DialogInterface dialog, int which) {
//      							fallen.finish();
//      							
//      						}
//      					});
//                        
//                     
//                          
//                	 }else{
//                		  newDialog(context,context.getString(R.string.attention),context.getString(R.string.type_wrong_password),"warning");
//                	 }
//                   
//                 }
//             })
//             .setNegativeButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.cancel), new DialogInterface.OnClickListener() {
//                 public void onClick(DialogInterface dialog, int whichButton) {
//                 }
//             })
//             .show();
//
//	}
//	/**
//	 * 如果之前有存密碼，當使用者又按一次設定解鎖密碼，會進來這裡要求先輸入之前的密碼，
//	 * 才能重新設定密碼
//	 * @param context 要做事情的呼叫主體
//	 */
//	public static void passwordToChangePass(final Context context){
//		 LayoutInflater factory = LayoutInflater.from(context);
//         final View EntryView = factory.inflate(context.getResources().getLayout(R.layout.password_to_exit), null);
//         new AlertDialog.Builder(context)
//             .setTitle(context.getResources().getText(R.string.password_type))
//             .setView(EntryView)
//             .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//                 public void onClick(DialogInterface dialog, int whichButton) {
//                	 EditText password=(EditText) EntryView.findViewById(R.id.password_to_exit);
//                	 if(password.getText().toString().equals(MySharedPreferences.getPreference(context, "unlock_password", ""))){
//                		 Intent intent = new Intent();
//                		 intent.setClass(context, PasswordInput.class);
//                		 context.startActivity(intent);
//                	 }else{
//                		  newDialog(context,context.getString(R.string.attention),context.getString(R.string.password_wrong),"warning");
//                	 }
//                   
//                 }
//             })
//             .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                 public void onClick(DialogInterface dialog, int whichButton) {
//                 }
//             })
//             .show();
//	}
//	/**
//	 * 使用者在設定新帳號密碼時，會被呼叫的視窗
//	 * @param context 啟動該視窗的呼叫主體
//	 */
//	public static void createNewPassword(final Context context,int title){
//		LayoutInflater factory = LayoutInflater.from(context);
//        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.create_new_password), null);
//        new AlertDialog.Builder(context)
//            .setIcon(context.getResources().getDrawable(com.funtrigger.ahmydroid.R.drawable.verify))
//            .setTitle(title)
//            .setView(EntryView)
//            .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                	
//
//                		Log.i(tag, "into unlock_password no data");
//                		EditText password1=(EditText) EntryView.findViewById(R.id.password_first_type);
//	                	EditText password2=(EditText) EntryView.findViewById(R.id.password_second_type);
//	                	if(password1.getText().toString().equals(password2.getText().toString())){
//	                		MySharedPreferences.addPreference(context, "unlock_password", password1.getText().toString());
//	                		newToast(context,context.getString(R.string.password_saved),R.drawable.verify);
//	                	}else{
//	                		MyDialog.newDialog(context, context.getString(R.string.attention), context.getString(R.string.type_dismatch), "warning");
//	                	}
//                	
//	                	
//                	
//                }
//            })
//            .setNegativeButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.cancel), new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//
//                    /* User clicked cancel so do some stuff */
//                }
//            })
//            .show();
//	}
	
//	/**
//	 * 修改拾獲者告知的訊息視窗
//	 * @param context 呼叫的主體
//	 */
//	public static void modifyPickUP(final Context context){
//		LayoutInflater factory = LayoutInflater.from(context);
//        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.context_to_pick), null);
//        final EditText text=(EditText) EntryView.findViewById(com.funtrigger.ahmydroid.R.id.type_pick_context);
//        text.setText(MySharedPreferences.getPreference(context, "pick_context", context.getString(com.funtrigger.ahmydroid.R.string.pick_default_context)));
//		new AlertDialog.Builder(context)
//        .setTitle(com.funtrigger.ahmydroid.R.string.pick_set)
//	    .setIcon(com.funtrigger.ahmydroid.R.drawable.pickup_spic)
//	    .setView(EntryView)
//	    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
//	    		   
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				
//				MySharedPreferences.addPreference(context, "pick_context", text.getText().toString());
//				MySharedPreferences.addPreference(context, "pick", true);
//				if(MySystemManager.checkTaskExist(context, ".PickUp"))PickUp.modify_pick_up(text.getText().toString());
//				
//			}
//		})
//	    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//			}
//	    	
//	    })
//	    	.show();
//
//	}
	
//	/**
//	 * 將Message的設定畫面叫出來
//	 */
//	public static void startMsgDialog(final Context context){
//		
//		//如果定位是開AGPS的話，要檢查有沒有開3G
//		if(LocationUpdateService.getMyBestProvider(context).equals("network") 
//				&& !InternetInspector.checkInternet(context).equals("mobile")){
//			need3G(context);
//			
//		}else{
//
//		
//		LayoutInflater factory= LayoutInflater.from(context);
//		final View EntryView = factory.inflate(R.layout.context_to_message, null);
//		
////		MyTime mytime=new MyTime();
//		//將時間和地點換成當下的值
//		
//		//將簡訊內文的第1行裡的經緯度抓一抓
//
//		TextView sys_cnx=(TextView) EntryView.findViewById(R.id.msg_sys_ctx);
//		sys_cnx.setText(sys_cnx.getText().toString().replace("#time",/* mytime.getHHMM()*/MyTime.getHHMM1()));
//		sys_cnx.setText(sys_cnx.getText().toString().replace("#location", LocationUpdateService.getRecordLocation(context)));
//		
//
//		final EditText num=(EditText) EntryView.findViewById(R.id.type_message_number);
//		final EditText msg_cnx=(EditText) EntryView.findViewById(R.id.type_message_context);
//
//		
//		//如果手機號碼和內文之前有存，也顯示出來
//		num.setText(MySharedPreferences.getPreference(context, "message_number", ""));
//		msg_cnx.setText(MySharedPreferences.getPreference(context, "message_context", context.getString(R.string.type_message_context)));
//		
//		 new AlertDialog.Builder(context)
//            .setTitle(context.getString(R.string.message_set))
//		    .setIcon(R.drawable.message_spic)
//		    .setView(EntryView)
//		    .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog,
//						int which) {				
//					
//					//不管電話號碼對不對，簡訊的內文都先存進去再說
//					MySharedPreferences.addPreference(context, "message_context", msg_cnx.getText().toString());
//					
////					Log.i(tag, "edittext= "+num.getText().toString());
//					if(num.getText().toString().equals("")){
//						MySharedPreferences.addPreference(context, "message_number", "");
//						if(MySystemManager.checkTaskExist(context, ".Settings")==true){
//							Settings.setMsgStatus(false);
//						}
//						
//
//					}else if(!num.getText().toString().equals("")){
//						
//						if(isNumeric(num.getText().toString())){
//							MySharedPreferences.addPreference(context, "message_number", num.getText().toString());
//							SwitchService.startService(context, LocationUpdateService.class);
//							if(MySystemManager.checkTaskExist(context, ".SetSendData"))MySharedPreferences.addPreference(context, "message", true);
//							if(MySystemManager.checkTaskExist(context, ".Settings"))Settings.setMsgStatus(true);
//						}else{
//							/*MyDialog.*/newDialog(context, context.getString(R.string.attention), context.getString(R.string.wrong_phone_number), "warning");
//							if(MySystemManager.checkTaskExist(context, ".Settings")==true){
//								Settings.setMsgStatus(false);
//							}
//							
//						}
//						
//					}
//					
//				}
//		    	
//		    })
//		    .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener(){
//
//				@Override
//				public void onClick(DialogInterface dialog,
//						int which) {
//					
//						if(MySharedPreferences.getPreference(context, "message_number", "").equals("")){
//							//如果沒資料了，則將勾勾取消
//							if(MySystemManager.checkTaskExist(context, ".Settings")==true){
//								Settings.setMsgStatus(false);
//							}
//							
//						}			
//					
//				}
//		    }
//		    )
//   .show();
//		}
//	}
	
	
//	/**
//	 * 將Facebook設定畫面叫出來
//	 */
//	public static void startFacebookDialog(final Context context,final Activity activity){
//		
//		if(!InternetInspector.checkInternet(context).equals("mobile")){
//			need3G(context);
//		
//	}else{
//		SwitchService.startService(context, LocationUpdateService.class);
//		
//		 //將Cookie先叫出來，好讓等等的Facebook能用
//        CookieSyncManager.createInstance(context);
//        final CookieManager cookie=CookieManager.getInstance();
//
//        
//		/*MyDialog.*/newOneBtnDialog(context, R.drawable.facebook_spic, context.getString(R.string.facebook_set), context.getString(R.string.facebook_instruction), context.getString(R.string.ok), new DialogInterface.OnClickListener(){
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				
//				
//				//如果檢查Cookie沒有資料,設定帳密
//				if(cookie.getCookie("http://www.facebook.com")==null||
//						cookie.getCookie("http://www.facebook.com").indexOf("m_user", 0)==-1){	
//					if(MySystemManager.checkTaskExist(context, ".Settings")==true){
//						Settings.setFacebookStatus(false);
//					}
//					
//					//連到Facebook去設定帳密
//					MyDispatcher mydispatcher = new MyDispatcher();
//					mydispatcher.facebookDispatcher(context, activity);
//					
//				}else{
//					//出現視窗問是否要清除的詢問畫面
//					Log.i(tag, "cookie: "+cookie.getCookie("http://www.facebook.com"));
//					Log.i(tag, "cookie INDEX: "+cookie.getCookie("http://www.facebook.com").indexOf("m_user", 0));
//					
//					
//					MyDialog.newTwoBtnDialog(context, R.drawable.warning, context.getString(R.string.attention), context.getString(R.string.has_old_cookie), 
//							context.getString(R.string.ok), new DialogInterface.OnClickListener(){
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									//先清除Cookie，並將顯示畫面的值設一設
//									cookie.removeAllCookie();
//									if(MySystemManager.checkTaskExist(context, ".Settings")==true){
//										Settings.setFacebookStatus(false);
//									}else if(MySystemManager.checkTaskExist(context, ".SetSendData")==true){
//										MySharedPreferences.addPreference(context, "facebook", false);
//									}
//									
//									
//									//再連到Facebook去設定帳密
//									MyDispatcher mydispatcher = new MyDispatcher();
//									mydispatcher.facebookDispatcher(context, activity);
//								}
//						
//					}, context.getString(R.string.cancel), new DialogInterface.OnClickListener(){
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							//按取消時，什麼事也不做
//						}
//						
//					});
//				}		
//				
//				
//			}
//			
//		});
//	}
//	}
		
//	/**
//	 * 出現視窗告知使用者必須先開啟3G連線
//	 * @param context 呼叫的主體
//	 */
//	public static void need3G(final Context context){
//		new AlertDialog.Builder(context)
//        .setTitle(com.funtrigger.ahmydroid.R.string.attention)
//	    .setIcon(com.funtrigger.ahmydroid.R.drawable.warning)
//	    .setMessage(com.funtrigger.ahmydroid.R.string.no_3g)
//	    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
//	    		   
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				
////				context.startActivity(new Intent(context, Keep3G.class));
//				/*if(MySystemManager.checkTaskExist(context, ".Settings")==true){
//					Settings.closeAllLocationSet();
//				}*/
//			}
//		})
////		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
////		    		   
////				
////				@Override
////				public void onClick(DialogInterface dialog, int which) {
////				}
////			})
//	    
//	    	.show();
//	}
		
//	/**
//	 * 該訊息視窗用來顯示每種告知功能的說明介紹
//	 * @param context
//	 */
//	public static void helpDialog(final Context context,int icon,String title,String content){
//		LayoutInflater factory = LayoutInflater.from(context);
//        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.help), null);
//        TextView helpContent=(TextView) EntryView.findViewById(R.id.help_context);
//        helpContent.setText(content);
//        new AlertDialog.Builder(context)
//            .setIcon(icon)
//            .setTitle(title)
//            .setView(EntryView)
//            .setPositiveButton(context.getResources().getString(com.funtrigger.ahmydroid.R.string.ok), new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                }
//            })
//
//            .show();
//	}

//	/**
//	 * 單按鈕使用教學的視窗
//	 * @param context 呼叫該視窗的主體
//     * @param msg_pic 欲顯示的內文圖示
//	 * @param msg_context 欲顯示的內文文字
//	 * @param PositiveBtnName 單擊按鈕的文字顯示
//	 * @param positiveDialogListener 當按[下一個]/[結束]的反應監聽
//	 */
//	public static AlertDialog tuitionOneBtnDialog(final Context context, int title,int msg_pic,int msg_context,int PositiveBtnName,DialogInterface.OnClickListener positiveDialogListener){
//		
//		LayoutInflater factory = LayoutInflater.from(context);
//        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.tuition), null);
//        ImageView tu_pic=(ImageView) EntryView.findViewById(R.id.tuition_pic);
//        TextView tu_cnx=(TextView) EntryView.findViewById(R.id.tuition_cnx);
//        tu_pic.setImageResource(msg_pic);
//        tu_cnx.setText(msg_context);
//        
//        return new AlertDialog.Builder(context)
//            .setTitle(title)
//            .setView(EntryView)
//            .setPositiveButton(PositiveBtnName, positiveDialogListener)
//           
//            .create();
//	}
//	
//	/**
//	 * 雙按鈕使用教學的視窗
//	 * @param context 呼叫該視窗的主體
//     * @param msg_pic 欲顯示的內文圖示
//	 * @param msg_context 欲顯示的內文文字
//	 * @param positiveDialogListener 當按[上一個]的反應監聽
//	 * @param negativeDialogListener 當按[下一個]的反應監聽
//	 */
//	public static AlertDialog tuitionTwoBtnDialog(final Context context, int title,int msg_pic,int msg_context,int positiveBtnText,int negativeBtnText,DialogInterface.OnClickListener positiveDialogListener,DialogInterface.OnClickListener negativeDialogListener){
//		
//		LayoutInflater factory = LayoutInflater.from(context);
//        final View EntryView = factory.inflate(context.getResources().getLayout(com.funtrigger.ahmydroid.R.layout.tuition), null);
//        ImageView tu_pic=(ImageView) EntryView.findViewById(R.id.tuition_pic);
//        TextView tu_cnx=(TextView) EntryView.findViewById(R.id.tuition_cnx);
//        tu_pic.setImageResource(msg_pic);
//        tu_cnx.setText(msg_context);
//        
//        return new AlertDialog.Builder(context)
//            .setTitle(title)
//            .setView(EntryView)
//            
//            .setPositiveButton(/*context.getResources().getString(com.funtrigger.ahmydroid.R.string.previous)*/positiveBtnText, positiveDialogListener)
//            .setNegativeButton(/*context.getResources().getString(com.funtrigger.ahmydroid.R.string.next)*/negativeBtnText, negativeDialogListener)
//            .create();
//	}
	

	
    /**
     * 這個函式專用來清除已顯示中的Toast
     */
    public static void cancelToast(){
    	
		if(toast!=null){
			toast.cancel();
			Log.i(tag, "into cancelToast");
			toast=null;
		}
    
    }
    
	/**
	 * 檢查輸入是否為數字
	 * @param 傳入欲檢查的字串
	 * @return 若符合，回傳true
	 */
	public static boolean isNumeric(String str){
	    Pattern pattern = Pattern.compile("[0-9]*");
//	    Log.i(tag, "isNumeric:"+String.valueOf(pattern.matcher(str).matches()));
	    return pattern.matcher(str).matches();   
	 } 
}
