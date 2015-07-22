package com.example.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	HorizonCalendarView viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		viewPager = (HorizonCalendarView) findViewById(R.id.viewpager);
	}
	
	public void btnClick(View v){
		viewPager.jump2Today();
	}
}
