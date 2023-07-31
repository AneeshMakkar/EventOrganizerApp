package comp3350.plarty.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.util.ArrayList;

import comp3350.plarty.R;
import comp3350.plarty.business.AccessSingleEvents;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;

/**
 * This Activity lets a user edit event information. They can either edit
 * existing event's information, or create a new one.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class CreateEventActivity extends Activity {

    private AccessSingleEvents accessSingleEvents;
    private AccessUsers accessUsers;
    private User currUser;
    private ArrayList<Integer> inviteeIds;
    private EditText name, location;
    private DatePicker startDate, endDate;
    private TimePicker startTime, endTime;
    private Event currEvent;
    private Button saveEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);
        accessSingleEvents = new AccessSingleEvents();
        accessUsers = new AccessUsers();
        currUser = new AccessUsers().getMainUser();
        inviteeIds = new ArrayList<>();

        name = findViewById(R.id.new_event_name);
        location = findViewById(R.id.new_event_location);
        startDate = findViewById(R.id.start_datepicker);
        endDate = findViewById(R.id.end_datepicker);
        startTime = findViewById(R.id.start_timepicker);
        endTime = findViewById(R.id.end_timepicker);

        saveEventButton = findViewById(R.id.save_event_button);
        saveEventButton.setEnabled(false);
        saveEventButton.setOnClickListener(view -> saveEvent(currEvent));
        Button inviteUserButton = findViewById(R.id.invite_users_button);
        inviteUserButton.setOnClickListener(view -> goToInviteUser());

        Toolbar header = findViewById(R.id.header_bar);
        header.setNavigationOnClickListener(view -> finish());

        TextView headerTitle = new TextView(this);
        headerTitle.setText(R.string.new_event_header_label);
        headerTitle.setTextAppearance(this, R.style.header_text);
        header.addView(headerTitle);

        TextWatcher fieldEditListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                checkEventInfo();
            }
        };

        DatePicker.OnDateChangedListener dateChangedListener = (datePicker, year, monthOfYear, dayOfMonth) -> checkEventInfo();
        TimePicker.OnTimeChangedListener timeChangedListener = (timePicker, hour, minute) -> checkEventInfo();

        DateTime initStart = DateTime.now();
        DateTime initEnd = DateTime.now().plusHours(1);

        startDate.init(initStart.getYear(), initStart.getMonthOfYear() - 1, initStart.getDayOfMonth(), dateChangedListener);
        endDate.init(initEnd.getYear(), initEnd.getMonthOfYear() - 1, initEnd.getDayOfMonth(), dateChangedListener);

        name.addTextChangedListener(fieldEditListener);
        location.addTextChangedListener(fieldEditListener);
        startTime.setOnTimeChangedListener(timeChangedListener);
        endTime.setOnTimeChangedListener(timeChangedListener);
        endTime.setHour(initEnd.getHourOfDay());

        int id = getIntent().getIntExtra("Event ID", -1);

        if (id >= 0) {
            currEvent = accessSingleEvents.getEvent(id);
            name.setText(currEvent.getName());
            location.setText(currEvent.getLocation());

            startDate.updateDate(currEvent.getStart().getYear(),
                    currEvent.getStart().getMonthOfYear() - 1,
                    currEvent.getStart().getDayOfMonth());

            startTime.setHour(currEvent.getStart().getHourOfDay());
            startTime.setMinute(currEvent.getStart().getMinuteOfHour());

            endDate.updateDate(currEvent.getEnd().getYear(),
                    currEvent.getEnd().getMonthOfYear() - 1,
                    currEvent.getEnd().getDayOfMonth());

            endTime.setHour(currEvent.getEnd().getHourOfDay());
            endTime.setMinute(currEvent.getEnd().getMinuteOfHour());

            ArrayList<User> invitees = accessSingleEvents.getInvitees((SingleEvent) currEvent);
            System.out.println(invitees);
            for (User user : invitees) {
                System.out.println("received invitee: "+user);
                inviteeIds.add(user.getId());
            }
        } else {
            goToInviteUser();
        }
    }

    private void checkEventInfo() {
        DateTime start = fieldsToDateTime(startDate, startTime);
        DateTime end = fieldsToDateTime(endDate, endTime);

        saveEventButton.setEnabled(accessSingleEvents.validEvent(
                name.getText().toString(),
                currUser,
                location.getText().toString(),
                start,
                end)
        );
    }

    protected void saveEvent(Event source) {
        DateTime start = fieldsToDateTime(startDate, startTime);
        DateTime end = fieldsToDateTime(endDate, endTime);

        if (source != null) {
            //edit an existing event
            if (accessSingleEvents.editEvent(currUser,
                    (SingleEvent) source,
                    name.getText().toString(),
                    location.getText().toString(),
                    start,
                    end)) {
                sendInvitations(source);
            } else {
                FeedbackDialog.create(this, "Error", "Event overlaps with something in your schedule!");
            }
        } else {
            //create a new event
            Event newEvent = accessSingleEvents.createSingleEvent(name.getText().toString(), currUser,
                    location.getText().toString(), start, end);
            if (newEvent == null) {
                FeedbackDialog.create(this, "Error","Event overlaps with something in your schedule!");
            } else {
                sendInvitations(newEvent);
            }
        }
    }

    private DateTime fieldsToDateTime(DatePicker date, TimePicker time) {
        return new DateTime(date.getYear(),
                date.getMonth() + 1,
                date.getDayOfMonth(),
                time.getHour(),
                time.getMinute());
    }

    private void goToInviteUser() {
        Intent inviteUsers = new Intent(this, InviteUserActivity.class);
        inviteUsers.putIntegerArrayListExtra("Invited users", inviteeIds);
        startActivityForResult(inviteUsers, RequestCode.INVITE_TO.ordinal());
    }

    /**
     * When returning from the Invite Users screen, updates the invitees according
     * to what the user checked off
     *
     * @param requestCode       the request code that called the returning Activity
     * @param resultCode        the result of the returning Activity
     * @param data              any data the returning Activity sent back; this
     *                          should contain a list of checked-off user IDs
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.INVITE_TO.ordinal() &&
                resultCode == RESULT_OK) {

            int[] newStartFields = data.getIntArrayExtra("Start datetime fields");
            int[] newEndFields = data.getIntArrayExtra("End datetime fields");

            if (newStartFields != null && newEndFields != null) {
                startDate.updateDate(newStartFields[0], newStartFields[1] - 1, newStartFields[2]);
                endDate.updateDate(newEndFields[0], newEndFields[1] - 1, newEndFields[2]);
                startTime.setHour(newStartFields[3]);
                startTime.setMinute(newStartFields[4]);
                endTime.setHour(newEndFields[3]);
                endTime.setMinute(newEndFields[4]);
            }

            inviteeIds = data.getIntegerArrayListExtra("Invited users");
        }
    }

    /**
     * Clears invitees and sends out a new batch of invites.
     *
     * @param event     the event to invite each user to
     */
    private void sendInvitations(Event event) {
        boolean inviteSuccessful = true;

        ArrayList<User> alreadyInvitedUsers = accessSingleEvents.getInvitees((SingleEvent) event);
        if (alreadyInvitedUsers.size() > 0) {
            for (User user : alreadyInvitedUsers) {
                if(!accessUsers.withdrawInvite(user, event)) {
                    inviteSuccessful = false;
                    break;
                }
            }
        }

        for (int userId : inviteeIds) {
            if(!accessSingleEvents.inviteUser((SingleEvent) event, accessUsers.getUser(userId)))  {
                inviteSuccessful = false;
                break;
            }
        }

        if(inviteSuccessful) {
            FeedbackDialog.createAndFinish(this, "Success", "Event information saved!");
        } else {
            FeedbackDialog.createAndFinish(this, "Error", "Couldn't invite users to event.");
        }
    }
}