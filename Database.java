import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Database {
  private static final Logger logger = LogManager.getLogger();
  private static Connection connection;
  private static ScriptRunner runner;
  public static Connection connect() {
    Connection conn = null;
      
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:data.db");
      connection = conn;
      runner = new ScriptRunner(conn, false, true);
    } catch (Exception e) {
      logger.error(String.format("Fatal error attempting to connect to database: %s", e.getMessage()));
      System.exit(1);
    }
    return conn;
  }

  public static Connection getConnection() {
    return connection;
  }

  public static void executeSQLFromFile(String path) {
    try {
      runner.runScript(new BufferedReader(new FileReader(path)));
    } catch (Exception e) {
      logger.error(String.format("Fatal error attempting to execute query from file `path`: %s", path, e.getMessage()));
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static int executeUpdate(String query) {
    try (Statement statement = connection.createStatement()) {
      int rowsAffected = statement.executeUpdate(query);
      return rowsAffected;
    } catch (SQLException e) {
      logger.error(String.format("Error attempting to execute update query `%s`: %s", query, e.getMessage()));
      e.printStackTrace();
    }
    return 0;
  }

  public static void restoreUsers() {
    String query = "SELECT id, username, phone, status, perms, points FROM users";
    try (Statement statement = connection.createStatement()) {
      ResultSet result = statement.executeQuery(query);
      while (result.next()) {
        User user = new User(
          UUID.fromString(result.getString("id")),
          result.getString("username"),
          result.getString("phone"),
          result.getInt("status"),
          result.getInt("perms"),
          result.getInt("points")
        );
      }
    } catch (SQLException e) {
      logger.error(String.format("Error attempting to restore users: %s", e.getMessage()));
      e.printStackTrace();
    }
  }

  public static void executeMigrations() {
    int userVersion = 0;
    try (Statement statement = connection.createStatement()) {
      ResultSet result = statement.executeQuery("PRAGMA user_version;");
      userVersion = result.getInt(1);
    } catch (SQLException e) {
      logger.error(String.format("Error attempting to retrieve user_version pragma: %s", e.getMessage()));
      e.printStackTrace();
      System.exit(1);
    }
    if (userVersion < 1) {
      try (Statement statement = connection.createStatement()) {
        statement.execute("ALTER TABLE users ADD COLUMN perms INTEGER DEFAULT 0;");
        statement.execute("PRAGMA user_version = 1;");
      } catch (SQLException e) {
        logger.error(String.format("Error attempting to add `perms` column to `users`: %s", e.getMessage()));
        e.printStackTrace();
        System.exit(1);
      }
    }
    if (userVersion < 2) {
      try (Statement statement = connection.createStatement()) {
        statement.execute("ALTER TABLE users ADD COLUMN points INTEGER DEFAULT 0;");
        statement.execute("PRAGMA user_version = 2;");
      } catch (SQLException e) {
        logger.error(String.format("Error attempting to add `points` column to `users`: %s", e.getMessage()));
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}