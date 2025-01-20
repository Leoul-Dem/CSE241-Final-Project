import java.util.*;
import java.sql.*;

public interface Loan {
    Scanner scan = new Scanner(System.in);
    public static void display_menu(){
        System.out.println(" [1] Apply for a Loan");
        System.out.println(" [2] Make a Payment on a Loan");
        System.out.println(" [3] Make a Payment on a Mortgage");
        System.out.println(" [4] Return to Main Menu");
    }

    public static void loan_services(){
        int choice = 0;
        while(choice != 4){
            display_menu();
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            switch(choice){
                case 1:
                    apply_for_loan();
                    loan_services();
                case 2:
                    make_payment_on_loan();
                    loan_services();
                case 3:
                    make_payment_on_mortgage();
                    loan_services();
                case 4:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
        scan.close();
    }

    /**
     * Displays a menu for applying for different types of loans and processes the user's choice.
     * The user can choose between applying for an unsecured loan, a mortgage loan, or returning to the loan menu.
     * The method will continue to prompt the user until they choose to return to the loan menu.
     */
    public static void apply_for_loan(){
        int choice = 0;
        while(choice != 3){
            System.out.println(" [1] Unsecured Loan");
            System.out.println(" [2] Mortgage Loan");
            System.out.println(" [3] Return to Loan Menu");
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            switch(choice){
                case 1:
                    apply_for_unsecured_loan();
                    break;
                case 2:
                    apply_for_mortgage_loan();
                    break;
                case 3:
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    /**
     * Applies for an unsecured loan by prompting the user for the loan amount and 
     * presenting loan terms including interest rate and monthly minimum payment.
     * The user can accept or deny the loan terms. If accepted, the loan details 
     * are inserted into the database.
     *
     * @return true if the loan is approved and successfully inserted into the database, false otherwise.
     */
    public static boolean apply_for_unsecured_loan(){
        System.out.println("Apply for an unsecured loan");
        float amount = 0;
        while(true){
            System.out.print("Enter the amount you would like to borrow: ");
            amount = scan.nextFloat();
            scan.nextLine();
            if(amount <= 0){
                System.out.println("Invalid amount.");
                System.out.println("Please try again.");
            }else{
                break;
            }
        }

        int counter = 0;
        int interest_rate = 0;
        int monthly_minimum = 0;
        while(counter < 3){
            interest_rate = new Random().nextInt(12) + 4;
            monthly_minimum = (int) (amount * (interest_rate / 100.0) / 12);

            System.out.println(" Interest rate: " + interest_rate + "%");
            System.out.println(" Monthly minimum payment: " + monthly_minimum);
            System.out.print(" Do you accept these terms? (y/n): ");
            String choice = scan.nextLine();
            if(choice.equalsIgnoreCase("y")){
                System.out.println("Loan approved.");
            }else if(choice.equalsIgnoreCase("n")){
                System.out.println("Loan denied.");
                return false;
            }else{
                if(counter == 2){
                    System.out.println("Too many invalid choices.");
                    System.out.println("Loan denied.");
                    return false;
                }
                System.out.println("Invalid choice.");
                System.out.println("Please try again.");
                counter++;
            }
        }

        String sql = "INSERT INTO loans VALUES(?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO unsecured VALUES(?)";
        String sql3 = "INSERT INTO borrowing VALUES(?, ?, ?)";

        try{
            int acct_num = helpfulMethods.valid_acct_num();
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, acct_num);
            stmt.setInt(2, interest_rate);
            stmt.setInt(3, monthly_minimum);
            stmt.setFloat(4, amount);
            stmt.setFloat(5, amount);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setInt(1, acct_num);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setInt(1, Prog.user_ssn);
            stmt.setString(2, helpfulMethods.getCustomerName());
            stmt.setInt(3, acct_num);
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Unsecured Loan approved.");
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }


        return true;
    }

    /**
     * This method handles the process of applying for a mortgage loan.
     * It prompts the user to enter the mortgage amount and the property address,
     * then calculates the interest rate and monthly mortgage payment.
     * The user is given three attempts to accept the loan terms.
     * If accepted, the loan details are inserted into the database.
     *
     * @return true if the loan is approved and successfully inserted into the database, false otherwise.
     */
    public static boolean apply_for_mortgage_loan(){
        System.out.println("Apply for a mortgage loan");

        float amount = 0;
        while(true){
            System.out.print("Enter the mortgage amount you would like to borrow: ");
            amount = scan.nextFloat();
            scan.nextLine();
            if(amount <= 0){
                System.out.println("Invalid amount.");
                System.out.println("Please try again.");
            }else{
                break;
            }
        }
        String address = "";
        while(true){
            System.out.print("Enter the address of the property you would like to purchase: ");
            address = scan.nextLine();
            if(address.length() == 0){
                System.out.println("Invalid address.");
                System.out.println("Please try again.");
            }else{
                break;
            }
        }

        int counter = 0;
        int interest_rate = 0;
        int monthly_minimum = 0;
        while(counter < 3){
            interest_rate = new Random().nextInt(12) + 4;
            monthly_minimum = (int) (amount * (interest_rate / 100.0) / 12);

            System.out.println(" Interest rate: " + interest_rate + "%");
            System.out.println(" Monthly mortgage payment: " + monthly_minimum);
            System.out.print(" Do you accept these terms? (y/n): ");
            String choice = scan.nextLine();
            if(choice.equalsIgnoreCase("y")){
                System.out.println("Loan approved.");
            }else if(choice.equalsIgnoreCase("n")){
                System.out.println("Loan denied.");
                return false;
            }else{
                if(counter == 2){
                    System.out.println("Too many invalid choices.");
                    System.out.println("Loan denied.");
                    return false;
                }
                System.out.println("Invalid choice.");
                System.out.println("Please try again.");
                counter++;
            }
        }

        String sql = "INSERT INTO loans VALUES(?, ?, ?, ?, ?)";
        String sql2 = "INSERT INTO mortgage VALUES(?, ?)";
        String sql3 = "INSERT INTO borrowing VALUES(?, ?, ?)";

        try{
            int acct_num = helpfulMethods.valid_acct_num();
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, acct_num);
            stmt.setInt(2, interest_rate);
            stmt.setInt(3, monthly_minimum);
            stmt.setFloat(4, amount);
            stmt.setFloat(5, amount);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql2);
            stmt.setInt(1, acct_num);
            stmt.setString(2, address);
            stmt.executeUpdate();

            stmt = Prog.db.conn.prepareStatement(sql3);
            stmt.setInt(1, Prog.user_ssn);
            stmt.setString(2, helpfulMethods.getCustomerName());
            stmt.setInt(3, acct_num);
            stmt.executeUpdate();

            Prog.db.conn.commit();
            System.out.println("Mortgage Loan approved.");
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * This method allows the user to make a payment on an unsecured loan.
     * It retrieves the loan details from the database and provides options for the user to:
     * 1. Make a monthly minimum payment.
     * 2. Make a custom payment.
     * 3. Pay off the loan.
     * 4. Return to the Loan Menu.
     * 
     * The user can choose to make the payment from either their checking or savings account.
     * 
     * The method interacts with the database to fetch loan details and updates the loan balance
     * based on the user's payment choice.
     */
    public static void make_payment_on_loan(){
        System.out.println("Make a payment on unsecured loan");
        String sql = "SELECT * FROM loans WHERE acct_num = ((SELECT acct_num FROM borrowing WHERE ssn = ?) INTERSECT (SELECT acct_num FROM unsecured))";
        float mon_pymnt = 0;
        float bal_remain = 0;
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Total amount borrowed: " + rs.getFloat("tot_loan"));
                System.out.println("Remaining balance: " + rs.getFloat("loan_remaining"));
                System.out.println("Monthly minimum payment: " + rs.getFloat("monthly_pymnt"));
                System.out.println("Interest rate: " + rs.getInt("interest_rate") + "%");
                bal_remain = rs.getFloat("loan_remaining");
                mon_pymnt = rs.getFloat("monthly_pymnt");
            }else{
                System.out.println("No unsecured loan found.");
                return;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        int choice = 0;
        while(choice != 4){
            System.out.println(" [1] Make a monthly minimum payment");
            System.out.println(" [2] Make a custom payment");
            System.out.println(" [3] Pay off the loan");
            System.out.println(" [4] Return to Loan Menu");
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            int choice2 = 0;
            switch(choice){
                case 1:
                    helpfulMethods.viewBalance();
                    while(choice2 != 3){
                        System.out.println(" Which account would you like to make a payment from?");
                        System.out.println(" [1] Checking");
                        System.out.println(" [2] Savings");
                        System.out.println(" [3] Return to previous menu");
                        System.out.print(" Enter your choice: ");
                        choice2 = scan.nextInt();
                        switch(choice2){
                            case 1:
                                make_payment(1, 1, mon_pymnt);
                                break;
                            case 2:
                                make_payment(2, 1, mon_pymnt);
                                break;
                            case 3:
                                break;
                            default:
                                System.out.println("Invalid choice.");
                                break;
                        }
                    }
                    break;
                case 2:
                    float custom_pymnt = 0;
                    while(true){
                        System.out.print("Enter the amount you would like to pay: ");
                        custom_pymnt = scan.nextFloat();
                        scan.nextLine();
                        if(custom_pymnt <= 0){
                            System.out.println("Invalid amount.");
                            System.out.println("Please try again.");
                        }else{
                            break;
                        }
                    }
                    helpfulMethods.viewBalance();
                    while(choice2 != 3){
                        System.out.println(" Which account would you like to make a payment from?");
                        System.out.println(" [1] Checking");
                        System.out.println(" [2] Savings");
                        System.out.println(" [3] Return to previous menu");
                        System.out.print(" Enter your choice: ");
                        choice2 = scan.nextInt();
                        switch(choice2){
                            case 1:
                                make_payment(1, 1, custom_pymnt);
                                break;
                            case 2:
                                make_payment(2, 1, custom_pymnt);
                                break;
                            case 3:
                                break;
                            default:
                                System.out.println("Invalid choice.");
                                break;
                        }
                    }
                    break;
                case 3:
                    helpfulMethods.viewBalance();
                    while(choice2 != 3){
                        System.out.println(" Which account would you like to make a payment from?");
                        System.out.println(" [1] Checking");
                        System.out.println(" [2] Savings");
                        System.out.println(" [3] Return to previous menu");
                        System.out.print(" Enter your choice: ");
                        choice2 = scan.nextInt();
                        switch(choice2){
                            case 1:
                                make_payment(1, 1, bal_remain);
                                break;
                            case 2:
                                make_payment(2, 1, bal_remain);
                                break;
                            case 3:
                                break;
                            default:
                                System.out.println("Invalid choice.");
                                break;
                        }
                    }
                    break;
                case 4:
                    break;
                default:    
                    System.out.println("Invalid choice.");
                    break;
            }

        }
    }

    /**
     * This method allows the user to make a payment on their mortgage loan.
     *
     * The method performs the following steps:
     * 1. Retrieves the mortgage loan details for the user based on their SSN.
     * 2. Displays the loan details including total amount borrowed, remaining balance,
     *    monthly minimum payment, and interest rate.
     * 3. Provides a menu for the user to choose the type of payment they want to make:
     *    - Monthly minimum payment
     *    - Custom payment
     *    - Pay off the loan
     * 4. Allows the user to select the account (checking or savings) from which the payment
     *    will be made.
     * 5. Processes the payment based on the user's choices.
     *
     * If no mortgage loan is found for the user, an appropriate message is displayed.
     */
    public static void make_payment_on_mortgage(){
        System.out.println("Make a payment on mortgage loan");
        String sql = "SELECT * FROM loans WHERE acct_num = ((SELECT acct_num FROM borrowing WHERE ssn = ?) INTERSECT (SELECT acct_num FROM mortgage))";
        float mon_pymnt = 0;
        float bal_remain = 0;
        try{
            PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
            stmt.setInt(1, Prog.user_ssn);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                System.out.println("Total amount borrowed: " + rs.getFloat("tot_loan"));
                System.out.println("Remaining balance: " + rs.getFloat("loan_remaining"));
                System.out.println("Monthly minimum payment: " + rs.getFloat("monthly_pymnt"));
                System.out.println("Interest rate: " + rs.getInt("interest_rate") + "%");
                bal_remain = rs.getFloat("loan_remaining");
                mon_pymnt = rs.getFloat("monthly_pymnt");
            }else{
                System.out.println("No mortgage loan found.");
                return;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        int choice = 0;
        while(choice != 4){
            System.out.println(" [1] Make a monthly minimum payment");
            System.out.println(" [2] Make a custom payment");
            System.out.println(" [3] Pay off the loan");
            System.out.println(" [4] Return to Loan Menu");
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            int choice2 = 0;
            switch(choice){
                case 1:
                    helpfulMethods.viewBalance();
                    while(choice2 != 3){
                        System.out.println(" Which account would you like to make a payment from?");
                        System.out.println(" [1] Checking");
                        System.out.println(" [2] Savings");
                        System.out.println(" [3] Return to previous menu");
                        System.out.print(" Enter your choice: ");
                        choice2 = scan.nextInt();
                        switch(choice2){
                            case 1:
                                make_payment(1, 2, mon_pymnt);
                                break;
                            case 2:
                                make_payment(2, 2, mon_pymnt);
                                break;
                            case 3:
                                break;
                            default:
                                System.out.println("Invalid choice.");
                                break;
                        }
                    }
                    break;
                case 2:
                    float custom_pymnt = 0;
                    while(true){
                        System.out.print("Enter the amount you would like to pay: ");
                        custom_pymnt = scan.nextFloat();
                        scan.nextLine();
                        if(custom_pymnt <= 0){
                            System.out.println("Invalid amount.");
                            System.out.println("Please try again.");
                        }else{
                            break;
                        }
                    }
                    helpfulMethods.viewBalance();
                    while(choice2 != 3){
                        System.out.println(" Which account would you like to make a payment from?");
                        System.out.println(" [1] Checking");
                        System.out.println(" [2] Savings");
                        System.out.println(" [3] Return to previous menu");
                        System.out.print(" Enter your choice: ");
                        choice2 = scan.nextInt();
                        switch(choice2){
                            case 1:
                                make_payment(1, 2, custom_pymnt);
                                break;
                            case 2:
                                make_payment(2, 2, custom_pymnt);
                                break;
                            case 3:
                                break;
                            default:
                                System.out.println("Invalid choice.");
                                break;
                        }
                    }
                    break;
                case 3:
                    helpfulMethods.viewBalance();
                    while(choice2 != 3){
                        System.out.println(" Which account would you like to make a payment from?");
                        System.out.println(" [1] Checking");
                        System.out.println(" [2] Savings");
                        System.out.println(" [3] Return to previous menu");
                        System.out.print(" Enter your choice: ");
                        choice2 = scan.nextInt();
                        switch(choice2){
                            case 1:
                                make_payment(1, 2, bal_remain);
                                break;
                            case 2:
                                make_payment(2, 2, bal_remain);
                                break;
                            case 3:
                                break;
                            default:
                                System.out.println("Invalid choice.");
                                break;
                        }
                    }
                    break;
                case 4:
                    break;
                default:    
                    System.out.println("Invalid choice.");
                    break;
            }

        }
    }

    /**
     * Processes a loan payment from a specified account type to a specified loan type.
     *
     * @param acct_type The type of account from which the payment is made:
     *                  1 for checking account, 2 for savings account.
     * @param loan_type The type of loan to which the payment is made:
     *                  1 for unsecured loan, 2 for mortgage loan.
     * @param amount The amount to be paid towards the loan.
     *
     * If the account has insufficient funds, the method prints an error message and returns without making any changes.
     * If an invalid account type or loan type is provided, the method prints an error message.
     */
    public static void make_payment(int acct_type, int loan_type, float amount){

        if (acct_type == 1 && loan_type == 1){ // Unsecured loan paid by checking account
            float balance = helpfulMethods.viewBalance(2);
            if(balance < amount){
                System.out.println("Insufficient funds.");
                return;
            }
            String sql = "UPDATE loans SET loan_remaining = loan_remaining - ? WHERE acct_num = ((SELECT acct_num FROM borrowing WHERE ssn = ?) INTERSECT (SELECT acct_num FROM unsecured))";
            String sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
            String sql3 = "INSERT INTO loan_payment VALUES(?, ?, ?, ?)";
            String sql4 = "INSERT INTO checking_transactions VALUES(?, ?, ?, ?)";
            String sql5 = "UPDATE checking SET balance = balance - ? WHERE acct_num = ?";
            try{
                // Update loan balance
                PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
                stmt.setFloat(1, amount);
                stmt.setInt(2, Prog.user_ssn);
                stmt.executeUpdate();

                long transaction_id = helpfulMethods.valid_transaction_id();

                // Add transaction to transactions table
                stmt = Prog.db.conn.prepareStatement(sql2);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(1));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to loan_payment table
                stmt = Prog.db.conn.prepareStatement(sql3);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(2));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(1));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to checking_transactions table
                stmt = Prog.db.conn.prepareStatement(sql4);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getLoanAcctNum(1));
                stmt.setLong(3, helpfulMethods.getAcctNum(2));
                stmt.setFloat(4, 0-amount);
                stmt.executeUpdate();

                // Update checking balance
                stmt = Prog.db.conn.prepareStatement(sql5);
                stmt.setFloat(1, amount);
                stmt.setLong(2, helpfulMethods.getAcctNum(2));
                stmt.executeUpdate();

                Prog.db.conn.commit();
                System.out.println("Payment successful.");
                System.out.println("New Remaining balance: " + helpfulMethods.viewLoanBalance(2));
                

            }catch(SQLException e){
                e.printStackTrace();
            }

        }else if(acct_type == 2 && loan_type == 1){ // Unsecured loan paid by savings account
            float balance = helpfulMethods.viewBalance(1);
            if(balance < amount){
                System.out.println("Insufficient funds.");
                return;
            }
            String sql = "UPDATE loans SET loan_remaining = loan_remaining - ? WHERE acct_num = ((SELECT acct_num FROM borrowing WHERE ssn = ?) INTERSECT (SELECT acct_num FROM unsecured))";
            String sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
            String sql3 = "INSERT INTO loan_payment VALUES(?, ?, ?, ?)";
            String sql4 = "INSERT INTO savings_transactions VALUES(?, ?, ?, ?)";
            String sql5 = "UPDATE savings SET balance = balance - ? WHERE acct_num = ?";
            try{
                // Update loan balance
                PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
                stmt.setFloat(1, amount);
                stmt.setInt(2, Prog.user_ssn);
                stmt.executeUpdate();

                long transaction_id = helpfulMethods.valid_transaction_id();

                // Add transaction to transactions table
                stmt = Prog.db.conn.prepareStatement(sql2);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(1));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to loan_payment table
                stmt = Prog.db.conn.prepareStatement(sql3);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(1));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to checking_transactions table
                stmt = Prog.db.conn.prepareStatement(sql4);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getLoanAcctNum(1));
                stmt.setLong(3, helpfulMethods.getAcctNum(1));
                stmt.setFloat(4, 0-amount);
                stmt.executeUpdate();

                // Update savings balance
                stmt = Prog.db.conn.prepareStatement(sql5);
                stmt.setFloat(1, amount);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));

                Prog.db.conn.commit();
                System.out.println("Payment successful.");
                System.out.println("New Remaining balance: " + helpfulMethods.viewLoanBalance(2));
                

            }catch(SQLException e){
                e.printStackTrace();
            }

        }else if(acct_type == 1 && loan_type == 2){ // Mortgage loan paid by checking account
            float balance = helpfulMethods.viewBalance(2);
            if(balance < amount){
                System.out.println("Insufficient funds.");
                return;
            }
            String sql = "UPDATE loans SET loan_remaining = loan_remaining - ? WHERE acct_num = ((SELECT acct_num FROM borrowing WHERE ssn = ?) INTERSECT (SELECT acct_num FROM mortgage))";
            String sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
            String sql3 = "INSERT INTO loan_payment VALUES(?, ?, ?, ?)";
            String sql4 = "INSERT INTO checking_transactions VALUES(?, ?, ?, ?)";
            String sql5 = "UPDATE checking SET balance = balance - ? WHERE acct_num = ?";
            try{
                // Update loan balance
                PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
                stmt.setFloat(1, amount);
                stmt.setInt(2, Prog.user_ssn);
                stmt.executeUpdate();

                long transaction_id = helpfulMethods.valid_transaction_id();

                // Add transaction to transactions table
                stmt = Prog.db.conn.prepareStatement(sql2);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(2));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to loan_payment table
                stmt = Prog.db.conn.prepareStatement(sql3);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(2));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(2));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to checking_transactions table
                stmt = Prog.db.conn.prepareStatement(sql4);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getLoanAcctNum(2));
                stmt.setLong(3, helpfulMethods.getAcctNum(2));
                stmt.setFloat(4, 0-amount);
                stmt.executeUpdate();

                // Update checking balance
                stmt = Prog.db.conn.prepareStatement(sql5);
                stmt.setFloat(1, amount);
                stmt.setLong(2, helpfulMethods.getAcctNum(2));
                stmt.executeUpdate();

                Prog.db.conn.commit();
                System.out.println("Payment successful.");
                System.out.println("New Remaining balance: " + helpfulMethods.viewLoanBalance(3));
                

            }catch(SQLException e){
                e.printStackTrace();
            }
        }else if(acct_type == 2 && loan_type == 2){ // Mortgage loan paid by savings account
            float balance = helpfulMethods.viewBalance(1);
            if(balance < amount){
                System.out.println("Insufficient funds.");
                return;
            }
            String sql = "UPDATE loans SET loan_remaining = loan_remaining - ? WHERE acct_num = ((SELECT acct_num FROM borrowing WHERE ssn = ?) INTERSECT (SELECT acct_num FROM mortgage))";
            String sql2 = "INSERT INTO transactions VALUES(?, ?, ?, ?)";
            String sql3 = "INSERT INTO loan_payment VALUES(?, ?, ?, ?)";
            String sql4 = "INSERT INTO savings_transactions VALUES(?, ?, ?, ?)";
            String sql5 = "UPDATE savings SET balance = balance - ? WHERE acct_num = ?";
            try{
                // Update loan balance
                PreparedStatement stmt = Prog.db.conn.prepareStatement(sql);
                stmt.setFloat(1, amount);
                stmt.setInt(2, Prog.user_ssn);
                stmt.executeUpdate();

                long transaction_id = helpfulMethods.valid_transaction_id();

                // Add transaction to transactions table
                stmt = Prog.db.conn.prepareStatement(sql2);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(2));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to loan_payment table
                stmt = Prog.db.conn.prepareStatement(sql3);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));
                stmt.setLong(3, helpfulMethods.getLoanAcctNum(2));
                stmt.setFloat(4, amount);
                stmt.executeUpdate();

                // Add transaction to checking_transactions table
                stmt = Prog.db.conn.prepareStatement(sql4);
                stmt.setLong(1, transaction_id);
                stmt.setLong(2, helpfulMethods.getLoanAcctNum(2));
                stmt.setLong(3, helpfulMethods.getAcctNum(1));
                stmt.setFloat(4, 0-amount);
                stmt.executeUpdate();

                // Update savings balance
                stmt = Prog.db.conn.prepareStatement(sql5);
                stmt.setFloat(1, amount);
                stmt.setLong(2, helpfulMethods.getAcctNum(1));

                Prog.db.conn.commit();
                System.out.println("Payment successful.");
                System.out.println("New Remaining balance: " + helpfulMethods.viewLoanBalance(3));
                

            }catch(SQLException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("Invalid account type or loan type.");
        }
    }
    
}
