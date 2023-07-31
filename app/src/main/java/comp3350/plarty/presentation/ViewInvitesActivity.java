package comp3350.plarty.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import java.util.ArrayList;
import comp3350.plarty.R;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.User;

/**
 * This Activity shows the events a user has been invited to, and lets them
 * choose to accept, decline, or say "maybe" to an invitation.
 * Invitation responses can be edited at any time.
 */
public class ViewInvitesActivity extends Activity {
    private User currUser;
    AccessUsers accessUsers;
    private RecyclerView invitationsRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessUsers = new AccessUsers();
        currUser = accessUsers.getMainUser();
        setContentView(R.layout.activity_viewinvitations);
        invitationsRecycler = findViewById(R.id.invitations_recycler);

        Toolbar header = findViewById(R.id.header_bar);
        header.setNavigationOnClickListener(view -> finish());

        TextView headerTitle = new TextView(this);
        headerTitle.setText(R.string.invitations_label);
        headerTitle.setTextAppearance(this, R.style.header_text);
        header.addView(headerTitle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ArrayList<Event> invitations = accessUsers.getInvitedEvents(currUser);
        InviteAdapter invitesAdapter = new InviteAdapter(invitations, currUser);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutManager and adapter to our recycler view.
        invitationsRecycler.setLayoutManager(linearLayoutManager);
        invitationsRecycler.setAdapter(invitesAdapter);
        invitesAdapter.setOnItemClickListener(new InviteAdapter.OnRespondListener() {
            public void onAcceptClick(int position) {
                if(accessUsers.respondToInvite(currUser, invitations.get(position), InviteResponse.ACCEPTED)) {
                    FeedbackDialog.create(ViewInvitesActivity.this, "Success", "Accepted invitation!");
                } else {
                    FeedbackDialog.create(ViewInvitesActivity.this, "Error", "You aren't available to attend this event.");
                }
            }

            public void onMaybeClick(int position) {
                if(accessUsers.respondToInvite(currUser, invitations.get(position), InviteResponse.MAYBE)) {
                    FeedbackDialog.create(ViewInvitesActivity.this, "Success", "Responded \"maybe\" to invitation.");
                } else {
                    FeedbackDialog.create(ViewInvitesActivity.this, "Error", "Couldn't send response.");
                }
            }

            public void onDeclineClick(int position) {
                if(accessUsers.respondToInvite(currUser, invitations.get(position), InviteResponse.DECLINED)) {
                    FeedbackDialog.create(ViewInvitesActivity.this, "Success", "Declined invitation.");
                } else {
                    FeedbackDialog.create(ViewInvitesActivity.this, "Error", "Couldn't send response.");
                }
            }
        });
    }
}
