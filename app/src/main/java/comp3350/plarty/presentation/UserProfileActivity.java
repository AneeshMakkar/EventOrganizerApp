package comp3350.plarty.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import comp3350.plarty.R;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.objects.User;

/**
 * This Activity displays the user's profile.
 * Currently, this just entails their name and options to view invitations or
 * update their active hours.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class UserProfileActivity extends Activity {

    final private User currUser;

    public UserProfileActivity() {
        super();
        AccessUsers accessUsers = new AccessUsers();
        currUser = accessUsers.getMainUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        Toolbar header = findViewById(R.id.profile_header);
        header.setNavigationOnClickListener(view -> finish());
        header.setTitle(currUser.getName());

        Button activeHours = findViewById(R.id.active_hours_button);
        activeHours.setOnClickListener(view -> goToActiveHours());

        Button invitations = findViewById(R.id.view_invitations_button);
        invitations.setOnClickListener(view -> goToInvitations());
    }

    private void goToInvitations() {
        Intent viewInvitations = new Intent(this, ViewInvitesActivity.class);
        startActivity(viewInvitations);
    }

    private void goToActiveHours() {
        Intent viewActiveHours = new Intent(this, ViewActiveHoursActivity.class);
        startActivity(viewActiveHours);
    }
}
