/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.print.DocFlavor.STRING;

import java.util.SortedSet;
import java.util.TreeSet;
import java.lang.Math;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Collections;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class GameRental {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of GameRental store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end GameRental

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            GameRental.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      GameRental esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Catalog");
                System.out.println("4. Place Rental Order");
                System.out.println("5. View Full Rental Order History");
                System.out.println("6. View Past 5 Rental Orders");
                System.out.println("7. View Rental Order Information");
                System.out.println("8. View Tracking Information");

                //the following functionalities basically used by employees & managers
                System.out.println("9. Update Tracking Information");

                //the following functionalities basically used by managers
                System.out.println("10. Update Catalog");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql, authorisedUser); break;
                   case 2: updateProfile(esql, authorisedUser); break;
                   case 3: viewCatalog(esql); break;
                   case 4: placeOrder(esql, authorisedUser); break;
                   case 5: viewAllOrders(esql, authorisedUser); break;
                   case 6: viewRecentOrders(esql, authorisedUser); break;
                   case 7: viewOrderInfo(esql, authorisedUser); break;
                   case 8: viewTrackingInfo(esql, authorisedUser); break;
                   case 9: updateTrackingInfo(esql, authorisedUser); break;
                   case 10: updateCatalog(esql, authorisedUser); break;
                   case 11: updateUser(esql, authorisedUser); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Reads the users input given from the keyboard
    * @String
    **/
    public static String readString(String prompt) {
      String input;
      // returns only if a correct value is given.
      do {
         System.out.print(prompt);
         try { // read the integer, parse it and break.
            input = in.readLine().trim();
            if(input.isEmpty() || input == null)
            {
               throw new Exception("String must not be empty!");
            }
            break;
         }catch (Exception e) {
            System.out.println(e);
            continue;
         }//end try
      }while (true);
      return input;
   }//end readString

   /*
    * Reads the users input as float given from the keyboard
    * @String
    **/
    public static String readFloatAsString(String prompt) {
      String input;
      // returns only if a correct value is given.
      do {
         System.out.print(prompt);
         try { // read the integer, parse it and break.
            input = Float.toString(Float.parseFloat(in.readLine())); // lol
            break;
         }catch (Exception e) {
            System.out.println(e);
            continue;
         }//end try
      }while (true);
      return input;
   }//end readFloatAsString

   /*
    * Creates a new user
    **/

   //Needs a trigger
   public static void CreateUser(GameRental esql){
      System.out.println("==========================");
      System.out.println("====== Registration ======");
      System.out.println("==========================");
      System.out.println();
      String username;
      String password;
      String phone_number;
      int made = 0;
      while (made == 0){
         try{
            System.out.println("Enter Username: ");
            username = in.readLine();

            
            String checkUserQuery = "SELECT u.login FROM Users AS u WHERE u.login = '" + username + "'";
            int userRowCount = esql.executeQuery(checkUserQuery);

            if(userRowCount == 0){
                  
               System.out.println("Enter Password: ");
               password = in.readLine();
                     
               System.out.println("Enter Phone Number: ");
               phone_number = in.readLine();
                        
               String insertUserQuery = "INSERT INTO Users(login, password, phoneNum, favGames) VALUES('" + username + "', '" + password + "', '" + phone_number + "', '')";
               esql.executeUpdate(insertUserQuery);
               made = 1;    
               } else {
                  System.out.println("Sorry, that username is taken. Please try again.");
                  PressEnterToContinue();
               }
           
         } catch (Exception e) {
            System.out.println("Error");
         }
      }//end CreateUser
   }


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(GameRental esql){
      System.out.println("============================");
      System.out.println("========== Login ===========");
      System.out.println("============================");
      String username;
      String password;
      try{
         System.out.println("Enter Username: ");
         username = in.readLine();
         System.out.println("Enter Password: ");
         password = in.readLine();
           
         String userQuery = "SELECT * " + 
                            "FROM Users AS u " + 
                            "WHERE u.login = '" + username + "' " + 
                            "AND u.password = '" + password + "' ";
         int rowCount = esql.executeQuery(userQuery);
         if(rowCount > 0){
            return username;
         } else {
            System.out.println("Wrong Username or Password");
            return null;
         }   
      } catch (Exception e){
         System.out.println("Input Error");
      }
     
      return null;
   }//end

