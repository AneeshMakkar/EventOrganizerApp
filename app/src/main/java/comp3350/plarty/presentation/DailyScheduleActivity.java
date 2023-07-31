package comp3350.plarty.presentation;

import comp3350.plarty.R;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import org.joda.time.DateTime;

/**
 * This activity displays all of a user's events for a given day.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class DailyScheduleActivity extends ScheduleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dailyschedule);
        initializeHeader(R.string.day_header_label);

        int [] dateArray = getIntent().getIntArrayExtra("Date");
        DateTime selectedDate = new DateTime(dateArray[0], dateArray[1], dateArray[2], 0, 0);
        displaySchedule(R.id.daily_schedule_events, selectedDate);
    }
}
