import java.util.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;

public interface helpfulMethods {
    /**
     * Generates a valid transaction ID that does not already exist in the database.
     * The transaction ID is a 12-digit positive number.
     * 
     * @return A unique 12-digit transaction ID.
     */
    public static long valid_transaction_id(){
        Random rand = new Random();
        long transaction_id = Math.abs(rand.nextLong() % 900000000000L + 100000000000L);
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT id FROM transactions where id = ?");
            stmt.setLong(1, transaction_id);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return valid_transaction_id();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return transaction_id;
    }

    /**
     * Generates a valid card number that does not already exist in the database.
     * The card number is a 16-digit positive number.
     * 
     * @return A unique 16-digit card number.
     */
    public static long valid_card_num(){
        Random rand = new Random();
        long card_num = Math.abs(rand.nextLong() % 9000000000000000L + 1000000000000000L);
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT card_num FROM debit_card UNION SELECT card_num FROM credit_card");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(rs.getLong("card_num") == card_num){
                    return valid_card_num();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return card_num;
    }

    /**
     * Generates a valid expiration date that is 5 years from the current date.
     * 
     * @return A valid expiration date.
     */
    public static Date valid_Date(){
       return Date.valueOf((LocalDate.now()).plusYears(5));
    }

    /**
     * Generates a valid account number that does not already exist in the database.
     * The account number is an 8-digit positive number.
     * 
     * @return A unique 8-digit account number.
     */
    public static int valid_acct_num(){
        Random rand = new Random();
        int acct_num = Math.abs(rand.nextInt() % 90000000 + 10000000);
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT acct_num FROM account UNION SELECT acct_num FROM loans");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(rs.getLong("acct_num") == acct_num){
                    return valid_acct_num();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return acct_num;
    }

    /**
     * Generates a valid routing number that does not already exist in the database.
     * The routing number is a 9-digit positive number.
     * 
     * @return A unique 9-digit routing number.
     */
    public static int valid_routing_num(){
        Random rand = new Random();
        int routing_num = Math.abs(rand.nextInt() % 900000000 + 100000000);
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT routing_num FROM bank");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(rs.getInt("routing_num") == routing_num){
                    return valid_routing_num();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return routing_num;
    }

    /**
     * Generates a valid social security number that does not already exist in the database.
     * The social security number is a 9-digit positive number.
     * 
     * @return A unique 9-digit social security number.
     */
    public static long valid_cc_num(){
        Random rand = new Random();
        long cc_num = Math.abs(rand.nextLong() % 9000000000000000L + 1000000000000000L);
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT card_num FROM credit_card");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(rs.getLong("card_num") == cc_num){
                    return valid_cc_num();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return cc_num;
    }

