import java.util.*;

public class BattleRequest {
  String initiatorID;
  String targetID;
  boolean engaged;
 
  public BattleRequest(String c, String t) {
    initiatorID = c;
    targetID = t;
    engaged = false;
    }

  public boolean matchIDs(BattleRequest other) {
    if (initiatorID.equals(other.getInitiatorID())&&(targetID.equals(other.getTargetID()))){
      return true;
    }
    else {
      return false;
    }
  }

  public boolean getEngaged() {
    return engaged;
  }

  public void setEngaged(boolean e) {
    engaged = e;
  }

  public String toString() {
    return initiatorID + "," + targetID + "/" + engaged;
  }

  public String getInitiatorID() {
    return initiatorID;
  }

  public String getTargetID() {
    return targetID;
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
