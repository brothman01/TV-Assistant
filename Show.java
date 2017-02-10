import java.io.*;

public class Show {
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
