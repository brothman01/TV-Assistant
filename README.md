# TV Assistant
### By Ben Rothman

*I wrote this program for fun in college.  It does not work anymore but it was fun.*

## 1. What is the Show Assistant

The Show Assistant is a tracker for all the shows you watch.  There are three main functions the program performs: 1) Tracking/Reminders, 2) Keeping track of which episodes you already watched and which you have yet to watch, and 3) getting a random episode of a show to watch.  The program gets season and episode data on your favorite shows (those listed in the ’favorites.txt’ file) from the internet and tells you which days to watch each show.

The program stores whether or not you have watched an episode.  Using the ’toggle’ command you can tell the program which episodes you have already watched so that you will not be reminded to watch it again.

The final bit of functionality the TV Assistant has is the ability to suggest a random episode from a random season of a show.  Just use the 'random' command and specify which show to get the episode from.

The TV Assistant was made to be a terminal program, but it can be run from any IDE that accepts Java and has input and output streams.

### 2. How to run TV Assistant from the Command Line
 1. `cd to parent folder.
 3. `$ java TVAssistant.`

## 3. Command Reference:
**reminders** - Get reminders to watch episodes that have already aired but that have not been watched.<br>
**list** - lists all of the available commands<br>
**info** - get info on a show or list all shows tracked by the program.<br>
**toggle** - change the ‘watched’ of an episode.<br>
**update** - updates the data with current information from the web.<br>
**watchedFile** - populates the watched file with ‘true’ for every episode. (will be removed in later versions)<br>
**random** - get a random episode of a show.<br>

## 4. How do the data files work
There are two main ways that the program gets data about your favorite shows: from the local data file and from the web.  Obviously the web has more current and complete data available about each show, but the local data file allows the user to customize the program to perform whatever checks they want.

If you want to add to or change the shows that this program displays info about then put the changes in this file and the program will reflect those changes.

## FAQ:

Q. I am trying to pass in the name of a show but the program is not finding it, what is wrong?

A. The program does not take spaces in show names.  Use camel case instead of spaces in the names of shows.<br>i.e. UglyAmericans

Q. How should a 'toggle' command be structured?

A. ’toggle show name seasonnumberxepisodenumber
i.e.:'toggle Archer 3x22'

Q. How Should a 'random' command be structured?

A. random showname
ie. 'random Archer'

Q. How do I add a show to the TV-Assistant so that it monitors that show too?

A. inside this TV-Assistant directory there is a text file called 'favorites.txt'.  Open that and add new shows with their info in the way shown in the header. DO NOT FORGET TO USE ALL OF THE PUNCTUATION AND DIVIDERS SHOWN ON THE FIRST LINE.



##Future Features:
- implement ‘reminders’ command
- recommend a show that user has never seen before
- store database of watched shows on a server so that the ‘watched?’ of a show is editable via smartphone
- use of the rules in the ‘rule.txt’ file in randomization
- use of rules in randomization
- Add a GUI