// Rest of the functions definition go in here

   public static void viewProfile(GameRental esql, String user) {
      int exit = 0;
      try{
         String profileQuery = "SELECT * FROM Users WHERE Users.login = '" + user + "'";
         List<List<String>> profile = esql.executeQueryAndReturnResult(profileQuery);
         List<String> inner = profile.get(0);

         System.out.println("Username: " + inner.get(0));
         System.out.println("Password: " + inner.get(1));
         System.out.println("Phone Number: " + inner.get(4));
         if(inner.get(3) == null || inner.get(3) == ""){
            System.out.println("You have no favorite games.");
         } else {
            System.out.println("Favorite Games: " + inner.get(3));
         }
         System.out.println("Number of Overdue Games: " + inner.get(5));

         System.out.println();
         
         PressEnterToContinue();
      } catch (Exception e){
         System.out.println("Query Error");
      }
   }

   private static String getPassword(GameRental esql, String username){
      try{
         String passwordQuery = "SELECT u.password FROM Users AS u WHERE u.login = '" + username + "'";
         return esql.executeQueryAndReturnResult(passwordQuery).get(0).get(0);
      }catch (Exception e){
         System.out.print("SQL Exception");
      }
      return null;
   }

   private static String getPhoneNumber(GameRental esql, String username){
      try{
         String pnQuery = "SELECT u.phoneNum FROM Users AS  u WHERE u.login = '" + username + "'";
         return esql.executeQueryAndReturnResult(pnQuery).get(0).get(0);
      }catch (Exception e){
         System.out.print("SQL Exception");
      }
      return null;
   }

   private static String getFavoriteGames(GameRental esql, String username){
       try{
         String fgQuery = "SELECT u.favGames FROM Users AS u WHERE u.login = '" + username + "'";
         return esql.executeQueryAndReturnResult(fgQuery).get(0).get(0);
      }catch (Exception e){
         System.out.print("SQL Exception");
      }
      return null;
   }

   private static void changePassword(GameRental esql, String truePassword, String authorisedUser){
      String newPassword = "";
      String oldPassword = "";
      boolean notSamePass = true;
      System.out.println("Insert New Password: ");
       
      try{   
         while(notSamePass){
            newPassword = in.readLine();
            if(newPassword.equals(truePassword)){
               System.out.println("Error Cannot Change Password to Already Current Password");
               System.out.println("Please try again");
               System.out.println("Insert New Password: ");
            } else {
               notSamePass = false; 
            }
               
         }   
         System.out.println("Enter Current Password: ");   
         while(true){       
            oldPassword = in.readLine();
            if(!oldPassword.equals(truePassword)){
               System.out.println("Error Input Does Not Match Current Password");
               System.out.println("Please try again");
               System.out.println("Insert Current Password: ");
            } else {
               break;
            }
            
         }
         String updatePassQuery = "UPDATE Users SET password = '" + newPassword + "'WHERE login = '" + authorisedUser + "'";       
         esql.executeUpdate(updatePassQuery);
      } catch(Exception e){
         System.out.println("Unrecognized Entry");
         return;
      }
      
   }

   private static void changePhoneNumber(GameRental esql, String truePhoneNumber, String authorisedUser, String truePassword){
      String newPhoneNumber = "";
      String currPassword = "";
      boolean notSamePass = true;
      System.out.println("Insert New Phone Number: ");
       
      try{   
         while(notSamePass){
            newPhoneNumber = in.readLine();
            if(newPhoneNumber.equals(truePhoneNumber)){
               System.out.println("Error Cannot Change Phone Number to Already Current Phone Number");
               System.out.println("Please try again");
               System.out.println("Insert New Phone Number: ");
            } else {
               notSamePass = false; 
            }
               
         }   
         System.out.println("Enter Current Password: ");   
         while(true){    
            currPassword = in.readLine();
            if(!currPassword.equals(truePassword)){
               System.out.println("Error Input Does Not Match Current Password");
               System.out.println("Please try again");
               System.out.println("Insert Current Password: ");
            } else {
               break;
            }
           
         }
         String updatePassQuery = "UPDATE Users SET phoneNum = '" + newPhoneNumber + "'WHERE login = '" + authorisedUser + "'";       
         esql.executeUpdate(updatePassQuery);
      } catch(Exception e){
         System.out.println("Unrecognized Entry");
         return;
      }
   }

   private static void deleteOneGame(GameRental esql, String truePassword, String authorisedUser, String favGames){
      if(favGames == ""){
         System.out.println("Error, you have no favorite games.");
         return;
      }
      String deletedGame = "";;
      try{
         System.out.println("Type the game you would like to delete from your favorite games: ");
         deletedGame = in.readLine();
         String[] games = favGames.split(",");
         StringBuilder output = new StringBuilder();
         for(String i : games){
            if(!i.equals(deletedGame)){
               output.append(i).append(",");
            }
         }
         String outputString = output.toString();
         if(outputString.length() > 0){
            outputString = outputString.substring(0, outputString.length() - 1);
         }
         String updateFav = "UPDATE Users SET favGames = '" + outputString + "' WHERE login = '" + authorisedUser + "'";
         esql.executeUpdate(updateFav);
      } catch (Exception e){
         System.out.println("Unrecognized Entry1");
         return;
      }
   }

   private static void updateFavGames(GameRental esql, String truePassword, String authorisedUser, String favGames){
      boolean isEmpty = (favGames == "") ? true : false;
      String inputFav = "";
      System.out.println("Insert all your favorite games that you want to add (Separate with commas (,))");
      try{
         inputFav = in.readLine();
         if(isEmpty){
            favGames = inputFav;
         } else {
            favGames += "," + inputFav;
         }
         String updateFav  = "UPDATE Users SET favGames = '" + favGames + "' WHERE login = '" + authorisedUser + "'";
         esql.executeUpdate(updateFav);
         return;
      } catch(Exception e){
         System.out.println("Unrecognized Entry 3");
      }
   }
   private static void deleteAllGames(GameRental esql, String truePassword, String authorisedUser, String favGames){
      if(favGames == ""){
         System.out.println("Error, you have no favorite games.");
         return;
      }
      String inputPass = "";
      System.out.println("You are about to delete all games from your favorite games. \n If you are sure about this, type your password");
      try{
         inputPass = in.readLine();
         if(inputPass.equals(truePassword)){
            
            String deleteGames = "UPDATE Users SET favGames = '' WHERE login = '" + authorisedUser + "'";
            esql.executeUpdate(deleteGames);
            
         } else{
            System.out.println("Password incorrect. Returning to main menu");
         }
      } catch (Exception e){
         System.out.println("Unrecognized Entry2");
      }
   }
   private static void changeFavoriteGames(GameRental esql, String truePassword, String authorisedUser, String favGames){
      System.out.println("What do you want to change?");
      System.out.println("1. Delete Game From Favorite Games");
      System.out.println("2. Update Favorite Games");
      System.out.println("3. Delete All Favorite Games");

      switch(readChoice()){
         case 1: deleteOneGame(esql, truePassword, authorisedUser, favGames ); break;
         case 2: updateFavGames(esql, truePassword, authorisedUser, favGames); break;
         case 3: deleteAllGames(esql, truePassword, authorisedUser, favGames); break;
         case 9: return;
         default : System.out.println("Unrecognized choice!"); break;
      }
      return;
   }

   public static void updateProfile(GameRental esql, String authorisedUser) {
      System.out.println("===========================");
      System.out.println("===    Update Profile   ===");
      System.out.println("===========================");
      String truePassword = getPassword(esql, authorisedUser);
      String truePhoneNumber = getPhoneNumber(esql, authorisedUser);
      String trueFavGames = getFavoriteGames(esql, authorisedUser);

      System.out.println("What do you want to change?");
      System.out.println("1. Password");
      System.out.println("2. Phone Number");
      System.out.println("3. Favorite Games");

      switch(readChoice()){
         case 1: changePassword(esql, truePassword, authorisedUser); break;
         case 2: changePhoneNumber(esql, truePhoneNumber, authorisedUser, truePassword); break;
         case 3: changeFavoriteGames(esql, truePassword, authorisedUser, trueFavGames); break;
         case 9: return;
         default : System.out.println("Unrecognized choice!"); break;
      }


   }

   private static void PressEnterToContinue()
   { 
      System.out.println("Press Enter key to continue...");
      try{
         in.read();
      }  
      catch(Exception e) {}  
   }

   /*
    * Self explanatory. Just a convenience wrapper.
    */
   private static void PrintCatalogMenu(String nameFilter, String genreFilter, int priceSort)
   {
      System.out.println("=========================");
      System.out.println("==    VIEW CATALOG     ==");
      System.out.println("=========================");
      System.out.println("Configure view setting with the menu below!");
      System.out.println("1. Game Name  [" + (nameFilter == null ? " any " : nameFilter) + "]");
      System.out.println("2. Genre      [" + (genreFilter == null ? " any " : genreFilter) + "]");

      String priceSortStr = "";
      if(priceSort  == 1) {priceSortStr = "none";}
      else if(priceSort == 2) {priceSortStr = "Ascending";}
      else if(priceSort == 3) {priceSortStr = "Descending";}
      System.out.println("3. Price Sort [" + priceSortStr + "]");
      System.out.println("4. Cancel");
      System.out.println("5. Search!");
   }

   /*
    * Convenience method for getting a distinct set of genres that currently exist in the database.
    * Useful for searching for a game via genre.
    */
   private static SortedSet<String> GetGenresFromDatabase(GameRental gameRental)
   {
      SortedSet<String> result = new TreeSet<String>(); 
      String genresQuery = "SELECT DISTINCT c.genre FROM Catalog c;";

      try
      {
         List<List<String>> queryResult = gameRental.executeQueryAndReturnResult(genresQuery);
         for(List<String> row : queryResult)
         {
            result.add(row.get(0));
         }
      }
      catch(Exception e)
      {
         System.out.println("Genre query failed");
      }

      return result; // cache?
   }

