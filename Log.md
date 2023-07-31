Hey! Welcome to the logs for the *Plarty People*

The git was created and shared by Chris on May 31
- Mia - first commit on June 1.
- Barb - first commit on June 1.
- Aneesh - first commit on June 2.
- Krushang - first commit on June 11.

Developer Tasks:
Big Story To Implement: Manage Schedule
- 1 View your Schedule
  - 1.1 Setup events in storage to use. Estimated Time: 3 hours
  - 1.2 Have event information we can expand upon such as name, date. 3 hours
  - 1.3 Retrieve events for a given user (setting up getters and setters) 2 hours
  - 1.4 Create a blank schedule template we can slot things into, object + UI. 4 hours
  - 1.5 Set up different views, monthly, weekly, and daily. Estimated Time: 4 hours
  - 1.6 Testing objects and logic. Estimated Time: 8 hours
  - 1.7 Create business logic for interacting with Users and Events with invites and schedules: 10 hours
- 2 Schedule Updates:
  - 2.1 Changes to the user schedule reflects on the view. Estimated Time: 1 hour
  - 2.2 Testing. Estimated Time: 2 hours
  - 2.3 Store schedule information for a user. Estimated Time: 3 hours
  - 2.4 Provide business functions to add and remove items from a schedule. Estimated Time: 3 hours

If the number is called 'HouseKeeping' is related to any code cleaning, documentation,
review of the project.

Log format:
- DD/MM/YYYY - Name
  - thing 1 you did
  - thing 2 you did
  Total Delta: ~n lines.

- 02/06/2022 - Group meeting to figure out responsibilities.

- 05/06/22
  Mia! - 1.5
  - added daily schedule UI activity.
    Total Delta: ~300 lines of XML.

- 06/05/2022
  Aneesh - 1.1
  - Made Event Object.
  - Made DataStub.
    Total Delta: ~100 lines

- 07/06/2022
  Barbara :) - 1.1 / 1.2 / 1.3
  - Research on Joda time.
  - Restructured event and dataStub to include Joda time.
  - Added basic User object.
    Total Delta: ~200 lines.

- 08/06/2022
  Chris - 1.2 / 1.3 / 2.3
  - Restructured event and user object code.
  - Added new functions and datatypes.
  - Set up initial code for AccessUsers and AccessLists.
    Total Delta: ~170 lines (merging and template file removal not included)

- 09/06/2022
  Barbara :) 1.2
  - Restructured to add intervals to events instead of having 2 date times.
    Total Delta: ~50 lines (?)

- 10/06/2022
  Mia! - HouseKeeping
  - Separated SRSYS files from Plarty stuff.
  - Changed manifest and project settings from SRSYS.

- 11/06/2022
  Krushang - 2.1
  - created event UI activity total
  - added new event UI activity
  - added recycler view in event UI to display event card
  - added card layout xml for myEvent cards
  - Total delta: ~ 195 lines

- 12/06/2022
  Aneesh - 1.6
  - Made one of the business object test.
    Total Delta: ~150 lines

- 12/06/2022
  Chris - 1.1 / 1.6 / 1.3 / 1.7 / 1.6 / 2.3 / 2.4
  - Revamped Data Access Stub and Services to allow for object storage and creation.
  - Started to update object tests to JUnit 4.
  - Created new object getter and setters
  - Created business layer logic, AccessUsers, AccessEvents and Atomic access to make changes to both.
  - Set up testing files, created basic tests for objects and AccessUsers
  - Total delta: ~873  (merging and template file removal not included)

- 12/06/22
  Mia - 1.4 / 1.6
  - Added unit tests for User object
  - Added daily schedule activity class to dynamically get user event info.
  - Added XML templates for weekly and monthly schedule.
    Total delta: ~470 lines

- 12/06/2022
  Barbara :) - 1.6
  - Started to clean up object test so they are separated by aspect.
  - Started to update object tests to JUnit 4.
  - Total delta: ~200 lines

- 13/06/2022
  Krushang - 2.1
  - Created recycler view Adapter
  - Added class to in Adapter to get event information
  - Some changes to Event UI and card layout
  - Total delta- ~100 lines

- 13/06/2022
  Barbara :) - 1.6
  - Finish More clean up to the business tests so they are separated by aspect.
  - Added tests to Event Tests.
  - Finished updating object and business tests to JUnit 4 + test Suite.
  - Note: The test pass individually but when I run all suites it doesn't work, I think it has to do.
    with the unordered testing so imma ask Braico about it tom bc I don't know how to separate them.
  - Still need to add edge cases to accessEvents and accessUser.
  - Total delta: ~300 lines

