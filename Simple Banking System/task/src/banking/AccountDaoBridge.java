package banking;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDaoBridge {
    DAO dao;

    AccountDaoBridge(String fileName){
        this.dao = new DAO(fileName);
    }

    public Account createNewAccount(){
        Account account = new Account();
        dao.insertIntoBase(account.getFoolCardNumber(),
                account.getCardPin(), account.getBalance());
        return account;
    };

    public long getBalance(long cardNumber){
        long balance = 0;
        try {
            ResultSet rs = dao.selectCard(cardNumber);
            balance = Long.parseLong(rs.getString(4));
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            return balance;
        }
    }

    public boolean deleteAccount(long cardNumber){
        return dao.deleteAccount(cardNumber);
    }

    //Метод для перевода transferAmount денег с cardNumber на cardNumberForTransfer
    public boolean transferMoney(long cardNumber, long cardNumberForTransfer, long transferAmount){
        return dao.transferMoney(cardNumber, cardNumberForTransfer, transferAmount);
    }

    public String addIncome(long cardNumber, long income){
        int changedRows = dao.addIncome(cardNumber, income);
        if(changedRows == 1){
            return "Income was added!\n";
        }
        return "Error in addIncome-method\n";
    }

    public boolean isCardExist(long cardNumberForTransfer){
        try (ResultSet resultSet = dao.selectCard(cardNumberForTransfer)) {
            if(resultSet.next()){
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean isAccountCorrect(long cardNumber, long cardPin){
        boolean marker = false;
        try {
            ResultSet rs = dao.selectCard(cardNumber);
            //int id = rs.getInt(1);
            String number = rs.getString(2);
            String pin = rs.getString(3);
            marker = Long.parseLong(pin) == cardPin ?true:false;
        } catch (SQLException e) {
            return marker;
        }
            return marker;
    }

    public void exitProcedure(){
        try {
            dao.exitDatabaseConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
