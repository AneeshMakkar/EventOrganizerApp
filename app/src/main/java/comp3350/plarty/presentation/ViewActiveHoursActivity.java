package comp3350.plarty.presentation;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import comp3350.plarty.R;
import comp3350.plarty.business.AccessRecurringEvents;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.objects.User;

/**
 * This Activity lets users review their specified active hours and update them.
 * Users cannot update their active hours if they've already scheduled events
 * outside of the new active hours.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class ViewActiveHoursActivity extends Activity {

	private AccessUsers accessUsers;
	private AccessRecurringEvents accessRecurringEvents;
	private User currUser;
	private TextView timesDisplay;
	private TimePicker start, end;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activehours);

		accessUsers = new AccessUsers();
		accessRecurringEvents = new AccessRecurringEvents();
		currUser = accessUsers.getMainUser();

		Toolbar header = findViewById(R.id.header_bar);
		header.setNavigationOnClickListener(view -> finish());

		TextView headerTitle = new TextView(this);
		headerTitle.setText(R.string.active_hours_label);
		headerTitle.setTextAppearance(this, R.style.header_text);
		header.addView(headerTitle);

		start = findViewById(R.id.set_active_start);
		end = findViewById(R.id.set_active_end);

		Button saveActive = findViewById(R.id.save_active_button);
		saveActive.setOnClickListener(view -> setTime());

		timesDisplay = findViewById(R.id.active_times_display);
		refreshView();
	}

	/**
	 * Re-retrieves the user's active hours and displays them.
	 * If the user has not set active hours, they are displayed as 0:00-0:00.
	 */
	private void refreshView() {
		int startHour, startMinute, endHour, endMinute;

		if(accessUsers.getInactive(currUser) != null) {
			startHour = accessUsers.getInactiveEndHour(currUser);
			startMinute = accessUsers.getInactiveEndMinute(currUser);
			endHour = accessUsers.getInactiveStartHour(currUser);
			endMinute = accessUsers.getInactiveStartMinute(currUser);
		} else {
			startHour = 0;
			startMinute = 0;
			endHour = 0;
			endMinute = 0;
		}

		start.setHour(startHour);
		start.setMinute(startMinute);
		end.setHour(endHour);
		end.setMinute(endMinute);

		String display = StringConverter.timeToString(startHour, startMinute)+" - "
				+StringConverter.timeToString(endHour, endMinute);

		timesDisplay.setText(display);
	}

	public void setTime() {
		if(!accessRecurringEvents.setActiveHours(currUser, start.getHour(), start.getMinute(), end.getHour(), end.getMinute())) {
			FeedbackDialog.create(this, "Error", "You already have scheduled events outside that interval.");
		} else {
			FeedbackDialog.create(this, "Success", "Saved new active hours.");
			refreshView();
		}
	}
}
