package com.funtrigger.followdroid;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.funtrigger.followdroid.R;

public class MyListAdapter extends BaseAdapter{
		Context c;
		MySqlHelper sqlHelper;
		static Cursor cursor = null;
		int count = 0;
		
	public MyListAdapter(Context mContext, MySqlHelper sqlHelper){
		c = mContext;
		this.sqlHelper = sqlHelper;
		
	}
	@Override
	public int getCount() {
		cursor = sqlHelper.getAll();
		count = cursor.getCount();
		cursor.close();
		return count;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("tag", "into getView");
		Holder holder = null;
		if(convertView == null){
			LayoutInflater li = LayoutInflater.from(c);
			convertView = li.inflate(R.layout.mylist, null);
			holder = new Holder();
			holder.text = (TextView) convertView.findViewById(R.id.text);
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}	
			cursor = sqlHelper.getAll();
			cursor.moveToPosition(position);
			holder.text.setText(cursor.getString(1));
			holder.image.setImageResource(R.drawable.remove);
			holder._id = cursor.getInt(0);
			
			cursor.close();
		return convertView;
	}
	
	public class Holder{
		TextView text;
		ImageView image;
		/**
		 * Save the _id from database row
		 */
		Integer _id;
	}


}
