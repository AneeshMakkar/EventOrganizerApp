package comp3350.plarty.tests.acceptance;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import android.view.View;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import comp3350.plarty.R;
import comp3350.plarty.application.Services;
import comp3350.plarty.presentation.MainActivity;

/*
ScheduleUpdateTests correspond to the big user story schedule updates. This story is about user schedules
being automatically updated by Plarty. These updates can come from invitations, active hours and event creation.
Updates in the schedule should also be aware of collisions between events, so Plarty should automatically
detect when a new event will collide with an existing event.
 */
public class ScheduleUpdateTests {
    @Rule
    public ActivityTestRule<MainActivity> homeActivity = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp(){
        Services.closeDataAccess();
        Services.createDataAccess();
        System.out.println("\nStarting Acceptance Test ScheduleUpdateTests");
    }


    /*
    In this test, the user will create an event, and confirm that the schedule automatically updates
    to display the new event, with the proper name.
     */
    @Test
    public void createEventConfirmAppearsTest() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Go to view events
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // create new event
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Enter details
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("This should appear"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("Chunky Cheese Ball Pit"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Set date to today, to make it easier to view
        DateTime now = DateTime.now();
        onView(withId(R.id.start_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_datepicker)).perform(PickerActions.setDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()));
        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(4, 0));

        onView(withId(R.id.end_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_datepicker)).perform(PickerActions.setDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(9, 30));

