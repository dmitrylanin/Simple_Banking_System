package banking;

public class Account {
    private final long MII = 4;
    private final long BIN = 400000;
    private long cardNumber;
    private long foolCardNumber;
    private long cardPin;
    private long balance;

    public Account(){
        do {
            this.cardNumber = generateAccountNumber();
        }
        while(AccountsMap.getAccounts().containsKey(cardNumber));
        this.cardPin = generatePin();
        this.balance = 0;
        setFoolCardNumber(cardNumber);
    }

    public static long generateAccountNumber(){
        long accountNumber = 0, currentNumber = 0, controlSum = 8;
        int count = 7;
        while (count<16){
            currentNumber = (long) Math.floor(Math.random() * 10);
            accountNumber = accountNumber*10+currentNumber;
            if(count%2 != 0){
                controlSum = currentNumber*2>9 ? controlSum+(currentNumber*2)-9 : controlSum+currentNumber*2;
            }else{
                controlSum+=currentNumber;
            }
            count++;
        }
        if(controlSum%10 == 0){
            return accountNumber*10;
        }
        return accountNumber*10 + (10 - controlSum%10);
    }

    public static long generatePin(){
       long pin = (long) Math.floor(Math.random() * Math.pow(10, 4));
       while (pin < 1000){
           pin =  (long) (pin*10 + Math.floor(Math.random()*10));
       }
       return pin;
    }

    public void setFoolCardNumber(Long cardNumber){
        this.foolCardNumber = (long) (BIN * Math.pow(10, 12) + cardNumber);
    }

    public long getFoolCardNumber(){
        return (long) (BIN * Math.pow(10, 10) + this.cardNumber);
    }

    public long getCardPin(){
        return this.cardPin;
    }

    public long getBalance(){
        return this.balance;
    }

    public long getCardNumber(){
        return this.cardNumber;
    }

    public void setBalance(long balance){
        this.balance = balance;
    }


    public static boolean isCorrectCard(Long cardNumber, Long pin){
        if(AccountsMap.isAccountNumberUnique(cardNumber)){
            if(AccountsMap.getAccount(cardNumber).getCardPin() == pin){
                return true;
            } return false;
        } return false;
    }
}