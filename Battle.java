import java.util.ArrayList;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Battle {
  /*
   * - A dataset of objects that handles battle request pool
   * - Should be designed to be very changable
   * - Input: STORES UserID/Phone number, target, input time
   */

  private static final Logger logger = LogManager.getLogger();

  private ArrayList<BattleRequest> requestQueue = new ArrayList<BattleRequest>(); 
  private ArrayList<InProgress> ongoing = new ArrayList<InProgress>();
  private ArrayList<String> returnQueue = new ArrayList<String>();


public Battle() {
  restoreRequestQ();
  restoreOngoingQ();
}

  
/**
  *method that adds a request at the end of the requestQueue arraylist. Outputs a String of status.
  ewturns:
  requestComplete = successful addition to the queue
  error: repetition = the code has found another request with the same initiator AND target, indicating that this user has submitted against this target already
  error: engaged = the code has found another match already ongoing with one of the UUID involved. 
  match accepted = the code has found another request with opposite UUIDs, meaning that there has already been a request towards the initiator by the target, and will therefore accept the request and proceed into the battle procedure. Returns "match accepted with " + [target's UUID].
*/
  public String addRequest(String c, String t) {
    BattleRequest temp = new BattleRequest(c,t);
//checking the following conditions against every item in que
    for (int i = 0; i < requestQueue.size(); i++) {
      BattleRequest r = requestQueue.get(i);
      //r: random 
      //t: temp
      //ini: initial 
      //tar: target
      String rini = r.getInitiatorID();
      String rtar = r.getTargetID();
      String tini = temp.getInitiatorID();
      String ttar = temp.getTargetID();
      
      // checking for repetitive requests
      if (temp.matchIDs(r) && !r.getEngaged()) {
        return "error: repetition";
      }
      // checking for already engaged users
      if (r.getEngaged()) {
        if (rini.equals(tini) || rini.equals(ttar)) {
          logger.info(rini, "is currently engaged in a battle"); 
          return "error: userengaged";
        }
        if (rtar.equals(tini) || rtar.equals(ttar)) {
          logger.info(rtar, "is currently engaged in a battle");
          return "error: opponentengaged";
        }
      }
      
      // checking for acceptable matches
      if (tini.equals(rtar) && ttar.equals(rini)) {
        temp.setEngaged(true);
        requestQueue.get(i).setEngaged(true);
        requestQueue.add(temp);
        ongoing.add(new InProgress(c,t));
        backupOngoingQ();
        backupRequestQ();
        return "match accepted with [" + rtar + "]";
      }
    }
    requestQueue.add(temp);
    backupRequestQ();
    return "request complete";
  }
/*
  public String logBattle(String Q, String content) {
    try {
    FileWriter out = new FileWriter(Q, true);
      out.write(content);
      out.newLine();
      out.close();    
      return content;
    } catch (IOException e) {
      return "Error: A log error occurred.";
    }
    
  }

  */

  
  public void reject(String c, String t) {
    BattleRequest temp = new BattleRequest(c,t);
    BattleRequest temp2 = new BattleRequest(t,c);
    // if battle exists with both users. remove it.
    for (int i = 0; i < requestQueue.size(); i++) {
        BattleRequest r = requestQueue.get(i);
        if((temp.matchIDs(r)) || temp2.matchIDs(r)) {
          requestQueue.remove(i);
        }
      }
    backupRequestQ();
  }

  public static void clearBattleQs() {
  
  }

  
public void backupRequestQ()
  // will write the requestQueue to a file
{
      String Q = "battleRQ.txt";
      File battleRQ = new File(Q);
      try { battleRQ.createNewFile();
           
      } catch (IOException e) {
      System.out.println("An error occurred creating the Battle Queue.");
      e.printStackTrace();
      }
      try {
      FileWriter brq = new FileWriter(Q);
      for(BattleRequest entry: requestQueue) {
          brq.write(entry.getInitiatorID()+","+entry.getTargetID()+","+entry.getEngaged()+"\n");
        System.out.println(entry.getInitiatorID()+","+entry.getTargetID()+","+entry.getEngaged()+"\n");
      }
        brq.close();
      } catch (IOException e) {
      System.out.println("An error occurred writing to the Battle Queue.");
      e.printStackTrace();
      }

}
   
public void restoreRequestQ()
    // reads the requestQueue from a backup file
{ 
      String line = "";  
      String splitBy = ",";  
      try   
      {  
       String Q = "battleRQ.txt";
      BufferedReader br = new BufferedReader(new FileReader(Q));  
      while ((line = br.readLine()) != null)   //returns a Boolean value  
      {  
        String[] record = line.split(",");    // use comma as separator  
        BattleRequest temp = new BattleRequest(record[0],record[1]);
        temp.setEngaged(record[2].equals("true"));
        requestQueue.add(temp);
      }  
      }   
      catch (IOException e)   
      {  
      e.printStackTrace();  
      }  
}  




public void backupOngoingQ()
{
  
      String Q = "battleOQ.txt";
      File battleOQ = new File(Q);
      try { battleOQ.createNewFile();
      } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
      }
      try {
      FileWriter orq = new FileWriter(Q);
      for(InProgress entry: ongoing) { orq.write(entry.getPlayer1()+","+entry.getPlayer2()+","+entry.getChoice1()+","+entry.getChoice2()+","+entry.getCompleted()+"\n");
      }
      orq.close();
      } catch (IOException e) {
      System.out.println("An error occurred writing to the Battle Queue.");
      e.printStackTrace();
      }  
}
  
