# TV Assistant
### By Ben Rothman



###1. What is the Show Assistant

The Show Assistant is a tracker for all the shows you watch.  There are three main functions the program performs: 1) Tracking/Reminders, 2) Keeping track of which episodes you already watched and which you have yet to watch, and 3) getting a random episode of a show to watch.  The program gets season and episode data on your favorite shows (those listed in the ’favorites.txt’ file) from the internet and tells you which days to watch each show.

Part of the data stored in the program is whether or not you have watched an episode.  Using the ’toggle’ command you can tell the program which episodes you have already watched so that you will not be reminded to watch it.

The final bit of functionality the Show Assistant has is the ability to suggest a random episode from a random season of a show.

###2. How to run Show Assistant from the terminal

	1. cd to the folder contains the TVAssistant files
	2. $ java TVAssistant

###3. Command Reference:
**reminders** - Get reminders to watch episodes that have already aired but that have not been watched.<br>
**list** - lists all of the available commands<br>
**info** - get info on a show or list all shows tracked by the program.<br>
**toggle** - change the ‘watched’ of an episode.<br>
**update** - updates the data with current information from the web.<br>
*watchedFile** - populates the watched file with ‘true’ for every episode. (will be removed in later versions)<br>
**random** - get a random episode of a show.<br>

###4. How do the data files work
There are two main ways that the program gets data about your favorite shows: from the local data file and from the web.  Obviously the web has more current and complete data available about each show, but the local data file allows the user to customize the program to perform whatever checks they want.

If you want to add to or change the shows that this program displays info about then put the changes in this file and the program will reflect those changes.

##FAQ:

Q. I am trying to pass in the name of a show but the program is not finding it, what is wrong?

A. The program does not take spaces in show names.  Use camel case instead of spaces in the names of shows. i.e. UglyAmericans

Q. How should a 'toggle' command be structured?

A. ’toggle show name seasonnumberxepisodenumber
i.e.:'toggle Archer 3x22'

Q. How Should a 'random' command be structured?

A. random showname
ie. 'random Archer'



##Future Features:

- implement ‘reminders’ command
- implement toggle command
- populate and allow the program to read and write to watched database
- recommend a show that user has never seen before
- store database of watched shows on a server so that the ‘watched?’ of a show is editable via smartphone
- use of the rules in the ‘rule.txt’ file in randomization
- use of rules in randomization
- Add a GUI
