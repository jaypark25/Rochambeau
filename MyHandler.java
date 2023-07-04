import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.twilio.rest.api.v2010.account.IncomingPhoneNumber;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileNotFoundException; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// This overrides the Java HTTPHandler to Listen for an HTTP Request and produce a different response
public class MyHandler implements HttpHandler {

  private static final Logger logger = LogManager.getLogger();
  
  User player;
  RPSClock clock = new RPSClock();
  
  public static Battle cho = new Battle();

  public static final Comparator<String> LEADERBOARD_COMPARATOR
    = new Comparator<String>()
    {
      @Override
      public int compare(final String o1, final String o2)
      {
        return Integer.parseInt(o2.substring(0, o2.indexOf(":"))) - Integer.parseInt(o1.substring(0, o1.indexOf(":")));
      }
    };

  @Override 
  public void handle(HttpExchange t) throws IOException {
    InputStream stream = t.getRequestBody();
    String str_full = new BufferedReader(
      new InputStreamReader(stream, "UTF-8"))
      .lines().collect(Collectors.joining("\n"));
  
  // extract the Body of the HTTPRequest from Twilio to String
    int str_start = str_full.indexOf("&Body")+6;
    String str = str_full.substring(str_start);
    int str_end = str.indexOf("&");
    String incoming_message = URLDecoder.decode(str.substring(0,str_end), "UTF-8"); 
  //  Log.add(str_full);
  // extract the incoming phone# of the HTTPRequest from Twilio to String
    str_start = str_full.indexOf("&From=%2B")+9;
    str = str_full.substring(str_start);
    str_end = str.indexOf("&");
    String incoming_phone = "+" + str.substring(0,str_end);

    str_start = str_full.indexOf("&To=%2B")+7;
    str = str_full.substring(str_start);
    str_end = str.indexOf("&");
    String who_did_they_text = str.substring(0,str_end);

    if(who_did_they_text.equals("12362372929")) {
       TwilioSMS.send(player.getPhoneNumber(), "D'Oh! The cell phone providers have done it again! Back to the first number!?");
    }
  
    if (User.getUserByPhoneNumber(incoming_phone) == null) {
      player = new User(incoming_phone);
    } 
    else {
      player = User.getUserByPhoneNumber(incoming_phone);
    }
    // debugging log to console    

    System.out.println("Message from " + player.getUsername());
    TwilioSMS.send(player.getPhoneNumber(), getReply(incoming_message.trim()));
    /*
    String response = "<?xml version='1.0' encoding='UTF-8' ?><Response><Sms>" + getReply(incoming_message.trim()) + "</Sms></Response>";
    */
    /*  This is the way to do it properly... no more Hlannon hacks

import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;

import static spark.Spark.*;

public class SmsApp {
    public static void main(String[] args) {
        get("/", (req, res) -> "Hello Web");

        post("/sms", (req, res) -> {
            res.type("application/xml");
            Body body = new Body
                    .Builder("The Robots are coming! Head for the hills!")
                    .build();
            Message sms = new Message
                    .Builder()
                    .body(body)
                    .build();
            MessagingResponse twiml = new MessagingResponse
                    .Builder()
                    .message(sms)
                    .build();
            return twiml.toXml();
        });
    }
}

*/
    /*
    t.sendResponseHeaders(200, response.length());
    OutputStream os = t.getResponseBody();
    os.write(response.getBytes());
    os.close();
    */
  }

  public boolean commandRegex(String msg, String cmdName) {
    Pattern pattern = Pattern.compile(String.format("^/(%s|%s)([ -].+|$)", cmdName, cmdName.substring(0, 1)));
    return pattern.matcher(msg).find();
  }

  /* deprecated
  public boolean command(String msg, String cmdName) {
    return (
      // long command
      (
        (msg.length() == cmdName.length()+1) 
        && (msg.substring(0, cmdName.length()+1).equals("/"+ cmdName))
      ) 
      || 
      // long command with arguments
      ((msg.length() > cmdName.length()+1) 
        && (
          msg.substring(0,cmdName.length()+2).equals("/"+ cmdName.length() + "-"))) 
      ||      
      // short command with arguments
      (
        (msg.length() > 2) 
        && (msg.substring(0,3).equals("/"+ cmdName.substring(0,1) + "-"))
      ) 
      || 
      // short command
      (
        (msg.length() == 2) 
        && (msg.substring(0,2).equals("/"+ cmdName.substring(0,1)))
      )
    );
  }
  */
  