public void restoreOngoingQ()
    // reads the requestQueue from a backup file
{ 
      String line = "";  
      String splitBy = ",";  
      try   
      {  
       String Q = "battleOQ.txt";
      BufferedReader br = new BufferedReader(new FileReader(Q));  
      while ((line = br.readLine()) != null)   //returns a Boolean value  
      {  
        String[] record = line.split(",");    // use comma as separator  
        InProgress temp = new InProgress(record[0],record[1]);
        temp.setChoice1(record[2]);
        temp.setChoice2(record[3]);
        temp.setCompleted(record[4].equals("true"));
        ongoing.add(temp);
      }  
      }   
      catch (IOException e)   
      {  
      e.printStackTrace();  
      }  
}  


  
  
//takes UserID of a user, tries to find the opponent and return their ID as a String
//if user is not engaged, return null
  public String userTargetID(String id) {
    int i = searchIniID(id);
    if (i < 0 || !requestQueue.get(i).getEngaged()) {
      return null;
    }
    return requestQueue.get(i).getTargetID();
  }
  
  public int searchIniID(String id) {
    for (int i = 0; i < requestQueue.size(); i++) {
      if (id.equals(requestQueue.get(i).getInitiatorID())) {
        return i;
      }
    }
    return -1;
  }

  public int getRequestTotal() {
    return requestQueue.size();
  }
  
  public String getRequest(int i) {
    return (requestQueue.get(i)).toString();
  }

  public int removeAllRequest(String UUID) {
    int count = 0;
    int i = searchIniID(UUID);
    if (i > 0) {
      requestQueue.remove(i);
      count++;
      removeAllRequest(UUID);
    }
    backupRequestQ();
    return count;
  }

  public String getAllQueue() {
    String output = "--------------------------- \nrequestQueue\n";
    for (BattleRequest r : requestQueue) {
      output += r.toString() + "\n";
    }
    output += "--------------------------- \nongoing\n";
    for (InProgress n : ongoing) {
      output += n.toString() + "\n";
    }
    output += "--------------------------- \nreturnQueue\n";
    for (String a : returnQueue) {
      output += a.toString() + "\n";
    }
    output += "---------------------------";
    return output;
  }

  public void printAllQueue() {
    System.out.println(getAllQueue());
  }
  
  public int scanOngoing(String ID1) {
    for (int i = 0; i < ongoing.size(); i++) {
      String ID2 = (ongoing.get(i)).getPlayer1();
      if (ID1.equals(ID2)) {
        return i;
      }
      String ID3 = (ongoing.get(i)).getPlayer2();
      if (ID1.equals(ID3)) {
        return i;
      }
    }
    return -1;
  }

  /**
    addChoice(String ID, String choice)
    method that modifies the choice under the user ID.
    outputs a String of status.
PRECONDITIONS: 
    choice MUST be in one of the three exact strings of "rock", "paper" or "scissor".

POSTCONDITIONS:
      error: choice unclear = if choice String isn't one of the three accepted values. 
      error: no match found = if input ID isn't found in the ongoing matches arraylist, indicating said user had never started a match in the current session. 
      error: completed match = if the choice was being added after the match has already been completed with both having selected a non-tieing option. 
      error: tie [String UserID][String otherUserID] = if the new choice is the same as the opponent's choice. Clears both player's choices.
      error: duplication = if the same player has already made a choice before. 
      waiting for other user = if no error was found, but the other user hasn't locked in an option yet. Sets inputed choice.
      match completed [String UserID][String otherUserID]: if no error was found, but both players has now input their choices and match is thus completed. Both choices are set and locked in.
  */
  public String addChoice(String ID, String choice) {
    if (!(choice.equals("rock") || choice.equals("paper") || choice.equals("scissors"))) {
      return "error: choice unclear";
    }
    int index = scanOngoing(ID);
    if (index < 0) {
      return "error: no match found";
    }
    InProgress temp = ongoing.get(index);
    // if completed, return error
    if (temp.getCompleted()) {
      return "error: completed match";
    }
    // checking for duplication
    if (ID.equals(temp.getPlayer1())) {
      if (!(temp.getChoice1().equals(""))) {
        return "error: duplication";
      }
      temp.setChoice1(choice);
    }
    else if (ID.equals(temp.getPlayer2())) {
      if (!(temp.getChoice2().equals(""))) {
        return "error: duplication";
      }
      temp.setChoice2(choice);
    }
    //checking for ties
    if (!(temp.getChoice1().equals("")) && temp.getChoice1().equals(temp.getChoice2())) {
      temp.setChoice1("");
      temp.setChoice2("");
      //temp.setEngaged(false);
      return "error: tie [" + temp.getPlayer1() + "][" + temp.getPlayer2() + "]";
    }
//checking for empty choice (which means incomplete match)
    if (temp.getChoice1().equals("")||temp.getChoice2().equals("")) {
      ongoing.set(index,temp);
      backupOngoingQ();
      return "waiting for other user";
    }
    else {
      String c1 = temp.getChoice1(); 
      String c2 = temp.getChoice2(); 
      String w;
      String l;
      String c; 
      if ((c1.equals("rock") && c2.equals("scissors")) || (c1.equals("scissors") && c2.equals("paper")) || (c1.equals("paper") && c2.equals("rock"))) {
        w = temp.getPlayer1();
        l = temp.getPlayer2();
        c = c1;
      } 
      else {
        w = temp.getPlayer2();        
        l = temp.getPlayer1();
        c = c2;
      }
      //ongoing.get(index).setCompleted(true);
      // debugging only:
      ongoing.remove(index);
      backupOngoingQ();
      returnQueue.add(w + "," + l + "," + c);
      return String.format("match completed with [%s][%s]", w, l);
    }
  }
  
  public ArrayList<String> getReturnQueue() {
    ArrayList<String> temp = new ArrayList<String>();
    for (String r : returnQueue) {
      temp.add(r);
    }
    // put these back in after clock gets added:
    // requestQueue.clear();
    // ongoing.clear();
    returnQueue.clear();
    return temp;    
  }
}
