package com.example.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class MainActivity extends Activity {
	ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
