ClioMatters
============

ClioMatters is a solution for programming exercise.

ClioMatters is an Android application that accesses Clio data using API calls to their backend. It displays all matters in a List View. Based on the status of the matter, it has an unique icon.
Matter's details are shown after clicking on the corresponding row of the List View.
Users can share matters via gmail, messages, etc.

MVC design pattern was used for implementing user interfaces.

All data is saved as parcelable in SQL Lite database after the first use of the app and available for using afterwards.

As for now, only READ functionality is implemented, but app has extra buttons and functions for adding more features such as search, update, edit, delete.

No external libraries were used.