// Rest of the functions definition go in here

   public static void viewCatalog(GameRental esql) {
      /*
       * Filter Options
       *    1. Game Name (contains, use "sql like")
       *    2. Game Genre (poll genres from db)
       *    3. Price (sort by ascending or descending)
       */
      String gameNameFilter    = null;
      String gameGenreFilter   = null;
      int    gamePriceSortType = 1; // 1 = none, 2 = ascending, 3 = descending

      boolean doSearch = false;
      boolean done = false;
      while(!done)
      {
         PrintCatalogMenu(gameNameFilter, gameGenreFilter, gamePriceSortType);
         
         try{
            int choice = readChoice();
            if(choice == 1){
               // Game Name filter
               try
               {
                  System.out.print("Enter game title: ");
                  gameNameFilter = in.readLine().trim();
                  if(gameNameFilter.isEmpty()) {gameNameFilter = null;}
               }
               catch(Exception e)
               {
                  System.out.println("Input Error");
                  PressEnterToContinue();
               }
            }
            else if(choice == 2){
               // Game Genre
               try
               {
                  SortedSet<String> genres = GetGenresFromDatabase(esql);
                  if(!genres.isEmpty())
                  {
                     String arr[] = new String[genres.size()];
                     arr = genres.toArray(arr); // 0-based index of genres

                     // Generate and print genre menu with our database data
                     for(int i = 0; i < arr.length; i++)
                     {
                        System.out.println(String.format("%d. %s", i + 1, arr[i]));
                     }

                     // Take user input
                     try
                     {
                        int genreChoice = readChoice();
                        if(genreChoice <= 0 || genreChoice > arr.length)
                        {
                           throw new Exception("Genre choice doesn't exist");
                        }

                        gameGenreFilter = arr[genreChoice - 1];
                     }
                     catch(Exception e)
                     {
                        System.out.println(e);
                        PressEnterToContinue();
                     }
                  }
               }
               catch(Exception e)
               {
                  System.out.println("Input Error");
                  PressEnterToContinue();
               }
            }
            else if(choice == 3){
               // Price Sort
               System.out.println("1. No Sorting");
               System.out.println("2. Ascending");
               System.out.println("3. Descending");
               try
               {
                  int sortChoice = readChoice();
                  if(sortChoice < 1 || sortChoice > 3)
                  {
                     throw new Exception("Invalid sort option!");
                  }

                  gamePriceSortType = sortChoice;
               }  
               catch(Exception e)
               {
                  System.out.println(e);
                  PressEnterToContinue();
               }
            }
            else if(choice == 4){
               // Cancel
               done = true;
               return;
            }
            else if(choice == 5){
               // Do search
               doSearch = true;
               done = true;
            }
            else{
               System.out.println("Invalid choice!");
            }
         }
         catch (Exception e){

         }
      }
   
      if(doSearch)
      {
         boolean hasWhereClause = false;
         String query = "";
         query += "SELECT c.gameName, c.genre, c.description, c.price \n";
         query += "FROM Catalog c \n";
         if(gameNameFilter != null || gameGenreFilter != null || gamePriceSortType != 1)
         {
            query += "WHERE ";

            if(gameNameFilter != null)
            {
               query += "c.gameName LIKE '%" + gameNameFilter + "%' ";
               hasWhereClause = true;
            }

            if(gameGenreFilter != null)
            {
               if(hasWhereClause)
               {
                  query += "AND ";
               }

               query += "c.genre = '" + gameGenreFilter + "' ";
               hasWhereClause = true;
            }

           if(gamePriceSortType != 1)
           {
               query += "\nORDER BY price";
               switch(gamePriceSortType)
               {
                  case 2: query += " ASC "; break;
                  case 3: query += " DESC "; break;
               }
           } 
         } // END filter query 
         query += "; ";
         
         try
         {
            List<List<String>> rows = esql.executeQueryAndReturnResult(query);
            for(List<String> row : rows)
            {
               System.out.println(String.format("%50s | %10s | %55s | %5s", row.get(0), row.get(1), row.get(2), row.get(3)));
            }

            if(rows.size() <= 0)
            {
               System.out.println("No entries matched your search...");
            }

            PressEnterToContinue();
         }
         catch(Exception e)
         {
            System.out.println(e);
            PressEnterToContinue();
         }
      }
   }

