import java.time.*;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/*
  interface timedFunction
  {
    public void theFunction();
  }
*/

public class RPSClock {

  
  /*
  public void setSomeEventListener (SomeEventListener listener) {
    this.listener = listener;
  }
  */
  private boolean isOn = true;
  private RPSHandler handler = new RPSHandler();
//An Array of all the events across a week
  private RPSEventDay[] week = new RPSEventDay[7];
  
  private class listener extends TimerTask
    {
      public void run()
      {
        if (isOn && !isGameOn()) {
          // this should trigger the eventHandler
          handler.theFunction();
          System.out.println("Game time just ended!");
        }
        isOn = isGameOn();       
      }
    }

  private String timeZone = "America/Vancouver";

  public RPSClock() {
    for(int i=0;i<7;i++){
        week[i] = new RPSEventDay();
    }
    long delay = 100;
    long period = 1000;
    /*
    Timer timer = new Timer();
    timer.schedule(new listener(), delay, period);
  */
  }


  
  public void addEvent(LocalTime startTime , LocalTime endTime , RPSEvent.DayOfWeek day, String name, String description) {   
    RPSEvent event = new RPSEvent(startTime, endTime, name, description);
    week[day.ordinal()].add(event);
  }


  public void addDailyEvent(LocalTime startTime , LocalTime endTime , String name, String description) {   
    RPSEvent event = new RPSEvent(startTime, endTime, name, description);
    for(int i=0;i<7;++i) {
      week[i].add(event);
    }
  }

  public void addWeekdayEvent(LocalTime startTime , LocalTime endTime , String name, String description) {   
    RPSEvent event = new RPSEvent(startTime, endTime, name, description);
      week[RPSEvent.DayOfWeek.MONDAY.ordinal()].add(event);
      week[RPSEvent.DayOfWeek.TUESDAY.ordinal()].add(event);
      week[RPSEvent.DayOfWeek.WEDNESDAY.ordinal()].add(event);
      week[RPSEvent.DayOfWeek.THURSDAY.ordinal()].add(event);
      week[RPSEvent.DayOfWeek.FRIDAY.ordinal()].add(event);
  }
  
  public void addWeekendEvent(LocalTime startTime , LocalTime endTime , String name, String description) {   
    RPSEvent event = new RPSEvent(startTime, endTime, name, description);
      week[RPSEvent.DayOfWeek.SATURDAY.ordinal()].add(event);
      week[RPSEvent.DayOfWeek.SUNDAY.ordinal()].add(event);    
  }

  public boolean isGameOn() {
    return true;
    /*
    int day = LocalDate.now(ZoneId.of(timeZone)).getDayOfWeek().ordinal();
    LocalTime time = getLocalTime();
    return week[day].isDuringEvent(time); */
  }

  public LocalTime getLocalTime() {
    LocalTime time = LocalTime.now(ZoneId.of(timeZone));
    return time;
  }

  public int getLocalDay() {
    int day = LocalDate.now(ZoneId.of(timeZone)).getDayOfWeek().ordinal();
    return day;
  }
  
  public String toString() {
    String str = "";
    for (RPSEvent.DayOfWeek day : RPSEvent.DayOfWeek.values()) { 
      str += day + " \n";
      str += week[day.ordinal()].toString();
    }
    return str;
  }
  
}


 