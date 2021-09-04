/*
 * Title: TV Assistant
 * Author: Ben Rothman
 * Author URL: www.BenRothman.org
 * Release Date: 8/26/16
 * Version: 0.1 (alpha)
 * description: Tracks data for all shows listed in the favorites file of each user
*/

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

public class TVAssistant
{
  private File favoritesFile = new File("favorites.txt"); // file containing the users favorites/preferences for what the program will search for

  private ArrayList<Show> allShows = new ArrayList<Show>(); // an array of every show with it's season and episode data

  private String listOfCommands =
    "reminders - get reminders for unwatched/new episodes of a show that have aired but not been watched, (specify 'all' for reminders about every show)\n"
  + "list - list of commands\n"
  + "info - get info about a show\n"
  + "toggle - toggle watched of episode\n"
  + "update - update local data file with data from the web. \n"
  + "random - get a random show or random episode from the specified show ('random Archer')\n"
  + "exit || quit || x || q - exit / quit";

  public TVAssistant()
  {
    System.out.println("Welcome to the Show Assistant 0.1\n(Type 'list' for a list of available commands)");
    System.out.println("Reading Data From Show URLs:");

    // read the info for each show in 'favorites' file and fill that show out with season and episode data then add it to an array
    for (Show show : readFavorites(favoritesFile)) {
       show.sanitizeAndBuild();
       allShows.add(show);
    }

   // get command from user (main menu) (loop forever until program termination)
     for (int c = 1; c > 0; c++)
     {
       String cmd = GetUserInput();
       execute(cmd);
       pause(1000);
    }


  }
  
  // get the user's input entered into the inputstream
  public String GetUserInput()
  {
        String cmd = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("$ ");

        try
        {
          cmd = reader.readLine();
        } catch (Exception e) {}

        return cmd;
  }

  // execute the given command
  public void execute(String cmd)
  {
    String params = cmd;

    if (cmd.startsWith("reminders")) {
      ArrayList<Episode> reminders = new ArrayList<Episode>();
      String[] param = cmd.split(" ");

      if (param[1].equals("all")) {
        // get reminders about all shows
        System.out.println("Getting reminders for all shows");

         for (Show show : allShows) {

              for (Season season : show.seasons) {

                // if episodes airdate is before or the same as the current date and the episode is unwatched then add it to the 'episodes' AL TODO
                for (Episode ep : season.episodes) {

                  if( (ep.airDate).before(new Date()) ) {
                    if (ep.watched == false) {
                      reminders.add(ep);
                    }
                  }

                }
              }
          }

      } else {
          for (Show show : allShows) {

            if (param[1].equals(show.title)) {

              System.out.println("Getting reminders for " + show.title);

              for (Season season : show.seasons) {

                // if episodes airdate is before or the same as the current date and the eoisode is unwatched then add it to the 'episodes' AL TODO
                for (Episode ep : season.episodes) {

                  if( (ep.airDate).before(new Date()) ) {
                    if (ep.watched == false) {
                      reminders.add(ep);
                    }
                  }

                }
              }
            }
          }


      }

     System.out.println("Reminders: " + reminders + " have already aired but not been watched.");


    // print out a list of commands
    } else if (cmd.startsWith("list")) {
      System.out.println(listOfCommands);

    // get info about a show
    } else if (cmd.startsWith("info")) {

      String theTitle = params.substring(5, params.length());

        for (Show show : allShows) {

          if ((show.title).equals(theTitle)) {
            System.out.println("Found show '" + theTitle + "'");
            display(show);
          }

        }
    } else if (cmd.startsWith("toggle")) {
      toggle(cmd);

    } else if (cmd.startsWith("update")) {

      System.out.println("\nReading Data From Show URLs:");


      for (Show show : readFavorites(favoritesFile)) {
        show.sanitizeAndBuild(); // sanitize the content gotten from the web in the last step
          allShows.add(show); // add the built show to the final array for shows
      }


    } else if (cmd.startsWith("watchedFile")) {
      System.out.println("Writing to the watched database file.");
      // populate watched
      String output = "Episode ID,watched\n";
      for (Show show : allShows) {

        for (Season season : show.seasons)
        {

          for (Episode ep : season.episodes) {
              String line = ep.epCode + "," + "TRUE" + "\n";
              output += line;
          }

        }

      }


      try {
      File file = new File("watched.txt");
      // creates a FileWriter Object
      FileWriter writer = new FileWriter(file);
      // Writes the content to the file
      writer.write(output);
      writer.flush();
      writer.close();
      } catch (Exception e) {}


    } else if (cmd.startsWith("random")) {

      try {

      String[] parts = params.split(" ");
      String theTitle = parts[1];
      String rules = "";

         for (Show show : allShows) {

          if ((show.title).equals(theTitle)) {
            Episode ep = randomEpisode(show, rules);

            System.out.println(ep.title + " - " + ep.epCode);
          }

        }

      } catch (Exception e) {}

    } else if (cmd.equals("exit") || cmd.equals("quit") || cmd.equals("q") || cmd.equals("x"))  { // exit program
      System.exit(0);
    }

    }

