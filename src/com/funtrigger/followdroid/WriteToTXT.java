package com.funtrigger.followdroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.os.Environment;
import android.util.Log;
import com.funtrigger.followdroid.R;

/**
 * 該類別提供各種函式，讓欲輸出的文字可以寫進SD卡裡
 * @author simon
 *
 */
public class WriteToTXT {
	private static String tag="tag";
	
	/**
	 * 將欲輸出的數據寫入SD卡成txt檔<br/>
	 * 該txt檔會隨著函數的呼叫不斷的被增加
	 * @param text 欲印成txt檔的字串
	 */
	public static void writeLogToTXT(String text){

			if (android.os.Environment.getExternalStorageState().equals(//如果有SD卡，才寫資料
			        android.os.Environment.MEDIA_MOUNTED)) {
				File file=new File(Environment.getExternalStorageDirectory().getPath()+"/ahmydroidLOG.txt");
				RandomAccessFile ra;
				try {
					
					    ra = new RandomAccessFile(file,"rw");
					    if(ra.read()!=0){
					    	ra.seek(ra.length());
							ra.writeBytes("\n");

						}
					 
						ra.writeBytes("(" + MyTime.getDate() + ")" + MyTime.getHHMMSS1()+" - "+text);
						ra.close();
					} catch (FileNotFoundException e) {
						Log.i(tag, "FileNotFoundException");
					}catch (IOException e) {
						Log.i(tag, "IOException");
					}				 
			}
	}
	
	
}