    /**
     * Retrieves the account number for a given account type.
     *
     * @param type the type of account (1 for checking, other values for savings)
     * @return the account number if found, otherwise -1
     */
    public static long getAcctNum(int type){
        String sql;

        if(type == 1){
            sql = "SELECT acct_num FROM checking WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        }else{
            sql = "SELECT acct_num FROM savings WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        }

        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getLong("acct_num");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves the account number for a given loan type.
     *
     * @param type the type of loan (1 for unsecured, other values for mortgage)
     * @return the account number if found, otherwise -1
     */
    public static long getLoanAcctNum(int type){
        String sql;
        if(type == 1){
            sql = "(select acct_num from borrowing where ssn = ?) intersect (select acct_num from unsecured)";
        }else{
            sql = "(select acct_num from borrowing where ssn = ?) intersect (select acct_num from mortgage)";
        }

        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getLong("acct_num");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Retrieves the credit card account number for a given social security number.
     *
     * @return the account number if found, otherwise -1
     */
    public static long getCCAcctNum(){
        String sql = "SELECT acct_num FROM has_cc WHERE ssn = ?";
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getLong("acct_num");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    
    /**
     * Retrieves the name of the customer based on the user's SSN.
     *
     * This method executes a SQL query to fetch the customer's name from the database
     * using the SSN stored in the Prog.user_ssn variable. If the query is successful
     * and a matching record is found, the customer's name is returned. If no matching
     * record is found or an exception occurs, the method returns null.
     *
     * @return the name of the customer if found, otherwise null
     */
    public static String getCustomerName(){
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT name FROM customer WHERE ssn = ?");
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString("name");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Retrieves and displays the balance of the specified account type for the current user.
     *
     * @param acct an integer representing the account type (1 for savings, other values for checking)
     * @return the balance of the specified account as a float, or -1.0f if an error occurs
     */
    public static float viewBalance(int acct){
        String sql;
        if(acct == 1){
            sql = "SELECT balance FROM savings WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        }else{       
            sql = "SELECT balance FROM checking WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        }
        
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                if(acct == 1){
                    System.out.println(" Savings Account Balance: " + rs.getFloat("balance"));
                    return rs.getFloat("balance");
                }else {
                    System.out.println(" Checking Account Balance: " + rs.getFloat("balance"));
                    return rs.getFloat("balance");
                }
            }            
        }catch(SQLException e){
            e.printStackTrace();
        }

        return -1.0f;
    }

    /**
     * Retrieves and displays the balance of the user's checking and savings accounts.
     * The method queries the database for the balance of the checking and savings accounts
     * associated with the user's social security number (SSN) and prints the balances.
     */
    public static void viewBalance(){
        String sql = "SELECT balance FROM checking WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";
        String sql2 = "SELECT balance FROM savings WHERE acct_num = (SELECT acct_num FROM owning WHERE ssn = ?)";

        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println(" Checking Account Balance: " + rs.getFloat("balance"));
            }

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setInt(1, Prog.user_ssn);
            rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println(" Savings Account Balance: " + rs.getFloat("balance"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Retrieves and displays the balance of the user's credit card, unsecured loan, and mortgage loan accounts.
     * The method queries the database for the balance of the credit card, unsecured loan, and mortgage loan accounts
     * associated with the user's social security number (SSN) and prints the balances.
     */
    public static void viewLoanBalance(){
        // Query to get current balance of credit card
        String sql = "SELECT curr_bal FROM credit_card WHERE acct_num = (SELECT acct_num FROM has_cc WHERE ssn = ?)";
        // Query to get current balance of unsecured loan
        String sql2 = "SELECT loan_remaining FROM loans WHERE acct_num = (SELECT acct_num FROM unsecured WHERE acct_num = (SELECT acct_num FROM borrowing WHERE ssn = ?))";
        // Query to get current balance of mortgage loan
        String sql3 = "SELECT loan_remaining FROM loans WHERE acct_num = (SELECT acct_num FROM mortgage WHERE acct_num = (SELECT acct_num FROM borrowing WHERE ssn = ?))";
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println(" Credit Card Balance: " + rs.getFloat("curr_bal"));
            }

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setInt(1, Prog.user_ssn);
            rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println(" Unsecured Loan Balance: " + rs.getFloat("loan_remaining"));
            }

            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setInt(1, Prog.user_ssn);
            rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println(" Mortgage Loan Balance: " + rs.getFloat("loan_remaining"));
            }


        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Retrieves and displays the loan balance based on the specified choice.
     *
     * @param choice an integer representing the type of loan balance to view:
     *               1 for credit card balance,
     *               2 for unsecured loan balance,
     *               3 for mortgage loan balance.
     * @return the loan balance as a float. Returns -1.0f if the choice is invalid or if an error occurs.
     */
    public static float viewLoanBalance(int choice){
        String sql;
        if(choice == 1){
            sql = "SELECT curr_bal FROM credit_card WHERE acct_num = (SELECT acct_num FROM has_cc WHERE ssn = ?)";
        }else if(choice == 2){
            sql = "SELECT loan_remaining FROM loans WHERE acct_num = (SELECT acct_num FROM unsecured WHERE acct_num = (SELECT acct_num FROM borrowing WHERE ssn = ?))";
        }else if(choice == 3){
            sql = "SELECT loan_remaining FROM loans WHERE acct_num = (SELECT acct_num FROM mortgage WHERE acct_num = (SELECT acct_num FROM borrowing WHERE ssn = ?))";
        }else{
            return -1.0f;
        }

        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                if(choice == 1){
                    System.out.println(" Credit Card Balance: " + rs.getFloat("curr_bal"));
                    return rs.getFloat("curr_bal");
                }else if (choice == 2){
                    System.out.println(" Unsecured Loan Balance: " + rs.getFloat("loan_remaining"));
                    return rs.getFloat("loan_remaining");
                }else{
                    System.out.println(" Mortgage Loan Balance: " + rs.getFloat("loan_remaining"));
                    return rs.getFloat("loan_remaining");
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1.0f;
    }

    /**
     * Checks if a given SSN exists in the customer database.
     *
     * @param ssn the SSN to check for existence
     * @return true if the SSN exists in the database, false otherwise
     */
    public static boolean ssnExists(int ssn){
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT ssn FROM customer");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(rs.getInt("ssn") == ssn){
                    return true;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Checks if the current user has a credit card.
     *
     * This method queries the database to check if the user's SSN is present
     * in the 'has_cc' table, indicating that the user has a credit card.
     *
     * @return true if the user has a credit card, false otherwise.
     */
    public static boolean hasCC(){
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement("SELECT ssn FROM has_cc");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(rs.getInt("ssn") == Prog.user_ssn){
                    return true;
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }    
}
