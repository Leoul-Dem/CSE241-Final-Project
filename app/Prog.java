import java.util.*;
import java.sql.*;

public class Prog extends Database implements Atm{

    // Databse created outside of methods so I don't have to pass it as a parameter
    public static Database db = getDatabase();
    public static int user_ssn;
    private static Scanner scan = new Scanner(System.in);

    public static void main(String [] args){
        if(db == null){
            System.out.println("You have exceeded the number of attempts.");
            System.out.println("Failed to connect to database.");
            System.out.println("Exiting program.");
            System.exit(0);
        }
         // Log in as a customer
        user_ssn = customerLogIn();
        if(user_ssn == -1){
            System.out.println("You have exceeded the number of attempts.");
            System.out.println("Exiting program.");
            System.exit(0);
        }

        boolean exit = false;
        while(!exit){
            // ATM or Online
            int choice = atmOrOnline();

            if(choice == 1){
                // ATM
                Atm.atmLoop();
            }else if(choice == 2){
                // Online
                Online_mobile.online_mobileLoop();
            } else if(choice == 3){
                // Make a purchase
                Purchase.purchaseLoop();
            }else{
                System.out.println("Exiting program.");
                exit = true;
            }  
        }
            
        scan.close();
        db.disconnect();
    }

    /**
     * Prompts the user to log in by entering their full name and the last 4 digits of their SSN.
     * The user has up to 3 attempts to enter valid credentials.
     * 
     * @return the SSN of the customer if login is successful, or -1 if login fails after 3 attempts.
     * 
     * The method performs the following steps:
     * 1. Prompts the user to enter their full name and validates the input.
     * 2. Prompts the user to enter the last 4 digits of their SSN and validates the input.
     * 3. Checks the entered credentials against the database.
     * 4. If the credentials match, the method returns the SSN of the customer.
     * 5. If the credentials do not match, the user is prompted to try again.
     * 6. After 3 unsuccessful attempts, the method returns -1.
     */
    public static int customerLogIn(){
        int return_val = -1;
        int attempts = 0;
        String name;    
        String last_4_ssn;
        while(attempts < 3){
            System.out.print("Enter your full name (first last): ");
            name = scan.nextLine();
            if(name.split(" ").length != 2){
                System.out.println("Invalid input.");
                System.out.println("Please try again.");
                continue;
            }
            name = capitalizeWords(name);
            System.out.print("Enter the last 4 digits of your SSN: ");
            last_4_ssn = scan.nextLine();
            if(last_4_ssn.length() != 4){
                System.out.println("Invalid input.");
                System.out.println("Please try again.");
                continue;
            }

            String sql = "SELECT name, ssn FROM Customer WHERE name = ? and SUBSTR(ssn, -4) = ?";
            try(PreparedStatement stmt = db.conn.prepareStatement(sql)){
                stmt.setString(1, name);
                stmt.setInt(2, Integer.parseInt(last_4_ssn));
                ResultSet rs = stmt.executeQuery();
                if(rs.next()){
                    System.out.println("Welcome " + name + "!");
                    return_val = rs.getInt("ssn");
                    break;
                }else{
                    System.out.println("Name not found in database.");
                    System.out.println("Please try again.");
                }
                
            }catch(SQLException e){
                e.printStackTrace();
            }
            attempts++;
        }

        return return_val;
    }

    /**
     * Capitalizes the first letter of each word in the given string.
     * Words are assumed to be separated by spaces.
     *
     * @param str the input string to be processed
     * @return a new string with the first letter of each word capitalized
     */
    public static String capitalizeWords(String str) {
        String[] words = str.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        return String.join(" ", words);
    }

    /**
     * Prompts the user to choose between using the ATM, Online services, making a purchase, or exiting.
     * Displays a menu with four options and reads the user's choice.
     * If the user enters an invalid choice, the method will prompt again until a valid choice is entered.
     *
     * @return the user's choice as an integer:
     *         1 for ATM,
     *         2 for Online,
     *         3 for Make a purchase,
     *         4 for Exit.
     */
    public static int atmOrOnline(){
        System.out.println("Would you like to use the ATM or Online?");
        System.out.println(" [1] ATM");
        System.out.println(" [2] Online");
        System.out.println(" [3] Make a purchase");
        System.out.println(" [4] Exit");
        System.out.print("Enter your choice: ");
        int choice = scan.nextInt();
        scan.nextLine();
        if(choice != 1 && choice != 2 && choice != 3 && choice != 4){
            System.out.println("Invalid choice. Please try again.");
            return atmOrOnline();
        }
        return choice;
    }
}