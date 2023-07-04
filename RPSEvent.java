import java.time.LocalTime;
 
class RPSEvent {
  
  public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY    
  }
  
  private LocalTime startTime;
  private LocalTime endTime;
  private DayOfWeek day;
  private String title;
  private String description;
  
  public RPSEvent(LocalTime startTime, LocalTime endTime, String title, String description) {
    if (endTime.compareTo(startTime) < 0) {
        throw new IllegalArgumentException("endTime must be after startTime"); 
      }
    this.startTime = startTime;
    this.endTime = endTime;
    this.title = title;
    this.description = description;
  }
  
  public String toString () {
      String str = "";
      str += this.getStartTime().toString();
      str += "|";
      str += this.getEndTime().toString();
      str += "|";
      str += this.getTitle();
      str += "|";
      str += this.getDescription();
      return str;
  }
  
  public LocalTime getStartTime() {
    return startTime;
  }
  
  public LocalTime getEndTime() {
    return endTime;
  }
  
  public DayOfWeek getDay() {
    return day;
  }
  
  public String getTitle() {
    return title;
  }
  public String getDescription() {
    return description;
  }
}
 