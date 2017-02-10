public class Season {
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
