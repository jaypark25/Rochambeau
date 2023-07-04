import java.io.File;  // Import the File class
import java.io.IOException;  // Import the IOException class to handle errors
import java.io.FileWriter;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files


public class Ledger {
  //Method that creates a text file 
  /*public static void makeTxt() {
    try {
      File myObj = new File("filename.txt");
      if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
      } else {
        System.out.println("File already exists.");
      }
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }  

  //Method that can write text to a text file
   public static void writeTxt(boolean append) {
    try {
      FileWriter myWriter = new FileWriter("filename.txt", append);
      myWriter.write("Jay loves blackpink!" + "\n");
      myWriter.close();
      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  //Method that can print out the contents of a text file
    public static void readTxt() {
    try {
      File myObj = new File("filename.txt");
      Scanner myReader = new Scanner(myObj);
      while (myReader.hasNextLine()) {
        String data = myReader.nextLine();
        System.out.println(data);
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }*/

  //Creates a new battle ledger text file
  public static void makeLedger()
  {
    try {
      File myObj = new File("ledger.txt");
      if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
      } else {
        System.out.println("File already exists.");
      }
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  //Adds a match to the battle ledger text file
  public static void addMatches(ArrayList<String> matches)
  {
    try {
      FileWriter myWriter = new FileWriter("ledger.txt", true);
      for(int i = 0; i < matches.size(); i++)
        {
      myWriter.write(matches.get(i) + "\n");
        }
      myWriter.close();
      System.out.println("Successfully added to ledger.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }
  
  //Returns an ArrayList with the line number of every match in the ledger where a string (UID) is present
    public static ArrayList<Integer> getData(String UID) {
      
      ArrayList<Integer> returnable = new ArrayList<Integer>();
      
    try {
      File myObj = new File("ledger.txt");
      Scanner myReader = new Scanner(myObj);
      int i = 1;
      
      while (myReader.hasNextLine()) {
        String line = myReader.nextLine();
        if(line.indexOf(UID)>-1){
          returnable.add(i);
        }
        i++;
       
      }
      
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
      return returnable;
    }

  //Takes in a line number and returns a string of what is written on the ledger on that line number
  public static String readMatch(int matchNumber)
  {
    int count = 0;
    String data = "";
    try {
      File myObj = new File("ledger.txt");
      Scanner myReader = new Scanner(myObj);
      while (count != matchNumber) {
        data = myReader.nextLine();
        count++;
      }
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
    return data;
  }

  public static String getWinner(String match)
  {
    int commaIndex = match.indexOf(',');
    String winner = match.substring(0, commaIndex);
    return winner;
  }

  public static String getLoser(String match)
  {
    int commaIndex = match.indexOf(',');
    int lCommaIndex = match.lastIndexOf(',');
    String loser = match.substring(commaIndex + 1, lCommaIndex);
    return loser;
  }

  /*public static ArrayList<Integer> getChoice(String choice) {
      
      ArrayList<Integer> returnable = new ArrayList<Integer>();
      
    try {
      File myObj = new File("filename.txt");
      Scanner myReader = new Scanner(myObj);
      int i = 1;
      
      while (myReader.hasNextLine()) {
        String line = myReader.nextLine();
        if(line.indexOf(choice)>-1){
          returnable.add(i);
        }
        i++; 
       
      }
      
      myReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
      return returnable;
    }*/
  
      
}


