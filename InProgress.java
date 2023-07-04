import java.util.*;

public class InProgress {
  private String player1;
  private String player2;
  private String choice1;
  private String choice2;
  private boolean completed;
  
  public InProgress(String c, String t) {
    player1 = c;
    player2 = t;
    choice1 = "";
    choice2 = "";
    completed = false;
    }
  
  public String getPlayer1() {
    return player1;
  }
  
  public String getPlayer2() {
    return player2;
  }
  
  public String getChoice1() {
    return choice1;
  }
  
  public String getChoice2() {
    return choice2;
  }

  public boolean getCompleted() {
    return completed;
  }

  public String setCompleted(boolean c) {
    completed = c;
    return "success";
  }

  public String setChoice1(String r) {
    choice1 = r;
    return "success";
  }

  public String setChoice2(String r) {
    choice2 = r;
    return "success";
  }
  
  public String toString() {
    return getPlayer1() + ", " + getPlayer2() + ", " + getChoice1() + ", " + getChoice2() + "/" + getCompleted();
  }
}
  

  /*
   * battle class
   * public void vote(); VERSION TWO FEATURE, IGNORE FOR NOW
   * //input UserID
   * //output double mutiplier
   * 
   * vote * b.getVoteMutiplier = vote;
   */

  /*
   * battle outputs:
   * boolean isMobLeader
   * boolean voteRight;
   * boolean victory;
   */

