# ClockodoWatcher
Clockodo Time-Watcher

This is a small command line application to verify and check your clockodo times.
In my case, my boss sometimes changed my work-times. Unfortunately, Clockodo does not tell me this. So my real worked time is wrong on some days. To document this, I wrote this small programm. My server executes this every day and checks my times!

Run this programm once. It will create a folder with two subfolders and an config file.
config file: Enter your Clockodo-E-Mail and your Clockodo API-Key (can be found in your settings)
root folder: Is always user.home. Named Clockodo
subfolder times: Here the watcher will store the latest requested Clockodo-Times
subfolder changed: If some changes are detected, the changes will be written in a new file and stored here. Here you can see on which day something changed.

Hint: Break-Time-changes are ignored for the last 7 Days. Reason: I often set my break time the next day. This is a valid change and should not be marked.
