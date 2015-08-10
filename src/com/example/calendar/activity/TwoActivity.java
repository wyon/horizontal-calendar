package com.example.calendar.activity;

import com.example.calendar.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class TwoActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_two);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		intent.putExtra("ok", "ok");
		setResult(RESULT_OK, intent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
	
	BaseAdapter ba;
	ListView lv;
}
