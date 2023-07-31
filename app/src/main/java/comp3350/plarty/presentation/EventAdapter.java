package comp3350.plarty.presentation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.joda.time.format.DateTimeFormat;
import java.util.ArrayList;

import comp3350.plarty.R;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.User;

/**
 * This is an Adapter used to display information about events from a list.
 * Each event's information is displayed according to the template defined here.
 */
@RequiresApi(Build.VERSION_CODES.M)
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.CardViewHolder> {

    private final User currUser;
    private final Context context;
    private final ArrayList<Event> eventArrayList;
    private OnDeleteItemListener onDelete;

    public EventAdapter(Context newContext, ArrayList<Event> eventArrayList, User user) {
        super();
        currUser = user;
        this.context = newContext;
        this.eventArrayList = eventArrayList;
    }

    /**
     * We implement a custom interface to delete events from the adapter.
     * This means we can manipulate the Activity that creates the adapter
     * from within it! (see ViewEventsActivity)
     */
    public interface OnDeleteItemListener {
        void onDeleteItem(int position);
    }

    public void setOnDeleteItemListener(OnDeleteItemListener listener){
        onDelete = listener;
    }

    public int getItemIdAt(int position) {
        return this.eventArrayList.get(position).getId();
    }

    @NonNull
    @Override
    public EventAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new CardViewHolder(context, view, onDelete);
    }

    /**
     * Executes when an Event object from our ArrayList is bound to a ViewHolder
     * so it can have its information displayed.
     *
     * @param holder    a ViewHolder that contains information about an event
     * @param pos       the position of the displayed event in our ArrayList
     */
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.CardViewHolder holder, int pos) {
        Event event = eventArrayList.get(pos);
        holder.eventId = event.getId();
        holder.setTitle(event.getName());
        String organiserName = "Owner: "+ event.getOrganiser().getName();
        String startTime = (event.getStart()).toString(DateTimeFormat.mediumDateTime());
        String endTime = (event.getEnd()).toString(DateTimeFormat.mediumDateTime());
        String locationTime = "Location: "+event.getLocation()+

                "\nStarts: "+startTime+
                "\nEnds: "+endTime;            

        holder.eventLocDateTV.setText(locationTime);
        holder.eventOrgTV.setText(organiserName);

        //we only have the option to edit or delete events that we've organised
        if(event.getOrganiserId() != currUser.getId()) {
            holder.deleteButton.setEnabled(false);
            holder.deleteButton.setVisibility(View.INVISIBLE);
            holder.editable = false;
        }
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }

    /**
     * The CardViewHolder that contains each event's actual information.
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventNameTV;
        private final TextView eventOrgTV;
        private final TextView eventLocDateTV;
        private final ImageView deleteButton;
        private int eventId;
        private final Context context;
        private boolean editable = true;

        public CardViewHolder(Context newContext, @NonNull View itemView, OnDeleteItemListener onDelete) {
            super(itemView);
            eventNameTV = itemView.findViewById(R.id.card_event_name);
            eventOrgTV = itemView.findViewById(R.id.event_org);
            eventLocDateTV = itemView.findViewById(R.id.event_location);
            deleteButton = itemView.findViewById(R.id.delete_event_button);
            context = newContext;

            itemView.setOnClickListener(view -> edit());

            deleteButton.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    FeedbackDialog.createChoice(context,
                            "Confirm",
                            "Delete "+eventNameTV.getText()+"?\nThis will withdraw all of its invitations.",
                            (dialog, i) -> onDelete.onDeleteItem(position),
                            null);
                    }
                }
            );
        }

        public void setTitle(String title) {
            eventNameTV.setText(title);
        }

        private void edit() {
            if(editable) {
                Intent goToEdit = new Intent(context, CreateEventActivity.class);
                goToEdit.putExtra("Event ID", eventId);
                ((Activity)context).startActivity(goToEdit);
            }
        }
    }
}
