import java.util.*;

public interface Online_mobile {
    Scanner scan = new Scanner(System.in);

    public static void displayMenu1(){
        System.out.println(" [1] Asset Services");
        System.out.println(" [2] Credit Card Services");
        System.out.println(" [3] Loan Services");
        System.out.println(" [4] Exit");
    }

    public static void displayMenu2(){
        System.out.println(" [1] Deposit Check");
        System.out.println(" [2] Transfer Funds");
        System.out.println(" [3] Debit Card");
        System.out.println(" [4] Open a New Account");
        System.out.println(" [5] Exit");
    }
    
    public static void displayMenu3(){
        System.out.println(" [1] Make a Payment");
        System.out.println(" [2] Apply for a Loan");
        System.out.println(" [3] Obtain a New Credit Card");
        System.out.println(" [4] Exit");
    }

    public static void displayMenu4(){
        System.out.println(" [1] Display Card Information");
        System.out.println(" [2] Replace Debit Card");
        System.out.println(" [4] Exit");
    }

    /**
     * This method represents the main loop for the online mobile service.
     * It continuously displays a menu and processes user input until the user chooses to exit.
     * 
     * The menu options are:
     * 1. Asset services
     * 2. Credit card services
     * 3. Loan services
     * 4. Exit the program
     * 
     * The user is prompted to enter their choice, and the corresponding service method is called.
     * If the user enters an invalid choice, an error message is displayed.
     */
    public static void online_mobileLoop(){
        int choice = 0;
        while(choice != 4){
            displayMenu1();
            System.out.print("Enter your choice: ");
            choice = scan.nextInt();
            switch(choice){
                case 1:
                    Asset.asset_services();
                    break;
                case 2:
                    Credit_card.credit_card_services();
                    break;
                case 3:
                    Loan.loan_services();
                    break;
                case 4:
                    System.out.println("Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }

    }
}
