import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class User {
  private static final Logger logger = LogManager.getLogger();

  // takes a String that only contains numbers, return an array of two indices to switch
  // the digitsum is converted into base b = s.length()-3, and temp stores the first 2 digits of the resulting base b integer
    private static int[] indices(String s) {
    int digitsum = 0;
    int[] temp = {3,3};
    
    char[] num = s.toCharArray();
    for (char n : num) {
      digitsum += ((int)n-48);
    }
    temp[0] += Integer.valueOf(Integer.toString(digitsum,(s.length()-4)).substring(0,1));
    temp[1] += Integer.valueOf(Integer.toString(digitsum,(s.length()-4)).substring(1,2));
    if (temp[0] == temp[1]) {
      temp[1] += 1;
    }
    return temp;
  }

  // takes a String that only contains numbers, swaps the digits at indices returned by indices(String s), and return the encrypted number
  private static String swap(String s) {
    char[] num = s.toCharArray();
    int[] swap = indices(s);
    char temp = num[swap[0]];
    num[swap[0]] = num[swap[1]];
    num[swap[1]] = temp;
    return (String.valueOf(num));
  }

  // encode & decode, takes phoneNumber format
  private static String encode(String s){
    String swapped = swap(s.substring(1));  
    //System.out.println("swapped = " + swapped);
    String shifted = swapped.substring(0,4)+swapped.substring(swapped.length()-1,swapped.length())+swapped.substring(4,swapped.length()-1);
    //System.out.println("shifted = " + shifted);
    return ("+" + shifted);
  }

  private static String decode(String k){
    String s = k.substring(1);
    String unshifted = s.substring(0,4)+s.substring(5,s.length())+s.substring(4,5);
    //System.out.println("unshifted = "+ unshifted);
    String unswapped = swap(unshifted);
    //System.out.println("unswapped = " + unswapped);
    return ("+" + unswapped);
  }

  
  public enum Status {
    Incomplete, Active, DND, Banned, Mod; // DO NOT MODIFY ORDER
  }

  private static Map<UUID, User> UUIDToUserMap = new HashMap<UUID, User>();
  private static Map<String, UUID> usernameToUUIDMap = new HashMap<String, UUID>();
  private static Map<String, UUID> phoneNumberToUUIDMap = new HashMap<String, UUID>();

  //set & get phoneNumber require swap(phoneNumber)
  //note: when instantiate user with only phone number, the username will be the valid phone number but the phoneNumber will be the encoded phone number. 

  private String phoneNumber, username;
  private UUID uuid;
  private Status status;
  private int permissions = 0;
  private int points = 0;

  public User(UUID id, String name, String phone, int stat, int perms, int pts) {
    switch (stat) {
      case 1:
        status = Status.Active;
        break;
      case 2:
        status = Status.DND;
        break;
      case 3:
        status = Status.Banned;
        break;
      case 4:
        status = Status.Mod;
        break;
      default:
        status = Status.Incomplete;
        break;
    }
    uuid = id;
    username = name;
    phoneNumber = phone;
    permissions = perms;
    points = pts;

    UUIDToUserMap.put(uuid, this);
    phoneNumberToUUIDMap.put(phoneNumber, uuid);
    usernameToUUIDMap.put(username, uuid);
  }

  public User(String num, String name) {
    status = (num == name) ? Status.Incomplete : Status.Active;
    phoneNumber = encode(num);
    username = name;  
    uuid = UUID.randomUUID();
    UUIDToUserMap.put(uuid, this);
    phoneNumberToUUIDMap.put(phoneNumber, uuid);
    usernameToUUIDMap.put(username, uuid);

    Database.executeUpdate(String.format("INSERT INTO users VALUES (\"%s\", \"%s\", \"%s\", %d, 0, 0)", uuid.toString(), username, phoneNumber, status.ordinal()));
  }

  public User(String num){
    this(num, num);
  }

  public static User getUserByID(UUID id) {
    if (id == null) {
      return null;
    }
    return UUIDToUserMap.get(id);
  }

  public static User getUserByID(String id) {
    if (id == null) {
      return null;
    }
    return UUIDToUserMap.get(UUID.fromString(id));
  }

  
  public static User getUserByUsername(String name) {
    return getUserByID(usernameToUUIDMap.get(name));
  }

  public static User getUserByPhoneNumber(String number) {
    return getUserByID(phoneNumberToUUIDMap.get(encode(number)));
  }

  public String getID() {
    return uuid.toString();
  }

  public String getUsername() {
    return username;
  }

  public String getPhoneNumber() {
    return decode(phoneNumber);
  }

  public Status getStatus() { 
    return status;
  }

  public int getPoints() { 
    return points;
  }

  public boolean isAdmin() {
    return (permissions & 1) == 1;
  }

  public void setAdmin() {
    permissions |= 1;
    Database.executeUpdate(String.format("UPDATE users SET perms = %s WHERE id = \"%s\"", permissions, uuid.toString()));
  }

  public void removeAdmin() {
    permissions &= ~1;
    Database.executeUpdate(String.format("UPDATE users SET perms = %s WHERE id = \"%s\"", permissions, uuid.toString()));
  }

  public boolean setUsername(String newName) {
    if (!usernameAvailable(newName)) {
      return false;
    }
    usernameToUUIDMap.put(newName, usernameToUUIDMap.remove(username));
    username = newName;
    
    Database.executeUpdate(String.format("UPDATE users SET username = \"%s\" WHERE id = \"%s\"", username, uuid.toString()));
    return true;
  }

  public void setStatus(Status s) {
    status = s;
    Database.executeUpdate(String.format("UPDATE users SET status = %d WHERE id = \"%s\"", status.ordinal(), uuid.toString()));
  }

  public int addPoints(int pts) {
    points += pts;
    Database.executeUpdate(String.format("UPDATE users SET points = %d WHERE id = \"%s\"", points, uuid.toString()));
    return points;
  }

  public void deleteUser() {
    Database.executeUpdate(String.format("DELETE FROM users WHERE id = \"%s\"", uuid.toString()));
  }
  
  public static boolean usernameAvailable(String name) {
    return (usernameToUUIDMap.get(name) == null);
  }

  public String toString() {
    return String.format("Username: %s \nUUID: %s \nStatus: %s \nPhone Number: %s", username, uuid.toString(), status, decode(phoneNumber));
  }

  /* 
   * Usage:
   * for (var entry : User.getAllUsers().entrySet()) {
   *   System.out.println(String.format("UUID: %s", entry.getKey().toString()));
   * }
   */
  public static Map<UUID, User> getAllUsers() {
    return UUIDToUserMap;
  }
}