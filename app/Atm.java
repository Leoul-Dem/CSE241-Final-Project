import java.util.*;
import java.sql.*;

public interface Atm{  

    Scanner scan = new Scanner(System.in);

    public static void displayMenu(){
        System.out.println(" [1] Withdraw");
        System.out.println(" [2] Check Balance");
        System.out.println(" [3] Exit");
        System.out.print("Choose an option: ");
    }

    /**
     * Withdraws funds from the user's account.
     * 
     * This method allows the user to withdraw funds from either their checking or savings account.
     * It first displays the current balance, then prompts the user to choose the account type and 
     * the amount to withdraw. The method checks for valid input and sufficient funds before 
     * performing the withdrawal. It updates the account balance and records the transaction in 
     * the database.
     */
    public static void withdraw_funds(){
        helpfulMethods.viewBalance();
        while(true){
            System.out.println(" [1] Checking Account");
            System.out.println(" [2] Savings Account");
            System.out.print("Choose account for withdrawal: ");
            int choice = scan.nextInt();
            if(choice != 1 && choice != 2){
                System.out.println(" Invalid input.");
                System.out.println(" Please try again.");
                continue;
            }
            System.out.print(" Enter amount to withdraw: ");
            int amount = scan.nextInt();
            if(amount < 0){
                System.out.println(" Invalid input.");
                System.out.println(" Please try again.");
                continue;
            }
            float balance = helpfulMethods.viewBalance(choice);
            if(balance < amount){
                System.out.println(" Insufficient funds.");
                System.out.println(" Please try again.");
                continue;
            }
            String sql;
            String sql2;
            String sql3;
            long transaction_id = helpfulMethods.valid_transaction_id();
            if(choice == 1){
                sql = "UPDATE checking SET balance = balance - ? WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
                sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
                sql3 = "INSERT INTO checking_transactions VALUES(?, ?, ?, ?)";
            }else{
                sql = "UPDATE savings SET balance = balance - ? WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
                sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
                sql3 = "INSERT INTO savings_transactions VALUES(?, ?, ?, ?)";
            }

            try{
                long acct_num = helpfulMethods.getAcctNum(choice);
                PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
                stmt.setInt(1, amount);
                stmt.setInt(2, Prog.user_ssn);
                stmt.executeUpdate();
                stmt = Prog.db.conn.prepareStatement(sql2);
                stmt.setLong(1, transaction_id);
                stmt.setString(2, "Cash");
                stmt.setLong(3, acct_num);
                stmt.setInt(4, 0-amount);
                stmt.executeUpdate();
                stmt = Prog.db.conn.prepareStatement(sql3); 
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, 0);
                stmt.setLong(3, acct_num);
                stmt.setInt(4, 0-amount);
                stmt.executeUpdate();
                
                Prog.db.conn.commit();
                System.out.println("Withdrawal successful.");
                break;
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        System.out.println("Withdraw Funds");
    }

    /**
     * This method checks and prints the balance of both checking and savings accounts
     * for the user identified by their SSN (Social Security Number).
     */
    public static void checkBalance(){
        String sql = "SELECT balance FROM checking WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        String sql2 = "SELECT balance FROM savings WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";

        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Checking balance: " + rs.getFloat("balance"));
            }else{
                System.out.println("Checking account not found.");
            }

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setInt(1, Prog.user_ssn);
            rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Savings balance: " + rs.getFloat("balance"));
            }else{
                System.out.println("Savings account not found.");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

     
    }

    /**
     * This method represents the main loop of the ATM system.
     * It continuously displays a menu and processes user input until the user chooses to exit.
     * 
     * The menu options are:
     * 1. Withdraw funds
     * 2. Check balance
     * 3. Exit
     * 
     * If the user enters an invalid choice, an error message is displayed and the menu is shown again.
     */
    public static void atmLoop(){
        int choice = 0;
        while(choice != 3){
            displayMenu();
            choice = scan.nextInt();
            scan.nextLine();
            switch(choice){
                case 1:
                    withdraw_funds();
                    break;
                case 2:
                    checkBalance();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    System.out.println("Please try again.");
                    break;
            }
        }
    }
}