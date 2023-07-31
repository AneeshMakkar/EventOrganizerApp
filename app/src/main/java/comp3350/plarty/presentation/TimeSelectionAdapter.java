package comp3350.plarty.presentation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.Interval;

import java.util.ArrayList;

/**
 * This is an Adapter used to display suggested event times in a list.
 */
public class TimeSelectionAdapter extends ArrayAdapter<Interval> {

	private final int resourceId;

	public TimeSelectionAdapter(Context context, int resource, ArrayList<Interval> times) {
		super(context, resource, times);
		resourceId = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Interval interval = getItem(position);
		if(convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
		}
		((TextView)convertView).setText(StringConverter.intervalToString(interval));
		return convertView;
	}
}
