import java.util.*;
import java.sql.*;

public interface Purchase {
    Scanner scan = new Scanner(System.in);

    public static void displayMenu(){
        System.out.println("Which card would you like to use?");
        System.out.println(" [1] Debit Card");
        System.out.println(" [2] Credit Card");
        System.out.println(" [3] Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * This method handles the purchase loop for a user.
     * It displays a menu and processes the user's choice.
     * The user can choose to pay with a debit card, credit card, or exit the menu.
     * If an invalid choice is made, the user is prompted to try again.
     * The loop continues until the user chooses to exit.
     */
    public static void purchaseLoop(){
        int choice = 0;
        while(choice != 3){
            displayMenu();
            choice = scan.nextInt();
            scan.nextLine();
            if(choice == 1){
                debitCard();
                break;
            }else if(choice == 2){
                creditCard();
                break;
            }else if(choice == 3){
                System.out.println("Exiting purchase menu.");
            }else{
                System.out.println("Invalid choice. Please try again.");
            }
        }
        
    }

    /**
     * Processes a purchase using a debit card.
     * 
     * The method performs the following steps:
     * 1. Prompts the user to enter the price of the purchase.
     * 2. Retrieves the current balance of the user's checking account.
     * 3. Checks if the balance is sufficient to cover the purchase price.
     * 4. If sufficient, updates the checking account balance by deducting the purchase price.
     * 5. Records the transaction in the transactions table.
     * 6. Records the transaction in the checking_transactions table.
     * 7. Commits the changes to the database.
     * 8. Prints a success message if the purchase is successful.
     * 
     * If there are insufficient funds, it prints an error message and terminates the process.
     */
    public static void debitCard(){
        System.out.println("Debit Card");
        System.out.print("Enter the price of the purchase: ");
        float price = scan.nextFloat();

        float balance = helpfulMethods.viewBalance(2);

        if (balance < price){
            System.out.println("Insufficient funds.");
            return;
        }

        String sql = "UPDATE checking SET balance = balance - ? WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        String sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
        String sql3 = "INSERT INTO checking_transactions VALUES(?, ?, ?, ?)";
        long transaction_id = helpfulMethods.valid_transaction_id();

        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setFloat(1, price);
            stmt.setInt(2, Prog.user_ssn);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setLong(1, transaction_id);
            stmt.setLong(2, helpfulMethods.getAcctNum(1));
            stmt.setLong(3, -1);
            stmt.setFloat(4, price);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setLong(1, transaction_id);
            stmt.setLong(2, -1);
            stmt.setLong(3, helpfulMethods.getAcctNum(1));
            stmt.setFloat(4, -price);
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Purchase successful.");

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Processes a credit card purchase for the current user.
     * 
     * The method performs the following steps:
     * 1. Retrieves the user's credit card information from the database using their SSN.
     * 2. Prompts the user to enter the price of the purchase and validates the input.
     * 3. Checks if the user's credit card has sufficient funds to cover the purchase.
     * 4. If sufficient funds are available, updates the credit card balance and records the transaction.
     * 5. Commits the transaction to the database.
     * 
     * If the user does not have a credit card or if there are insufficient funds, the method prints an appropriate
     * message and terminates.
     */
    public static void creditCard(){
       float price = 0;
       float balance = 0;

        String sql = "SELECT * from credit_card WHERE acct_num = (SELECT acct_num FROM has_cc WHERE ssn = ?)";
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                while(true){
                    System.out.println("Credit Card");
                    System.out.print("Enter the price of the purchase: ");
                    price = scan.nextFloat();
                    if(price <= 0){
                        System.out.println("Invalid price. Please try again.");
                        continue;
                    }else{
                        break;
                    }
                }
                balance = rs.getFloat("credit_limit") - rs.getFloat("curr_bal");

                if (balance < price){
                    System.out.println("Insufficient funds.");
                    return;
                }

                
            }else{
                System.out.println("No credit card found.");
                return;
            }
            String sql2 = "UPDATE credit_card SET curr_bal = curr_bal + ? WHERE acct_num = (SELECT acct_num FROM has_cc WHERE ssn = ?)";
            String sql3 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
            String sql4 = "INSERT INTO cc_transactions VALUES(?, ?, ?, ?)";
            long transaction_id = helpfulMethods.valid_transaction_id();
            
            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setFloat(1, price);
            stmt.setInt(2, Prog.user_ssn);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setLong(1, transaction_id);
            stmt.setLong(2, helpfulMethods.getCCAcctNum());
            stmt.setLong(3, -1);
            stmt.setFloat(4, price);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql4);
            stmt.setLong(1, transaction_id);
            stmt.setLong(2, helpfulMethods.getCCAcctNum());
            stmt.setLong(3, -1);
            stmt.setFloat(4, price);
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Purchase successful.");
            
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    
}