import java.sql.*;
import java.sql.Date;
import java.util.*;

public interface Asset {
    Scanner scan = new Scanner(System.in);
    
    public static void displayMenu2(){
        System.out.println(" [1] Deposit Check");
        System.out.println(" [2] Transfer Funds");
        System.out.println(" [3] Debit Card");
        System.out.println(" [4] Open a New Account");
        System.out.println(" [5] Exit");
    }
    
    public static void asset_services(){
        int choice = 0;
        while(choice != 5){
            displayMenu2();
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            switch(choice){
                case 1:
                    deposit_check();
                    break;
                case 2:
                    transfer_funds();
                    break;
                case 3:
                    debit_card();
                    break;
                case 4:
                    open_new_account();
                    break;
                case 5:
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }
    
    /**
     * This method allows the user to deposit money into either their checking or savings account.
     * The user is prompted to choose the account type and the amount to deposit.
     * The method validates the input and updates the corresponding account balance in the database.
     */
    public static void deposit_check(){
        int choice = 0;
        int amount = 0;

        while(choice != 3){
            helpfulMethods.viewBalance();
            System.out.println(" [1] Checking Account");
            System.out.println(" [2] Savings Account");
            System.out.print("Choose account for deposit: ");
            choice = scan.nextInt();
            if(choice != 1 && choice != 2){
                System.out.println(" Invalid input.");
                System.out.println(" Please try again.");
                continue;
            }
            System.out.println(" Enter amount to deposit: ");
            amount = scan.nextInt();
            if(amount < 0){
                System.out.println(" Invalid input.");
                System.out.println(" Please try again.");
                continue;
            }
            String sql;
            if(choice == 1){
                sql = "UPDATE checking SET balance = balance + ? WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
            }else{
                sql = "UPDATE savings SET balance = balance + ? WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
            }
            try{
                PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
                stmt.setInt(1, amount);
                stmt.setInt(2, Prog.user_ssn);
                stmt.executeUpdate();

                Prog.db.conn.commit();
                System.out.println("Deposit successful.");
                choice = 3;
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Transfers funds between the user's savings and checking accounts.
     * 
     * The method ensures that the user has sufficient funds in the source account before proceeding
     * with the transfer. If the transfer is successful, a confirmation message is printed.
     */
    public static void transfer_funds(){
        int acct_from = 0;
        int acct_to = 0;
        int amount = 0;
        long transaction_id = helpfulMethods.valid_transaction_id();
        while(true){
            System.out.println(" [1] Savings Account");
            System.out.println(" [2] Checking Account");
            System.out.print("Choose account to transfer from: ");
            acct_from = scan.nextInt();
            if(acct_from != 1 && acct_from != 2){
                System.out.println("Invalid input.");
                System.out.println("Please try again.");
                continue;
            }
            break;
        }
        acct_to = acct_from == 1 ? 2 : 1;
        while(true){
            System.out.print("Transfer amount: ");
            amount = scan.nextInt();
            if(amount < 0 && amount > helpfulMethods.viewBalance(acct_from)){
                System.out.println("Insufficient funds.");
                System.out.println("Please try again.");
                continue;
            }
            break;
        }

        String sql = "UPDATE ? SET balance = balance ? ? WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        String sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
        String sql3 = "INSERT INTO ?_transactions VALUES(?, ?, ?, ?)";

        try{

            // Deduct amount from the source account
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setString(1, acct_from == 1 ? "savings" : "checking");
            stmt.setString(2, "-");
            stmt.setInt(3, amount);
            stmt.setInt(4, Prog.user_ssn);
            stmt.executeUpdate();

            // Add amount to the destination account
            stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setString(1, acct_to == 1 ? "savings" : "checking");
            stmt.setString(2, "+");
            stmt.setInt(3, amount);
            stmt.setInt(4, Prog.user_ssn);
            stmt.executeUpdate();

            // Insert transaction into transactions table
            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setLong(1, transaction_id);
            stmt.setLong(2, helpfulMethods.getAcctNum(acct_from == 1 ? 2 : 1));
            stmt.setLong(3, helpfulMethods.getAcctNum(acct_from == 1 ? 1 : 2));
            stmt.setInt(4, amount);
            stmt.executeUpdate();

            // Insert negative transaction into the source account's transactions table
            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setString(1, (acct_from == 1 ? "savings" : "checking"));
            stmt.setLong(2, transaction_id);
            stmt.setLong(3, helpfulMethods.getAcctNum(acct_from == 1 ? 2 : 1));
            stmt.setLong(4, helpfulMethods.getAcctNum(acct_from == 1 ? 1 : 2));
            stmt.setInt(5, 0 - amount);
            stmt.executeUpdate();

            // Insert positive transaction into the destination account's transactions table
            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setString(1, (acct_from == 2 ? "savings" : "checking"));
            stmt.setLong(2, transaction_id);
            stmt.setLong(3, helpfulMethods.getAcctNum(acct_from == 1 ? 1 : 2));
            stmt.setLong(4, helpfulMethods.getAcctNum(acct_from == 1 ? 2 : 1));
            stmt.setInt(5,  amount);
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Transfer successful.");
        }catch(SQLException e){
            e.printStackTrace();
        }
     


        System.out.println("Transfer Funds");
    }
    
    /**
     * This method provides a menu for managing debit card operations.
     * The user can choose to display card information, replace the debit card, or exit the menu.
     * The method will continue to prompt the user until they choose to exit.
     * 
     * Menu options:
     *  [1] Display Card Information - Calls the display_card_info() method.
     *  [2] Replace Debit Card - Calls the replace_debit_card() method.
     *  [3] Exit - Exits the menu.
     * 
     * If an invalid choice is entered, an error message is displayed.
     */
    public static void debit_card(){
        int choice = 0;
        boolean quit = false;
        while (!quit) {
            System.out.println(" [1] Display Card Information");
            System.out.println(" [2] Replace Debit Card");
            System.out.println(" [3] Exit");
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            switch (choice){
                case 1:
                    display_card_info();
                    return;
                case 2:
                    replace_debit_card();
                    return;
                case 3:
                    quit = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            } 
        }
        System.out.println("Obtain a New Debit Card");
    }

    /**
     * Displays the debit card information (card number and expiration date) for the current user.
     * The user's SSN is used to query the database for the associated debit card details.
     * If a debit card is found, its information is printed to the console.
     * If no debit card is found, a message indicating this is printed.
     */
    public static void display_card_info(){
        String sql = "SELECT card_num, exp_date FROM DEBIT_CARD WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Card Number: " + rs.getLong("card_num"));
                System.out.println("Expiration Date: " + rs.getString("exp_date"));
            }else{
                System.out.println("No debit card found.");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Replaces the debit card information in the database for the current user.
     */
    public static void replace_debit_card(){
        String sql = "UPDATE DEBIT_CARD SET card_num = ?, exp_date = ? WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            long card_num = helpfulMethods.valid_card_num();
            Date exp_date = helpfulMethods.valid_Date();
            stmt.setLong(1, card_num);
            stmt.setDate(2, exp_date);
            stmt.setInt(3, Prog.user_ssn);
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Debit card replaced.");
            display_card_info();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Opens a new account by collecting user information and inserting it into the database.
     * 
     * The method ensures that:
     * - First and last names are alphabetic and up to 8 characters long.
     * - SSN is a 10-digit number and does not already exist in the database.
     * - Initial deposit amount is at least $100.
     * - Checking and savings accounts have different routing numbers.
     * 
     * If any validation fails, the user is prompted to re-enter the information. 
     */
    public static void open_new_account(){
        String first_name;
        while(true){
            System.out.print(" Enter First Name: ");
            first_name = scan.nextLine();
            String regex = "^[a-zA-Z]{1,8}$";
            if(!first_name.matches(regex)){
                System.out.println("Invalid input.");
                System.out.println("Please try again.");
                continue;
            }
            break;
        }
        String last_name;
        while(true){
            System.out.print(" Enter Last Name: ");
            last_name = scan.nextLine();
            String regex = "^[a-zA-Z]{1,8}$";
            if(!first_name.matches(regex)){
                System.out.println("Invalid input.");
                System.out.println("Please try again.");
                continue;
            }
            break;
        }
        int ssn;
        while(true){
            System.out.print(" Enter your SSN: ");
            ssn = scan.nextInt();
            scan.nextLine();
            String regex = "^\\d{10}$";
            if(!String.valueOf(ssn).matches(regex) || helpfulMethods.ssnExists(ssn)){
                System.out.println("Invalid input or account already exists.");
                System.out.println("Please try again.");
                continue;
            }
            break;
        }
        scan.nextLine();
        int deposit;
        while(true){
            System.out.print(" Enter deposit amount (at least $100): ");
            deposit = scan.nextInt();
            if(deposit < 100){
                System.out.println("Invalid amount.");
                System.out.println("Please try again.");
                continue;
            }
            break;
        }
        int acct_num = helpfulMethods.valid_acct_num();
        int checking_route_num = helpfulMethods.valid_routing_num();
        int savings_route_num;
        while(true){
            savings_route_num = helpfulMethods.valid_routing_num();
            if(savings_route_num != checking_route_num){
                break;
            }
        }       
        String sql = "INSERT INTO customer VALUES(?, ?)";
        String sql2 = "INSERT INTO account VALUES(?)";
        String sql3 = "INSERT INTO checking VALUES(?, ?, ?)";
        String sql4 = "INSERT INTO savings VALUES(?, ?, ?, ?, ?)";
        String sql5 = "INSERT INTO owning VALUES(?, ?, ?)";     
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setString(1, first_name + " " + last_name);
            stmt.setInt(2, ssn);
            stmt.executeUpdate();     

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setInt(1, acct_num);
            stmt.executeUpdate();       

            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setInt(1, acct_num);
            stmt.setInt(2, checking_route_num);
            stmt.setInt(3, 0);
            stmt.executeUpdate(); 

            stmt = Prog.db.conn.prepareStatement(sql4);
            stmt.setInt(1, acct_num);
            stmt.setInt(2, savings_route_num);
            stmt.setInt(3, 4);
            stmt.setInt(4, 100);
            stmt.setInt(5, deposit);
            stmt.executeUpdate(); 

            stmt = Prog.db.conn.prepareStatement(sql5);
            stmt.setInt(1, ssn);
            stmt.setString(2, first_name + " " + last_name);
            stmt.setInt(3, acct_num);
            stmt.executeUpdate();  

            Prog.db.conn.commit();
            System.out.println("Account created.");     
            helpfulMethods.viewBalance();

        }catch(SQLException e){
            e.printStackTrace();
        }       

    }
    
}
