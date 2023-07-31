package comp3350.plarty.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import comp3350.plarty.business.AccessSingleEvents;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.User;
import comp3350.plarty.R;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * This Activity shows users information about events they've created and accepted
 * invitations to.
 * They can also edit and delete their own events from here.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class ViewEventsActivity extends Activity {
    private User currUser;
    private AccessUsers accessUsers;
    private AccessSingleEvents accessSingleEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessUsers = new AccessUsers();
        currUser = accessUsers.getMainUser();
        accessSingleEvents = new AccessSingleEvents();

        setContentView(R.layout.activity_viewevents);

        Toolbar header = findViewById(R.id.header_bar);
        header.setNavigationOnClickListener(view -> finish());

        TextView headerTitle = new TextView(this);
        headerTitle.setText(R.string.events_header_label);
        headerTitle.setTextAppearance(this, R.style.header_text);
        header.addView(headerTitle);

        Button newEventButton = findViewById(R.id.new_event_button);
        newEventButton.setOnClickListener(view -> createNewEvent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshEvents();
    }

    private void refreshEvents() {
        RecyclerView eventsRecycler = findViewById(R.id.event_recycler);
        ArrayList<Event> events = accessUsers.getNonInactiveEvents(currUser);
        EventAdapter eventAdapter = new EventAdapter(this, events, currUser);

        // for managing the RecyclerView that contains event information
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eventsRecycler.setLayoutManager(linearLayoutManager);
        eventsRecycler.setAdapter(eventAdapter);

        eventAdapter.setOnDeleteItemListener(position -> {
            if(accessSingleEvents.deleteEvent(eventAdapter.getItemIdAt(position))) {
                FeedbackDialog.create(this, "Success", "Event deleted!");
                events.remove(position);
                eventAdapter.notifyItemRemoved(position);
            } else {
                FeedbackDialog.create(this, "Error", "Couldn't delete event.");
            }
        });
    }

    public void createNewEvent() {
        Intent createEvent = new Intent(this, CreateEventActivity.class);
        startActivity(createEvent);
    }
}