- 12/06/2022
  Chris - 1.7
  - Small business bug fixes
  - Total delta: ~180  (merging and template file removal not included)

- 14/06/2022
  Aneesh - 1.6 / HouseKeeping
  - Made little changes to object testing
  - Made architectural sketch.
  - Total Delta: ~ 183 lines + a diagram.

- 14/06/2022
  Mia <3 1.5
  - Pass user data to weekly schedule view
  - UI tweaks
  Total delta: ~205 (mostly XML)

- 14/06/2022
  Barbara :) 1.6 / HouseKeeping
  - Ok for real I finished restructuring objects tests and event tests
  - I went to Braico's office hours about the testing we need and he help me clarify stuff
  - I think they are good for real, just the user tests to do
  - First pass on General clean up of unused imports
  - Total delta: ~300 lines

- 15/06/2022
  Aneesh 1.5
  - Created main page of app.
  - Total delta: ~200 lines

- 15/06/2022
  Chris - 1.7 / 1.6
  - Added business functions
  - Wrote lots of tests (many were integration tests)
  - Fixed Event bugs revealed by tests
  - Cleaned up tests
  - Project finalization
  - Total delta: ~ 1370 (merging and template file removal not included) (up to cleaning up tests)

- 15/06/2022
  Barbara :) 1.6 / HouseKeeping
  - Added unit tests to EventAccess.
  - Cleaned up unused functions, added little descriptions, unused imports, and warning
    the Logs, Business, Persistence and Object layers
  - First Pass on the ReadMe.
  - Final fix to the all suites tests so that all tests pass.
  - Project finalization.
  - Total delta: ~200 lines (without readme)

- 16/06/2022
  Mia! 1.2 /  2.1 / HouseKeeping
  - Tapping an event in the list lets you edit its info
  - Misc. cleanup of functions and classes
  - Total delta: ~50 lines

- 15/06/2022
  Barbara :) HouseKeeping
  - Bugfixing UI
  - Total delta: 3 lines lol

____________________________________________________________________________________________________
Iteration 2:
Base things to do:
I2.1 Revise the logic and put them out in its own java file. Barbara. 2 hours
I2.2 Revise testing *unit + integration*. Chris 4 hours
I2.3 Setup database with events and people invited. Aneesh. 3 hours
I2.4 Let the main user have some events by default. Krushang. 1 hour (wait for setup and revise)
I2.3 Revise database and stub are the same. Mia 1 hour

Big User Story  -Analyze Events:
3 Availability Summaries
3.1 Let the user have set at preferred available time, setters and validation. Chris (8 hours)
3.2 Have a user profile(?) so the user can add and see their preferred available time. Mia
3.3 Set a place in the database to store the preferred times.

4 Time Preference Summaries:
4.1 Add logic so that it inputs a list of users with preferred time to suggest the best time for
the venue. Barbara (4 hours)
4.2 UI Let users get invited to the events. Krushang/mia (3-6 hours) (4 july)
4.3 Let users accept invitations to events Krushang/mia (3-6 hours) (4 july)
4.4 UI create a view to see who is invited to the event. Krushang (3 hours) (29 june-1 July)
4.5 Let the event organiser invite people in the db and suggest different preferred times

- 18/06/2022
  Mia - HouseKeeping
  - Added error dialog to event creator
  - Added end date to event list items
  - Total delta: ~15 lines

- 30/06/2022
  Mia - 3.2 / HouseKeeping
  - fixed bug that lets users add null events
  - removed unused CLI class
  - created a user profile drawer template
  - Total delta: 60 lines

- 30/06/2022
  Barbara :) I2.1
  - Taking the logic out of the access methods
  - Changing the name of some return methods
  - Added a validation class for easier null checks
  - Total delta: ~400 lines (mostly abstracting things and adding try catch blocks)

- 1/7/2022 2.2
  Christopher - Cleaning up things we lost marks on
- Removed all single lettered variables from business code because we lost marks for it
- Reordered all methods across classes to make them more consistent, like toString after equals
- Added class level comments, and constructor comments
- Added some multi line comments because we lost marks for only having single line comments
- Total delta: ~280 lines.

