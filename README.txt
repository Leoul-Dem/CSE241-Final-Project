Leoul Demissie
lgs226


Interface
    1. Login for the database.

    2. Customer login page.
        - Name: Ella Hembrow
        - Last 4 SSN: 4688

    3. ATM, Online/ mobile banking, or Make a purchase

        1. ATM - Withdraw cash (cheking account only) or view balance

        2. Online/ mobile banking
           
            1. Asset Services (All things regarding checking, savings, and investment accounts)
               
                - Deposit Check: Customer chooses where to deposit check (checking or savings) as long as its greater than 0 it is accepted.

                - Transfer Funds: Customer chooses which account to transfer from and destination is automatically selected, customer inputs the amount, if they have the balance the transfer is processed, otherwise the transfer is rejected.

                - Debit Card: All functionalities for a debit card 
                    - Display card info
                    - Replace lost or stolen debit card

                - Open an account: Customer enters info (first name, last name, ssn) and deposits funds (at least $100).

            2. Credit Card Services (All things regarding credit card) - I check the customer has a credit card first, if they do proceed if not they are given the option to open one or exit.

                - Make payment on credit card: Customer can use either a savings or checking account to make a payment. Customer has the open to pay the minimum (10% of the running balance), pay in full, or enter a custom amount.

                - Replace lost or stolen credit card.

                - Apply for a credit card: Customer enters a credit limit they want and create a credit card.

            3. Loan Services (All things regarding unsecured and mortgage loans)

                - Apply for a loan:
                    - Apply for unsecured loan: Customer enters the amount they wish to borrow, I then generate an interest rate and minimum monthly payment based on the interest rate and the customer is prompted to accept or reject the offer. They can reject 2 times and be given 2 other offers; if they reject the third one, the application is terminated.

                    - Apply for a mortgage loan: Customer enters the amount they wish to borrow and the address of the property. Then the rest of the process is like unsecured loan.

                - Make payment on loan (process for unsecured and mortgage is the same). Customer can use either a savings or checking account to make a payment. Customer has the open to pay the minimum (10% of the running balance), pay in full, or enter a custom amount.
        
        3. Make a purchase: Customer can make a purchase using their credit or debit card. They enter the price of their purchase, as long as the amount does not exceed their balance, the purchase is approved.


Assumptions
    1. Asset services
        - Deposit check: as long as the customer is depositing a checking greater than 0 dollars, it is considered a valid check and gets deposited.
        - Transfer funds: funds can only be transferred between savings and checking account.
        - Open an account: only an existing customer can open an account for another person, no self signup.

    2. Credit card services
        - A checking/ savings account is required to have a credit card.
        - A customer is allowed to set their credit limit, all positive integers are accepted.

    3. Loan services
        - There is no cap on how much a customer can borrow.
        - Mortgage is a the secured loan since the house is collateral.
            - I assume what ever the address given is a valid address.


Fuctionalities Implemented
    1. Account deposit/ withdrawal.
    2. Payment on loan/ credit card.
    3. Open a new bank account.
    4. Obtain a new credit card.
    5. Replace lost or stolen debit and credit card.
    6. Take out a new loan.
    7. Make a purchase using debit or credit card.
