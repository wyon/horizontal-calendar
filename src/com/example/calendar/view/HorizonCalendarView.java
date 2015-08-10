package com.example.calendar.view;

import java.util.Calendar;
import java.util.Date;

import com.example.calendar.R;
import com.example.calendar.R.drawable;
import com.example.calendar.R.id;
import com.example.calendar.R.layout;

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
		todayHolder.setValue(calendar, currentHolder);
		adapter = new CalendarAdpater();
		this.setAdapter(adapter);
		setCurrentItem(PAGE_COUNT / 2, true);
	}

	private GridView addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		GridView gridView = new GridView(getContext());
		gridView.setNumColumns(7);
		gridView.setGravity(Gravity.CENTER_HORIZONTAL);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridView.setVerticalSpacing(1);
		gridView.setHorizontalSpacing(1);
		gridView.setAdapter(new CalendarItemsAdapter());
		gridView.setLayoutParams(params);
		gridView.setOnItemClickListener(this);
		return gridView;
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
			try {
				((ViewPager) container).addView(item, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return item;
		}

		private void generateCalendar(GridView view, int position) {
			if (lastPosition == position)
				return;
			DateHolder start;
			int weekOffset = 0;
			if (lastPosition < 0) {
				// 第一次加载，初始化时间
				calendar.setTime(new Date());
				int rollValue = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				if (rollValue > 0) {
					calendar.add(Calendar.DAY_OF_YEAR, -1 * rollValue);
				}
				start = new DateHolder();
				start.setValue(calendar, currentHolder);
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
				dates[i].setValue(calendar, currentHolder);
				calendar.add(Calendar.DAY_OF_YEAR, 1);

				Log.i(TAG, "computeCalendar: result[" + i + "] = " + dates[i]);
			}
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
			}

			TextView tvWeekName, tvDay;

			tvWeekName = (TextView) convertView.findViewById(R.id.tv_week_name);
			tvDay = (TextView) convertView.findViewById(R.id.tv_day);
			tvDay.setTag(date);

			tvWeekName.setText(WEEK_NAME[position]);
			tvDay.setText(String.valueOf(date.day));

			int selectorID = R.drawable.selector_calendar_notoday;
			if (date.equals(todayHolder)) {
				selectorID = R.drawable.selector_calendar_today;
				if (currentHolder == null) {
					selectedCalendar((GridView) parent, convertView, position);
				}
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
		int dayOfWeek;
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
			setValue(calendar, null);
		}

		void setValue(Calendar calendar, DateHolder selectedDate) {
			this.year = calendar.get(Calendar.YEAR);
			this.month = calendar.get(Calendar.MONTH);
			this.day = calendar.get(Calendar.DAY_OF_MONTH);
			this.weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
			this.dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			setSelected(this.equals(selectedDate));
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

		void setSelected(boolean isSelected) {
			if (tvDay != null && isSelected != tvDay.isSelected()) {
				tvDay.setSelected(isSelected);
			}
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

		@Override
		protected Object clone() {
			DateHolder obj = new DateHolder();
			obj.year = this.year;
			obj.month = this.month;
			obj.day = this.day;
			obj.weekOfYear = this.weekOfYear;
			obj.tvDay = this.tvDay;

			return obj;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		selectedCalendar((GridView) parent, view, position);
	}

	private void selectedCalendar(GridView parent, View view, int position) {
		DateHolder selectedDate = (DateHolder) ((CalendarItemsAdapter) parent.getAdapter()).getItem(position);
		if (selectedDate.tvDay == null) {
			selectedDate.tvDay = (TextView) view.findViewWithTag(selectedDate);
		}

		if (!selectedDate.equals(currentHolder)) {
			if (currentHolder != null && currentHolder.tvDay != selectedDate.tvDay) {
				currentHolder.setSelected(false);
			}
			selectedDate.setSelected(true);

			currentHolder = (DateHolder) selectedDate.clone();
			// onSelectedCalendarChanged();
		}
		Log.i(TAG, "selected: " + currentHolder);
	}
}