Note: I really wish we got some sort of style guide from the markers so that we know what they are expecting.  
Each company I have worked with has different style expectations and defined quality code differently.
So for example on iteration 1, we lost marks for using single letter variables and different method ordering across two classes.
At my last job we were actually encouraged to use single letter variables in obvious cases; The idea was that providing
overly descriptive names for variables would often slow readers down more than small, simple and obvious names such as Event e, User u.
Similarly, I have not previously seen enforcement on method ordering, like requiring toString to come before Equals consistently across all classes.
In the same way that employers provide style guides before employees write code, if we were provided with a style guide from the markers
describing their expectations, that would allow us to meet grading expectations much easier.

- 1/7/2022
  Aneesh - Created database using hsqldb
  - Read documentary provided on hsqldb
  - Created some folders and files for database.
  - Created the tables & inserted some data in those tables.
  - Total Delta: 100 lines.

- 2/7/2022
  Aneesh - Created connection with Database using android studio and embedded some queries.
  - Made DatabaseAccessObject in which connection with android studio is established.
  - Made Interface for Database
  - Total Delta: 250 lines

- 02/07/2022
  Mia - 3.2
  - fixed user profile drawer layout
  - Total delta: 510 lines

- 03/07/2022
  Mia - 3.2 / HouseKeeping
  - programmatically add user info to profile layout
  - layout optimizations
  - note: user profile exists, but doesn't appear; layout is unfinished!
  - Total delta: 260 lines

- 04/07/2022
  Mia - 3.2
  - fixed a typo in the log lol
  - layout optimizations
  - added buttons to profile (no destinations yet)
    - view/set preferred times
    - view pending invitations
  - profile can be opened from all schedule views
  - Total delta: 400 lines

- 04/07/2022
  Barbara :) 4.1
  - Adding the time logic to the timeManagement
  - Added some tests to it
  - Total delta: ~200 lines

- 06/07/2022
  Chris, 3.1
  - Restructured Event class to allow for recurring events
  - Added Abstract Event, Single Event, RecurringEventGenerator, RecurringEvent
  - Created new business logic for new events
  - Still needs to be tested
  - Total Delta: 400

- 07/07/2022
  Chris, 3.1
  - Created new functions to generate user schedules
  - Tons of refactoring
  - Total Delta: 250

- 06/07/2022
  Aneesh
  - Replaced DataStub with Database.
  - Edited Main and Services Class.
  - Refactored stuff including interface and Database
  - Fixed some errors.
  - Total Delta: 250 lines

- 08/07/2022
  Chris, 3.1, 2.2
  - Created new tests for Event objects
  - Light debugging on AccessEventsTest and TimeManagementTest
  - Total Delta: 275

- 08/07/2022
  Mia
  - debugging event creation/edits
  - fixing display of events in daily schedule
  - Total delta: 160 lines

- 09/07/2022
  Aneesh
  - Added invite table to the database.
  - Populated table with data.
  - Added update commands to the Database object.
  - Refactored code according to the sample project.
  - Total Delta: 200 lines

- 09/07/2002
  Mia
  - reworks to account for changes to the event class structure. these include:
    - made AccessEvents abstract
    - added concrete classes AccessSingleEvents and AccessRecurringEvents to handle unique functionality
    - separated createEvent function into createSingleEvent and createRecurringEvent
    - made business layer responsible for constructing Event objects and assigning IDs
    - let persistence layer accept Event objects to add directly to the DB instead of constructing them
    - edited all tests with events to refer to SingleEvents
  - miscellaneous style fixes for consistency :)
  - Total delta: 155 lines

- 09/07/2022
  Chris, 3.1, 2.2
  - Created new logic for checking if recurrent events overlap with other recurrent events
  - Refactored tests to remove some single letter variables
  - Made business functions accept objects rather than ids as per SOC
  - Total Delta: 490

- 09/07/2022
  Chris, 3.1
  - Refactored to move invitations to Single Events only.
  - Total Delta: 100

- 10/07/2022
  Aneesh
  - Made datastub replica of real database.
  - Added and deleted some values + made some adjustments.
  - Made Architectural Sketch.
  - Total Delta: 100 lines

- 10/07/2022
  Chris
  - Refactoring.
  - Total Delta: 100

- 10/07/2022
  Mia
  - integration tests for AccessRecurringEvents and subsequent refactors/additions
  - refactored some existing integration tests to match code changes
  - added methods & tests for recurring events in AccessAtomic
  - minor UI cleanup
  - Total delta: 815 lines

