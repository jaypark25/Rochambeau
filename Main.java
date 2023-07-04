import java.sql.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
  private static final Logger logger = LogManager.getLogger();
  //public Battle cho;
  
  public static void main(String[] args) throws Exception {
    
    // Create a TwilioSMS instance to begin an HTTP service and Authenticate Client
    Connection connection = Database.connect();
    if (connection != null) {
      Log.add("Database connection established");
      logger.info("Database connection established");
      System.out.println("Database connection established");
    }
    Database.executeSQLFromFile("sql/init.sql");
    Database.executeMigrations();
    Database.restoreUsers();

    // add a first user for debugging:
    /* User first = new User("+17787935244");
    System.out.println(first);
    first.setUsername("7787935244");
    first.setStatus(User.Status.Active);
    System.out.println(first); */
    
    TwilioSMS service = new TwilioSMS();
    
    // Send a test Message
    //System.out.println(service.send("+12368821283", "HTTP Service is Running!")); // 2506613358

    //System.out.println(service.send("+12369798029", "HTTP Service is Running!"));

    // System.out.println(command("/username-","username"));
    // System.out.println(command("username-","username"));
    
    
  }

}