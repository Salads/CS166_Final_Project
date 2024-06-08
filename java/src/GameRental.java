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
 import java.util.SortedSet;
 import java.util.TreeSet;
 import java.lang.Math;
 import java.util.Arrays;
 
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
                    case 1: viewProfile(esql); break;
                    case 2: updateProfile(esql); break;
                    case 3: viewCatalog(esql); break;
                    case 4: placeOrder(esql); break;
                    case 5: viewAllOrders(esql); break;
                    case 6: viewRecentOrders(esql); break;
                    case 7: viewOrderInfo(esql); break;
                    case 8: viewTrackingInfo(esql); break;
                    case 9: updateTrackingInfo(esql); break;
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
       String username;
       String password;
       String phone_number;
       int made = 0;
       while (made == 0){
          try{
             System.out.println("Enter Username: ");
             username = in.readLine();
 
             try{
                String checkUserQuery = "SELECT u.login FROM Users AS u WHERE u.login = '" + username + "'";
                int userRowCount = esql.executeQuery(checkUserQuery);
 
                if(userRowCount == 0){
                   try{
                      System.out.println("Enter Password: ");
                      password = in.readLine();
                      try{
                         System.out.println("Enter Phone Number: ");
                         phone_number = in.readLine();
                         try{
                            String insertUserQuery = "INSERT INTO Users(login, password, phoneNum) VALUES('" + username + "', '" + password + "', '" + phone_number + "')";
                            esql.executeUpdate(insertUserQuery);
                            made = 1;
                         } catch(Exception e) {
                            System.out.println(e);
                            System.out.println(e);
                         }
                      } catch (Exception e) {
                         System.out.println("Error");
                         break;
                      }
                   } catch (Exception e) {
                      System.out.println("Error");
                      break;
                   }
                } else {
                   System.out.println("Sorry, that username is taken. Please try again.");
                   continue;
                }
             } catch (Exception e){
                System.out.println("Query Error");
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
       String username;
       String password;
       try{
          System.out.println("Enter Username: ");
          username = in.readLine();
 
          try{
             System.out.println("Enter Password: ");
             password = in.readLine();
             try{
                String userQuery = "SELECT * " + 
                                   "FROM Users AS u " + 
                                   "WHERE u.login = '" + username + "' " + 
                                     "AND u.password = '" + password + "' ";
                int rowCount = esql.executeQuery(userQuery);
                if(rowCount > 0){
                   return username;
                } else {
                   return null;
                }
          } catch (Exception e){
             System.out.println("Query Error");
          }
          } catch (Exception e){
             System.out.println("Input Error");
          }
          
          
       } catch (Exception e){
          System.out.println("Input Error");
       }
      
       return null;
    }//end
 
 // Rest of the functions definition go in here
 
    public static void viewProfile(GameRental esql) {}
    public static void updateProfile(GameRental esql) {}
 
    private static void PressEnterToContinue()
    { 
       System.out.println("Press Enter key to continue...");
       try{
          in.read();
       }  
       catch(Exception e) {}  
    }
 
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
    public static void placeOrder(GameRental esql) {}
    public static void viewAllOrders(GameRental esql) {}
    public static void viewRecentOrders(GameRental esql) {}
    public static void viewOrderInfo(GameRental esql) {}
    public static void viewTrackingInfo(GameRental esql) {}
    public static void updateTrackingInfo(GameRental esql) {}
 
    private static String GetFieldChangeString(String oldVal, String newVal)
    {
       if(oldVal == newVal)
       {
          return String.format("[%s]", oldVal);
       }
       else
       {
          return String.format("[%s => %s]", oldVal, newVal);
       }
    }
 
    private static void EditStringField(String prompt, List<String> destArray, int arrayIdx)
    {
       boolean loop = true;
       String newVal = readString(prompt);
       destArray.set(arrayIdx, newVal);
    }
 
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
                      System.out.println("Chosen User: " + userLogin);
                      // Print field choooser menu, don't edit login (primary key)
                      for(int i = 0; i < colDisplayNames.size(); i++)
                      {
                         System.out.println(String.format("%d. %-50s [%s => %s]", i + 1, colDisplayNames.get(i), userRow.get(i + 1), newVals.get(i + 1)));
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
 
 