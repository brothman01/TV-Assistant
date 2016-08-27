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
  
  private DateFormat dateFormat = new SimpleDateFormat("MM.dd.yy");
  private Date date = new Date();
  
  private ArrayList<Show> simpleShows = new ArrayList<Show>(); // an array containing every show with only the simple data found in the data file
  private ArrayList<Show> allShowsWithData = new ArrayList<Show>(); // an array of every show with it's season and episode data
  
  private String listOfCommands = 
    "reminders - get reminders for unwatched/new episodes for that day (specify 'all' for unwatched episodes of every show from any date) [NOT DONE]\n"
  + "list - list of commands\n"
  + "info - get info about a show\n"
  + "toggle - toggle watched of episode\n"
  + "update - update local data file with data from the web. \n"
  + "watchedFile - populate the watched file with 'true' for every episode (remove command later)\n"
  + "random - get a random show or random episode from the specified show ('random Archer')\n"
  + "exit || quit || x || q - exit / quit";
  
  public TVAssistant()
  {
    System.out.println("Welcome to the Show Assistant 0.1\n(Type 'list' for a list of available commands)"); // print welcome message once at program startup
        
    System.out.println("\nReading Data From Show URLs:");
    
    simpleShows = readFavorites(favoritesFile); // read the file containing a list of every show to be included and its URL + add each show found to 'simpleShows'
    
    // a loop to read the info for each show in 'simpleShows' and fill that show out with season and episode data
    for (Show show : simpleShows) { 
       show.sanitizeAndBuild();
      allShowsWithData.add(show); // add populated show to 'allShowsWithData'      
    }
    
   // get command from user (main menu) (loop forever until program termination)
     for (int c = 1; c > 0; c++)
     {
       String cmd = GetUserInput();
       execute(cmd);
       pause(1000);
    }
     
     
  }
  // get the user's input entered into the inputstream (magic)
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
  
  // execute the command given by the user
  public void execute(String cmd)
  {
    String params = cmd;
    
    // get reminders about a show
    if (cmd.startsWith("reminders")) { 
     
    // print out a list of commands
    } else if (cmd.startsWith("list")) {
      System.out.println(listOfCommands);
     
    // get info about a show
    } else if (cmd.startsWith("info")) {
      
      String theTitle = params.substring(5, params.length());
      
        for (Show show : allShowsWithData) {
          
          if ((show.title).equals(theTitle)) {
            System.out.println("Found show '" + theTitle + "'");
            display(show);
          }

        }
    } else if (cmd.startsWith("toggle")) { // toggle the watched of an episode
      toggle(cmd);
      
    } else if (cmd.startsWith("update")) { // update local data file with data from the web
      
      System.out.println("\nReading Data From Show URLs:");
      
      
      for (Show show : simpleShows) {
        show.sanitizeAndBuild(); // sanitize the content gotten from the web in the last step 
          allShowsWithData.add(show); // add the built show to the final array for shows
      }
      
    
    } else if (cmd.startsWith("watchedFile")) {
      System.out.println("Writing to the watched database file.");
      // populate watched
      String output = "Episode ID,watched\n";
      for (Show show : allShowsWithData) {
          
        for (Season season : show.seasons)
        {
          
          for (Episode ep : season.episodes) {
              String line = ep.epCode + "," + "TRUE" + "\n";
              output += line;
          }
          
        }
        
      }
      
      //System.out.println(output); // DEBUG
      
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
      
         for (Show show : allShowsWithData) {
          
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
      for (Show show : allShowsWithData) {
          
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

class Show {
  public String title = ""; // the title of the current show
  public String genres = ""; // genres the show falls under
  public String siteURL = ""; // URL of the info page for the site
  public String nextAirDate = ""; // airdate of the next unwatched episode
  
  private File LocalDataFile = new File("local_data.txt"); // file containing the local database of data
  
  public ArrayList<Season> seasons = new ArrayList<Season>(); // AL to contain all of the seasons of the current show
  
  private static boolean WRITER = false; // when true, writes the current line of the crawled site content to 'webContent'
  
  public String raw_content = ""; // info from the web after it has been made readable
 
  public String showCode = "";
  
  
  
  public Show(String favoritesData) {
    readAttributes(favoritesData); // read attributes of show from line of data file
     
    String html = ""; // variable to hold the raw data from each site
    
    // get the raw content of the show from the website
    if (!favoritesData.equals("")) {
      System.err.println("Reading '" + siteURL + "'"); // tell the user progress is being made
    }

    try {
    //Set URL
    URL url = new URL(siteURL);
    URLConnection spoof = url.openConnection();
    
    //Spoof the connection so we look like a web browser
    spoof.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
    BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
    String strLine = "";
    
    //Loop through every line of the source and add it to 'html'
    while ((strLine = in.readLine()) != null) {
           //Prints each line to the console
           html += strLine + "\n";
    }
    
    } catch (Exception e) {}
    
    // set this shows raw_content to everything read from the page
    String[] raw_lines = html.split("\n");
    raw_content = html;
    
    String content = "";
    String lines = "";
    String final_lines = "";
    WRITER = false;
    
    // GET EACH SEASON BY ITSELF AND PASS IT TO THE SEASON OBJECT

    
    int whichSeason = 0;    
    seasons.clear();
    Season season = new Season(whichSeason, showCode, html);
    
    for (String line : raw_lines) {

        Pattern regex = Pattern.compile("Season ([0-9])"); // regular expression to find
    
        Matcher searchString = regex.matcher(line); // create a 'Matcher' and pass the regex to find as the parameter to the 'matcher' function
        
        if (searchString.find()) {
          whichSeason++;
          seasons.add(season);
          season = new Season(whichSeason, showCode, html);
        }
        
        if ((line.contains("NoPrint"))) {
          seasons.add(season); // add the last season
          return;
        }
        
    }
    
    
    
    try {
      
      if ( (seasons.get(0)).seasonNumber == 0) {
        seasons.remove(0); // remove the 0th season
      }
      
    } catch (Exception e) {}
    
  }
  
  // print the title of the show
  public String toString() {
    return title;
  }
  
  // print every season of the show
  public String printAll()
  {
    String allShow = "";
      
    for (Season season : seasons)
    {
      allShow += season;
      
    }
    
    return allShow;
  }

  
  private void readAttributes(String raw) {
    
    try {
      
    String[] parts = raw.split("\\[");
    
    // set each of the main attributes of the show
    title = parts[0];
    genres = parts[1].substring(0, parts[1].length() -1);
    siteURL = parts[2].substring(0, parts[2].length() - 1);
    nextAirDate = "???";
    showCode = parts[3].substring(0, parts[3].length() - 1);
    
    } catch (Exception e) {}
    
  }
  
  // get the source code for the webpage given

  
  public void sanitizeAndBuild() {
    String content = "";
    String[] raw_lines = raw_content.split("\n");
    String lines = "";
    String final_lines = "";
    WRITER = false;
    
    for (String line : raw_lines) {
      
      if (line.contains("Season 1")) {
        WRITER = true;
      }
      
      if (line.contains("Specials")) {
        WRITER = false;
      }
      
      if (WRITER) {
        lines += line + "\n"; 
      }
      
    }
    
    // GET EACH SEASON BY ITSELF AND PASS IT TO THE SEASON OBJECT
    raw_content = lines;
    raw_lines = lines.split("\n");
    
    int whichSeason = 0;
    WRITER = false;
    
    seasons.clear();
    
    Season season = new Season(-1, showCode, "");
    
    for (String line : raw_lines) {

        Pattern regex = Pattern.compile("Season ([0-9])"); // regular expression to find
    
        Matcher searchString = regex.matcher(line); // create a 'Matcher' and pass the regex to find as the parameter to the 'matcher' function
        
        if (searchString.find()) {
          season.raw_content = content;
          content = "";
          WRITER = true;
          whichSeason++;
          seasons.add(season);
          season = new Season(whichSeason, showCode, "");
        }
        
        if ((line.contains("NoPrint"))) {
          seasons.add(season); // add the last season
          WRITER = false;
          return;
        }
                
        if (WRITER) {
          
          if (!line.startsWith("Season ") && !line.equals("") && line != null &! line.contains("id=\"latest") &! line.contains("pre>") &! line.contains("div>")) { // stop bug episodes from being added
            content += line + "\n";
            Episode ep = new Episode(line, showCode);
            season.addEpisode(ep);
          }
          
        }
        
    }
    
    
    
    try {
      
      if ( (seasons.get(0)).seasonNumber == 0) {
        seasons.remove(0); // remove the 0th season
      }
      
    } catch (Exception e) {}

  }
  
  
}

class Season {
  public int seasonNumber = 0;
  public ArrayList<Episode> episodes = new ArrayList<Episode>();
  public String raw_content = "";
  public String showCode = "";
  private boolean WRITER = false;
  
  public Season(int num, String code, String html) {
    seasonNumber = num;
    showCode = code;
    raw_content = html;
    
    String seasonNumberString = "Season " + seasonNumber;
    String nextSeasonNumberString = "Season " + (seasonNumber + 1);

    String[] temp = html.split("\n");
    String lines = "";
    
    for (int x = 2; x < temp.length; x++) {
      String line = temp[x];
      
      if (line.contains(seasonNumberString)) {
        WRITER = true;
      }
      
      if (line.contains(nextSeasonNumberString)) {
        WRITER = false;
        break;
      }
          
      if (WRITER) {

        
        
        lines += temp[x] + "\n";
      }
      
    }
    
    //raw_content = lines + "\n";

  }
  

  // add an episode to this season's episode arraylist
  public void addEpisode(Episode ep) {
    episodes.add(ep); 
  }
  
  // print the season number followed by every episode of this season
  public String toString()
  {
    String allEps = "";
    
    allEps += "Season " + seasonNumber + "\n";
    
    for (Episode ep : episodes) {
      allEps += ep; 
    }
    
    return allEps;
    
  }
  
  
}

class Episode {
  public String title = ""; // title of the current episode
  public String infoURL = ""; // URL leading to in-depth info about the episode
  public String airDate = "";
  public String epID = "";
  public String epCode = ""; // variable to store whether or not the episode has been seen (NEED TO PRESERVE THIS DATA THROUGH UPDATES !!)
  
  public String raw_content = ""; // raw content line
  
  private boolean WRITER = false;
  
  public Episode(String raw, String showCode) {
    raw_content = raw;
    
    // get the title
    try {
    String[] getTitle = raw.split("\">");
    title = getTitle[1].substring(0, getTitle[1].length() - 4);
    } catch (Exception e) {}
    
    
    // get the info url
    try {
      String[] getURL = raw.split("href=\"");
      infoURL = getURL[1].substring(0, getURL[1].length() - (4 + title.length() + 2));
    } catch (Exception e) {}
    
    // get the air date
    try {
      String temp = "";
      
      String[] getDate1 = raw.split("<a");
      String[] getDate2 = getDate1[0].split("-");
      temp = getDate2[1];
      
      for (char i : temp.toCharArray()) {
        if ( (temp.substring(0, 1)).equals(" ")) {
          // ignore character
          break;
        } else {
          temp = temp.substring(1, temp.length());
        }
      
      }
      
      // translate airdate into American date format?
      airDate = temp.trim();
      
    } catch (Exception e) {}
    
       // get the epID
       try {
         String[] getID1 = raw.split("\\.");
         String[] getID2 = getID1[1].split("<a");
         String theID = getID2[0].trim();
         theID = theID.substring(0, (theID.length() - 9));
         theID = theID.trim();
         theID = theID.replaceAll("-", "x");
         epID = theID;                                 
      
      } catch (Exception e) {}
    
      // get the epcode
      String[] getCode1 = raw.split("\\.");
      epCode = getCode1[0];
        
    // get the episode code
    epCode = showCode + epCode;
    
    //
    
  } 
  
  // print the attribute data for this episode
  public String toString() {
     return title + "~" + infoURL + "~" + airDate + "~" + epCode + "\n";
  }
  
  
}

