package comp3350.plarty.business;

import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.HashMap;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;
import comp3350.plarty.objects.ObjectValidator;

/**
 * This class handles access functions for SingleEvents, events that only occur once
 * at a set date and time.
 *
 * Users can only invite others to SingleEvents, not RecurringEvents.
 * Invitation logic is contained in AccessSingleEvents for this reason!
 */
public class AccessSingleEvents extends AccessEvents {

	/**
	 * If possible, creates a SingleEvent with the given metadata.
	 *
	 * @param name			the name of the new SingleEvent
	 * @param organiser		the user organizing the new event
	 * @param location		the name of the event's location
	 * @param start			the DateTime timestamp when the event starts
	 * @param end			the DateTime timestamp when the event ends
	 * @return				the new event if it is successfully created, or null
	 */
	public Event createSingleEvent(String name, User organiser, String location, DateTime start, DateTime end) {
		Event event = null;
		if(validEvent(name, organiser, location, start, end)) {
			ArrayList<Event> conflicts = accessUsers.getConflictingEvents(organiser, start, end);
			if(conflicts.size() == 0) {
				event = new SingleEvent(name, assignEventId(), organiser, location, start, end);
				dataAccess.createEvent(event);
			}
		}
		return event;
	}

	public HashMap<User, InviteResponse> getInviteResponses(SingleEvent e){
		return dataAccess.getInvitedUsers(e);
	}

	public ArrayList<User> getInvitees(SingleEvent event) {
		HashMap<User, InviteResponse> inviteResponses = getInviteResponses(event);
		return new ArrayList<>(inviteResponses.keySet());
	}

	/**
	 * Invites a user to an event by adding them to the user event's list.
	 * If an invitation is sent to a user other than the main user, a randomly-
	 * generated response is selected for simulation purposes.
	 *
	 * @param event		the SingleEvent to invite a user to
	 * @param user		the User to invite to the event
	 * @return			true if the invitation is successfully sent
	 * 					false if the invitation cannot be sent
	 */
	public boolean inviteUser(SingleEvent event, User user) {
		boolean userInvited = false;
		try {
			ObjectValidator.nullCheck(event, "Event");
			ObjectValidator.nullCheck(user, "User");

			if (!isUserAnOrganizer(event, user) && !accessUsers.isUserInvited(event, user)) {
				userInvited = dataAccess.createInvite(event.getId(), user.getId());
				if (userInvited && user.getId() != 0){
					dataAccess.updateInvite(event.getId(), user.getId(), InviteResponse.values()[(int)(Math.random()*2 + 1)]);
				}
				userInvited = true;
			}
		} catch (IllegalArgumentException IAE) {
			System.err.println("inviteUser - IllegalArgumentException: " + IAE.getMessage());
		}
		return userInvited;
	}

	/**
	 * Takes new metadata, and if possible, applies it to the given event.
	 *
	 * @param user			the user requesting the edit operation
	 * @param event			the event the user is trying to edit
	 * @param newName		a new name for the event
	 * @param newLocation	a new location for the event
	 * @param newStart		a new starting DateTime for the event
	 * @param newEnd		a new end DateTime for the event
	 * @return				true if the edit was successfully performed
	 * 						false if the event could not be edited
	 */
	public boolean editEvent(User user, SingleEvent event, String newName, String newLocation, DateTime newStart, DateTime newEnd) {
		boolean success = true;
		if(user.getId() == event.getOrganiserId()) {
			ArrayList<Event> conflicts = accessUsers.getConflictingEvents(user, newStart, newEnd);
			for(Event conflict : conflicts) {
				if (conflict.getId() != event.getId()) {
					success = false;
					break;
				}
			}
			if(success) {
				event.setName(newName);
				event.setLocation(newLocation);
				event.setStart(newStart);
				event.setEnd(newEnd);
				success = dataAccess.updateEvent(event);
			}
		}
		return success;
	}
}
