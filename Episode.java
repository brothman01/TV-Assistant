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

public class Episode {

  public String title = ""; // title of the current episode
  public String infoURL = ""; // URL leading to in-depth info about the episode
  public Date airDate = new Date();
  public String epID = "";
  public String epCode = ""; // variable to store whether or not the episode has been seen (NEED TO PRESERVE THIS DATA THROUGH UPDATES !!)

  public String raw_content = ""; // raw content line

  private boolean WRITER = false;

  public boolean watched = false;

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
      String getDate3 = temp.trim();
      String year = getDate3.substring(0, 2);
      String month = getDate3.substring(5, 8);
      String day = getDate3.substring(getDate3.length() - 2, getDate3.length());

      String string = month + "/" + day + "/" + year;
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
      Date d = sdf.parse("21/12/2012");

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

      // get the epsiode ID
      String[] getCode1 = raw.split("\\.");
      epCode = getCode1[0];

    // build and set the episode code
    epCode = showCode + epCode;

    // get the 'watched' of this episode
    watched = getWatched();
  }

  private boolean getWatched() {

    try {
      // Construct BufferedReader from FileReader
      BufferedReader br = new BufferedReader(new FileReader(new File("watched.txt")));

      String line = null;
      while ((line = br.readLine()) != null) {

        if (line.startsWith(epCode + ",")) {

          String[] parts = line.split(",");
          String watched = parts[1];

          if (watched.equals("TRUE")) {
            return true;

          } else if (watched.equals("FALSE")) {
            return false;
          }

        }

      }

      br.close();
    } catch (Exception e) {}


    return false;
  }

  // print the attribute data for this episode
  public String toString() {
     DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy ");

     return title + "~" + infoURL + "~" + dateFormat.format(airDate) + "~" + epCode + "\n";
  }


}