  private void toggle(String raw_line)
  {
    String data = "";
    String[] pieces = raw_line.split(" ");
    String code = "";

    Show theShow = new Show("");
    Season theSeason = new Season(0, "", "");
    Episode theEp = new Episode("", "");

    // toggle Archer 3x22
    if (pieces.length == 3) {

      String showTitle = pieces[1];
      String inputEpisode = pieces[2];

      String[] ePieces = inputEpisode.split("x");

      // get the show based on the title and test for season and episode existance.
      for (Show show : allShows) {

        if ((show.title).equals(showTitle)) {
            theShow = show;
            break;
        }

      }

      for (Season season : theShow.seasons) {

        if (season.seasonNumber == Integer.parseInt(ePieces[0])) {
           theSeason = season;
           break;
        }

      }


      for (Episode ep : theSeason.episodes) {

        if (inputEpisode.equals( ep.epID )) {
           theEp = ep;
           break;
        }

      }

     code = theEp.epCode;


    } else {
      System.err.println("Invalid toggle selection.");
    }

    // write new text
    writeToggle(code);
   // System.out.println(allData.size() + "");


  }


   private void writeToggle(String sub)
  {
     String output = "";

     try {
 // Construct BufferedReader from FileReader
 BufferedReader br = new BufferedReader(new FileReader(new File("watched.txt")));

 String line = null;
 while ((line = br.readLine()) != null) {

   if (line.startsWith(sub + ",")) {

     String[] parts = line.split(",");
     String ans = parts[1];

     if (ans.equals("TRUE")) {
       ans = "FALSE";

     } else if (ans.equals("FALSE")) {
       ans = "TRUE";
     }

     String builder = sub + "," + ans;
     output += builder + "\n";

   } else {
     // write line to output
     output += line + "\n";
   }

 }

 br.close();
     } catch (Exception e) {}



//    // write outout to watched file
    try {
   FileWriter fw = new FileWriter(new File("watched.txt"));
   BufferedWriter bw = new BufferedWriter(fw);
   bw.write(output);
   bw.close();

   System.out.println("Done");

  } catch (IOException e) {
   e.printStackTrace();
  }

  }



  private ArrayList<Show> readFavorites(File showsFile)
  {
    ArrayList<Show> data = new ArrayList<Show>();

    try
    {
      FileInputStream fstream = new FileInputStream(showsFile);

      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine = "";

      while ((strLine = br.readLine()) != null)
      {
          try {

            if (strLine.contains("#title#[genre(s)]#[URL]#[show code]")) {
              // ignore line

            } else {
              Show aShow = new Show(strLine); // replace "" with epcode from file
              data.add(aShow);
            }
            // read episode data for the show

          } catch (Exception ee) {}
      }



    in.close();

    } catch (Exception e) {}

   // System.out.println(allData.size() + "");

    return data;
  }


  private void pause(int num)
  {
    try {
      Thread.sleep(num);
    } catch (Exception e) {}

  }

  private void display(Show show) { // displays info about the given show
    // display info about the given show
    System.err.println("\n" + show + ":"); // print the title

    System.err.println("Seasons: " + (show.seasons).size()); // print the number of seasons
    System.err.println("Genre(s): " + show.genres); // print the gemres
    System.err.println("Epguides URL: " + show.siteURL); // print the site URL
    System.err.println("Next Airdate: " + show.nextAirDate); // print the next airdate ??
    System.err.println("Showcode: " + show.showCode); // print the showcode

      for (int x = 1; x < (show.seasons).size(); x++) {
        System.err.println("Season " + ((show.seasons).get(x)).seasonNumber + ": " + (((show.seasons).get(x)).episodes).size() + " episodes");
      }


  }

  // returns a random episode from the given show
  private Episode randomEpisode(Show show, String rules) {

    Episode theEp;

    if (rules.equals("")) {
    // get a randomly generated season
    int seasons = (show.seasons).size();
    Random rand = new Random();
    int randomSeason = rand.nextInt(seasons) + 1;
    randomSeason = randomSeason - 1;
    Season season = (show.seasons).get(randomSeason);

    // get randomly generated episode from the randomly generated season
    int totalEpisodes = (season.episodes).size();
    int randomEpisode = rand.nextInt(totalEpisodes) + 1;
    randomEpisode = randomEpisode - 1;
    theEp = (season.episodes).get(randomEpisode);

    } else {
     // apply the rules
      theEp = new Episode("", "");
    }

    return theEp;

  }

  public static void main(String[] Args)
  {
      new TVAssistant();
  }

}