- 10/07/2022
  Krushang
  - Created object for User Profile Activity UI
  - Created layout for the invite card for card
  - Created an adapter for Invite event for invited event activity
  - Created View invited events Activity object
  - Some minor bug fixes in the presentation layer and UI
  - Total Delta: 370

- 11/07/2022
  Mia
  - UI cleanup and integration
  - fixing hsqldb data access
  - fixed timestamp storage in db
  - fixed owned event retrieval from persistence
  - fixed event creation in presentation
  - fixed event editing in persistence
  - fixed invited event retrieval from persistence
  - added suggested time to event creation after users are invited
  - some changes to suggested time retrieval
  - style fixes for ui java and xml
  - refactoring tests to fit code restructures
  - Total delta: 1300

- 11/07/2022
  Chris, 2.2
  - Tons of refactoring.
  - Removal of bidirectional dependencies
  - Fixing tests
  - Allowing for storage of Recurrent Events in DB
  - Total Delta: 1786

- 11/07/2022
  Aneesh
  - Fixed data stub bug in the tests.
  - Total Delta : 100

- 11/07/2022
    Krushang
  - Created a page to invite users in presentation
  - Created the layout for invite user
  - added invited users to card layout in my event
  - added delete event button in my event presentation and Event card layout
  - Bug fixes in presentation and UI
  - Total Delta: 1010

- 12/07/2022
  Mia
  - removing dead code and warnings
  - small db debug
  - Total delta: 210
- 12/07/2022
  Aneesh
  - Fixed one of the business layer test.
  - Total Delta: 50 lines.

- 12/07/2022
  Chris
  - Bug Fixes
  - Total Delta: 65

- 12/07/2022
  Krushang
  - Minor bug Fixes
  - Total Delta: 50

- 12/07/2022
  Barbara :)
  - Note: I did more stuff on different days but this is the whole smack of it
  - Access Stub refactor
  - Cleaning up Tests
  - Adding tests
  - Bug Fixes
  - Total delta: ~600 lines

____________________________________________________________________________________________________
Iteration 3:

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

- 13/07/2022 Group meeting ~30 min
             Attendees: Aneesh, Barbara, Chris, Krushang, Mia
  - We talked about the retro and things we need to work on such as communication
  - We started on dev tasks but we need to keep digging through them
  - Set out first due, to date to clean up iteration 2 by this friday July 15

- 13/07/2022
  Chris HouseKeeping
  - Bug Fixes on Stub Database
  - Total Delta: 90

- 13/07/2022
  Barbara :) HouseKeeping
  - Commenting previous iteration code
  - Total delta: 15

- 14/07/2022
  Barbara :) HouseKeeping
  - GenerateRecurrentEventsTest Review finished
  - first pass on GetIntervalFromDefault, started on generateEvents test
  - Total delta: ~250

- 14/07/2022
  Chris 5.1
  - Default Generation.
  - Put default generated responses to invites in DB.
  - Total Delta: 210

- 15/07/2022
  Mia :) 6.1
  - added UI for active hours, currently non-functional
  - misc. bug fixes and ui refactors
  - Total delta: 350 lines

- 16/07/2022
  Barbara :) HouseKeeping
  - finished generateEvents test
  - finished all the GenerateRecurringEventsTest
  - Will have another pass later, I can probably think of more edge cases
  - Need to segregate the tests have a function for each test case
  - Total delta: ~300 lines

- 16/07/2022
  Chris HouseKeeping
  - Fixed SQL Database.
  - Type checking for Single Events in functions.
  - Total Delta: 290

- 16/07/2022
  Mia HouseKeeping / Refactoring
  - added business functions to set active times
  - moved some logic out of presentation
  - added a table to HSQLDB to track users' special "inactive" events
  - added a hashmap to stub for consistency w/db
  - cleaned up db constraint names
  - added extra TODOs to take care of later yahoo
  - Total delta: 400 lines

- 16/07/2022
  Krushang HouseKeeping
  - Added getSuggestedTimeList to timeManagement
  - minor bug Fixes
  - Total delta: 109 lines

- 17/07/2022
  Mia Testing / 10
  - added tests for active hour functions
  - edited AccessUser tests to pass with new generated data
  - presentation layer cleanup and additions
    - sorting events in the My Events page
    - colouring inactive event differently on the schedule
    - letting active times display a user's current active hours by default
  - Total delta: 305 lines

