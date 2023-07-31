package comp3350.plarty.tests.acceptance;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import comp3350.plarty.R;
import comp3350.plarty.application.Services;
import comp3350.plarty.presentation.MainActivity;

/*
ViewSchedule tests correspond to the big user story View Schedule. This big user story is about
letting users be able to see their schedule. The test here focus on users seeing their schedule,
and creating events and being able to see those events in the schedule.
 */
public class ViewScheduleTests {
    @Rule
    public ActivityTestRule<MainActivity> homeActivity = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp() {
        Services.closeDataAccess();
        Services.createDataAccess();
        System.out.println("\nStarting Acceptance Test ViewScheduleTests");
    }


    /*
    This is a simple test where the user simply opens up their schedule, tries the buttons to change months,
    then opens the calendar to today's date. This is just a user viewing their schedule.
     */
    @Test
    public void viewSchedule() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Open the schedule
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withText("MY SCHEDULE")).perform(click());

        // Try the back button
        onView(withResourceName("prev")).check(matches(isDisplayed()));
        onView(withResourceName("prev")).perform(click());

        // Try the forward button
        onView(withResourceName("next")).check(matches(isDisplayed()));
        onView(withResourceName("next")).perform(click());

        // Check that there is a today button
        onView(withId(R.id.today_button)).check(matches(isDisplayed()));
        onView(withId(R.id.today_button)).perform(click());
    }


    /*
    This is a test where a user creates two events today, then opens the schedule and confirms they can view them.
    This test confirms that events can be viewed in the schedule as per the big user story.
     */
    @Test
    public void testChangingInvitations() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Create a new event
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());


        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Set name and location for event
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("Event Numero 1"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("Testarino"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(1, 0));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(5, 30));

        // Save event
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());

        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());


        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Set name and location for event
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("Event Numero 2"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("Testarino 2"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(5, 31));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(8, 30));

        // Save event
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());

        onView(withText("Event Numero 1")).check(matches(isDisplayed()));
        onView(withText("Event Numero 2")).check(matches(isDisplayed()));
    }

}