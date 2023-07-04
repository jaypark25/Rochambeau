import java.util.ArrayList;
import java.time.LocalTime;

class RPSEventDay {

  //An ArrayList of all Rochambeau events in a day
  private ArrayList <RPSEvent> today = new ArrayList<RPSEvent>();
  
  public void add(RPSEvent event) {
    today.add(event);
  }

  public boolean isDuringEvent(LocalTime time) {
    //Checks each event in the ArrayList to see if the time is between the start time or end time of any of the events 
    for(RPSEvent e : today) {
      if (e.getStartTime().compareTo(time) < 0) {
        if (e.getEndTime().compareTo(time) > 0) {
          return true;
        }  
      }
    }
    return false;
    
  }
  
  public String toString() {
    String str = "";
    for(RPSEvent t:today) {
      str += t.toString() + "\n";
    }
    return str;
  }
}
 