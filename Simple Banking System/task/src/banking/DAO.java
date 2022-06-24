package banking;

import org.sqlite.SQLiteDataSource;
import java.sql.*;

public class DAO{
    private Statement card;
    private Connection connection;
    private PreparedStatement newCardStatement;
    private PreparedStatement selectByCardNumber;
    private PreparedStatement selectMaxIdCard;
    private PreparedStatement updateCardBalance;
    private PreparedStatement cardBalanceWhereTransferFrom;
    private PreparedStatement cardBalanceTransferTo;
    private PreparedStatement deleteCard;

    DAO(String dbName){
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:"+dbName);
        try{
            this.connection = dataSource.getConnection();
            try{
                this.card = connection.createStatement();
                card.executeUpdate("CREATE TABLE IF NOT EXISTS card (" +
                        "id INTEGER PRIMARY KEY, " +
                        "number TEXT NOT NULL, " +
                        "pin TEXT NOT NULL, " +
                        "balance INTEGER DEFAULT 0)");


                String insertNewCard = "INSERT INTO card (id, number, pin,balance) Values (?, ?, ?, ?)";
                this.newCardStatement = connection.prepareStatement(insertNewCard);

                String selectCard = "SELECT * FROM card where number = ?";
                this.selectByCardNumber = connection.prepareStatement(selectCard);

                String maxIdCard = "SELECT * FROM card ORDER BY id DESC LIMIT 1";
                this.selectMaxIdCard = connection.prepareStatement(maxIdCard);

                String cardBalance = "UPDATE card SET balance = ? WHERE id = ?";
                this.updateCardBalance = connection.prepareStatement(cardBalance);

                String cardBalanceFrom = "UPDATE card SET balance = ? WHERE number = ?";
                this.cardBalanceWhereTransferFrom = connection.prepareStatement(cardBalanceFrom);

                String cardBalanceTo = "UPDATE card SET balance = ? WHERE number = ?";
                this.cardBalanceTransferTo = connection.prepareStatement(cardBalanceTo);

                String deleteCard = "DELETE FROM card WHERE number = ?";
                this.deleteCard = connection.prepareStatement(deleteCard);
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean deleteAccount(long foolCardNumber){
        try {
            connection.setAutoCommit(false);
            deleteCard.setString(1, String.valueOf(foolCardNumber));
            deleteCard.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public ResultSet selectCard(long foolCardNumber) throws SQLException {
        String number = Long.toString(foolCardNumber);
        selectByCardNumber.setString(1, number);
        return selectByCardNumber.executeQuery();
    }

    public boolean transferMoney(long cardNumber, long cardNumberForTransfer, long transferAmount){
        try {
            connection.setAutoCommit(false);
            ResultSet cardTo = selectCard(cardNumberForTransfer);
            long balanceCardTo = cardTo.getLong(4);

            ResultSet cardFrom = selectCard(cardNumber);
            long balanceCardFrom = cardFrom.getLong(4);

            cardBalanceWhereTransferFrom.setString(1, String.valueOf(balanceCardFrom - transferAmount));
            cardBalanceWhereTransferFrom.setString(2, String.valueOf(cardNumber));
            cardBalanceWhereTransferFrom.executeUpdate();

            cardBalanceTransferTo.setString(1, String.valueOf(balanceCardTo+transferAmount));
            cardBalanceTransferTo.setString(2, String.valueOf(cardNumberForTransfer));
            cardBalanceTransferTo.executeUpdate();

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int addIncome(long foolCardNumber, long cardBalance){
        int changedRows = 0;
        try {
            ResultSet currentCard = selectCard(foolCardNumber);
            long newBalance = cardBalance + currentCard.getLong(4);
            long currentId = currentCard.getLong(1);
            updateCardBalance.setString(1, String.valueOf(newBalance));
            updateCardBalance.setString(2, String.valueOf(currentId));
            changedRows = updateCardBalance.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return changedRows;
    }

    public int insertIntoBase(long foolCardNumber, long pinCard, long cardBalance){
        String id = Long.toString(getLastIdInBase());
        String number = Long.toString(foolCardNumber);
        String pin = Long.toString(pinCard);
        String balance = Long.toString(cardBalance);
        int changedRows = 0;
        try {
            connection.setAutoCommit(false);

            newCardStatement.setString(1, id);
            newCardStatement.setString(2, number);
            newCardStatement.setString(3, pin);
            newCardStatement.setString(4, balance);

            changedRows = newCardStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return changedRows;
    }

    public int getLastIdInBase(){
        try {
            ResultSet rs = selectMaxIdCard.executeQuery();
            return rs.getInt(1)+1;
        } catch (SQLException e) {
            //System.out.println("Не удалось определить последний номер в базе");
        }
        return 1;
    }

    public void exitDatabaseConnection() throws SQLException {
        newCardStatement.close();
        selectByCardNumber.close();
        selectMaxIdCard.close();
        connection.close();
    }
}