  public String getReply(String msg) {
    Log.add("Got message \"" + msg + "\" from " + player.getUsername());
    logger.debug(String.format("%s sent message %s", player.getUsername(), msg));
    if (player.getStatus() == User.Status.Banned) {
      return "You have been banned from Rochambeau.";
    }
    
    if (commandRegex(msg, "username")) {
      Log.add(player.getUsername()+"|u|"+getArgument(msg));
      //set username method

      String username = stripSpaces(getArgument(msg));

      if (username == "") {
        return "Sorry, you can't set your username as blank.";
      } 
      if (username.equals(player.getPhoneNumber())) {
        return "Sorry, you can't set your username as your phone number, try another one.";
      }
      boolean success = player.setUsername(username);
      if (success) {
        player.setStatus(User.Status.Active);
        return "Success! Your new username is " + username + "\nTo see a menu of commands please reply with /menu";
      }

      return "Sorry, the username is already taken, try another one.";
    } 
    
    if (player.getStatus() == User.Status.Incomplete) {
       return """        
         Hello! You are not set up as a user in the Rochambeau system yet. Please set a username. 
         
         Type the command:\n \"/username {your name}\" or \"/u {your name}\" (but without the curly brackets). 
         
         Please use a letter in the first character and don't include any spaces or special characters in your username. ie (only characters in a-z|A-Z|0-9). There shoud be a single space between the command /username and your actual username.
         """;  
    }

    //check status: if exists then can do commands
    if (player.getStatus() == User.Status.Active) {
      Log.add(player.getUsername() + " is active");
      if (commandRegex(msg, "battle")) {
        Log.add(player.getUsername()+"|battle|"+getArgument(msg));        
        String opponentName = getArgument(msg);
        if (!LedgerAnalyzer.isBoss(player.getID())) {
          return "I am sorry. You were defeated in battle and now just a loyal mob member. You can no longer battle anyone. Encourage your mob boss: " + User.getUserByID(LedgerAnalyzer.findMobOfPlayer(player.getID())).getUsername() + " to pick their battles wisely and improve your rank! Message them with the command /mobchat {your message}";
        }
        
        if (opponentName == "") {
          
          return "Invalid command. Please enter a username";
        } 

        if (User.getUserByUsername(opponentName) == null) {
          return "Sorry, this username does not exist. Please try again.";
        }

        if (!LedgerAnalyzer.isBoss(User.getUserByUsername(opponentName).getID())) {
          User mobBoss = User.getUserByID(LedgerAnalyzer.findMobOfPlayer(User.getUserByUsername(opponentName).getID()));
          return "I am sorry. But that opponent is no longer a mob boss so they cannot battle with you. If you win a battle with that player's boss: " + mobBoss.getUsername() + "[" + mobBoss.getPoints()  + "], you might win and make them join your mob!";
        }
        
        if(!clock.isGameOn()) {
          return "Rochambeau is not currently running. The game will continue at"; 
          // add this feature!
        } 
  
        User opponent = User.getUserByUsername(opponentName);
        String result = cho.addRequest(player.getID(),opponent.getID());
        
        if (result == "request complete") {
          TwilioSMS.send(opponent.getPhoneNumber(), player.getUsername() + " [" + player.getPoints() + "] has requested to battle you! \nPlease respond with /accept " + player.getUsername() + " or /reject " + player.getUsername());
          Log.add(cho.getAllQueue());
          return "This battle request is being processed...";
        } 
        
        if (result == "error: repetition") {   
          return "You have already initiated a battle with this player. We are waiting for them to accept your request";
        }

        if (result == "error: userengaged") {   
          return "You are currently locked in another battle. Once that game resolves you can battle another player";
        }
        if (result == "error: opponentengaged") {
          return "Your opponent is currently locked in another battle. Once that game resolves you can battle them";
        }
        return "There was an error processing this request. Please try again.";         
      }
    
      if (commandRegex(msg, "leaderboard")) {
        boolean allFlag = (getArgument(msg).equals("all") || getArgument(msg).equals("a"));
       
        Log.add(player.getUsername()+"|leaderboard|");                
        String userList = "Top Mobs\n\n";
        userList += "mob size : player [lirops] \n";
        userList += "-----------------------------\n";
        ArrayList<String> users = new ArrayList<String>();
        for (var u : User.getAllUsers().entrySet()) {
          users.add(u.getKey().toString());
        }  
        ArrayList<String> sizesAndUsers = LedgerAnalyzer.getMobSizes(users);
        Collections.sort(sizesAndUsers, LEADERBOARD_COMPARATOR);  
        int i = 0;
        for (String sizeAndUser : sizesAndUsers) {
          if ((!allFlag) && (i >= 15)) return userList;
          User leader = User.getUserByID(sizeAndUser.substring(sizeAndUser.indexOf(":")+1));
          if (leader.getStatus() == User.Status.Active) {
            i++;
            userList += sizeAndUser.substring(0,sizeAndUser.indexOf(":")) + " : " +  leader.getUsername() + " [" + leader.getPoints() + "]\n";
          }
        }
        //User[] List = User.getAllUsers();
        return userList;
      }
    
      if (commandRegex(msg, "rank")) {
        Log.add(player.getUsername()+"|rank|");          
        return "Your rank within your mob is... undefined \n(work in progress) ";
      }


      if (commandRegex(msg, "whine")) {
      Log.add(player.getUsername()+"|whine|"+getArgument(msg));
      //set username method

      String whining = stripSpaces(getArgument(msg));

      if (whining == "") return "";
      
      // send message to all admin  
      for (var entry : User.getAllUsers().entrySet()) {
            User user = User.getUserByID(entry.getKey().toString());
            if (user.isAdmin()) {
              String phone = user.getPhoneNumber();
              try {
                TwilioSMS.send(phone, player.getUsername() + " is whining: " + getArgument(msg));
              } catch (Exception e) {
               // User.getUserByID(u.getKey().toString()).deleteUser();
                System.out.println(e);
              }
            }
          }
        return player.isAdmin() ? "" : "Your whining is valuable to us. Don't forget: We are players too. We will remember your insolence on the battlefield.";
     
    }

      
      
      if (commandRegex(msg, "DND")) {
        Log.add(player.getUsername()+"|dnd|");       
        player.setStatus(User.Status.DND);
        return "You are now in do not disturb mode. Type in /active to be active again.";
      }
  
      if (commandRegex(msg, "invite")) {
        String invite_num = stripPhoneNum(getArgument(msg));
        Log.add(player.getUsername()+"|invite|"+invite_num);
        //invite user by phone number
        if (User.getUserByPhoneNumber(invite_num) == null) {
          User invitee = new User(invite_num);
          invitee.setUsername(invite_num);
          // invitee.setStatus(User.Status.Active);
          TwilioSMS.send(invite_num, player.getUsername() + " has invited you to ROCHAMBEAU, a fierce mob turf war. Please set a username to play by REPLYing to this text with /username {your name}... but without the curly brackets eg '/username Boss'");
          return "We've sent the invite!";
        } 
        else {
          return "This phone number already exists as a user.";
        }
      }
        
      if (commandRegex(msg, "menu")) {
        Log.add(player.getUsername()+"|menu|");         
        return """        
          Here are your command options: 
           
          /menu or /m 
           --> calls this menu 
           
          /battle or /b {opponentName}
           --> sends battle request
           
          /invite or /i {phoneNumber}
           --> invite your friend to the game 
           
          /leaderboard or /l
           --> returns current mob leaderboard 
           --> use "/l all" to see all the mob leaders

          /mobchat or /mc {message}
           --> talk to your mob boss or moblings    
                     
          /rank or /r
           --> returns your current mob ranking 
              (work in progress)
           
          /username or /u {newName}
           --> change username 

          /whine {message}
           --> send the Rochambeau admins a message. 
              Bug fix? Feature suggestions? Bribes?
          
          /DND 
           -->messages will not be sent to you until you turn if off 
          """ ;

        
      }
      
      if (commandRegex(msg, "accept")) {
        
        
        Log.add(player.getUsername()+"|accept|"+getArgument(msg)); 
        //initiate battle

        if (!LedgerAnalyzer.isBoss(player.getID())) {
          return "Sorry, you are merely a mobling, and cannot accept a battle. You need to ask your mob boss to pick fights for you. Try using: \"/mobchat " + User.getUserByID(LedgerAnalyzer.findMobOfPlayer(player.getID())).getUsername()  + " {message} \"to encourage them";
        }
        
        String opponentName = getArgument(msg);

        if (!clock.isGameOn()) {
          return "Rochambeau is not currently running. The game will continue at"; 
        // add this feature!  
        } 
        
        if (opponentName == "") {
          return "Invalid command. Please enter a username";
        } 
    
        if (User.getUserByUsername(opponentName) == null) {
          return "Sorry, this username does not exist. Please try again.";
        }
        
        User opponent = User.getUserByUsername(opponentName);        
        String result = cho.addRequest(player.getID(),opponent.getID());
        
        if (result.substring(0,5).equals("match")) {
          TwilioSMS.send(opponent.getPhoneNumber(), "Your battle request with " + player.getUsername() + " has been accepted. \n"  +
            """
            Please choose your battle weapon:
             /rock 
             /paper 
             /scissors
  
            OR do some trash talking first:
             /trash {message}
              -->trash talk your opponent  
            """);
          //Log.add(cho.getAllQueue());
          return "Your battle request with " + opponentName + " has been accepted. \n"  +
            """
            Please choose your battle weapon:
             /rock 
             /paper 
             /scissors
  
            OR do some trash talking first:
             /trash {message}
              -->trash talk your opponent  
            """;
        }
        
        // I don't think we do this          
        TwilioSMS.send(opponent.getPhoneNumber(), "Your battle request was unsuccessful.");

        return "Battle acceptance not successful.";
      }
      
      if (commandRegex(msg, "reject")) {
        String opponentName = getArgument(msg);
        if (opponentName=="") {
          return "Invalid command. Please enter a username";
        } 
        User opponent = User.getUserByUsername(opponentName); 
        if (opponent == null) {
          return "Sorry, this username does not exist. Please try again.";
        }
          
        cho.reject(player.getID(),opponent.getID());
        TwilioSMS.send(opponent.getPhoneNumber(), "Your battle request to " + player.getUsername() + " has been rejected.\n\nPro Tip: Try to request battles with mobs of similar size. There isn't much incentive for a bigger mob to battle a much smaller one");
        return "Battle request from " + opponent.getUsername() + " has been rejected.";
      } 

      if (commandRegex(msg, "trash")) {
        String opponentID = cho.userTargetID(player.getID()); 
        if (opponentID == null) {
          return "Sorry, you are not in a battle.";
        }
        String trashTalk = getArgument(msg);
        if (trashTalk=="") {
          return "Please trash talk something useful!";
        }
        User opponent = User.getUserByID(UUID.fromString(opponentID)); 
        Log.add(player.getUsername() + " trashtalked " + opponent.getUsername() + "\""+trashTalk+"\"");
        TwilioSMS.send(opponent.getPhoneNumber(), player.getUsername() + " is trash talking you: \n\""+trashTalk+"\"");
        return "Sent trash talk to " + opponent.getUsername() + ": \n\""+trashTalk+"\"";
      }


      
      if (commandRegex(msg, "mobchat")|| commandRegex(msg, "mc")) {        
        String message = getArgument(msg);
        if (message=="") {
          return "Please enter a message after - ";
        }
        if (LedgerAnalyzer.isBoss(player.getID())) {
          ArrayList<String> moblingList = LedgerAnalyzer.findPlayersInMob(player.getID());
          for (String mobling : moblingList) {
           TwilioSMS.send(User.getUserByID(mobling).getPhoneNumber(),"Your mob boss " + player.getUsername() + " says: \n\""+message+"\"");
          }
          return "Sent message to your moblings: \n\""+message+"\"";
        }
        TwilioSMS.send((User.getUserByID(LedgerAnalyzer.findMobOfPlayer(player.getID()))).getPhoneNumber(),"Your mobling " + player.getUsername() + " says: \n\""+message+"\"");
        return "Sent message to your mob boss: \n\""+message+"\"";
      }


      if (commandRegex(msg, "rock")) {
        Log.add(cho.getAllQueue());
        return matchParse(cho.addChoice(player.getID(), "rock"));
      } 

      if (commandRegex(msg, "paper")) {
        Log.add(cho.getAllQueue());
        return matchParse(cho.addChoice(player.getID(), "paper"));
      }
      
      if (commandRegex(msg, "scissors")) {
        Log.add(cho.getAllQueue());
        return matchParse(cho.addChoice(player.getID(), "scissors"));      
      } 

      // admin only functions: 
      // broadcast messages to all active users
      // cleanup invalid/incomplete user accounts
      
      if (player.isAdmin()) {

        if (commandRegex(msg, "test")) {
          TwilioSMS.send(player.getPhoneNumber(), "testtwilio");
          return "testreturn";
        }
        
        if (commandRegex(msg, "adminmenu")) {
          Log.add(player.getUsername()+"|adminMenu|");         
          return """
            Here are your options: 
            
            /battledump
            /broadcast {content}
            /leaderlist
            /message {playername} {content}
            /setadmin {playername}
            /addpoints {playername} {pts}
            /deductpoints {playername} {pts}
            """ ;
        }

        if (commandRegex(msg, "broadcast")) {
          Log.add("admin|broadcast|"+getArgument(msg)); 
          for (var entry : User.getAllUsers().entrySet()) {
            User user = User.getUserByID(entry.getKey().toString());
            if (user.getStatus() == User.Status.Active) {
              String phone = user.getPhoneNumber();
              try {
                TwilioSMS.send(phone, getArgument(msg));
              } catch (Exception e) {
               // User.getUserByID(u.getKey().toString()).deleteUser();
                System.out.println(e);
              }
            }
          }  
          return "I hope you know what you are doing! You just broadcasted :)";
        }
        
        if (commandRegex(msg, "battledump")) {
          Log.add("admin|battledump|");           
          String dumpString = cho.getAllQueue();
          for (var entry : User.getAllUsers().entrySet()) {
             User user = User.getUserByID(entry.getKey().toString());       
             dumpString = dumpString.replace(user.getID(),user.getUsername());
            }
          return dumpString;
        }  
        
        // unneccessary
        /*        if (commandRegex(msg, "cleanup")) {
          Log.add("admin|cleanup|");           
          for (var entry : User.getAllUsers().entrySet()) {
            User u = User.getUserByID(entry.getKey().toString());
            char ch=u.getUsername().charAt(0);
              if (!Character.isLetter(ch)) {
                  u.deleteUser();          
                  Log.add("Deleted user: " + u.getUsername());
                }
            }
          return "Cleaned";
        }
*/
        /*
       if (commandRegex(msg, "changeuser")) {
          Log.add("admin|deleteuser|");           
          for (var entry : User.getAllUsers().entrySet()) {
            User u = User.getUserByID(entry.getKey().toString());
            Log.add(u.getPhoneNumber());
            if(u.getUsername().equals("{Béatrice}")){
                u.setUsername("Beatrice");  
                return "changed";
            }
        }
       }
      */

        if (commandRegex(msg, "leaderlist")) {

      String leadersFile = "Leaderboard.txt";
      File leaderboard = new File(leadersFile);
      try { leaderboard.createNewFile();
           
      } catch (IOException e) {
      System.out.println("An error occurred creating the Battle Queue.");
     // e.printStackTrace();
      }
      try {
        FileWriter leadersFW = new FileWriter(leadersFile);

        Log.add(player.getUsername()+"|admin|leaderlist|");                
        String userList = "Top Mobs\n\n";
        userList += "mob size : player [lirops] \n";
        userList += "-----------------------------\n";
        ArrayList<String> users = new ArrayList<String>();
        for (var u : User.getAllUsers().entrySet()) {
          users.add(u.getKey().toString());
        }  
        ArrayList<String> sizesAndUsers = LedgerAnalyzer.getMobSizes(users);
        Collections.sort(sizesAndUsers, LEADERBOARD_COMPARATOR);  
        int i = 0;
        for (String sizeAndUser : sizesAndUsers) {
        
          User leader = User.getUserByID(sizeAndUser.substring(sizeAndUser.indexOf(":")+1));
          if (leader.getStatus() == User.Status.Active) {
            leadersFW.write(sizeAndUser.substring(0,sizeAndUser.indexOf(":")) + " : " +  leader.getUsername() + " [" + leader.getPoints() + "]\n");
          }
        } 
        leadersFW.close();
        } catch (IOException e) {
        System.out.println("An error occurred writing to the Leaderboard.");
       // e.printStackTrace();
      }
      return "leaderlist was generated";
    }
        
        if (commandRegex(msg, "message")) {
          String content = getArgument(msg);
          User recipient = User.getUserByUsername(content.substring(0,content.indexOf(" ")));
          if (recipient == null) {
            return "User under such name does not exist.";
          }
          
          String message = getArgument(content); 
          if (message=="") {
            return "Please enter a message after - ";
          }

          Log.add(player.getUsername() + " messaged " + recipient.getUsername() + "\""+message+"\"");
          TwilioSMS.send(recipient.getPhoneNumber(), "Admin " + player.getUsername() + " said to you: \n\""+message+"\"");
          return "Sent message to " + recipient.getUsername() + ": \n\""+message+"\"";
        }

        if (commandRegex(msg, "setadmin")) {
          String username = getArgument(msg);
          User.getUserByUsername(username).setAdmin();
          TwilioSMS.send(
            User.getUserByUsername(username).getPhoneNumber(),
            "Admin " + player.getUsername() + " granted you admin access. Use it wisely!");
          return "Granted " + username + " admin access!";
        }

        if (commandRegex(msg, "revokeadmin")) {
          String username = getArgument(msg);
          User.getUserByUsername(username).removeAdmin();
          TwilioSMS.send(
            User.getUserByUsername(username).getPhoneNumber(),
            "Your admin access has been revoked.");
          return "Revoked " + username + "'s admin access!";
        }

        if (commandRegex(msg, "addpoints")) {
          String args = getArgument(msg);
          String username = args.split(" ")[0];
          int points = Integer.parseInt(args.substring(args.indexOf(" ") + 1));
          int newPoints = User.getUserByUsername(username).addPoints(points);
          return String.format("Added %d points to %s. They now have %d points.", points, username, newPoints);
        }
        
        if (commandRegex(msg, "deductpoints")) {
          String args = getArgument(msg);
          String username = args.split(" ")[0];
          int points = Integer.parseInt(args.substring(args.indexOf(" ") + 1));
          int newPoints = User.getUserByUsername(username).addPoints(-points);
          return String.format("Deducted %d points from %s. They now have %d points.", points, username, newPoints);
        }
      }
    } 

    else if (player.getStatus() == User.Status.DND) {
      if ((msg.length() >= 7) && msg.substring(0,7).equals("/active")) {
        player.setStatus(User.Status.Active);
        return "You are now active and can recieve messages";
      }
      return "You can turn off your do not disturb by typing /active. ";
    }  
  
    else {
      return "Please set a username first.\n/username- or /u-\n(type your username behind the \"-\") ";
    }
    
    return "Sorry I don't understand your command, please type /menu or /m for commands";
  }

