package comp3350.plarty.presentation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.Button;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import comp3350.plarty.R;

/**
 * This Activity lets a user select dates in a month to view the events they have
 * scheduled on that day.
 * It also includes a "Today" button, from which the user can automatically see
 * the events they've scheduled on the day they launch the app.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class MonthlyScheduleActivity extends ScheduleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monthlyschedule);
		initializeHeader(R.string.month_header_label);

		CalendarView calendar = findViewById(R.id.MonthCalendar);
		// select the current date by default
		calendar.setDate(Calendar.getInstance().getTimeInMillis());

		//months are 0-indexed, so we have to add 1 when we pass it as a parameter
		calendar.setOnDateChangeListener((calendarView, year, month, date) -> goToDailyView(year, date, month+1));

		//get Day,Month,Year from date
		SimpleDateFormat daySdf = new SimpleDateFormat("dd");
		SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
		String dayS = daySdf.format(new Date(calendar.getDate()));
		String monthS = monthSdf.format(new Date(calendar.getDate()));
		String yearS = yearSdf.format(new Date(calendar.getDate()));
		int day = Integer.parseInt(dayS);
		int month = Integer.parseInt(monthS);
		int year = Integer.parseInt(yearS);

		Button todayButton = findViewById(R.id.today_button);
		todayButton.setOnClickListener(calendarView -> goToDailyView(year, day, month));
	}

	private void goToDailyView(int year, int date, int month) {
		Intent dailyViewIntent = new Intent(this, DailyScheduleActivity.class);
		dailyViewIntent.putExtra("Date", new int[] {year, month, date});
		startActivity(dailyViewIntent);
	}
}