// Rest of the functions definition go in here

   /*: user can order any game from the game rental store. User will be
asked to input every gameID and unitsOrdered (the amount of copies of the game they
want) for each game they want to rent. The total price of their rental order should be
returned and output to the user. After placing the rental order, the rental order
information needs to be inserted in the RentalOrder table with a unique rentalOrderID.
Each gameID, rentalOrderID, and the unitsOrdered should be inserted into
GamesInOrder for every game in the order. Also, a TrackingInfo record with a unique
trackingID should be created for the order*/
   private static boolean isValidGame(GameRental esql, String gameID){

      String gameIDQuery = "SELECT c.gameID FROM catalog AS c WHERE c.gameID = '" + gameID + "'";
      try{
         int rowCount = esql.executeQuery(gameIDQuery);
         if(rowCount == 1){
            return true;
         } else {
            return false;
      }
      } catch (Exception e){
         System.out.println("Error: " + e.getMessage());
         return false;
      }
      
   }

   private static double getCost(GameRental esql, List<String> gamesOrdered, List<Integer> numUnits){
      try{
         double cost = 0.0;
         for(int i = 0; i < gamesOrdered.size(); i++){
            String findCostQuery = "SELECT c.price FROM catalog AS c WHERE c.gameID = '" + gamesOrdered.get(i) + "'";
            List<List<String>> query = esql.executeQueryAndReturnResult(findCostQuery);
            cost += (1.0 * Double.parseDouble(query.get(0).get(0)) * numUnits.get(i));
         }  
         return cost;
      } catch (Exception e){
         System.out.println("Error: " + e.getMessage());
         return 0.0;
      }
   }


   public static void placeOrder(GameRental esql, String authorisedUser) {
      int numGames = 0;
      String gameID = "";
      int unitsOrdered = 0;
      double cost = 0.0;
      boolean validValue = false;
      List<String> gamesOrdered = new ArrayList<>();
      List<Integer> numUnits = new ArrayList<>();

      System.out.println("How many unique games do you want to rent?");
      try{
         numGames = Integer.parseInt(in.readLine());
         for(int i = 0; i < numGames; i++){
            while(true){
               System.out.println("Enter the Game ID: ");
               gameID = in.readLine();
               if(isValidGame(esql, gameID)){
                  gamesOrdered.add(gameID);
                  while(!validValue){
                     try{
                        System.out.println("Enter how many units of " + gameID + ": ");
                        unitsOrdered = Integer.parseInt(in.readLine());
                        numUnits.add(unitsOrdered);
                        validValue = true;
                     } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid integer.");
                     }
                  }
                  break;
               } else {
                  System.out.println("Invalid Game ID. Please try again.");
               }
            }
            
         }
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
         return;
      }
      try{
         cost = getCost(esql, gamesOrdered, numUnits);
         System.out.println("The total cost of this rental is " + cost);
         String insertIntoRental = "INSERT INTO RentalOrder(login, noOfGames, totalPrice) VALUES('" + authorisedUser + "', " + numGames + ", " + cost + ")";
         esql.executeUpdate(insertIntoRental);
         List<List<String>> rentalID = esql.executeQueryAndReturnResult("SELECT r.rentalOrderID FROM rentalOrder AS r WHERE login = '" + authorisedUser + "'");   
         String insertIntoGIO = "INSERT INTO GamesInOrder(rentalOrderId, gameID, unitsOrdered) VALUES";
         for(int i = 0; i < numGames; i++){
            insertIntoGIO += "('" + rentalID.get(rentalID.size() - 1).get(0) + "', " + gamesOrdered.get(i) + ", " + numUnits.get(i) + ")";
            if(i < numGames - 1){
               insertIntoGIO += ",";
            }
         }
         esql.executeUpdate(insertIntoGIO);
      } catch (Exception e) {
         System.out.println("Error: " + e.getMessage());
         return;
      }
      

      
   }
   public static void viewAllOrders(GameRental esql, String authorizedUser) 
   {
      // View all customer's rental history
      System.out.println("==============================================================================================");
      System.out.println("==                                     Rental History                                       ==");
      System.out.println("==============================================================================================");

      String query = "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate FROM RentalOrder WHERE login = '" 
      + authorizedUser + "' ORDER BY dueDate DESC;";

      try
      {
         System.out.println(String.format("%-20s | %-9s | %-12s | %-22s | %-22s", "Order ID", "Num Games", "Total Price", "Order Time", "Due Date"));
         System.out.println("----------------------------------------------------------------------------------------------");
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         for(List<String> row : result)
         {
            System.out.println(String.format("%-20s | %-9s | %-12s | %-22s | %-22s", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
         }

         if(result.size() <= 0)
         {
            System.out.println("No games rented on this account!");
         }
         PressEnterToContinue();
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
      }

   }

   public static void viewRecentOrders(GameRental esql, String authorizedUser) 
   {
      // View all customer's rental history
      System.out.println("==============================================================================================");
      System.out.println("==                                     Recent Orders                                        ==");
      System.out.println("==============================================================================================");

      String query = "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate FROM RentalOrder WHERE login = '" 
      + authorizedUser + "' ORDER BY orderTimestamp DESC LIMIT 5;";

      try
      {
         System.out.println(String.format("%-20s | %-9s | %-12s | %-22s | %-22s", "Order ID", "Num Games", "Total Price", "Order Time", "Due Date"));
         System.out.println("----------------------------------------------------------------------------------------------");
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         for(List<String> row : result)
         {
            System.out.println(String.format("%-20s | %-9s | %-12s | %-22s | %-22s", row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
         }

         if(result.size() <= 0)
         {
            System.out.println("No games rented on this account!");
         }
         PressEnterToContinue();
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
      }
   }

   public static void viewOrderInfo(GameRental esql, String authorizedUser) 
   {
      try
      {
         System.out.println("==========================================");
         System.out.println("==            View Order Info           ==");
         System.out.println("==========================================");

         // View all orders for user (choose)
         String query = "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate FROM RentalOrder WHERE login = '" 
         + authorizedUser + "' ORDER BY dueDate DESC;";

         boolean loopChooseOrder = true;
         while(loopChooseOrder)
         {
            System.out.println(String.format("%-23s | %-9s | %-12s | %-22s | %-22s", "Order ID", "Num Games", "Total Price", "Order Time", "Due Date"));
            System.out.println("----------------------------------------------------------------------------------------------");
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            for(int i = 0; i < result.size(); i++)
            {
               List<String> row = result.get(i);
               System.out.println(String.format("%d. %-20s | %-9s | %-12s | %-22s | %-22s", i + 1, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
            }

            if(result.size() <= 0)
            {
               System.out.println("No games rented on this account!");
               PressEnterToContinue();
               loopChooseOrder = false;
               return;
            }

            int numResults = result.size();
            System.out.println(String.format("%d. Cancel", numResults + 1));

            int choice = readChoice();
            if(choice < 1 || choice > numResults + 1)
            {
               throw new Exception("Invalid choice!");
            }

            if(choice == numResults + 1)
            {
               return;
            }

            List<String> chosenOrder = result.get(choice - 1);
            // With our chosen row, we need the tracking id, and list of games for the order.
            // tracking id is from a single row
            // list of games is multiple rows where a rental order id matches
            String rentalOrderId = chosenOrder.get(0);

            String trackingIdQuery = "SELECT t.trackingID FROM TrackingInfo t WHERE t.rentalOrderID = '" + rentalOrderId + "';";
            List<List<String>> trackingResults = esql.executeQueryAndReturnResult(trackingIdQuery);
            if(trackingResults.size() <= 0)
            {
               System.out.println("No tracking info for this order!");
               PressEnterToContinue();
               loopChooseOrder = false;
               return;
            }

            String trackingID = trackingResults.get(0).get(0); // Assume only 1 exists. The id is the first col

            String gamesQuery = "SELECT gameID, unitsOrdered FROM GamesInOrder WHERE rentalOrderID = '" + rentalOrderId + "';";
            List<List<String>> gamesInOrder = esql.executeQueryAndReturnResult(gamesQuery);
            if(gamesInOrder.size() <= 0)
            {
               System.out.println("No games in this order!");
               PressEnterToContinue();
               loopChooseOrder = false;
               return;
            }

            String catalogQuery = "SELECT * FROM Catalog;";
            List<List<String>> catalog = esql.executeQueryAndReturnResult(catalogQuery);

           
            // Order generic information
            // Then, list of game x(n)
            System.out.println("====================================================================================");
            System.out.println("==                                Order Details                                   ==");
            System.out.println("====================================================================================");
            System.out.println(String.format("%-25s | %-25s | %-11s | %-15s", "Order Time", "Due Date", "Total Price", "Tracking ID"));
            System.out.println("____________________________________________________________________________________");
            System.out.println(String.format("%-25s | %-25s | %-11s | %-40s", chosenOrder.get(3), chosenOrder.get(4), chosenOrder.get(2), trackingID));
            System.out.println("------------------------------------------------------------------------------------");
            System.out.println("--                                    Games                                       --");
            System.out.println("------------------------------------------------------------------------------------");
            for(int i = 0; i < gamesInOrder.size(); i++)
            {
               List<String> gameRow = gamesInOrder.get(i);
               String gameID = gameRow.get(0);
               String gameIDNumStr = gameID.substring(4);
               int gameIDNum = Integer.parseInt(gameIDNumStr);
               String numUnits = gameRow.get(1);

               List<String> gameCatalogRow = catalog.get(gameIDNum - 1);
               String gameFriendlyName = gameCatalogRow.get(1);

               System.out.println(String.format("%-50s x%s", gameFriendlyName, numUnits));
            }


            PressEnterToContinue();
         }
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
      }
   }

   public static void viewTrackingInfo(GameRental esql, String authorizedUser) 
   {
      // Choose Rental Order
      try
      {
         boolean loopChooseViewTrackingInfoMethod = true;
         while(loopChooseViewTrackingInfoMethod)
         {
            System.out.println("=============================================");
            System.out.println("==            View Tracking Info           ==");
            System.out.println("=============================================");

            System.out.println("1. Find by exact Tracking ID");
            System.out.println("2. Find by order history");
            System.out.println("3. Cancel");
            int userTrackMethod = readChoice();
            if(userTrackMethod < 1 || userTrackMethod > 3)
            {
               throw new Exception("Invalid choice...");
            }

            if(userTrackMethod == 3)
            {
               return;
            }

            List<String> trackingInfo = null;
            if(userTrackMethod == 1)
            {
               String trackingID = readString("Enter exact Tracking ID: ");

               String trackingInfoQuery = "SELECT t.courierName, t.rentalOrderID, t.currentLocation, t.status, t.lastUpdateDate, t.additionalComments\n";
               trackingInfoQuery += "FROM TrackingInfo t WHERE t.trackingID = '" + trackingID + "' AND EXISTS ";
               trackingInfoQuery += "(SELECT 1 FROM RentalOrder r WHERE r.login = '" + authorizedUser + "' AND r.rentalOrderID = t.rentalOrderID );";

               List<List<String>> result = esql.executeQueryAndReturnResult(trackingInfoQuery);
               if(result.size() <= 0)
               {
                  throw new Exception("No tracking info for this id! (Maybe not allowed)");
               }

               trackingInfo = result.get(0);
            }
            else if(userTrackMethod == 2)
            {
               // List all orders, then find the tracking id from that
               // View all orders for user (choose)
               String query = "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate FROM RentalOrder WHERE login = '" 
               + authorizedUser + "' ORDER BY dueDate DESC;";

               boolean loopChooseOrder = true;
               while(loopChooseOrder)
               {
                  System.out.println(String.format("%-23s | %-9s | %-12s | %-22s | %-22s", "Order ID", "Num Games", "Total Price", "Order Time", "Due Date"));
                  System.out.println("----------------------------------------------------------------------------------------------");
                  List<List<String>> ordersResults = esql.executeQueryAndReturnResult(query);
                  for(int i = 0; i < ordersResults.size(); i++)
                  {
                     List<String> row = ordersResults.get(i);
                     System.out.println(String.format("%d. %-20s | %-9s | %-12s | %-22s | %-22s", i + 1, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
                  }

                  if(ordersResults.size() <= 0)
                  {
                     System.out.println("No games rented on this account!");
                     PressEnterToContinue();
                     loopChooseOrder = false;
                     return;
                  }

                  int numResults = ordersResults.size();
                  System.out.println(String.format("%d. Cancel", numResults + 1));

                  int choice = readChoice();
                  if(choice < 1 || choice > numResults + 1)
                  {
                     throw new Exception("Invalid choice!");
                  }

                  if(choice == numResults + 1)
                  {
                     return;
                  }

                  List<String> chosenOrder = ordersResults.get(choice - 1);
                  String rentalOrderID = chosenOrder.get(0);
                  String trackingInfoQuery = "SELECT courierName, rentalOrderID, currentLocation, status, lastUpdateDate, additionalComments\n";
                  trackingInfoQuery += "FROM TrackingInfo WHERE rentalOrderID = '" + rentalOrderID + "';";
   
                  List<List<String>> trackingResults = esql.executeQueryAndReturnResult(trackingInfoQuery);
                  if(trackingResults.size() <= 0)
                  {
                     throw new Exception("No tracking info for this order!");
                  }
   
                  trackingInfo = trackingResults.get(0);
                  loopChooseOrder = false;
               }
            }

            // We have the tracking info, just display it.
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(String.format("%-12s | %-20s | %-30s | %-16s | %-27s | %-25s", 
               "Courier Name", 
               "Rental Order ID", 
               "Current Location", 
               "Status", 
               "Last Update Date", 
               "Additional Comments"));
            
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(String.format("%-12s | %-20s | %-30s | %-16s | %-27s | %-25s",
               trackingInfo.get(0),
               trackingInfo.get(1),
               trackingInfo.get(2),
               trackingInfo.get(3),
               trackingInfo.get(4),
               trackingInfo.get(5)));

            PressEnterToContinue();
         }
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
      }
   }

   public static void updateTrackingInfo(GameRental esql, String authorizedUser) 
   {
      // Choose Rental Order
      try
      {
         boolean loopChooseViewTrackingInfoMethod = true;
         while(loopChooseViewTrackingInfoMethod)
         {
            System.out.println("=============================================");
            System.out.println("==            View Tracking Info           ==");
            System.out.println("=============================================");

            System.out.println("1. Find by exact Tracking ID");
            System.out.println("2. Find by order history");
            System.out.println("3. Cancel");
            int userTrackMethod = readChoice();
            if(userTrackMethod < 1 || userTrackMethod > 3)
            {
               throw new Exception("Invalid choice...");
            }

            if(userTrackMethod == 3)
            {
               return;
            }

            List<String> trackingInfo = null;
            if(userTrackMethod == 1)
            {
               String trackingID = readString("Enter exact Tracking ID: ");

               String trackingInfoQuery = "SELECT t.trackingID, t.courierName, t.rentalOrderID, t.currentLocation, t.status, t.lastUpdateDate, t.additionalComments\n";
               trackingInfoQuery += "FROM TrackingInfo t WHERE t.trackingID = '" + trackingID + "' AND EXISTS ";
               trackingInfoQuery += "(SELECT 1 FROM RentalOrder r WHERE r.login = '" + authorizedUser + "' AND r.rentalOrderID = t.rentalOrderID );";

               List<List<String>> result = esql.executeQueryAndReturnResult(trackingInfoQuery);
               if(result.size() <= 0)
               {
                  throw new Exception("No tracking info for this id! (Maybe not allowed)");
               }

               trackingInfo = result.get(0);
            }
            else if(userTrackMethod == 2)
            {
               // List all orders, then find the tracking id from that
               // View all orders for user (choose)
               String query = "SELECT rentalOrderID, noOfGames, totalPrice, orderTimestamp, dueDate FROM RentalOrder WHERE login = '" 
               + authorizedUser + "' ORDER BY dueDate DESC;";

               boolean loopChooseOrder = true;
               while(loopChooseOrder)
               {
                  System.out.println(String.format("%-23s | %-9s | %-12s | %-22s | %-22s", "Order ID", "Num Games", "Total Price", "Order Time", "Due Date"));
                  System.out.println("----------------------------------------------------------------------------------------------");
                  List<List<String>> ordersResults = esql.executeQueryAndReturnResult(query);
                  for(int i = 0; i < ordersResults.size(); i++)
                  {
                     List<String> row = ordersResults.get(i);
                     System.out.println(String.format("%d. %-20s | %-9s | %-12s | %-22s | %-22s", i + 1, row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
                  }

                  if(ordersResults.size() <= 0)
                  {
                     System.out.println("No games rented on this account!");
                     PressEnterToContinue();
                     loopChooseOrder = false;
                     return;
                  }

                  int numResults = ordersResults.size();
                  System.out.println(String.format("%d. Cancel", numResults + 1));

                  int choice = readChoice();
                  if(choice < 1 || choice > numResults + 1)
                  {
                     throw new Exception("Invalid choice!");
                  }

                  if(choice == numResults + 1)
                  {
                     return;
                  }

                  List<String> chosenOrder = ordersResults.get(choice - 1);
                  String rentalOrderID = chosenOrder.get(0);
                  String trackingInfoQuery = "SELECT trackingID, courierName, rentalOrderID, currentLocation, status, lastUpdateDate, additionalComments\n";
                  trackingInfoQuery += "FROM TrackingInfo WHERE rentalOrderID = '" + rentalOrderID + "';";
   
                  List<List<String>> trackingResults = esql.executeQueryAndReturnResult(trackingInfoQuery);
                  if(trackingResults.size() <= 0)
                  {
                     throw new Exception("No tracking info for this order!");
                  }
   
                  trackingInfo = trackingResults.get(0);
                  loopChooseOrder = false;
               }
            }

            // We have the tracking info, now we can do our edit routine.
            // trackingID, courierName, rentalOrderID, currentLocation, status, lastUpdateDate, additionalComments
            List<String> newVals = new ArrayList<>(trackingInfo);
            boolean loopEditTrackingInfo = true;
            while(loopEditTrackingInfo)
            {
               System.out.println(String.format("1. Status: %s",              GetFieldChangeString(trackingInfo.get(4), newVals.get(4))));
               System.out.println(String.format("2. Current Location: %s",    GetFieldChangeString(trackingInfo.get(3), newVals.get(3))));
               System.out.println(String.format("3. Courier Name: %s",        GetFieldChangeString(trackingInfo.get(1), newVals.get(1))));
               System.out.println(String.format("4. Additional Comments: %s", GetFieldChangeString(trackingInfo.get(6), newVals.get(6))));
               System.out.println("5. Cancel");
               System.out.println("6. Apply Changes");

               // lastUpdateDate should be updated using a trigger.
               List<String> fieldNames = Arrays.asList("Status", "Current Location", "Courier Name", "Additional Comments");
               List<Integer> fieldValCols = Arrays.asList(4, 3, 1, 6);

               int fieldSelection = readChoice();
               if(fieldSelection < 1 || fieldSelection > 6)
               {
                  throw new Exception("Invalid choice.");
               }

               if(fieldSelection == 5)
               {
                  return;
               }
               else if(fieldSelection == 6)
               {
                  // Apply Changes
                  String updateTrackingInfoQuery = "";
                  updateTrackingInfoQuery += "UPDATE TrackingInfo\n";

                  updateTrackingInfoQuery += "SET ";
                  updateTrackingInfoQuery += "courierName = '"          + newVals.get(1) + "', ";
                  updateTrackingInfoQuery += "currentLocation = '"      + newVals.get(3) + "', ";
                  updateTrackingInfoQuery += "status = '"               + newVals.get(4) + "', ";
                  updateTrackingInfoQuery += "additionalComments = '"   + newVals.get(6) + "'\n";

                  updateTrackingInfoQuery += "WHERE trackingID = '"     + trackingInfo.get(0) + "';";

                  esql.executeUpdate(updateTrackingInfoQuery);
                  return;
               }
               else if(fieldSelection == 1)
               {
                  // Edit status, should be only a limited set of values.
                  List<String> allowedValues = Arrays.asList(
                     "Delayed",
                     "In Transit",
                     "Arrived at Facility",
                     "Out for Delivery",
                     "Returned to Sender",
                     "Attempted Delivery",
                     "Ready for Pickup",
                     "Delivered");

                  // Print edit menu
                  for(int i = 0; i < allowedValues.size(); i++)
                  {
                     System.out.println(String.format("%d. %s", i + 1, allowedValues.get(i)));
                  }

                  System.out.println(String.format("%d. Cancel", allowedValues.size() + 1));
                  int newValueIdx = readChoice();
                  if(newValueIdx < 1 || newValueIdx > allowedValues.size() + 1)
                  {
                     return;
                  }

                  if(newValueIdx == allowedValues.size() + 1)
                  {
                     continue;
                  }

                  newVals.set(4, allowedValues.get(newValueIdx - 1));
               }
               else
               {
                  String prompt = String.format("Enter new value for '%s': ", fieldNames.get(fieldSelection - 1));
                  EditStringField(prompt, newVals, fieldValCols.get(fieldSelection - 1));
               }
            }
         }
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
      }
   }

   private static String GetFieldChangeString(String oldVal, String newVal)
   {
      if(oldVal.equals(newVal))
      {
         return String.format("[%s]", oldVal);
      }
      else
      {
         return String.format("[%s => %s]", oldVal, newVal);
      }
   }

   /*
    * Convenience method for editing a simple string field.
    */
   private static void EditStringField(String prompt, List<String> destArray, int arrayIdx)
   {
      boolean loop = true;
      String newVal = readString(prompt);
      destArray.set(arrayIdx, newVal);
   }

   /*
    * Builds the UPDATE query for editing game information.
    */
   private static String BuildEditGameInformationQuery(List<String> colNames, List<String> values, String gameID)
   {
      String updateGameQuery = "";
      updateGameQuery += "UPDATE Catalog \n SET ";
      for(int x = 1; x < colNames.size(); x++)
      {
         if(x != 3)
         {
            updateGameQuery += String.format("%s = '%s' ", colNames.get(x), values.get(x));
         }
         else // price
         {
            updateGameQuery += String.format("%s = %s ", colNames.get(x), values.get(x));
         }

         if(x < colNames.size() - 1)
         {
            updateGameQuery += ", ";
         }
         else
         {
            updateGameQuery += "\n";
         }
      }

      updateGameQuery += "WHERE gameID = '" + gameID + "';";
      return updateGameQuery;
   }

   /*
    * Wrapper function for editing game information. Will also send the update query.
    */
   private static void EditGameInformation(GameRental esql, List<String> curVals, List<String> newVals, String gameId)
   {
      // Now we ask which field(s) the manager wants to update.
      List<String> colNames = Arrays.asList("gameID", "gameName", "genre", "price", "description", "imageURL");
                   
      boolean loopFieldEditorMenu = true;
      while(loopFieldEditorMenu)
      {
         // Print numbered menu
         System.out.println("Choose Field to Edit");
         System.out.println(String.format("%-15s %s", "1. Game Name "  , GetFieldChangeString(curVals.get(1), newVals.get(1))));
         System.out.println(String.format("%-15s %s", "2. Genre "      , GetFieldChangeString(curVals.get(2), newVals.get(2))));
         System.out.println(String.format("%-15s %s", "3. Price "      , GetFieldChangeString(curVals.get(3), newVals.get(3))));
         System.out.println(String.format("%-15s %s", "4. Description ", GetFieldChangeString(curVals.get(4), newVals.get(4))));
         System.out.println(String.format("%-15s %s", "5. Image URL "  , GetFieldChangeString(curVals.get(5), newVals.get(5))));
         System.out.println("6. Cancel");
         System.out.println("7. Apply Edits");

         try
         {
            int editFieldChoice = readChoice();
            if(editFieldChoice < 1 || editFieldChoice > 7)
            {
               throw new Exception("Invalid choice!");
            }

            if(editFieldChoice <= 5 && editFieldChoice != 3) // if it's not the price field (the only number)
            {
               EditStringField(String.format("Enter new value for field %s: ", colNames.get(editFieldChoice)), newVals, editFieldChoice);
            }
            else if (editFieldChoice == 3)
            {
               // This is a number (float) input, so we need to handle it specifically.
               String newVal = readFloatAsString(String.format("Enter new value for field %s: ", colNames.get(editFieldChoice)));
               newVals.set(editFieldChoice, newVal);
            }
            else if(editFieldChoice == 6)
            {
               loopFieldEditorMenu = false;
               return;
            }
            else if(editFieldChoice == 7)
            {
               String updateGameQuery = BuildEditGameInformationQuery(colNames, newVals, gameId);

               System.out.println(updateGameQuery);
               esql.executeUpdate(updateGameQuery);
               loopFieldEditorMenu = false;
               return;
            }
         }
         catch(Exception e)
         {
            System.out.println(e);
            PressEnterToContinue();
         }
      }
   }

   public static void updateCatalog(GameRental esql, String authorizedUser) {

      // Check if current user is a manager, return if not
      String checkIsManagerQuery = "SELECT role FROM Users WHERE login = '" + authorizedUser + "' AND role = 'manager'";
      try
      {
         int numRows = esql.executeQuery(checkIsManagerQuery);
         if(numRows <= 0)
         {
            throw new Exception("User is not a manager! Updating catalog is disallowed.");
         }
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
         return;
      }

      // Select a game to modify (needs gameid primary key)
         // game id (exact match, error if not found)
         // filter by name contains, select with number choice
      
      boolean methodMenu = true;
      while(methodMenu)
      {
         System.out.println("=======================================");
         System.out.println("==      Update Game Information      ==");
         System.out.println("=======================================\n");

         System.out.println("How do you want to find the game?");
         System.out.println("1. Game ID");
         System.out.println("2. Game Title");
         System.out.println("3. Cancel");

         try
         {
            int updateGameMethodChoice = readChoice();
            if(updateGameMethodChoice < 1 || updateGameMethodChoice > 3)
            {
               throw new Exception("Invalid choice!");
            }

            if(updateGameMethodChoice == 1)
            {
               try
               {
                  String gameId = readString("Enter exact Game ID: ");

                  String gameIDSearchQuery = "SELECT * FROM Catalog WHERE gameID = '" + gameId + "';";
                  List<List<String>> result = esql.executeQueryAndReturnResult(gameIDSearchQuery);
                  if(result.size() <= 0)
                  {
                     throw new Exception("Could not find game!");
                  }

                  List<String> currentGameRow = result.get(0); // Since we filter on the unique gameID, there should only be 1 result, if any.
                  List<String> newGameRow = new ArrayList<String>(currentGameRow);
                  EditGameInformation(esql, currentGameRow, newGameRow, gameId);
               }
               catch(Exception e)
               {
                  System.out.println(e);
                  PressEnterToContinue();
               }  
            }
            else if(updateGameMethodChoice == 2)
            {
               // Find game to update via game title contains (use selector list)
               try
               {
                  String gameTitleContainsStr = readString("Enter Game Title (Contains): ");
                  String gameTitleLikeQuery = "SELECT * FROM Catalog WHERE gameName LIKE '%" + gameTitleContainsStr + "%';";
                  List<List<String>> likeGames = esql.executeQueryAndReturnResult(gameTitleLikeQuery);

                  if(likeGames.size() <= 0)
                  {
                     throw new Exception("No games matched your query...");
                  }

                  boolean loopLikeFilter = true;
                  while(loopLikeFilter)
                  {
                     for(int x = 0; x < likeGames.size(); x++)
                     {
                        List<String> row = likeGames.get(x);
                        System.out.println(String.format("%d. %-50s | %-8s", x+1, row.get(1), row.get(0)));
                     }

                     try
                     {
                        System.out.print("Select Game #: ");
                        int gameChoiceIdx = readChoice();
                        if(gameChoiceIdx < 1 || gameChoiceIdx > likeGames.size())
                        {
                           throw new Exception("Choice is not in accepted range!");
                        }

                        List<String> selectedGameRow = likeGames.get(gameChoiceIdx - 1);
                        List<String> newGameRow = new ArrayList<String>(selectedGameRow);
                        String gameId = selectedGameRow.get(0);
                        EditGameInformation(esql, selectedGameRow, newGameRow, gameId);
                        break;
                     }
                     catch(Exception e)
                     {
                        System.out.println(e);
                        PressEnterToContinue();
                     }
                  }
               }
               catch(Exception e)
               {
                  System.out.println(e);
                  PressEnterToContinue();
               }
            }
            else if(updateGameMethodChoice == 3)
            {
               methodMenu = false;
               return;
            }
         }
         catch(Exception e)
         {
            System.out.println(e);
            PressEnterToContinue();
         }
      }
   }

   /*
    * Wrapper function for updating a user's favorite games.
    *
    * packedFavGamesStr: the raw column value in the user's row.
    *
    * newVals: The user's row, so this function can update the row.
    */
   private static void UpdateUserFavoriteGames(GameRental esql, String favGamesPacked, List<String> newVals)
   {
      try
      {
         String[] favGamesArray = favGamesPacked.split(",");
         List<String> favGames = new ArrayList<String>(); // avoid empty string 1 elem array if no fav games

         if(favGamesArray.length == 1 && favGamesArray[0].trim().isEmpty())
         {
            // No favorite games
            System.out.println("No favorite games!");
         }
         else
         {
            for(int i = 0; i < favGamesArray.length; i++)
            {
               favGames.add(favGamesArray[i]);
            }
         }

         while(true)
         {
            System.out.println("-------------------------------------------------------------");
            System.out.println("--                 Edit Favorite Games                     --");
            System.out.println("-------------------------------------------------------------");
            int numGames = favGames.size();
            for(int i = 0; i < numGames; i++)
            {
               System.out.println(String.format("%d. %-64s", i + 1, favGames.get(i)));
            }

            System.out.println(String.format("%d. Add Game", numGames + 1));
            System.out.println(String.format("%d. Remove All Games", numGames + 2));
            System.out.println(String.format("%d. Cancel", numGames + 3));
            System.out.println(String.format("%d. Done (Not Applied)", numGames + 4));

            int favGameChoice = readChoice();
            if(favGameChoice < 1 || favGameChoice > numGames + 4)
            {
               throw new Exception("Invalid favorite game choice");
            }

            // Print menu for operation
            // It's either a fav game slot (edit), or add game
            if(favGameChoice == numGames + 1)
            {
               // Add Game
               String newFavGame = ChooseGameByTitleContains(esql, false);
               if(favGames.contains(newFavGame))
               {
                  System.out.println("This game is already a favorite!");
                  continue;
               }

               favGames.add(newFavGame);
            }
            else if(favGameChoice == numGames + 2) // Remove all games
            {
               while(true)
               {
                  String input = readString("WARNING: You are about to remove ALL your favorite games. Continue? [y/n]: ");
                  if(input.equals("y") || input.equals("Y"))
                  {
                     newVals.set(3, "");
                     break;
                  }
                  else if(input.equals("n") || input.equals("N"))
                  {
                     break;
                  }
                  else
                  {
                     System.out.println("Only enter 'y' or 'n'.");
                  }
               }

               break;
            }
            else if(favGameChoice == numGames + 3) // cancel
            {
               break;
            }
            else if(favGameChoice == numGames + 4) // done (not applied)
            {
               String packedString = "";
               for(int i = 0; i < favGames.size(); i++)
               {
                  packedString += favGames.get(i);
                  if(i < favGames.size() - 1)
                  {
                     packedString += ",";
                  }
               }

               newVals.set(3, packedString);
               break;
            }
            else
            {
               // Edit game
               while(true) // Edit game menu
               {
                  System.out.println("-------------------------------------------------------------");
                  System.out.println(String.format("Editing Favorite Game '%s'", favGames.get(favGameChoice - 1)));
                  System.out.println("1. Change Game");
                  System.out.println("2. Delete Game");
                  System.out.println("3. Cancel");

                  int editChoice = readChoice();
                  if(editChoice < 1 || editChoice > 3)
                  {
                     throw new Exception("Invalid choice for favorite game edit!");
                  }

                  if(editChoice == 1)
                  {
                     // Change Game
                     String favGamesSqlList = "(";
                     for(int i = 0; i < favGames.size(); i++)
                     {
                        favGamesSqlList += "'" + favGames.get(i) + "'";
                        if(i < favGames.size() - 1)
                        {
                           favGamesSqlList += ", ";
                        }
                     }

                     if(favGames.size() <= 0)
                     {
                        favGamesSqlList += "''";
                     }
                     favGamesSqlList += ")";

                     String newGame = ChooseGameByTitleContains(esql, false, favGamesSqlList);
                     if(newGame == null)
                     {
                        break;
                     }

                     favGames.set(favGameChoice - 1, newGame);
                  }
                  else if(editChoice == 2)
                  {
                     // Delete Game
                     favGames.remove(favGameChoice - 1);
                  }
                  else if(editChoice == 3)
                  {
                     break;
                  }

                  break;
               }
            }
         }
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
      }
   }

   /*
    * Overload for ChooseGame function. This doesn't require a sql string, 
    * in case you don't want to use it.
    */
   private static String ChooseGameByTitleContains(GameRental esql, boolean retID)
   {
      return ChooseGameByTitleContains(esql, retID, "('')");
   }

   /*
    * Convenience method to choose a game by title, using a "contains" filter.
    *
    * Exclude string is a sql string, i.e. a list (value1, value2, ...) that filters out the game name using the values
    * in the list. If you don't want to deal with it, use the overloaded function that provides a default value for the exclude str.
    */
   private static String ChooseGameByTitleContains(GameRental esql, boolean retID, String excludeSqlStr)
   {
      while(true)
      {
         try
         {
            // Get string to search by
            System.out.println("-------------------------------------------------------------------");
            String searchStr = readString("Enter Game Title (Contains): ");

            String gamesQuery = "SELECT g.gameID, g.gameName FROM Catalog g WHERE g.gameName LIKE '%" + searchStr + "%' AND g.gameName NOT IN " + excludeSqlStr + ";";
            System.out.println(gamesQuery);
            List<List<String>> gameRows = esql.executeQueryAndReturnResult(gamesQuery);
            int numRows = gameRows.size();
            if(numRows <= 0)
            {
               System.out.println("No games fit your search!");
               PressEnterToContinue();
               continue;
            }

            System.out.println("Edit Game Menu\n-----------------------------");
            for(int i = 0; i < numRows; i++)
            {
               System.out.println(String.format("%d. %-64s", i + 1, gameRows.get(i).get(1)));
            }

            System.out.println(String.format("%d. Cancel", numRows + 1));

            int userChoice = readChoice();
            if(userChoice < 1 || userChoice > numRows + 1)
            {
               throw new Exception("Invalid choice for game!");
            }

            if(userChoice == numRows + 1)
            {
               return null;
            }

            List<String> chosenGameRow = gameRows.get(userChoice - 1);
            return (retID ? chosenGameRow.get(0) : chosenGameRow.get(1));
         }
         catch(Exception e)
         {
            System.out.println(e);
            PressEnterToContinue();
            return null;
         }
      }
   }

   public static void updateUser(GameRental esql, String authorizedUser) {

      // Check if current user is a manager, return if not
      String checkIsManagerQuery = "SELECT role FROM Users WHERE login = '" + authorizedUser + "' AND role = 'manager'";
      try
      {
         int numRows = esql.executeQuery(checkIsManagerQuery);
         if(numRows <= 0)
         {
            throw new Exception("User is not a manager! Updating catalog is disallowed.");
         }

         // Now we actually start the process.
         // This is just like Update Game information, except for users.
         // Either select exact user login id, or choose login with like

         boolean loopUpdateUser = true;
         while(loopUpdateUser)
         {
            System.out.println("=================================");
            System.out.println("==        Update User          ==");
            System.out.println("=================================");

            try
            {
               String searchLogin = readString("Enter user login to edit (contains): ");
               String searchUserQuery = "SELECT * FROM Users WHERE login LIKE '%" + searchLogin + "%';";
               List<List<String>> matchingUsers = esql.executeQueryAndReturnResult(searchUserQuery);

               for(int i = 0; i < matchingUsers.size(); i++)
               {
                  System.out.println(String.format("%d. %-25s", i + 1, matchingUsers.get(i).get(0)));
               }

               System.out.println(String.format("%d. Cancel", matchingUsers.size() + 1));

               try
               {
                  int choice = readChoice();
                  if(choice < 1 || choice > matchingUsers.size() + 1)
                  {
                     throw new Exception("Choice is invalid!");
                  }

                  if(choice == matchingUsers.size() + 1)
                  {
                     loopUpdateUser = false;
                     break;
                  }

                  List<String> userRow = matchingUsers.get(choice - 1);
                  List<String> newVals = new ArrayList<String>(userRow);
                  String userLogin = userRow.get(0);
                  List<String> colDisplayNames = Arrays.asList("Password", "Role", "Favorite Games", "Phone Num");
                  List<String> colDataNames = Arrays.asList("password", "role", "favGames", "phoneNum");

                  boolean loopEditMenu = true;
                  while(loopEditMenu)
                  {
                     System.out.println("-------------------------------------------------------------");
                     System.out.println("Editing User: " + userLogin);
                     System.out.println("-------------------------------------------------------------");
                     // Print field choooser menu, don't edit login (primary key)
                     for(int i = 0; i < colDisplayNames.size(); i++)
                     {
                        System.out.println(String.format("%d. %-25s %s", i + 1, colDisplayNames.get(i), GetFieldChangeString(userRow.get(i + 1).trim(), newVals.get(i + 1).trim())));
                     }

                     System.out.println(String.format("%d. Cancel", colDisplayNames.size() + 1));
                     System.out.println(String.format("%d. Apply Changes", colDisplayNames.size() + 2));

                     int fieldChoice = readChoice();
                     if(fieldChoice < 1 || fieldChoice > colDisplayNames.size() + 2)
                     {
                        throw new Exception("Invalid Choice.");
                     }

                     if(fieldChoice == colDisplayNames.size() + 1)
                     {
                        return;
                     }
                     else if (fieldChoice == 2)
                     {
                        // Update role
                        List<String> allowedRoles = Arrays.asList("customer", "employee", "manager");
                        for(int i = 0; i < allowedRoles.size(); i++)
                        {
                           System.out.println(String.format("%d. %s", i + 1, allowedRoles.get(i)));
                        }

                        System.out.println(String.format("%d. Cancel", allowedRoles.size() + 1));
                        int newValueIdx = readChoice();
                        if(newValueIdx < 1 || newValueIdx > allowedRoles.size() + 1)
                        {
                           return;
                        }

                        if(newValueIdx == allowedRoles.size() + 1)
                        {
                           continue;
                        }

                        newVals.set(2, allowedRoles.get(newValueIdx - 1));
                     }
                     else if (fieldChoice == 3)
                     {
                        // Edit fav games
                        String favGamesPacked = newVals.get(3);
                        UpdateUserFavoriteGames(esql, favGamesPacked, newVals);
                     }
                     else if (fieldChoice == colDisplayNames.size() + 2)
                     {
                        // Built query string and execute
                        String updateQuery = "UPDATE Users\nSET ";
                        for(int x = 0; x < colDataNames.size(); x++)
                        {
                           updateQuery += String.format("%s = '%s'", colDataNames.get(x), newVals.get(x + 1));
                           if(x < colDataNames.size() - 1)
                           {
                              updateQuery += ", ";
                           }
                        }
                        updateQuery += "\nWHERE login = '" + userRow.get(0) + "';";
                        esql.executeUpdate(updateQuery);
                        return;
                     }
                     else
                     {
                        // Change new values
                        String newVal = readString("Enter new value: ");
                        newVals.set(fieldChoice, newVal);
                     }
                  }
               }
               catch(Exception e)
               {
                  System.out.println(e);
                  PressEnterToContinue();
               }
               
            }
            catch(Exception e)
            {
               System.out.println(e);
               PressEnterToContinue();
            }

         }
      }
      catch(Exception e)
      {
         System.out.println(e);
         PressEnterToContinue();
         return;
      }
   }

}//end GameRental

