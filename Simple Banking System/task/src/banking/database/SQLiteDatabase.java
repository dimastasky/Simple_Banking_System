package banking.database;

import banking.Account;
import banking.bankcard.Card;
import org.sqlite.SQLiteDataSource;

import java.sql.*;


//SQLite Database class
public class SQLiteDatabase implements InterfaceDB{

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS card (" +
                    "id INTEGER PRIMARY KEY," +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER DEFAULT 0)";
    private static final String SQL_ADD_ACCOUNT = "INSERT INTO card (number, pin) VALUES (?, ?)";
    private static final String SQL_LOGIN_ACCOUNT = "SELECT number, pin, balance FROM card WHERE number = ? AND pin = ?";
    private static final String SQL_UPDATE_ACCOUNT = "UPDATE card SET balance = balance + ? WHERE number = ?";
    private static final String SQL_DELETE_ACCOUNT = "DELETE FROM card WHERE number = ?";
    private static final String SQL_FIND_ACCOUNT = "SELECT number FROM card WHERE number = ?";

    private SQLiteDataSource dataSource;
    private Connection con;
    private final String DATABASE_URL;

    /**
     * Constructor to create SQLite database
     * @param databaseName Name of the database
     */
    public SQLiteDatabase(String databaseName) {
        dataSource = new SQLiteDataSource();
        DATABASE_URL = "jdbc:sqlite:" + databaseName;
        dataSource.setUrl(DATABASE_URL);
        try {
            con = dataSource.getConnection();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        createTable();
    }

    //Function for creating DB table using SQL_CREATE_TABLE
    private void createTable() {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(SQL_CREATE_TABLE);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save user's account data into DB using SQL_ADD_ACCOUNT
     * @param userAccount Generated Card number and Pin
     */
    @Override
    public void createAccount(Account userAccount) {
        try(PreparedStatement ps = con.prepareStatement(SQL_ADD_ACCOUNT)) {
            ps.setString(1, userAccount.getCard().getNumber());
            ps.setString(2, userAccount.getCard().getPin());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Create Account Error!");
        }
    }

    /**
     * Login into account using SQL_LOGIN_ACCOUNT
     * @param userCard Inputed Card number and Pin
     * @return If logged in, returns Card and Balance
     */
    @Override
    public Account loginAccount(Card userCard) {
        Account resultAccount = null;
        try (PreparedStatement ps = con.prepareStatement(SQL_LOGIN_ACCOUNT)) {
            ps.setString(1, userCard.getNumber());
            ps.setString(2, userCard.getPin());
            ResultSet resultSet = ps.executeQuery();
            int balance = resultSet.getInt("balance");
            resultAccount = new Account(userCard, balance);
            if (resultSet.next()) {
                return resultAccount;
            }

        } catch (SQLException e) {
            System.out.println("Login Account Error!");
        }

        return resultAccount;
    }

    /**
     * Update balance of specified card using SQL_UPDATE_ACCOUNT
     * @param cardNumber Card number, which balance would be modified
     * @param changeBalance Modified balance
     */
    @Override
    public void updateAccount(String cardNumber, int changeBalance) {
        try (PreparedStatement ps = con.prepareStatement(SQL_UPDATE_ACCOUNT)) {
            ps.setString(1, Integer.toString(changeBalance));
            ps.setString(2, cardNumber);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Update account error!");
        }
    }

    /**
     * Delete account from DB using SQL_DELETE_ACCOUNT
     * @param userAccount User account to be deleted
     */
    @Override
    public void deleteAccount(Account userAccount) {
        try (PreparedStatement ps = con.prepareStatement(SQL_DELETE_ACCOUNT)) {
            ps.setString(1, userAccount.getCard().getNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete account error!");
        }
    }

    /**
     * Find account (to check if account exists) using SQL_FIND_ACCOUNT
     * @param cardNumber Card number to be found in DB
     * @return If TRUE - account is found, if FALSE - account not found
     */
    @Override
    public boolean findAccount(String cardNumber) {
        try (PreparedStatement ps = con.prepareStatement(SQL_FIND_ACCOUNT)){
            ps.setString(1, cardNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Find account error!");
        }
        return false;
    }

    //Close connection to DB
    @Override
    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("Closing Connection to DB Error!");
        }
    }
}
