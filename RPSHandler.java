import java.util.*;
  
public class RPSHandler implements timedFunction {

  //Code that should trigger when game time ends
  public void theFunction() {
    ArrayList<String> battles = MyHandler.cho.getReturnQueue();
    for(int i = 0; i < battles.size(); i++){
      String winLeader = DataBase.getWinner(battles.get(i));
      String loseLeader = DataBase.getLoser(battles.get(i));
      
      ArrayList<String> winMob = LedgerAnalyzer.findPlayersInMob(winLeader);
      ArrayList<String> loseMob = LedgerAnalyzer.findPlayersInMob(loseLeader);

      
      for (String w : winMob) {
        TwilioSMS.send(w, "Your mob won the battle!");
      }

      for (String l : loseMob) {
        TwilioSMS.send(l, "Your mob lost the battle!");
      }

      DataBase.addMatches(battles);
    }
  }
}