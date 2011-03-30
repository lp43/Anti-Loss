package com.funtrigger.followdroid;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import com.funtrigger.followdroid.R;


/**
 * 該類別管理各種類別的Sensor感測器
 * 摔落演算法也寫在這裡面
 * @author simon
 */
public class MySensor implements SensorEventListener{


	/**
     * 控制Gsensor的變數
     */
    private SensorManager sensormanager;
	/**
	 * 取得時間的實體變數
	 */
	MyTime gettime;
	private String tag="tag";

	private Context context;
	/**
	 * 當震痛數據[0,0,0]啟動後，會檢查前面的數據，
	 * 是不是有掉落的跡象，
	 * 以協助判是否為摔。
	 * 檢查的依據是取震痛數據的前10筆資料，
	 * 如果符合乖巧數據(每組的x,y,z值皆互相差正負3以內)
	 * 才會啟發掉落畫面
	 */
	ArrayList listbefore000;	
	/**
	 * 經測試發現如果是正常摔落的數據，
	 * 後面的x值不會亂七八槽
	 * 這個集合用來存放反作用力[0,0,0]之後的數據
	 * 好讓程式能判斷︰手機持續在動、還是摔後的靜止狀態
	 */
	ArrayList<String> listAfter000;
	/**
	 * 一旦為true時，記錄檔便開始儲存[0,0,0]之後的重力數據
	 */
	boolean startRecord=false;
	/**
	 * [0,0,0]後的檢查次數記錄，
	 * 系統會依程式片斷所寫的i總值，
	 * 去計算要去量測幾筆數值以判斷是否為「真摔」
	 */
	int i=0;
	/**
	 * 使用者在SharedPreferences裡所設定的感測值靈敏度
	 */
	int sensitivity;
	/**
	 * 感測器靈敏度<br/>
	 * 0:最低 →需要4組相同重感數據才能啟動警報畫面
	 */
	final int LOW_SENSITIVITY=0;
	/**
	 * 感測器靈敏度<br/>
	 * 1:中等 →需要3組相同重感數據以啟動警報畫面
	 */	
	final int MEDIUM_SENSITIVITY=1;
	/**
	 * 感測器靈敏度<br/>
	 * 2:最高 →只要2組相同重感數據就能啟動警報畫面
	 */
	final int HIGH_SENSITIVITY=2;
	
	
	/**
	 * 啟動Sensor
	 * @param context 傳入取得Sensor的主體
	 * @param sensorType 欲啟動Sensor的類型
	 * @param sensorRate 欲啟動Sensor的感測度
	 */
	public void startSensor(Context getContext,int sensorType, int sensorRate){
		context=getContext;
		
		
		sensormanager=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list=sensormanager.getSensorList(sensorType);
		
		sensormanager.registerListener(MySensor.this,list.get(0), sensorRate);
		listAfter000 = new ArrayList();
		listbefore000 = new ArrayList();
	}
	
	
	public void stopSensor(){
		if(sensormanager!=null){
			sensormanager.unregisterListener(this);
		}
	}

	private void sendBroadToFallen(Context context){
		Intent intent=new Intent();
		intent.setAction("STARTFALLEN");
		intent.putExtra("filter", "sendBroadcastFrom: STARTFALLEN");
		context.sendBroadcast(intent);
	}
	
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		
		//從系統設定值先抓取要用哪種感度
		sensitivity=MySharedPreferences.getPreference(context, "sensitivity", 1);
		
		float[] buf=event.values;
		
		float a=buf[0];
		float b=buf[1];
		float c=buf[2];
		int x=(int) Math.abs(a);
		int y=(int) Math.abs(b);
		int z=(int) Math.abs(c);
		
