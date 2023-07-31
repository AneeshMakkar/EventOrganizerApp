package comp3350.plarty.presentation;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import org.joda.time.format.DateTimeFormat;
import java.util.ArrayList;
import comp3350.plarty.R;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.User;

/**
 * This is an Adapter used to display information about invitations from a list.
 * Each invitation's information is displayed according to the template defined here.
 */
public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.CardViewHolder> {

    private final User currUser;
    private final ArrayList<Event> events;
    private OnRespondListener onClick;

    /**
     * We implement a custom listener interface so that the parent Activity
     * class set how responses are handled.
     */
    public interface OnRespondListener {
        void onAcceptClick(int position);

        void onMaybeClick(int position);

        void onDeclineClick(int position);
    }

    public void setOnItemClickListener(OnRespondListener listener) {
        onClick = listener;
    }

    public InviteAdapter(ArrayList<Event> eventList, User user) {
        super();
        currUser = user;
        events = eventList;
    }

    @NonNull
    @Override
    public InviteAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_card_layout, parent, false);
        return new CardViewHolder(view, currUser, onClick);
    }

    /**
     * Executes when an Invitation from our ArrayList is bound to a ViewHolder
     * so it can have its information displayed.
     *
     * @param holder    a ViewHolder that contains information about an invitation
     * @param pos       the position of the displayed invitation in our ArrayList
     */
    @Override
    public void onBindViewHolder(@NonNull InviteAdapter.CardViewHolder holder, int pos) {
        Event newEvent = events.get(pos);
        holder.event = newEvent;
        holder.eventNameLabel.setText(newEvent.getName());
        holder.organiserNameLabel.setText(newEvent.getOrganiser().getName());
        String startTime = (newEvent.getStart()).toString(DateTimeFormat.mediumDateTime());
        String endTime = (newEvent.getEnd()).toString(DateTimeFormat.mediumDateTime());
        String locationTime = "Location: " + newEvent.getLocation() +
                "\nStarts: " + startTime +
                "\nEnds: " + endTime;
        holder.locationDateLabel.setText(locationTime);
        holder.refreshButtonState();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * The CardViewHolder that contains each event's information and response
     * buttons.
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final User currUser;
        private Event event;
        private final AccessUsers accessUsers;
        private final TextView eventNameLabel, organiserNameLabel, locationDateLabel;
        private final Button acceptInvite, maybeInvite, declineInvite;

        public CardViewHolder(@NonNull View itemView, User user, OnRespondListener onClick) {
            super(itemView);
            currUser = user;
            accessUsers = new AccessUsers();

            eventNameLabel = itemView.findViewById(R.id.invited_event_name);
            organiserNameLabel = itemView.findViewById(R.id.invited_event_org);
            locationDateLabel = itemView.findViewById(R.id.invited_event_location);

            acceptInvite = itemView.findViewById(R.id.accept_invite);
            maybeInvite = itemView.findViewById(R.id.maybe_invite);
            declineInvite = itemView.findViewById(R.id.decline_invite);

            acceptInvite.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && accessUsers.getResponseToEvent(currUser, event) != InviteResponse.ACCEPTED) {
                    onClick.onAcceptClick(position);
                    refreshButtonState();
                }
            });
            maybeInvite.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && accessUsers.getResponseToEvent(currUser, event) != InviteResponse.MAYBE) {
                    onClick.onMaybeClick(position);
                    refreshButtonState();
                }
            });
            declineInvite.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && accessUsers.getResponseToEvent(currUser, event) != InviteResponse.DECLINED) {
                    onClick.onDeclineClick(position);
                    refreshButtonState();
                }
            });
        }

        /**
         * Selects and deselects buttons from the buttonbar based on the user's
         * response.
         */
        public void refreshButtonState() {
            InviteResponse response = accessUsers.getResponseToEvent(currUser, event);
            acceptInvite.setSelected(response == InviteResponse.ACCEPTED);
            maybeInvite.setSelected(response == InviteResponse.MAYBE);
            declineInvite.setSelected(response == InviteResponse.DECLINED);
        }
    }
}
