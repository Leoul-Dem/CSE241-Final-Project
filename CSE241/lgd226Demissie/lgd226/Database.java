import java.sql.*;
import java.util.*;

public class Database{
    Connection conn;
    

    public Database(){}
    
    /**
     * Establishes a connection to the database using user-provided credentials.
     * Prompts the user to enter their username and password up to three times if the connection fails.
     * 
     * @return a Database object with an established connection if successful, otherwise null.
     */
    public static Database getDatabase(){
        Scanner scan = new Scanner(System.in);
        Database return_val = new Database();
        String dbUrl = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";
        String username = "";
        String password = "";
        int counter = 0;
        while(counter < 3){
            System.out.print("Enter your username: ");
            username = scan.nextLine();
            System.out.print("Enter your password: ");
            password = scan.nextLine();
            try{
                return_val.conn = DriverManager.getConnection(dbUrl, username, password);
                return_val.conn.setAutoCommit(false);
                System.out.println("Connection to database established.");
                scan.close();
                return return_val;
            }catch(SQLException e){
                e.printStackTrace();
                counter++;
            }
            
        }
        scan.close();
        return null;
    }

    /**
     * Disconnects from the database if a connection exists.
     * 
     * @return true if the connection was successfully closed, false otherwise.
     */
    public boolean disconnect(){
        if(conn != null){
            try{
                conn.close();
                System.out.println("Connection to database closed.");
                return true;
            }catch(SQLException e){
                e.printStackTrace();
                return false;
            }
        }
        System.out.println("Connection to database not found.");
        return false;
    }


}