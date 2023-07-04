import java.time.LocalDateTime; //Import LocalDateTime to get the time
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;
import java.io.FileNotFoundException;  // Import this class to handle errors

public class Log
{

  public static void add(String str)
  {
    LocalDateTime present = LocalDateTime.now();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss");  
    String time = present.format(format);

    String addition = time + "; " + str;

    try {
      FileWriter myWriter = new FileWriter("log.txt", true);
      myWriter.write(addition + "\n");
      myWriter.close();
      // System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred trying to write to log.");
      e.printStackTrace();
    }
  }
}