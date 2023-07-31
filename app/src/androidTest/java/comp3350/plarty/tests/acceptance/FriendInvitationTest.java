package comp3350.plarty.tests.acceptance;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.rule.ActivityTestRule;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import comp3350.plarty.R;
import comp3350.plarty.application.Services;
import comp3350.plarty.objects.User;
import comp3350.plarty.presentation.MainActivity;

/*
FriendInvitationTests correspond to the Invite Friends big user story. This story is about letting
the main user Invite others to their events, as well as remove invitations send to other users.
 */
public class FriendInvitationTest {
    @Rule
    public ActivityTestRule<MainActivity> homeActivity = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void setUp(){
        Services.closeDataAccess();
        Services.createDataAccess();
        System.out.println("\nStarting Acceptance Test FriendInvitationTest");
    }


    /*
    For this test, we have a user that wants to invite their friends to a party. For this tests, they will
    create an event, invite their friends, invite someone else by accident, uninvite this person, then check
    that the invite was created.
     */
    @Test
    public void groupInvitation() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Open events page
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Create event
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());

        // Invite friends
        User searchUser = new User("Guillermo de la Cruz", 12);
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Invite Nandor
        searchUser = new User("Nandor Cravensworth", 18);
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Invite Colin
        searchUser = new User("Colin Robinson", 6);
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Invite Troy
        searchUser = new User("Troy Barnes", 29);
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Invite Señor Chang by accident
        searchUser = new User("Señor Ben Chang", 35);
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Uninvite Señor Chang as he would harsh the mellow
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(searchUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));

        // Skip recommended times
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Add event details
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("Super Fun Event"));

        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("My House!!!!!"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Set the date to today
        DateTime now = DateTime.now();
        onView(withId(R.id.start_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_datepicker)).perform(PickerActions.setDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()));
        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(8, 0));

        onView(withId(R.id.end_datepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_datepicker)).perform(PickerActions.setDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth()));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(8, 30));

        // Save event
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Open schedule
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());

        // Go to today
        onView(withId(R.id.today_button)).check(matches(isDisplayed()));
        onView(withId(R.id.today_button)).perform(click());

        // Verify event was created
        onView(withText("Super Fun Event")).check(matches(isDisplayed()));

        // Goes back to the Month View
        onView(withId(R.id.header_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.header_bar)).perform(click());

        // Goes back to the Main Menu
        onView(withId(R.id.header_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.header_bar)).perform(click());

        Espresso.pressBack();
        Espresso.pressBack();

        // Verify we are back to the front page
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }


    /*
    For this test, the user creates a new event, invites one of their friends, but wavers on whether or not
    they should add another. They alternate between setting the event to invite the user or not, before deciding
    ultimately to invite them. The user then checks if the event was created, and if the correct users are invited.
     */
    @Test
    public void testChangingInvitations() {
        onView(withText("Plarty™")).check(matches(isDisplayed()));

        // Create a new event
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());
        onView(withId(R.id.new_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_button)).perform(click());

        // Invite Colin
        User colinUser = new User("Colin Robinson", 6);
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Invite Señor Chang
        User benUser = new User("Señor Ben Chang", 35);
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));


        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Set name and location for event
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(typeText("COLIN ROBINSON HANGOUT"));
        onView(withId(R.id.new_event_location)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_location)).perform(typeText("Good Burger, Home of the Good Burger"));

        // Close the keyboard
        onView(isRoot()).perform(closeSoftKeyboard());

        // Check the invited list
        onView(withId(R.id.invite_users_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_users_button)).perform(click());
        try {
            Thread.sleep(500);
        }
        catch(Exception e){ e.printStackTrace(); }

        // Confirm that colin is in the invited list
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Uninvite Señor Ben Chang, he is a mellow harsher
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));

        // Set hours for event
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        onView(withId(R.id.start_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.start_timepicker)).perform(PickerActions.setTime(8, 0));
        onView(withId(R.id.end_timepicker)).check(matches(isDisplayed()));
        onView(withId(R.id.end_timepicker)).perform(PickerActions.setTime(8, 31));

        // Save event
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Return to main menu
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Open schedule
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());

        // Go to today
        onView(withId(R.id.today_button)).check(matches(isDisplayed()));
        onView(withId(R.id.today_button)).perform(click());

        // Confirm event exists
        onView(withText("COLIN ROBINSON HANGOUT")).check(matches(isDisplayed()));

        // Return to events page
        Espresso.pressBack();
        Espresso.pressBack();
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).perform(click());

        // Edit the new event
        onView(withText("COLIN ROBINSON HANGOUT")).check(matches(isDisplayed()));
        onView(withText("COLIN ROBINSON HANGOUT")).perform(click());
        onView(isRoot()).perform(closeSoftKeyboard());

        // Open the invited users list
        onView(withId(R.id.invite_users_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_users_button)).perform(click());

        try {
            Thread.sleep(500);
        }
        catch(Exception e){ e.printStackTrace(); }

        // Confirm colin is invited
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Double check senor chang was uninvited
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));

        // Invite senor chang
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());

        // Change event name to include senor chang
        onView(withId(R.id.new_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.new_event_name)).perform(clearText());
        onView(withId(R.id.new_event_name)).perform(typeText("HANGOUT WITH COLIN AND BEN"));
        onView(isRoot()).perform(closeSoftKeyboard());

        // Save changes
        onView(withId(R.id.invite_users_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_users_button)).perform(click());
        try {
            Thread.sleep(500);
        }
        catch(Exception e){ e.printStackTrace(); }

        // check the invited list for colin
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Invite senor chang
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(not(isChecked())));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(click());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Save event
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());

        // Return to event page
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());

        // Confirm edited event is there
        onView(withText("HANGOUT WITH COLIN AND BEN")).check(matches(isDisplayed()));
        onView(withText("HANGOUT WITH COLIN AND BEN")).perform(click());
        onView(isRoot()).perform(closeSoftKeyboard());

        // Check the invited list
        onView(withId(R.id.invite_users_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_users_button)).perform(click());

        try {
            Thread.sleep(500);
        }
        catch(Exception e){ e.printStackTrace(); }

        // Confirm colin is invited
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(colinUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Confirm senor chang is invited
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).perform(scrollTo());
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isDisplayed()));
        onData(is(benUser)).inAdapterView(withId(R.id.users_list)).check(matches(isChecked()));

        // Return to main page
        onView(withId(R.id.invite_without_time_button)).check(matches(isDisplayed()));
        onView(withId(R.id.invite_without_time_button)).perform(click());
        onView(withId(R.id.save_event_button)).check(matches(isDisplayed()));
        onView(withId(R.id.save_event_button)).perform(click());
        onView(withText("CLOSE")).check(matches(isDisplayed()));
        onView(withText("CLOSE")).perform(click());
        Espresso.pressBack();

        // Open schedule
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_schedule_button)).perform(click());

        // Go to today
        onView(withId(R.id.today_button)).check(matches(isDisplayed()));
        onView(withId(R.id.today_button)).perform(click());

        // Confirm event is found
        onView(withText("HANGOUT WITH COLIN AND BEN")).check(matches(isDisplayed()));

        // Return to main menu, confirm we are on main page
        Espresso.pressBack();
        Espresso.pressBack();
        onView(withId(R.id.view_schedule_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_events_button)).check(matches(isDisplayed()));
        onView(withId(R.id.view_profile_button)).check(matches(isDisplayed()));
    }
}
