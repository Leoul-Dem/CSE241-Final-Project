import java.util.*;
import java.sql.*;
import java.sql.Date;

public interface Credit_card {
    Scanner scan = new Scanner(System.in);

    public static void display_menu(){
        System.out.println(" [1] Make a Payment on a credit card");
        System.out.println(" [2] Replace a Lost or Stolen Credit Card");
        System.out.println(" [3] Return to Main Menu");
    }

    public static void credit_card_services(){
        boolean good = helpfulMethods.hasCC();
        if(!good){
            System.out.println("You do not have a credit card.");
            while(true){
                System.out.println("Would you like to apply for one? (y/n)");
                String choice = scan.nextLine();
                if(choice.equals("n")){
                    return;
                }else if(choice.equals("y")){
                    break;
                }else{
                    System.out.println("Invalid choice. Please try again.");
                }
            }
            apply_for_cc();
        }
        int choice = 0;
        while(choice != 4){
            display_menu();
            System.out.print(" Enter your choice: ");
            choice = scan.nextInt();
            switch(choice){
                case 1:
                    make_payment();
                    break;
                case 2:
                    replace_cc();
                    break;
                case 3:
                    break;
                default:
                    System.out.println(" Invalid choice.");
                    System.out.println(" Please try again.");
                    break;
            }
        }
    }

    /**
     * This method facilitates making a payment on a credit card. It retrieves the current balance 
     * and minimum monthly payment for the user's credit card and provides options to pay from 
     * either a savings or checking account. The user can choose to pay the minimum payment, 
     * the full balance, or a custom amount.
     * 
     * The method interacts with the database to fetch credit card details and uses helper methods 
     * to display account balances.
     */
    public static void make_payment(){
        System.out.println("Make a payment on a credit card");
        String sql = "SELECT * FROM credit_card WHERE acct_num = (SELECT acct_num FROM has_cc WHERE ssn = ?)";
        float mon_pymnt = 0;
        float total = 0;
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                System.out.println("Current Balance: " + rs.getFloat("curr_bal"));
                System.out.println("Minimum Payment: " + rs.getFloat("min_monthly_pymnt"));

                mon_pymnt = rs.getFloat("min_monthly_pymnt");
                total = rs.getFloat("curr_bal");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        int choice = 0; 
        while(choice != 3){
            helpfulMethods.viewBalance();
            System.out.println(" [1] Savings Account");
            System.out.println(" [2] Checking Account");
            System.out.println(" [3] Return to previous menu");
            System.out.print(" Enter your choice: ");
            choice = scan.nextInt();
            scan.nextLine();
            if(choice == 3){
                return;
            }
            if(choice != 1 && choice != 2){
                System.out.println("Invalid choice. Please try again.");
                continue;
            }
        }

        int choice2 = 0;
        while(choice != 4){
            System.out.println(" [1] Pay the minimum payment");
            System.out.println(" [2] Pay the full balance");
            System.out.println(" [3] Pay a custom amount");
            System.out.println(" [4] Return to previous menu");
            System.out.print(" Enter your choice: ");
            choice2 = scan.nextInt();
            scan.nextLine();

            switch(choice2){
                case 1:
                    make_payment_2(choice, mon_pymnt);
                    break;
                case 2:
                    make_payment_2(choice, total);
                    break;
                case 3:
                    int amount = 0;
                    while(true){
                        System.out.print(" Enter the amount you would like to pay: ");
                        amount = scan.nextInt();
                        if(amount < 0 || amount > total){
                            System.out.println("Invalid amount. Please try again.");
                        }else{
                            break;
                        }
                    }
                    make_payment_2(choice, amount);
                    break;
                case 4:
                    break;
                default:
                    System.out.println(" Invalid choice.");
                    System.out.println(" Please try again.");
                    break;
            }
        }



    }

