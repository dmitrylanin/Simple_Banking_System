package banking;

import java.util.Scanner;

public class Menu{
    private Scanner scanner;
    private boolean workingMarker;
    private AccountDaoBridge accountDaoBridge;

    Menu(String fileName){
        this.scanner = new Scanner(System.in);
        this.workingMarker = true;
        this.accountDaoBridge = new AccountDaoBridge(fileName);
    }

    public void menuEngine(){
        while (workingMarker){
            printMenu();
            long menuPoint = getScanner();
            if(menuPoint == 1){
                printNewAccountInfo();
            }else if(menuPoint == 2){
                printAccountInfo();
                break;
            }else{
                exitProcedure();
                break;
            }
        }
    }

    public void printNewAccountInfo(){
        Account account = accountDaoBridge.createNewAccount();
        System.out.println("\nYour card has been created");
        System.out.format("Your card number:\n%d%n", account.getFoolCardNumber());
        System.out.format("Your card PIN:\n%d%n\n", account.getCardPin());
    }

    public void printAccountInfo(){
        System.out.println("\nEnter your card number:");
        long cardNumber = getScanner();
        System.out.println("Enter your PIN:");
        long pin = getScanner();
        boolean marker = accountDaoBridge.isAccountCorrect(cardNumber, pin);
        if(marker){
            System.out.println("\nYou have successfully logged in!\n");
            successAuthorization(cardNumber);
        }else{
            unSuccessAuthorization();
        }
    }

    public void exitProcedure(){
        System.out.println("Bye!");
        accountDaoBridge.exitProcedure();
    }

    public void successAuthorization(long cardNumber){
        String frontChecksBeforeTransferMoneyStatus;

        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit\n");


        long successAuthorizationPoint = getScanner();
        switch ((int) successAuthorizationPoint){
            case 1:
                System.out.format("\nBalance: %d%n\n", accountDaoBridge.getBalance(cardNumber));
                successAuthorization(cardNumber);
                break;
            case 2:
                System.out.println("Enter income:");
                long income = getScanner();
                System.out.println(accountDaoBridge.addIncome(cardNumber, income));
                successAuthorization(cardNumber);
                break;
            case 3:
                System.out.println("\nTransfer");
                System.out.println("Enter card number:");
                long cardNumberForTransfer = getScanner();
                frontChecksBeforeTransferMoneyStatus = frontChecksBeforeTransferMoney(cardNumber, cardNumberForTransfer);
                if(frontChecksBeforeTransferMoneyStatus.equals("All right!")){
                    System.out.println("Enter how much money you want to transfer:");
                    long transferAmount = getScanner();
                    System.out.println(transferMoney(cardNumber, cardNumberForTransfer, transferAmount));
                }else{
                    System.out.println(frontChecksBeforeTransferMoneyStatus);
                }
                successAuthorization(cardNumber);
                break;
            case 4:
                if(accountDaoBridge.deleteAccount(cardNumber)){
                    System.out.println("\nThe account has been closed!\n");
                    menuEngine();
                }else{
                    System.out.println("Unknown error! Account has not been deleted");
                }
                break;
            case 5:
                System.out.println("\nYou have successfully logged out!\n");
                menuEngine();
                break;
            default:
                exitProcedure();
                break;
        }
    }


    public String transferMoney(long cardNumber, long cardNumberForTransfer, long transferAmount){
        if(accountDaoBridge.getBalance(cardNumber)<transferAmount){
            return "Not enough money!";
        }else if(accountDaoBridge.transferMoney(cardNumber, cardNumberForTransfer, transferAmount)){
            return "Success!\n";
        }else{
            return "Unknown error!\n";
        }
    }


    /*
        Фронтовые проверки:
            - карты, с которой отправляются деньги НЕ равна карте, на которую отправляют
            - карта, на которую отправляют деньги, существует в базе
            - карта, на которую отправляют деньги, соответствует алгоритму Луна
     */
    public String frontChecksBeforeTransferMoney(long currentCardNumber, long cardNumberForTransfer){
        if(currentCardNumber == cardNumberForTransfer){
            return "You can't transfer money to the same account!\n";
        }

        if(!isValidLuhn(String.valueOf(cardNumberForTransfer))){
            return "Probably you made a mistake in the card number. Please try again!\n";
        }

        if(!accountDaoBridge.isCardExist(cardNumberForTransfer)){
            return "Such a card does not exist.\n";
        }

        return "All right!";
    }

    public void unSuccessAuthorization(){
        System.out.println("\nWrong card number or PIN!\n");
        menuEngine();
    }

    public long getScanner(){
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLong();
    }

    public boolean isValidLuhn(String value) {
        int sum = Character.getNumericValue(value.charAt(value.length() - 1));
        int parity = value.length() % 2;
        for (int i = value.length() - 2; i >= 0; i--) {
            int summand = Character.getNumericValue(value.charAt(i));
            if (i % 2 == parity) {
                int product = summand * 2;
                summand = (product > 9) ? (product - 9) : product;
            }
            sum += summand;
        }
        return (sum % 10) == 0;
    }

    public void printMenu(){
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }
}