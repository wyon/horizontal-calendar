package com.example.calendar.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("wyon", "receiver="+intent.getStringExtra("alarm"));
		boolean initTimeExist = intent.hasExtra("inittime");
		Log.e("wyon", "startTimeExist = " + initTimeExist);
		if(initTimeExist){
			long length = System.currentTimeMillis() - intent.getLongExtra("inittime", -1);
			Log.e("wyon", "time=" + length / 1000 +"s" + "; " + length + "ms");
		}
	}
	
}