  public String getArgument(String msg) {
    if (msg.indexOf(" ") > 0) {
      return msg.substring(msg.indexOf(" ") + 1);
    }
    return "";
  }

  public String stripSpaces(String str) {
    return str.replace(" ","");
  } 
  
  public String stripPhoneNum(String num) {
    return num.replaceAll("([^(\\d\\+)]|[() -])", "");
  } 

  public String matchParse(String matchResult) {
      Log.add(matchResult);

    // pull users from matchResults
      int x1 = matchResult.indexOf("[");
      int x2 = matchResult.indexOf("]");
    
    if (x1>0) {
      // users are in the return string
      String uid = matchResult.substring(x1+1,x2);
      String uid2 = matchResult.substring(x2+2,matchResult.length()-1);
      User player2 = User.getUserByID(UUID.fromString(uid2));  
    
    if (matchResult.substring(0,20).equals("match completed with")){
      // winner established 
      // include in Ledger
      // JUST FOR DEBUGGING: the clock should normally do this - not myHandler!
      Ledger.addMatches(cho.getReturnQueue());
      // delete all match requests involving participants
      cho.removeAllRequest(uid);
      cho.removeAllRequest(uid2);
      
      // player is winner and is listed first
      if (player.getID().equals(uid)) {
        
      // we should let player2's moblings know they lost
      // we should let player's moblings know they won
        
      //player2 is loser
        TwilioSMS.send(player2.getPhoneNumber(), "I am so sorry. Despite your valiant efforts, you have been defeated in battle. You now must join " + player.getUsername() + "'s mob");
        return "Congratulations! You just won your battle against " + player2.getUsername() + "! " + player2.getUsername() + " 's mob has just merged with your mob";
      } else {
        // player2 is winner
        player2 = User.getUserByID(UUID.fromString(uid));
        TwilioSMS.send(player2.getPhoneNumber(), "Congratulations! You just won your battle against " + player.getUsername() + "! " + player.getUsername() + " 's mob has just merged with your mob");

        // we should let player's moblings know they lost
        // we should let player2's moblings know they won
        
        // player is the loser
        return "I am so sorry. Despite your valiant efforts, you have been defeated in battle. You now must join " + player2.getUsername() + "'s mob";
      }
    } else if(matchResult.substring(0,10).equals("error: tie")) {

      //result is a tie
      // get player2 object 
       if (!player.getID().equals(uid)) player2 = User.getUserByID(UUID.fromString(uid));
        
       TwilioSMS.send(player2.getPhoneNumber(), "You tied with the other player! Please submit a new battle weapon  \n /rock \n /paper \n /scissors"); 
      return "You tied with the other player! Please submit a new battle weapon \n /rock \n /paper \n /scissors";
    } else {      
      return matchResult;
    }
  } else {
      return matchResult;      
  }
  
}

}
