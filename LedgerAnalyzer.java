import java.util.*;
public class LedgerAnalyzer {

/* Takes in a player user as a UUID String returns a UUID String of the mob boss the player belongs to */
  public static String findMobOfPlayer(String UID) {
    ArrayList<Integer> matchNumbers = DataBase.getData(UID);

    int lastMatchNumber = matchNumbers.get(matchNumbers.size() - 1);
    String match = DataBase.readMatch(lastMatchNumber);
    if(match.indexOf(UID) == 0)
    {
      return UID;
    }
    else
    {
      int commaIndex = match.indexOf(',');
      String winner = match.substring(0, commaIndex);
      return findMobOfPlayer(winner);
    }
  }

/*Takes in a mob leader as a String UUID and returns an ArrayList<String> of the UUIDs of all the players that belong in that mob*/
  public static ArrayList<String> findPlayersInMob(String leader) {
  //Log.add("FindingMob of "+ leader);
  ArrayList<String> mob = new ArrayList<String>();
  // a.add(m); where m is the mob leader
  //for (m last win to m first win) {
  // p = last person that m beat
  // if (p has won) {
  // findPlayersInMob(p)
  //}
  // a.stitch(all players in p's mob into m's mob)
  //}
  mob.add(leader);
  ArrayList<Integer> matchHistory = DataBase.getData(leader);
  int lastMatchNum = matchHistory.get(matchHistory.size() - 1);
  //System.out.println(leader + ", " + matchHistory.size());
  if(findLoser(lastMatchNum).equals(leader))
  {
    matchHistory.remove(matchHistory.size() - 1);
  }
  //System.out.println(leader + ", " + matchHistory.size());
  //System.out.println(leader + ", " + matchHistory.size());
  if(matchHistory.size() > 0)
  {
    for(int i = 0; i < matchHistory.size(); i++)
    {
        //System.out.println(mob.size());
       //System.out.println(leader + ", " + findLoser(matchHistory.get(i)));
        stitch(mob, findPlayersInMob(findLoser(matchHistory.get(i))));
    }
  } 
    return mob; 
}
  public static boolean isBoss(String UID) {
   ArrayList<Integer> matchNumbers = DataBase.getData(UID);
    
    if(matchNumbers.size() == 0)
    {
      return true;
    }

    int lastMatchNumber = matchNumbers.get(matchNumbers.size() - 1);
    String match = DataBase.readMatch(lastMatchNumber);
    int i=0;
    while (match.charAt(i) != ','){
      i++;
    }
    String winner = match.substring(0,i);
  
   return winner.equals(UID);
    
    
  }

  //Combines 2 ArrayLists
  public static void stitch(ArrayList<String> mob1, ArrayList<String> mob2) {
    for (int i = 0; i < mob2.size(); i++) {
      mob1.add(mob2.get(i));
    }
  }
//Takes in a match number and finds loser of the match
  public static String findLoser(int matchNumber) {
    String match = DataBase.readMatch(matchNumber);
    int commaIndex = match.indexOf(',');
    int lastCommaIndex = match.lastIndexOf(',');
    String sub = match.substring(commaIndex + 1, lastCommaIndex);

    return sub;
  }

  public static ArrayList<String> getMobSizes(ArrayList<String> users) {
    ArrayList<String> sizes = new ArrayList<String>();
    for(String u: users)
    {
      ArrayList<Integer> matchHistory = DataBase.getData(u);
      if(matchHistory.size() == 0)
      {
        sizes.add("1:" + u);
      //  Log.add("1:"+u);
      }
      else if(isBoss(u))
      {
        ArrayList<String> userMob = findPlayersInMob(u);
        sizes.add(userMob.size() + ":" + u);
       // Log.add(userMob.size()+ ":" +u);
      }
    }
    return sizes;
  }

}