		//這段是要取乖巧數據專用的判斷式
		if(!(x<2 && y<2 && z<2) && startRecord==false ){
			WriteToTXT.writeLogToTXT("x"+String.valueOf(x)+", y"+String.valueOf(y)+", z"+String.valueOf(z));
			listbefore000.add(new int[]{x,y,z});

			//乖巧數據為震痛數據前6組，讓之後的Method能檢查
			if(listbefore000.size()==7){
				listbefore000.remove(0);
			}
		}
		
		
		//撞到地板得到[0,0,0]才產生摔落數據
		if(x<2 && y<2 && z<2){
			Log.i(tag, "into x,y,z<2");
			startRecord=true;			
			
		}else{
			//否則就是發送廣播標籤︰FALLENSENSORCHANGED，目的是Fallen裡不斷播動畫和音效
			Intent intent2=new Intent();
			intent2.setAction("FALLENSENSORCHANGED");
			intent2.putExtra("filter", "sendBroadcastFrom: FALLENSENSORCHANGED");
			context.sendBroadcast(intent2);
//			Log.i(tag, "sensor move");
			
		}

		waitForSameNums(x,y,z);

		
		//如果是3面向都大於15，發送廣播標籤︰STARTFALLEN，目的是開啟Fallen事件
//		if(a>15||b>15||c>15){
	/*	if(Ahmydroid.times==0){
			MyLog.writeLogToTXT(a,b,c);
	
		}else if(Ahmydroid.times==1){
			MyLog.writeLogToTXT2(a,b,c);
			
		}*/
		
		
		
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * [0,0,0]震痛數據被啟動後，
	 * 進來該函式等待靜止數據。
	 * 註︰從該函式中，正式劃分3種感測度等級，
	 * 取決於要程式等待幾組靜止數據才會啟發掉落畫面
	 * @param x 目前x的重力值
	 * @param y 目前y的重力值
	 * @param z 目前z的重力值
	 */
	private void waitForSameNums(int x,int y, int z){

		if(startRecord==true){
			
			if((x<2 && y<2 && z<2)){
				WriteToTXT.writeLogToTXT("====== x"+String.valueOf(x)+", y"+String.valueOf(y)+", z"+String.valueOf(z)+"======");
			}else{
				WriteToTXT.writeLogToTXT("---x"+String.valueOf(x)+", y"+String.valueOf(y)+", z"+String.valueOf(z)+"---");
			}
			
			listAfter000.add(String.valueOf(x)+String.valueOf(y)+String.valueOf(z));
			
			//如果使用者設成高感度，針測2組數據相同就啟動警報畫面
			//這組數據很容易啟動Fallen畫面
			switch(sensitivity){
			case HIGH_SENSITIVITY:
				Log.i(tag, "into sensitivity==HIGH_SENSITIVITY");
				if(listAfter000.size()==2&i<6){
					Log.i(tag, "now array0= "+listAfter000.get(0));
					Log.i(tag, "now array1= "+listAfter000.get(1));
					Log.i(tag, "i="+i);
					
					if(listAfter000.get(0).equals(listAfter000.get(1))){
						Log.i(tag, "into arraylist0 == arraylist1");
						//如果達成力道則廣播以開啟Fallen.java
						sendBroadToFallen(context);	
						
						startRecord=false;
						i=0;
					}else{
						Log.i(tag, "into ! arraylist.size()==2 & i<6");
						listAfter000.remove(0);
						i++;				
					}
			
				}else if(i>5){
					Log.i(tag, "set startRecord=false");
					startRecord=false;
					i=0;
					listAfter000.clear();
					Log.i(tag, "now i is: "+i);
				}
				break;
			case MEDIUM_SENSITIVITY:
				//中感度沒有用到乖巧數據判斷，但是他有用乖巧的末2組去判斷是否震痛前2組數據是否是平放狀態
				//另外，在震痛後的數據只等待3組，若相等，才啟動摔落畫面
				Log.i(tag, "into sensitivity==MEDIUM_SENSITIVITY");
				//如果感度被設為中感度
				if(listAfter000.size()==3&i<8){
					Log.i(tag, "now array0= "+listAfter000.get(0));
					Log.i(tag, "now array1= "+listAfter000.get(1));
					Log.i(tag, "now array2= "+listAfter000.get(2));
					Log.i(tag, "i="+i);
					
					if(listAfter000.get(0).equals(listAfter000.get(1))&&listAfter000.get(1).equals(listAfter000.get(2))){
						Log.i(tag, "into arraylist0 == arraylist1 == arraylist2");
						
						ArrayList checklistbefore000=new ArrayList();
						//將儲存乖巧數據的ArrayList複製一份出來檢查
						//讓原本的ArrayList能夠繼續存放數據庫
						checklistbefore000=(ArrayList) listbefore000.clone();
						if(checkFlatOrNot(checklistbefore000)==true){
							//如果判斷震痛數據前是0,0,10則不啟動摔落畫面
							startRecord=false;
							i=0;
							listAfter000.clear();
							
						}else{
							Log.i(tag, "276");
							//如果達成力道則廣播以開啟Fallen.java
							sendBroadToFallen(context);
							startRecord=false;
							i=0;
						}
						
						
					}else{
						Log.i(tag, "into ! arraylist.size()==3 & i<8");
						listAfter000.remove(0);
						i++;				
					}
			
				}else if(i>7){
					Log.i(tag, "set startRecord=false");
					startRecord=false;
					i=0;
					listAfter000.clear();
					Log.i(tag, "now i is: "+i);
				}
				break;
			case LOW_SENSITIVITY:
				Log.i(tag, "into sensitivity==LOW_SENSITIVITY");
				//如果感度被設為低感度，將會很難啟動畫面
				if(listAfter000.size()==4&i<10){
					Log.i(tag, "now array0= "+listAfter000.get(0));
					Log.i(tag, "now array1= "+listAfter000.get(1));
					Log.i(tag, "now array2= "+listAfter000.get(2));
					Log.i(tag, "now array3= "+listAfter000.get(3));
					Log.i(tag, "i="+i);
					
					if(listAfter000.get(0).equals(listAfter000.get(1))&&listAfter000.get(1).equals(listAfter000.get(2))&&listAfter000.get(2).equals(listAfter000.get(3))){
						Log.i(tag, "into arraylist0 == arraylist1 == arraylist2 == arraylist3");
						
						/*此為舊寫法。新寫法加入了乖巧數據的判斷模式
						 * //如果達成力道則廣播以開啟Fallen.java
						sendBroadToFallen(context);*/
						
						//啟動乖巧運算
						checkGoodChildNums();
						listAfter000.clear();//要記得把listAfter000清空，否則乖巧公式若沒啟動Fallen，下一次listAfter000這裡會失效
						
						
						startRecord=false;
						i=0;
					}else{
						Log.i(tag, "into ! arraylist.size()==4 & i<10");
						listAfter000.remove(0);
						i++;				
					}
			
				}else if(i>9){
//					Log.i(tag, "set startRecord=false");
					startRecord=false;
					i=0;
					listAfter000.clear();
//					Log.i(tag, "now i is: "+i);
				}
				break;
			}
		}
	
	}
	