        // Save event
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Close page, return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Open schedule
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());

        // Go to today
        onView(withId(R.id.today_button)).check(matches(isDisplayed()));
        onView(withId(R.id.today_button)).perform(click());

        // Confirm event was created
        onView(withText("This should appear")).check(matches(isDisplayed()));

        // Go back to main menu
        Espresso.pressBack();
        Espresso.pressBack();

        // Confirm we are on the home page
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }


    /*
    When a user accepts an invite, the schedule should automatically be updated to show the change.
    The schedule should also be able to automatically detect collisions.
    In this test, the user will create an event, then try to accept an invite to a conflicting event.
    They should receive an error message. The user will then delete the conflicting event, they should
    then be able to accept the invite. The user will then decline the invitation, and confirm that the
    corresponding event was removed from their events and schedule.
     */
    @Test
    public void acceptEvent() {

        // Create an event
        // Open event page
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Crate event
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Set event name details
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("My Big Fat Greek Wedding"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("my basement"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Set the date to September 6 2022 from 8:00am-2:30pm.
        onView(withId(R.id.start_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_datepicker)).perform(PickerActions.setDate(2022, 9, 6));
        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(8, 0));

        onView(withId(R.id.end_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_datepicker)).perform(PickerActions.setDate(2022, 9, 6));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(14, 30));

        // Save the event
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Check invitations
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).perform(click());
        onView((withId(R.id.view_invitations_button))).check(matches(isDisplayed()));
        onView((withId(R.id.view_invitations_button))).perform(click());

        // Try to accept invitation to My Barbeque, however, this event takes place at the same time as the previously created
        // user event, this should fail.
        onView(allOf(withId(R.id.accept_invite), withParent(withParent(withChild(withText("My Barbeque")))))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.accept_invite), withParent(withParent(withChild(withText("My Barbeque")))))).perform(click());

        // Confirm we can't create event
        onView(withText("You aren't available to attend this event.")).check(matches(isDisplayed()));

        // Return to main page
        onView(withText("Close")).check(matches(isDisplayed()));
        onView(withText("Close")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        // Return to event page
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Delete conflicting event
        onView(allOf(withId(R.id.delete_event_button), withParent(withChild(withText("My Big Fat Greek Wedding"))))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.delete_event_button), withParent(withChild(withText("My Big Fat Greek Wedding"))))).perform(click());
        onView(withText("YES")).check(matches(isDisplayed()));
        onView(withText("YES")).perform(click());

        // Return to main page
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Return to invitation page
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).perform(click());
        onView((withId(R.id.view_invitations_button))).check(matches(isDisplayed()));
        onView((withId(R.id.view_invitations_button))).perform(click());

        // Accept invite, this should work because the conflict was removed
        onView(allOf(withId(R.id.accept_invite), withParent(withParent(withChild(withText("My Barbeque")))))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.accept_invite), withParent(withParent(withChild(withText("My Barbeque")))))).perform(click());

        // Return to main page
        onView(withText("Close")).check(matches(isDisplayed()));
        onView(withText("Close")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        // Go to the event page, where all events are stored
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Confirm that My Barbeque was added to users events
        onView(withText("My Barbeque")).check(matches(isDisplayed()));

        // Return to main page
        Espresso.pressBack();

        // Open up the schedule view
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());

        // Open up schedule to Sept 6. This is tricky to do as Espresso does not interface with
        // Simple Calendars.
        // Determine how many months we need to move between
        int month = DateTime.now().getMonthOfYear();
        int SEPTEMBER = 9;
        int clicks = SEPTEMBER - month;
        String button = "next";
        if(clicks < 0)
            button = "back";
        for(int i=0; i<clicks; i++){
            onView(withResourceName(button)).check(matches(isDisplayed()));
            onView(withResourceName(button)).perform(click());
        }

        // Click the position for 6 on the calendar. (There is no way to do this via id or text)
        onView(withResourceName("day_picker_view_pager")).perform(clickPos(300, 285));

        // Confirm event was added to the schedule
        onView(withText("My Barbeque")).check(matches(isDisplayed()));

        // Return to main page
        Espresso.pressBack();
        Espresso.pressBack();

        // Confirm we are on main page
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));

        // Go back to the invitations page
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).perform(click());
        onView((withId(R.id.view_invitations_button))).check(matches(isDisplayed()));
        onView((withId(R.id.view_invitations_button))).perform(click());

        // Decline the invite
        onView(allOf(withId(R.id.decline_invite), withParent(withParent(withChild(withText("My Barbeque")))))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.decline_invite), withParent(withParent(withChild(withText("My Barbeque")))))).perform(click());

        // Return to main page
        onView(withText("Close")).check(matches(isDisplayed()));
        onView(withText("Close")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        // Go to events page
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Confirm we no longer have the event
        onView(withText("My Barbeque")).check(doesNotExist());

        // Go back to main page
        Espresso.pressBack();

        // Go to the schedule view
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());

        // Open up the Calendar to Sept 6.
        for(int i=0; i<clicks; i++){
            onView(withResourceName(button)).check(matches(isDisplayed()));
            onView(withResourceName(button)).perform(click());
        }
        onView(withResourceName("day_picker_view_pager")).perform(clickPos(300, 285));
        // Confirm that the event was not created
        onView(withText("My Barbeque")).check(doesNotExist());

        // Return to main menu
        Espresso.pressBack();
        Espresso.pressBack();

        // Confirm we are on the main page
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }


    /*
    When users edit their events, the schedule should update accordingly to reflect the changes in time.
    In this test a user will create an event, confirm that it is in the schedule on the correct day,
    they will then edit the event and confirm that it is found in the schedule on the correct day.
     */
    @Test
    public void testEditEvent() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Confirm we are on main page
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));

        // Create a new event
        onView(withId(R.id.view_events_button)).perform(click());
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Set event details
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("IMPORTANT BUSINESS MEETING!!!!!!"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("The Chunky Cheese House of Fun"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Create event on today's date from 4:30 am - 12:30pm
        DateTime now = DateTime.now();
        onView(withId(R.id.start_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_datepicker)).perform(PickerActions.setDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()));
        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(4, 30));

        onView(withId(R.id.end_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_datepicker)).perform(PickerActions.setDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(12, 30));

        // Save event, go back to main menu
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // View schedule, confirm today has the event
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());
        onView(withId(R.id.today_button)).check(matches(isDisplayed()));
        onView(withId(R.id.today_button)).perform(click());
        onView(withText("IMPORTANT BUSINESS MEETING!!!!!!")).check(matches(isDisplayed()));

        // Return to main menu
        Espresso.pressBack();
        Espresso.pressBack();

        // Confirm the event is in the my events page
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Edit the event
        onView(withText("IMPORTANT BUSINESS MEETING!!!!!!")).check(matches(isDisplayed()));
        onView(withText("IMPORTANT BUSINESS MEETING!!!!!!")).perform(click());
        onView(isRoot()).perform(closeSoftKeyboard());
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));

        // Change the name and date of the event
        onView(withId(R.id.new_event_name)).perform(clearText());
        onView(withId(R.id.new_event_name)).perform(typeText("MEETING AT THE BUSINESS OFFICE!!"));
        onView(isRoot()).perform(closeSoftKeyboard());

        // Set date to September 6 2022
        onView(withId(R.id.start_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_datepicker)).perform(PickerActions.setDate(2022, 9, 6));
        onView(withId(R.id.end_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_datepicker)).perform(PickerActions.setDate(2022, 9, 6));

        // Save changes
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Confirm event is found in schedule on Sept 6 2022
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());
        int month = DateTime.now().getMonthOfYear();
        int SEPTEMBER = 9;
        int clicks = SEPTEMBER - month;
        String button = "next";
        if(clicks < 0)
            button = "back";
        for(int i=0; i<clicks; i++){
            onView(withResourceName(button)).check(matches(isDisplayed()));
            onView(withResourceName(button)).perform(click());
        }
        onView(withResourceName("day_picker_view_pager")).perform(clickPos(300, 285));
        onView(withText("MEETING AT THE BUSINESS OFFICE!!")).check(matches(isDisplayed()));

        // Return to main menu
        Espresso.pressBack();
        Espresso.pressBack();
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }


    /*
    This test checks to confirm that active hours work as intended.
    In this test, the actions of a user who is trying to make events that collide with active hours,
    and active hours that collide with events are tested.
     */
    @Test
    public void activeHourCreation() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Create an event
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Set event name
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("Grinding out iteration 3"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("My room!"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Set date to today from 1am - 6:30am
        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(1, 0));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(6, 30));

        // Save event, go back to main menu
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Go to the active hours page
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).perform(click());
        onView(withId(R.id.active_hours_button)).check(matches(isDisplayed()));
        onView(withId(R.id.active_hours_button)).perform(click());

        // Try to set the active hours from 7:30am-11:30pm, this conflicts with the event we just created!
        onView(withId(R.id.set_active_start)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_start)).perform(PickerActions.setTime(7, 30));

        onView(withId(R.id.set_active_end)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_end)).perform(PickerActions.setTime(23, 30));


        onView(withId(R.id.save_active_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_active_button)).perform(click());

        // Check that creating the active hours failed.
        onView(withText("You already have scheduled events outside that interval.")).check(matches(isDisplayed()));

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        // go to events page
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Delete the new conflicting event
        onView(allOf(withId(R.id.delete_event_button), withParent(withChild(withText("Grinding out iteration 3"))))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.delete_event_button), withParent(withChild(withText("Grinding out iteration 3"))))).perform(click());
        onView(withText("YES")).check(matches(isDisplayed()));
        onView(withText("YES")).perform(click());
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());

        // Return to the active hours page
        Espresso.pressBack();
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).perform(click());
        onView(withId(R.id.active_hours_button)).check(matches(isDisplayed()));
        onView(withId(R.id.active_hours_button)).perform(click());

        // Try to set active hours from 7:30am - 11:30pm
        onView(withId(R.id.set_active_start)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_start)).perform(PickerActions.setTime(7, 30));
        onView(withId(R.id.set_active_end)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_end)).perform(PickerActions.setTime(23, 30));

        // Try to save event, it should create
        onView(withId(R.id.save_active_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_active_button)).perform(click());

        // Return to main page
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        // Try to create an event that overlaps with the active hours
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Set event name and location
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("Grinding out iteration 3"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("My room!"));

        onView(isRoot()).perform(closeSoftKeyboard());

        // Set hour to a time which conflicts with active hours
        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(1, 0));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(8, 30));

        // Try to save event
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Confirm the event can't be created
        onView(withText("Event overlaps with something in your schedule!")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());

        // Change the time to not conflict w/ active hours
        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(7, 30));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(12, 30));

        // Event should save correct
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());

        // Open schedule
        Espresso.pressBack();
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());

        // Go to today
        onView(withId(R.id.today_button)).check(matches(isDisplayed()));
        onView(withId(R.id.today_button)).perform(click());

        // Confirm that the event is found there
        onView(withText("Grinding out iteration 3")).check(matches(isDisplayed()));

        // Return to main menu, confirm we are on main menu
        Espresso.pressBack();
        Espresso.pressBack();
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }


    // This is needed to interact with Simple Calendars with Espresso
    public static ViewAction clickPos(int x, int y){
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        int[] viewPos = new int[2];
                        view.getLocationOnScreen(viewPos);
                        float abs_x = viewPos[0] + x;
                        float abs_y = viewPos[1] + y;
                        float[] coordinates = {abs_x, abs_y};
                        return coordinates;
                    }
                },
                Press.FINGER);
    }
}
