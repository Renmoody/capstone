# UI Section 
//TODO UI is subject to HEAVY change, I have lots of plans in the works. Must implement Firebase into the project configuration and start implementing that into the project.
Each fragment has a ViewModel that persists through the application lifecycle, TODO I have to add the data from each ViewModel to the database on application closing.
## HomeFragment
Home is where the user can see their feed. Their feed is going to be constructed through an algorithm that populates an event list with events posted from the users connections. If they have a connection who posted an event for people to RSVP to, then the user may see it on their feed. Professors will also be able to post events for people to RSVP for.
## DashboardFragment
Dashboard is where a user can create an event, they can add new events which populate a list for them to scroll through. The user can click on events and edit or delete them.
//TODO add the ability to invite users to events, add notifications
## MessagesFragment
Users will be able to message friends and groups about event details
//TODO implement messaging with firebase
## SettingsFragment
Users can set their information in this part of the app, possibly will add the ability for the user to switch the theme. 
## SignUp and LogIn
This is where firebase authentication will be implemented.
