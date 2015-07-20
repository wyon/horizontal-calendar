package com.example.calendar;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HorizonCalendarView extends ViewPager implements AdapterView.OnItemClickListener {

	public static final String TAG = HorizonCalendarView.class.getSimpleName();

	private static final String[] WEEK_NAME = { "日", "一", "二", "三", "四", "五", "六" };
	private static final int PAGE_COUNT = 100;

	private GridView[] viewPageList = new GridView[4];
	private CalendarAdpater adapter;
	private Calendar calendar;
	private DateHolder todayHolder;
	private DateHolder currentHolder; // 选中的时间

	public HorizonCalendarView(Context context) {
		super(context);
		init();
	}

	public HorizonCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		for (int i = 0; i < viewPageList.length; i++) {
			viewPageList[i] = addGridView();
		}
		calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		todayHolder = new DateHolder();
		todayHolder.setValue(calendar);
		adapter = new CalendarAdpater();
		this.setAdapter(adapter);
		setCurrentItem(PAGE_COUNT / 2, true);
	}

	private GridView addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		GridView gridView = new GridView(getContext());
		gridView.setNumColumns(7);
		gridView.setGravity(Gravity.CENTER_VERTICAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setAdapter(new CalendarItemsAdapter());
		gridView.setLayoutParams(params);
		gridView.setOnItemClickListener(this);
		return gridView;
	}

	public void selectDate(DateHolder date) {
		// if (date == null)
		// return;
		// s
	}

	public DateHolder getToday() {
		return this.todayHolder;
	}

	class CalendarAdpater extends PagerAdapter {

		int lastPosition = -1;

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(viewPageList[position % viewPageList.length]);

		}

		/**
		 * 载page进去，用当前的position 除以 数组长度取余数
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			Log.i(TAG, "instantiateItem: position=" + position);
			GridView item = (GridView) viewPageList[position % viewPageList.length];
			generateCalendar(item, position);
			((ViewPager) container).addView(item, 0);
			return item;
		}

		private void generateCalendar(GridView view, int position) {
			if (lastPosition == position)
				return;
			DateHolder start;
			int weekOffset = 0;
			if (lastPosition < 0) {
				// 第一次加载，初始化时间
				int rollValue = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				if (rollValue > 0) {
					calendar.add(Calendar.DAY_OF_YEAR, -1 * rollValue);
				}
				start = new DateHolder();
				start.setValue(calendar);
			} else {
				GridView lastView = (GridView) viewPageList[lastPosition % viewPageList.length];
				CalendarItemsAdapter adapter = (CalendarItemsAdapter) lastView.getAdapter();
				start = adapter.dates[0];
				weekOffset = position - lastPosition;
			}

			((CalendarItemsAdapter) view.getAdapter()).computeCalendar(start, weekOffset);

			lastPosition = position;
		}
	}

	class CalendarItemsAdapter extends BaseAdapter {
		DateHolder[] dates = new DateHolder[7];
		private int selectedPosition = -1;

		public void computeCalendar(DateHolder start, int weekOffset) {
			Log.i(TAG, "computeCalendar: start=" + start + "; weekOffset=" + weekOffset);
			if (start.equals(dates[0]) && weekOffset == 0)
				return;
			calendar.set(start.year, start.month, start.day);
			if (weekOffset != 0) {
				calendar.add(Calendar.WEEK_OF_YEAR, weekOffset);
			}
			for (int i = 0; i < dates.length; i++) {
				if (dates[i] == null) {
					dates[i] = new DateHolder();
				}
				dates[i].setValue(calendar);
				calendar.add(Calendar.DAY_OF_YEAR, 1);

				Log.i(TAG, "computeCalendar: result[" + i + "] = " + dates[i]);
			}
		}

		public void clearSelected() {
			selectedItem(-1);
		}

		public DateHolder selectedItem(int position) {
			if (position < 0 || position >= dates.length) { // clear selected
				if (selectedPosition >= 0) { // had selected
					dates[selectedPosition].tvDay.setSelected(false);
					selectedPosition = -1;
				}
				return null;
			}
			if (position != selectedPosition) {
				dates[position].tvDay.setSelected(true);
				if (selectedPosition >= 0) {
					dates[selectedPosition].tvDay.setSelected(false);
				}
				selectedPosition = position;
			}
			return dates[position];
		}

		@Override
		public int getCount() {
			return dates.length;
		}

		@Override
		public Object getItem(int position) {
			return dates[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DateHolder date = (DateHolder) getItem(position);
			
			Log.i(TAG, "getView convertView == null" + (convertView == null) + "date:" + date);
			
			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.calendar_item, null);
			}else{
				if(date == convertView.getTag()){
//					
//					Log.i(TAG, "convertView tag same");
//					//((TextView) convertView.findViewById(R.id.tv_day)).setSelected(true);
//					
					return convertView;
				}
			}
			
			convertView.setTag(date);
			
			TextView tvWeekName, tvDay;
			
			tvWeekName = (TextView) convertView.findViewById(R.id.tv_week_name);
			tvDay = (TextView) convertView.findViewById(R.id.tv_day);
			
			tvWeekName.setText(WEEK_NAME[position]);
			tvDay.setText(String.valueOf(date.day));

			date.tvDay = tvDay;

			int selectorID = R.drawable.selector_calendar_notoday;
			if (date.equals(todayHolder)) {
				selectorID = R.drawable.selector_calendar_today;
			}

			tvDay.setBackgroundResource(selectorID);
			
			return convertView;
		}
	}

	public static class DateHolder {
		int year;
		int month;
		int day;
		int weekOfYear;
		TextView tvDay;

		DateHolder() {
		}

		/**
		 * @param year
		 *            1970 - 2100
		 * @param month
		 *            0 - 11
		 * @param day
		 *            1 - 31
		 */
		public DateHolder(int year, int month, int day) {
			if (year < 1970 || year > 2100)
				throw new IllegalArgumentException("year must >= 1970 and <=2100");
			if (month < 0 || month > 11)
				throw new IllegalArgumentException("month must be 0 - 11");
			if (day < 1 || day > 31)
				throw new IllegalArgumentException("day is invalid");
			this.year = year;
			this.month = month;
			this.day = day;

			Calendar calendar = Calendar.getInstance();
			calendar.setFirstDayOfWeek(Calendar.SUNDAY);
			calendar.set(this.year, this.month, this.day);
			setValue(calendar);
		}

		void setValue(Calendar calendar) {
			this.year = calendar.get(Calendar.YEAR);
			this.month = calendar.get(Calendar.MONTH);
			this.day = calendar.get(Calendar.DAY_OF_MONTH);
			this.weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
		}

		public int getYear() {
			return this.year;
		}

		public int getMonth() {
			return this.month;
		}

		public int getDay() {
			return this.day;
		}

		public boolean equals(DateHolder o) {
			if (o == null) {
				return false;
			}
			if (o == this) {
				return true;
			}
			if (this.year == o.year && this.month == o.month && this.day == o.day) {
				return true;
			}
			return false;
		}

		public int compareTo(DateHolder o) {
			if (o == null) {
				throw new IllegalArgumentException("the arg is null");
			}
			if (equals(o)) {
				return 0;
			}
			if (this.year > o.year) {
				return 1;
			} else if (this.year < o.year) {
				return -1;
			}
			if (this.month > o.month) {
				return 1;
			} else if (this.month < o.month) {
				return -1;
			}
			if (this.day > o.day) {
				return 1;
			}
			return -1;
		}

		@Override
		public String toString() {
			return String.format("%1$s年%2$s月%3$s日", this.year, this.month + 1, this.day);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedCalendar((GridView) parent, position);
	}

	private void selectedCalendar(GridView view, int position) {
		DateHolder selectedDate = ((CalendarItemsAdapter) view.getAdapter()).selectedItem(position);
		if (currentHolder != null && currentHolder.weekOfYear != selectedDate.weekOfYear) {
			for (GridView gridView : viewPageList) {
				if (gridView != view) {
					((CalendarItemsAdapter) gridView.getAdapter()).clearSelected();
				}
			}
		}
		
		if(!selectedDate.equals(currentHolder)){
			currentHolder = selectedDate;
			//onSelectedCalendarChanged();
		}
		Log.i(TAG, "selected: " + currentHolder);
	}
}