- 18/07/2022
  Barbara :) HouseKeeping
  - Base for the 3 types of tests we need to do
  - Cleaned up AccessRecurringEventsTest, SingleEventsTests and GenerateRecurringEventsTests
  - Started to clean up TimeManagement, will keep doing it later after watching all the lectures
  - Some Cleanup here and there
  - Total delta: ~300 lines

- 18/07/2022
  Mia HouseKeeping / Refactoring
  - cleaning up presentation and moving logic into business
  - fixed a couple of DB bugs when editing and assigning ids
  - Total delta: 300, a lot of it was just renaming

- 19/07/2022
  Barbara :) HouseKeeping
  - Cleanup and adding tests most of object test layer
  - Didn't touch userTest yet, but the rest of the object testing have like 2 test cases each to do
  - Currently, all the tests in the object layer pass
  - Moved acceptance tests layout to a proper folder
  - Total delta: ~350 lines

- 20/07/2022
  Mia 8
  - fixing some display bugs
  - added string converter to presentation
  - fixed logic for suggesting times based on selected users
  - invite page layout change
  - Total delta: 850

- 20/07/2022
  Barbara :) Testing
  - Cleanup and adding tests most of object test layer to actually be in Junit4
  - Making the persistence layer test to junit 4 + adding more tests
  - Event and RecurringEvent toString tests added
  - Total delta: ~600 lines

- 20/07/2022
  Aneesh Testing
  - Resurrected Business, Integration and Persistence Test classes.
  - Added DataAccess Tests
  - Added DataAccessHSQLDBTest
  - Refactored some code.
  - Moved DataStub and GenerateUtil to tests section.
  - Total Delta: ~350 lines

- 21/07/2022
  Chris 5.1
  - Finished up first round of tasks!
  - Debugging work
  - Total delta: 10

- 21/07/2022
  Mia 6.2
  - finished reworking of invite page!
  - a little bit of persistence debugging
  - some more miscellaneous style refactors for consistency
  - Total delta: 410

- 21/07/2022
  Aneesh Testing
  - Added Acceptance tests
  - Made changes in structure + gradle
  - Added BusinessPersistenceSeamTests
  - Total Delta: ~700 lines

- 22/07/2022
  Barbara :) Testing / HouseKeeping
  - Cleaned Seam tests
  - Cleaned up Access & their tests
  - Total delta: ~600 lines

- 23/07/2022
- Aneesh HouseKeeping
  - Test debugging
  - Total Delta: ~15 lines

- 24/07/2022
  Chris HouseKeeping
  - Added business functions for lists of events
  - Total delta: 30

- 24/07/2022
  Mia Refactoring
  - added polymorphic event methods to get rid of instanceOf statements
  - Total delta: 200 lines

- 25/07/2022
  Krushang 8
  - added Today button to monthly view
  - minor Fixes
  - ~ 43 lines

- 25/07/2022
  Chris Housekeeping
  - Finished up first round of tasks!
  - Debugging work
  - Acceptance Testing
  - Total delta: 600

- 26/07/2022
  Mia 9 / 10
  - added feedback & confirmation dialogs for important events
  - invitation responses can be edited after being sent through invitations page
  - Total delta: 390

- 26/07/2022
  Chris Testing / HouseKeeping
  - Wrote acceptance tests
  - Database Debugging
  - Total delta: 670

- 27/07/2022
  Mia HouseKeeping
  - last commit! goodbye plarty, it was fun!
  - SO much javadoc (not included in delta bc it felt like cheating)
  - ui bug fixes
  - adding object validation in certain business functions
  - Total delta: 280 (1k javadoc)

- 27/07/2022
  Chris Testing
  - Finished Acceptance Tests
  - A lot of debugging
  - Total delta: 2300

- 27/07/2022
  Barbara :) HouseKeeping
  - Finished Integration tests
  - Debugging
  - Total delta: ~410 lines

- 28/07/2022
  Krushang Testing
  - added tests for edge cases in time Management tests
  - code cleanup and comments
  - added tests for invalid cases in DataStubTests
  - minor bug fixes
  - Total Delta ~374 lines

- 28/07/2022
  Aneesh Testing
  - Stub tests
  - Total Delta: ~433 lines

- 28/07/2022
  Chris Housekeeping
  - Code clean up
  - Total delta: 100

- 28/07/2022
  Barbara :) HouseKeeping
  - Code/Project clean up
  - Debugging
  - Total delta: ~100 lines
