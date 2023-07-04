import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import java.io.OutputStream;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class TwilioSMS {
  private static final Logger logger = LogManager.getLogger();

  private static final String ACCOUNT_SID = "ACab044c9c91f07e2d62db93df75281ce8";
	private static final String AUTH_TOKEN = "0a46564421ff0bc9618d1da82d17167b";
	private static final String ACCOUNT_NUMBER = "+17787439111";

  private static final long readyTimestamp = Instant.now().getEpochSecond();

  private static String userlist = "";
  private static long userlistGenTime = 0;

  public TwilioSMS() throws Exception {
    
    // authenticate the Twilio Client
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    genUserlist();

    // create and HTTP Service to listen for Twilio sms messages
    HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
    server.createContext("/sms", new MyHandler());
    server.createContext("/web", new HttpHandler() {
      @Override
      public void handle(HttpExchange t) throws IOException {
        logger.info("Web interface accessed.");
        if (Instant.now().getEpochSecond() - userlistGenTime > 15) {
          genUserlist();
        }
        String response = "<!DOCTYPE html><html><head><title>Rochambeau Info</title></head><body>" + getUserlist() + "</body></html>";
        Headers headers = t.getResponseHeaders();
        headers.add("content-type", "text/html; charset=UTF-8");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
      }
    });
    server.createContext("/ping", new HttpHandler() {
      @Override
      public void handle(HttpExchange t) throws IOException {
        String response = String.format("{\"since\":%d,\"now\":%d}", readyTimestamp, Instant.now().getEpochSecond());
        Headers headers = t.getResponseHeaders();
        headers.add("content-type", "application/json");
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
      }
    });
    server.setExecutor(null); // creates a default executor
    server.start();
  }

  public static void genUserlist() {
    System.out.println("Regenerating user list for leaderboard");
    userlist = "";
    for (var u : User.getAllUsers().entrySet()) {
      if (u.getValue().getStatus() == User.Status.Incomplete) continue;
      userlist += u.getValue().getUsername()+" [" + u.getValue().getPoints() +"]<br>";
    }
    userlistGenTime = Instant.now().getEpochSecond();
  }

  public static String getUserlist() {
    return userlist;
  }
  
  public static String send(String phone, String msg) {   
    // method to Send an sms to any number 
    Message sms = Message.creator(new PhoneNumber(phone),
        new PhoneNumber(ACCOUNT_NUMBER), msg).create();
    return sms.getSid();
  }
  
}
 