    /**
     * Processes a payment from a specified account type and amount.
     *
     * @param acct_type The type of account to make the payment from (1 for savings, other values for checking).
     * @param amount The amount to be paid.
     */
    public static void make_payment_2(int acct_type, float amount){
        String sql = "";
        if(acct_type == 1){
            sql = "UPDATE savings SET balance = balance - ? WHERE acct_num = ((SELECT acct_num FROM owning WHERE ssn = ?) intersect (SELECT acct_num FROM savings))";
        }else{
            sql = "UPDATE checking SET balance = balance - ? WHERE acct_num = ((SELECT acct_num FROM owning WHERE ssn = ?) intersect (SELECT acct_num FROM checking))";
        }

        String sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
        String sql3 = "INSERT INTO cc_payment VALUES(?, ?, ?, ?)";
        String sql4 = "INSERT INTO ?_transactions VALUES(?, ?, ?, ?)";
        String sql5 = "UPDATE ? SET balance = balance - ? WHERE acct_num = ?";

        long transaction_id = helpfulMethods.valid_transaction_id();
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setFloat(1, amount);
            stmt.setInt(2, Prog.user_ssn);
            stmt.executeUpdate();
            
            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setLong(1, transaction_id);
            stmt.setLong(2, helpfulMethods.getAcctNum(acct_type));
            stmt.setLong(3, helpfulMethods.getCCAcctNum());
            stmt.setFloat(4, amount);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setLong(1, transaction_id);
            stmt.setLong(2, helpfulMethods.getAcctNum(acct_type));
            stmt.setLong(3, helpfulMethods.getCCAcctNum());
            stmt.setFloat(4, amount);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql4);
            if(acct_type == 1){
                stmt.setString(1, "savings");
            }else{
                stmt.setString(1, "checking");
            }
            stmt.setLong(2, transaction_id);
            stmt.setLong(3, helpfulMethods.getAcctNum(acct_type));
            stmt.setLong(4, helpfulMethods.getCCAcctNum());
            stmt.setFloat(5, amount);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql5);
            if(acct_type == 1){
                stmt.setString(1, "savings");
            }else{
                stmt.setString(1, "checking");
            }
            stmt.setFloat(2, amount);
            stmt.setLong(3, helpfulMethods.getAcctNum(acct_type));
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Payment successful.");
            System.out.println("New Remaining Balance: " + helpfulMethods.viewLoanBalance(1));


        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * This method allows a user to apply for a new credit card.
     * It prompts the user to enter a desired credit limit and validates the input.
     * It then generates a valid account number, credit card number, and date.
     * The method inserts the new credit card information into the database.
     */
    public static void apply_for_cc(){
        System.out.println("Apply for a new credit card");

        float credit_limit = 0;
        while(true){
            System.out.print("Enter the credit limit you would like: ");
            credit_limit = scan.nextFloat();
            if(credit_limit < 0){
                System.out.println("Invalid credit limit. Please try again.");
            }else{
                break;
            }
        }

        long acct_num = helpfulMethods.valid_acct_num();
        long cc_num = helpfulMethods.valid_cc_num();
        Date date = helpfulMethods.valid_Date();
        int curr_bal = 0;
        int min_monthly_pymnt = 0;

        String sql = "INSERT INTO credit_card VALUES(?, ?, ?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO has_cc VALUES(?, ?, ?)";
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setLong(1, acct_num);
            stmt.setLong(2, cc_num);
            stmt.setDate(3, date);  
            stmt.setFloat(4, credit_limit);
            stmt.setFloat(5, curr_bal);
            stmt.setFloat(6, min_monthly_pymnt);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setLong(1, Prog.user_ssn);
            stmt.setString(2, helpfulMethods.getCustomerName());
            stmt.setLong(3, acct_num);
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Credit card application successful.");


        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Replaces a lost or stolen credit card by updating the credit card number and expiration date
     * in the database for the user identified by their social security number (SSN).
     */
    public static void replace_cc(){
        System.out.println("Replace a lost or stolen credit card");

        String sql = "UPDATE credit_card SET card_num = ?, exp_date = ? WHERE acct_num = (SELECT acct_num FROM has_cc WHERE ssn = ?)";
        long cc_num = helpfulMethods.valid_cc_num();
        Date date = helpfulMethods.valid_Date();
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setLong(1, cc_num);
            stmt.setDate(2, date);
            stmt.setInt(3, Prog.user_ssn);
            stmt.executeUpdate();
            
            Prog.db.conn.commit();
            System.out.println("Credit card replacement successful.");

            stmt = Prog.db.conn.prepareStatement("SELECT * FROM credit_card WHERE acct_num = (SELECT acct_num FROM has_cc WHERE ssn = ?)");
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Account Number: " + rs.getLong("acct_num"));
                System.out.println("Credit Card Number: " + rs.getLong("card_num"));
                System.out.println("Expiration Date: " + rs.getDate("exp_date"));
                System.out.println("Credit Limit: " + rs.getFloat("credit_limit"));
                System.out.println("Current Balance: " + rs.getFloat("curr_bal"));
                System.out.println("Minimum Monthly Payment: " + rs.getFloat("min_monthly_pymnt"));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

    }
    
} 
