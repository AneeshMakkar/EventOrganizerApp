package comp3350.plarty.presentation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;

import java.util.ArrayList;
import comp3350.plarty.R;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.User;

/**
 * This provides a base class for schedule activities.
 * It is only subclassed by DailyScheduleActivity, but in further iterations could
 * be adapted to provide a WeeklyScheduleActivity that shows a full week of
 * events.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class ScheduleActivity extends AppCompatActivity {
	private float dpScale;
	final private User currUser;
	final private AccessUsers accessUsers;
	private ArrayList<Event> currSchedule;
	private int inactiveId = -1;

	public ScheduleActivity() {
		super();
		accessUsers = new AccessUsers();
		currUser = accessUsers.getMainUser();

		Event inactive = accessUsers.getInactive(currUser);
		if(inactive != null) {
			inactiveId = accessUsers.getInactive(currUser).getId();
		}
		currSchedule = null;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dpScale = getResources().getDisplayMetrics().density;
	}

	/**
	 * Displays time blocks based on events in the User's schedule on the
	 * given date.
	 *
	 * @param scheduleId		depending on which "schedule view" calls this
	 *                          method (only Daily exists currently), the
	 *                          dimensions of the time blocks changes.
	 * @param timestamp			the day of the events we want to display
	 */
	protected void displaySchedule(int scheduleId, DateTime timestamp) {
		RelativeLayout schedule = findViewById(scheduleId);

		DateTime startOfDay = timestamp.withTimeAtStartOfDay();
		DateTime endOfDay = startOfDay.plusDays(1);
		Interval dayInterval = new Interval(startOfDay, endOfDay);

		//in case events span multiple days, we check events from yesterday til
		//the end of today
		currSchedule = accessUsers.getSchedule(currUser, startOfDay.minusDays(1), endOfDay);
		for(Event anEvent : currSchedule) {
			if(dayInterval.overlaps(anEvent.getInterval())) {
				TextView eventSlot = new TextView(schedule.getContext());
				schedule.addView(eventSlot);

				ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) eventSlot.getLayoutParams();
				params.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;

				int eventStart = anEvent.getStart().getMinuteOfDay();
				int eventEnd = anEvent.getEnd().getMinuteOfDay();

				if (anEvent.getStart().isBefore(startOfDay)) {
					eventStart = 0;
				}

				if (anEvent.getEnd().isAfter(endOfDay)) {
					eventEnd = DateTimeConstants.MINUTES_PER_DAY;
				}

				//to offset the time blocks correctly, their height corresponds
				//with their length in minutes, and they have a margin from
				//the top of the view equal to the time in minutes they start at
				params.height = (int)(dpScale * (eventEnd - eventStart));
				params.setMargins(0, (int)(dpScale * eventStart), 0, 0);
				eventSlot.setLayoutParams(params);

				eventSlot.setText(anEvent.getName());

				//"inactive" events will display with different colours
				if(anEvent.getId() == inactiveId) {
					eventSlot.setTextColor(getResources().getColor(R.color.color_white));
					eventSlot.setBackgroundColor(getResources().getColor(R.color.color_primary_dark));
				} else {
					eventSlot.setTextColor(getResources().getColor(R.color.color_text));
					eventSlot.setBackgroundColor(getResources().getColor(R.color.color_accent));
				}

				eventSlot.setPadding((int)dpScale * 10,
						(int)dpScale * 10,
						0, 0);
			}
		}
	}

	protected void initializeHeader(int headerLabelId) {

		Toolbar header = findViewById(R.id.header_bar);
		header.setNavigationOnClickListener(view -> finish());

		TextView headerTitle = new TextView(this);
		headerTitle.setText(headerLabelId);
		headerTitle.setTextAppearance(this, R.style.header_text);
		header.addView(headerTitle);

		MenuInflater profileMenu = getMenuInflater();
		profileMenu.inflate(R.menu.header_menu, header.getMenu());
	}

	public void showProfile(MenuItem menuItem) {
		Intent goToProfile = new Intent(this, UserProfileActivity.class);
		startActivity(goToProfile);
	}
}
