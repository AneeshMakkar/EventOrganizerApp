package comp3350.plarty.tests.acceptance;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import comp3350.plarty.R;
import comp3350.plarty.application.Services;
import comp3350.plarty.objects.User;
import comp3350.plarty.presentation.MainActivity;

/*
Time searching tests correspond to the Time Searching Big User story. This story was about
users being able to find recommended times for Events based on availability.
 */
public class TimeSearchingTests {
    @Rule
    public ActivityTestRule<MainActivity> homeActivity = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp(){
        Services.closeDataAccess();
        Services.createDataAccess();
        System.out.println("\nStarting Acceptance Test TimeSearchingTests");
    }

    /*
    This test checks to see if the recommended times for only the main user is correct. For this test, a
    user will first try to create an event, then see the recommended times. Because the user has not set their
    active hours, it will recommend 12pm as a start time. The user will then set their active hours, and check that
    the first recommended time is when their active hours begins.
     */
    @Test
    public void checkSingleUserRecommendations() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Go to view events
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // create new event
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());

        // The default recommended time
        onView(withSubstring("12:00 AM - 1:00 AM on")).check(matches(isDisplayed()));

        // The default recommended time
        onView(withSubstring("12:00 AM - 1:00 AM on")).check(matches(isDisplayed()));
        onView(withSubstring("12:00 AM - 1:00 AM on")).perform(click());

        // Ensure the correct value was actually entered
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("12")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.start_timepicker)))))).check(matches(withText("AM")));

        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("1")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.end_timepicker)))))).check(matches(withText("AM")));

        Espresso.pressBack();
        Espresso.pressBack();

        // Go to the active hours page
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).perform(click());
        onView(withId(R.id.active_hours_button)).check(matches(isDisplayed()));
        onView(withId(R.id.active_hours_button)).perform(click());

        // Try to set the active hours from 6:00am-11:30pm, this conflicts with the event we just created!
        onView(withId(R.id.set_active_start)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_start)).perform(PickerActions.setTime(6, 0));

        onView(withId(R.id.set_active_end)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_end)).perform(PickerActions.setTime(23, 30));

        onView(withId(R.id.save_active_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_active_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        // Go to view events
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // create new event
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());

        // The default recommended time
        onView(withSubstring("6:00 AM - 7:00 AM on")).check(matches(isDisplayed()));
        onView(withSubstring("6:00 AM - 7:00 AM on")).perform(click());

        // Ensure the correct value was actually entered
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("6")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.start_timepicker)))))).check(matches(withText("AM")));

        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("7")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.end_timepicker)))))).check(matches(withText("AM")));

        // Set event name
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("Recommended Time Event"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("The Interweb"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Save event, go back to main menu
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Confirm we are on the home page
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }


    /*
    This test checks to see if the recommended times when inviting other users. For this test, a
    user will first check the recommended times when inviting another user. The main user will then change
    their active hours to be later than this recommended time, and they will check their recommended times
    with inviting this user again.
     */
    @Test
    public void checkInvitedUsersRecommendations() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Go to view events
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // create new event
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());

        // Invite Colin
        User searchUser = new User("Colin Robinson", 6);
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Colin's active hours start at 7:00 am
        onView(withSubstring("7:00 AM - 8:00 AM on")).check(matches(isDisplayed()));
        onView(withSubstring("7:00 AM - 8:00 AM on")).perform(click());


        // Check that the times are correct
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("7")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.start_timepicker)))))).check(matches(withText("AM")));

        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("8")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.end_timepicker)))))).check(matches(withText("AM")));

        Espresso.pressBack();
        Espresso.pressBack();

        // Go to the active hours page
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).perform(click());
        onView(withId(R.id.active_hours_button)).check(matches(isDisplayed()));
        onView(withId(R.id.active_hours_button)).perform(click());

        // Try to set the active hours from 6:00am-11:30pm, this conflicts with the event we just created!
        onView(withId(R.id.set_active_start)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_start)).perform(PickerActions.setTime(8, 0));

        onView(withId(R.id.set_active_end)).check(matches(isDisplayed()));
        onView(withId(R.id.set_active_end)).perform(PickerActions.setTime(23, 30));

        onView(withId(R.id.save_active_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_active_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();
        Espresso.pressBack();

        // Go to view events
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // create new event
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());

        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Our active hours start at 8, and colin's are 7, so the earliest we could have this is 8am
        onView(withSubstring("8:00 AM - 9:00 AM on")).check(matches(isDisplayed()));
        onView(withSubstring("8:00 AM - 9:00 AM on")).perform(click());

        // Check that the times are correct
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("8")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.start_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.start_timepicker)))))).check(matches(withText("AM")));

        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("hour")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("9")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("minute")), withParent(withParent(withParent(withParent(withId(R.id.end_timepicker))))))).check(matches(withText("00")));
        onView(allOf(withResourceName("numberpicker_input"), withParent(withResourceName("amPm")), withParent(withParent(withParent(withId(R.id.end_timepicker)))))).check(matches(withText("AM")));

        // Set event name
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("Recommended Time Event 2"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("The Interweb 2"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Save event, go back to main menu
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Confirm we are on the home page
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }
}
