package comp3350.plarty.presentation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import comp3350.plarty.objects.User;

/**
 * This is an adapter used to display potential event invitees in a list.
 */
public class UserSelectionAdapter extends ArrayAdapter<User> {

	private final int resourceId;

	public UserSelectionAdapter(@NonNull Context context, int resource, ArrayList<User> users) {
		super(context, resource, users);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User user = getItem(position);
		if(convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
		}
		((TextView)convertView).setText(user.getName());
		return convertView;
	}
}