	/**
	 * 檢查在震痛數據前是否為平放狀態，
	 * 如果前面5組數據都一樣，代表是平放狀態
	 * @return 平放狀態，回傳true 
	 */
	private boolean checkFlatOrNot(ArrayList checklistbefore000){
			boolean return_value=false;
			int position0_bad_count=0;
			int position1_bad_count=0;
			int position2_bad_count=0;
			
			Log.i(tag, "349");
		try{
			//如果壞孩子數據小於2，判斷別正常摔落。
			//啟動摔落畫面
			Log.i(tag, "353");
//			Log.i(tag, "checklistbefore000.SIZE: "+checklistbefore000.size());
			
			//震痛數據破壞0,0,10的組數
			
			
			for(int i=0;i<checklistbefore000.size();i++){
				
					//如果迴圈已經運行到最後一組，就不用再算了
//					if(i==checklistbefore000.size()-1){
//						break;
//					}else{
						int[] a =(int[])checklistbefore000.get(i);
						int[] b =(int[])checklistbefore000.get(i+1);
						
					
							if(a[0]!=b[0]){
								position0_bad_count++;
							}
						
						
						
							if(a[1]!=b[1]){
								position1_bad_count++;
							}
						
						
							if(a[2]!=b[2]){
								position2_bad_count++;
							}
							
						
					
						Log.i(tag, "a: "+a[0]+","+a[1]+","+a[2]+" ;b: "+b[0]+","+b[1]+","+b[2]);
//					}
					
				
			}
			
		}catch(ArrayIndexOutOfBoundsException e){
			
			Log.i(tag, "397:ArrayIndexOutOfBoundsException"+e.getMessage());
		}catch(IndexOutOfBoundsException e){
			
			Log.i(tag, "400:IndexOutOfBoundsException"+e.getMessage());
		}		finally{
			if((position0_bad_count==0 & position1_bad_count==0)
					|(position1_bad_count==0 & position2_bad_count==0)
					|(position2_bad_count==0 & position0_bad_count==0)){
				return_value=true;
				Log.i(tag, "Now is flating!! stop Fallen");
			}
			Log.i(tag, "position0_bad_count: "+position0_bad_count+" ,position1_bad_count: "+position1_bad_count+" ,position2_bad_count: "+position2_bad_count);
		}
	
		
		
		
		return return_value;
		
	}
	
	
	/**
	 * 檢查震動數據前的乖巧數據，
	 * 乖巧數據是x,y,z彼此間的引數，皆不超過正負3
	 */
	private void checkGoodChildNums(){
		Log.i(tag, "into checkGoodChildNums");
		ArrayList checklistbefore000=new ArrayList();
		int[] bufferBefore=null;
		int[] bufferNow=null;
		int badResult=0;
		//將儲存乖巧數據的ArrayList複製一份出來檢查
		//讓原本的ArrayList能夠繼續存放數據庫
		checklistbefore000=(ArrayList) listbefore000.clone();
		listbefore000.clear();//將程式一直在記錄的乖巧數據庫清空，讓新數據能一直記進來，
							  //而不會被這個函式裡的運算影響
		
		try{

			Log.i(tag, "checklistbefore000 size: "+checklistbefore000.size());
		
				for(int i=0;i<checklistbefore000.size();i++){
					//取得之前一組的乖巧數據
					bufferBefore=(int[]) checklistbefore000.get(i);
					//取得現在的乖巧數據
					bufferNow=(int[]) checklistbefore000.get(i+1);
					
					
					Log.i(tag, "bufferBefore: "+bufferBefore[0]+","+bufferBefore[1]+","+bufferBefore[2]);
					if(bufferNow!=null)Log.i(tag, "bufferNow: "+bufferNow[0]+","+bufferNow[1]+","+bufferNow[2]);
					
					
						//乖巧數據公式，若前組數據跟後組數據相差3以上，為不乖數據
						if((Math.abs((bufferNow[0]-bufferBefore[0]))>3)
								   |  (Math.abs((bufferNow[1]-bufferBefore[1]))>3)
								   |  (Math.abs((bufferNow[2]-bufferBefore[2]))>3)){
									
									badResult++;
									Log.i(tag, " --> BAD Result count:"+badResult);
								}				
					
				}
			}catch(IndexOutOfBoundsException e){
				Log.i(tag, "370:IndexOutOfBoundsException"+e.getMessage());
				Log.i(tag, "Bad Result is: "+badResult);
			}
		  
		
//		try{
			//如果壞孩子數據小於2，判斷別正常摔落。
			//啟動摔落畫面
//			int[] buffer=(int[]) checklistbefore000.get(checklistbefore000.size()-1);	
		
			//預防值為0,0,9或0,0,10，是因為有時候放在桌面啟動小安也會進入震痛數據
//			if((buffer[0]==0&buffer[1]==0&buffer[2]==9)|(buffer[0]==0&buffer[1]==0&buffer[2]==10)){
//				Log.i(tag, "num is: "+buffer[0]+","+buffer[1]+","+buffer[2]+", doint nothing...");
			if(checkFlatOrNot(checklistbefore000)==true){
				//如果判斷為震痛數據前判斷為0,0,10，不做任何事情
			}else if(badResult<=2){
				Log.i(tag, "Bad Result is: "+badResult+", into badResult<=2");
				sendBroadToFallen(context);
			}
//		}catch(ArrayIndexOutOfBoundsException e){
//			Log.i(tag, "388:ArrayIndexOutOfBoundsException"+e.getMessage());
//		}
		
		
	}
}