package comp3350.plarty.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;

import comp3350.plarty.R;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.business.TimeManagement;
import comp3350.plarty.objects.User;

/**
 * This Activity lets a user check off invitees to their event and choose
 * a time that Plarty suggests to them.
 */
public class InviteUserActivity extends Activity {
    private AccessUsers accessUsers;
    private ListView selectUsersList, selectTimeList;
    private User currUser;
    private TimeManagement timeManagement;
    private ArrayList<User> selectedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessUsers = new AccessUsers();
        currUser = accessUsers.getMainUser();
        timeManagement = new TimeManagement();
        selectedUsers = new ArrayList<>();
        selectedUsers.add(currUser);

        setContentView(R.layout.activity_inviteusers);
        selectUsersList = findViewById(R.id.users_list);
        selectTimeList = findViewById(R.id.suggested_times_list);
        Button inviteWithoutTimeButton = findViewById(R.id.invite_without_time_button);

        inviteWithoutTimeButton.setOnClickListener(v -> inviteSelected(null));

        Toolbar header = findViewById(R.id.header_bar);
        header.setNavigationOnClickListener(view -> finish());

        TextView headerTitle = new TextView(this);
        headerTitle.setText(R.string.new_event_header_label);
        headerTitle.setTextAppearance(this, R.style.header_text);
        header.addView(headerTitle);

        selectUsersList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        selectUsersList.setOnItemClickListener((adapterView, view, i, l) -> updateTimesList());

        initUserListView();

        selectTimeList.setOnItemClickListener((adapterView, view, i, l) -> createWithTime((Interval)selectTimeList.getItemAtPosition(i)));
    }
    /**
     * Obtains a list of Users that can be invited for a event.
     * pass this list to an adapter which is used to display users selected.
     * gets the count of Users that are selected to get invitations.
     */
    private void initUserListView()  {
        ArrayList<User> allOtherUsers = accessUsers.getAllOtherUsers(currUser.getId());
        ArrayList<User> users = new ArrayList<>(allOtherUsers);

        UserSelectionAdapter arrayAdapter = new UserSelectionAdapter(this, android.R.layout.simple_list_item_multiple_choice, users);

        selectUsersList.setAdapter(arrayAdapter);

        ArrayList<Integer> inviteeIds = getIntent().getIntegerArrayListExtra("Invited users");
        if(inviteeIds != null) {
            for(int id : inviteeIds) {
                selectedUsers.add(accessUsers.getUser(id));
            }
        }

        for(int i = 0; i < selectUsersList.getCount(); i++) {
            User user = (User) selectUsersList.getItemAtPosition(i);
            selectUsersList.setItemChecked(i, selectedUsers.contains(user));
        }

        updateTimesList();
    }

    /**
     * Sends data about selected invitees back to the parent Activity so it can
     * send invitations when the user saves the event information.
     *
     * @param returnIntent      an intent containing the user's selected time, if
     *                          they chose one of Plarty's suggestions; null if
     *                          they chose to continue without choosing a time
     */
    private void inviteSelected(Intent returnIntent) {
        ArrayList<Integer> inviteeIds = new ArrayList<>();
        for(User user : selectedUsers) {
            if(!user.equals(currUser) && !inviteeIds.contains(user.getId())) {
                inviteeIds.add(user.getId());
            }
        }

        if(returnIntent == null) {
            returnIntent = new Intent();
        }

        returnIntent.putIntegerArrayListExtra("Invited users", inviteeIds);
        setResult(RESULT_OK, returnIntent);
        finish(); //go back to previous page with updated invite + time info
    }

    /**
     * Obtains a list of invitees given the names that were checked off and finds
     * a list of times at which each invitee is available.
     */
    private void updateTimesList() {
        SparseBooleanArray checkedUsers = selectUsersList.getCheckedItemPositions();
        for(int i = 0; i < selectUsersList.getCount(); i++) {
            User user = (User) selectUsersList.getItemAtPosition(i);
            if(checkedUsers.get(i) && !selectedUsers.contains(user)) {
                selectedUsers.add(user);
            } else if(!checkedUsers.get(i)) {
                selectedUsers.remove(user);
            }
        }

        ArrayList<Interval> times = timeManagement.getSuggestedTimeList(
                new ArrayList<>(selectedUsers),
                DateTime.now().plusDays(1).withTimeAtStartOfDay(),
                DateTime.now().plusMonths(1).withTimeAtStartOfDay(),
                60);

        TimeSelectionAdapter timeAdapter = new TimeSelectionAdapter(this, android.R.layout.simple_selectable_list_item, times);
        selectTimeList.setAdapter(timeAdapter);
    }

    /**
     * If the user chooses a time from the suggested list, adds it to the return
     * intent so the parent Activity knows which time to use.
     *
     * @param selectedTimes     the time interval chosen by the user
     */
    private void createWithTime(Interval selectedTimes) {
        Intent returnWithTime = new Intent();
        DateTime start = selectedTimes.getStart();
        DateTime end = selectedTimes.getEnd();
        int [] startDateTimeFields = {start.getYear(), start.getMonthOfYear(), start.getDayOfMonth(), start.getHourOfDay(), start.getMinuteOfHour()};
        int [] endDateTimeFields = {end.getYear(), end.getMonthOfYear(), end.getDayOfMonth(), end.getHourOfDay(), end.getMinuteOfHour()};

        returnWithTime.putExtra("Start datetime fields", startDateTimeFields);
        returnWithTime.putExtra("End datetime fields", endDateTimeFields);
        inviteSelected(returnWithTime);
    }
}
