###The exercise
Create an app that displays a list of all matters on Clio using the results of a call to `https://app.goclio.com/api/v2/matters`. Upon clicking on a matter row, the app should drill into the matter’s profile. On the individual profile page, you should show the matter’s display name, client name, description, open date, and status. Other profile fields are optional.

###Additional details
* The exercise should take a good afternoon's worth of work.
* The app should work offline from a fresh open (e.g. force close and opening the app in airplane mode should still work fine after one previous launch). The persistence implementation does not matter, but the app should ideally be written in such a way that you could swap out implementations at a later date.
* You need only support API 15 and above.
* You are encouraged to use any 3rd party libraries that you deem appropriate. Please provide a brief explanation of why you chose to use each of the libraries you end up using.
* Any design details are also up to you.
* The app should look and feel like something you are proud of. Feel free to have some fun :)

###Instructions for submitting the exercise
1. Create a new git repo and Android Studio project, committing with frequency and with the type of commit messages you would write on a typical project.
2. A README that gives and overview of the project is strongly encouraged 
3. Zip up the repo and send as an attachment, as well as the github repo link.
4. We'll build it your app directly from the repo you provide

###Credentials
Use the following API method and the following API token to complete this task.
Documentation: http://api-docs.clio.com/v2/index.html#get-all-matters <br>
The following HTTP headers should be set for each request:
"Authorization" => "Bearer Xzd7LAtiZZ6HBBjx0DVRqalqN8yjvXgzY5qaD15a"
"Content-Type" => "application/json"
"Accept" => "application/json"
