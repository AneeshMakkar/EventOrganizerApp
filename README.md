# Plarty: Your Party Planning Helper

[Repository](https://github.com/chrisVat/3350_Plarty) | [Log (requires repository access)](https://github.com/chrisVat/3350_Plarty/blob/main/Log.md)
The repository has been shared with "jbraico@cs.umanitoba.ca"

## Vision
Plarty will help larger groups of people organize their events. A common struggle among event 
planners is establishing clear communication with a large group of people about their availability, 
and handling all of that data even more so. Like a secretary for a group, Plarty simplifies the 
planning process by polling potential party-goers on their availability and cleanly reporting 
information to its planners so that they may optimally time their get-togethers.

## Group members
- Aneesh Makkar
- Barbara Guzman Romero
- Christopher Vattheuer
- Krushang Patel
- Mia Battad

## Iteration 3

### Included Features
Iteration 1 included the "Manage Schedule" full story, which had 
different views of the app, ensured that the user can attend events, and display them on 
their schedule.

Iteration 2 included the story "Analyze Events," which lets users invite others to
events they've organized, respond to invitations that others send them, and automatically
see a time interval in which invitees are free.

Iteration 3 cleaned up and included functionality we wanted in Iteration 2, particularly
expanding upon the "active hours"/"recurring events" creation and invitation logic.

### Packages and source files
The major source file is located under: **app/src/main/java/comp3350/plarty**

In that folder you are going to find the three major layers of architecture:
- Business: Here is where all the logic and accessor to the objects are located. 
  - Object: The domain objects are:
    - Event: Holds all the data of the events such as name, organizer, time, invited people.
    - RecurringEventGenerator: A template event that can recur on multiple days of the week in a user-defined date range.
    - RecurringEvent: A single instance of a RecurringEventGenerator that takes place on a specific date.
    - SingleEvent: A typical event as in Iteration 1 that holds data for a singularly-occurring event.
    - User: Holds the users of the app, with information such as name, schedule, invitation to event. 
    - InviteResponse: An enum that holds all the possibilities of answering to an event.
    - ObjectValidator: Contains validation methods to ensure objects are being created properly.
- Persistence: The DataAccessStub and HSQLDB DataAccessObject are contained here, as well as the DataAccess interface
through which they're accessible.
- Presentation: Activity classes allowing UI access to the business layer are contained here.

### Big User Story and Dev Tasks
We broke our tasks into I2 cleanup and feedback and the "Invite People" story.

The Invite People story had the following detailed stories:

5. Simulate other users' responses
   5.1 Edit the response function to return a randomly-generated response when not the main user (3 hours)

6. Invite chosen users to an event
   6.1 Allow an existing event's invited users to be pre-selected on load (3 hours)
   6.2 Launch an invitation screen when creating new events to let the user choose a suggested time (6 hours)

From I2 feedback, we implemented the following:
8. Create a "Today" button to take the user to their schedule on the given day
9. Allow users to edit their responses to invitations
10. Show feedback and confirmation dialogs when the user takes important actions

#### How to find implemented features
There are three buttons on the main home screen:
1. My Schedule
   - Displays a calendar that lets a user select a date and see their events on that date
     - The "daily schedule" contains both the user's created events and events they've accepted invitations to
   - The "Today" button at the top will automatically take a user to their daily schedule for the current day
   - Contains a header bar from which the user profile is accessible (top right icon)
2. My Events
   - Displays events that a user has created
   - Displays events that a user has accepted invitations to
   - Allows users to delete events they've created with the garbage can icon
   - Allows users to edit events they've created by tapping the event card
   - Allows users to create new events with the "Add Event" button at the bottom
   2.1. Create Event
     - When creating an event for the first time, prompts a user to select invitees (see 2.1.1)
     - Invitees can be edited at any time with the "Invite Users" button
     - Prompts a user to enter a name and location for the event, as well as select a date and time
     - A user cannot try to save an event until its information is valid
     - A user cannot save an event if they are unavailable at the time they've chosen
     2.1.1. Invite Users
       - Displays a list of Plarty users that can be selected or deselected
       - When one or more users are selected, a list of three or fewer suggested times is displayed at the bottom of the screen
       - Tapping a suggested time will exit the invitation screen, and initialize the "Create Event" screen with the selected time
       - Tapping the "Set my own time" button will save the checked invitees, but will not use a pre-set time
       - Tapping the "Skip" button will exit the invitation screen without saving any invitees or times
       - Invitations are only sent when the event information is saved
3. My Info
   - Displays a "My Invitations" button that shows a user's invitations 
   3.1. My Invitations
     - A user can respond to an invitation with the "Accept," "Maybe," or "Decline" buttons
     - On a response, a new entry is added to the database representing the response
     - Responses can be edited at any time
     - A user cannot accept an invitation if they aren't available to attend it
   - Displays a "My Active Hours" button that allows a user to specify when they're active
   3.2. My Active Hours
     - Default active hours are always, or 0:00 - 0:00
     - Setting "active hours" means a user can't attend events outside of those hours, and when others invite them to events, times outside of those hours will not be suggested
     - A user cannot change their active hours to exclude events they've already created or accepted invitations to

#### Architectural Sketch
Here is a picture of the Architectural Sketch:
![img.png](architectural_sketch.png)

(If you cant see the image, the architectural sketch can be found at 3350_Plary/architectural_sketch.png

### Logs
The logs are kept at the root of the folder, in the same place where you'll find this document.
We used the logs to keep track of what we implemented to ensure that it follows from the dev tasks
We had a separate document to brainstorm ideas but this is the clean copy of it.
Log contributions contain date, name, dev tasks/accomplishments, as well as total delta (lines added + lines deleted).

### Testing
We tested the app on the Nexus 7 emulator with Marshmallow 6, API level 23.
As well as the unit/integration tests under the **app/src/test** to ensure the logic worked as promised.

#### To launch the app
1. Make sure you open the project in android studio and have the emulator already set up.
2. Choose the app and Nexus 7 emulator and click run.
3. Give the computer some time to compile and enjoy the app!
4. To reset the database for a *pleasant testing experience*, go to the emulator's Device File
   Explorer and navigate to data > data > comp3350.plarty and delete "app_db." 

#### To test the Unit/Integration Tests
1. Go to **app/src/test/java/comp3350/plarty/tests**
2. Click on **AllTests.java** and press **Run 'AllTest'**

### Iteration 2 Retro
After discussing for some time, it became apparent that *lack of communication* was the main issue
we had as a team, here is what we came up in how to fic it 

#### How to fix:
- Deadline: Having a deadline, if you don't meet it, someone else will take it.

- Initiative: After you finish a task, start working on the next thing and announce it to the group.

- Biweekly 15 min checkups: Just to see where is everyone at if you are stuck, how much work have you done

- Documenting things more: Adding more things to the log and keeping it updating.

- Familiarize yourself with the code: So that you know what your are doing and how everything works

#### Evaluate success: 
- If we arent cramming until 5am it worked. 
- Submitting without known bugs. 
- The logs will show up the meetings. 
- Submitting the day before the due date so that we are not rushing the last date.

