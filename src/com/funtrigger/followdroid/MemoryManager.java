package com.funtrigger.followdroid;
import android.os.Debug;
import android.util.Log;
import com.funtrigger.followdroid.R;

/**
 * 記憶體偵測類
 * @author simon
 *
 */
public class MemoryManager {
	/**
	 * 偵測記憶體已使用量
	 * @return
	 */
	public static int getUsedMemory(){
		 
        int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
        Log.i("tag", "usedMemory: "+Debug.getNativeHeapAllocatedSize()/ 1048576L);
		return usedMegs;
	}
	
	public static int getMemoryAmounts(){
		  
		int AllMegs = (int)(Runtime.getRuntime().maxMemory()/ 1048576L);
		Log.i("tag", "MemoryAmounts: "+AllMegs);
		return AllMegs;
	}
}
