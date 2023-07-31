package comp3350.plarty.presentation;

import org.joda.time.Interval;

import java.util.HashMap;

/**
 * Formats date and time parameters as Strings consistently.
 */
public class StringConverter {

	private static final HashMap<Integer, String> MONTHS = new HashMap<Integer, String>(){ {
			put(1, "January");
			put(2, "February");
			put(3, "March");
			put(4, "April");
			put(5, "May");
			put(6, "June");
			put(7, "July");
			put(8, "August");
			put(9, "September");
			put(10, "October");
			put(11, "November");
			put(12, "December");
		}
	};

	static String intervalToString(Interval interval) {
		String startTime = timeToString(interval.getStart().getHourOfDay(),
				interval.getStart().getMinuteOfHour());
		String endTime = timeToString(interval.getEnd().getHourOfDay(),
				interval.getEnd().getMinuteOfHour());
		String period = startTime + " - " + endTime;

		return period + " on " + dateToString(interval.getStart().getYear(),
				interval.getStart().getMonthOfYear(),
				interval.getStart().getDayOfMonth()) + "\n";
	}

	static String timeToString(int hour, int minute) {
		String period = " AM";

		if(hour >= 12) {
			period = " PM";

			if(hour > 12) {
				hour -= 12;
			}
		}

		if(hour == 0){
			hour = 12;
			period = " AM";
		}

		String result = hour+":";

		if(minute < 10) {
			result += "0";
		}

		result += String.valueOf(minute);

		return result + period;
	}

	static String dateToString(int year, int month, int day) {
		return MONTHS.get(month) + " " + day + ", " + year;
	}
}
