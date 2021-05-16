package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {
    static Scanner scanner=new Scanner(System.in);
    static String url=null;
    static int id=1;

    public DatabaseConnection(String url) {
        DatabaseConnection.url =url;
    }

    void createNewTable() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE TABLE if not exists card (" +
                        "id INTEGER," +
                        "number TEXT NOT NULL," +
                        "pin TEXT NOT NULL," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void createDatabase() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("CREATE DATABASE data");}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     void logIn() throws SQLException {
        boolean loggedIn=false;
        System.out.println("Enter your card number:");
        String cardNumInput=scanner.next();
        System.out.println("Enter your PIN:");
        String PINInput=scanner.next();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet cards = statement.executeQuery("SELECT number, pin FROM card where number=" + cardNumInput + " AND pin=" + PINInput)) {

                    if(cards.getString("number").equals(cardNumInput)&&cards.getString("pin").equals(PINInput)){
                        System.out.println("You have successfully logged in!");
                        loggedIn=true;
                    } else {System.out.println("Wrong card number or PIN!");
                        return;}

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if(loggedIn)
            user(cardNumInput,PINInput);
    }

     void user(String card, String PIN) throws SQLException {
        while (true){
            userMenu();
            switch (scanner.nextInt()){
                case 1:
                    showBalance(card,PIN);
                    break;
                case 2:
                    addIncome(card,PIN);
                    break;
                case 3:
                    doTransfer(card,PIN);
                    break;
                case 4:
                    closeAccount(card);
                    break;
                case 5:
                    return;
                case 0:
                    System.exit(0);
            }
        }
    }

     void closeAccount(String card) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.execute("DELETE from card where number="+card);
                System.out.println("Card deleted!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

      int showBalance(String card, String pin) throws SQLException {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        int balance=0;
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet cards = statement.executeQuery("SELECT  balance FROM card where number=" + card + " AND pin=" + pin)) {
                    balance=cards.getInt("balance");
                    System.out.println("Balance: "+cards.getInt("balance"));

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return balance;
    }
     void doTransfer(String card, String PIN) throws SQLException {
        System.out.println("Transfer\nEnter card number");

        String toCardNum=scanner.next();
        if(card.equals(toCardNum))
            System.out.println("You can't transfer money to the same account!");

        int sum=0;
        for (int i=0;i<toCardNum.length();i++){
            int x=Character.getNumericValue(toCardNum.charAt(i));
            int order=i+1;
            if(order%2!=0){
                x*=2;
                if(x>9)
                    x-=9;
            }
            sum+=x;
        }
        if(sum%10!=0) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        }

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                try (ResultSet cards = statement.executeQuery("SELECT number FROM card")) {
                    boolean cardInData=false;
                    while (cards.next()){
                        if (cards.getString("number").equals(toCardNum)){
                            cardInData=true;
                            break;
                        }
                    }
                    if(!cardInData){
                        System.out.println("Such a card does not exist.");
                        return;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int balance=showBalance(card,PIN);
        String addTO="UPDATE card set balance=balance+? where number=?";
        String subtractFrom="UPDATE card set balance=balance-? where number=?";
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement add = con.prepareStatement(addTO);
                 PreparedStatement subtract= con.prepareStatement(subtractFrom)) {

                System.out.println("Enter how much money you want to transfer:");
                int transfer=scanner.nextInt();
                if(transfer>balance) {
                    System.out.println(transfer+" : "+balance);
                    System.out.println("Not enough money!");
                    return;
                }
                add.setInt(1,transfer);
                add.setString(2,toCardNum);
                add.executeUpdate();

                subtract.setInt(1,transfer);
                subtract.setString(2,card);
                subtract.executeUpdate();

                con.commit();
                System.out.println("Success");

            } catch (SQLException e) {
                System.out.println("Such a card does not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Such a card does not exist.");
        }
    }

     void addIncome(String card, String PIN) {
        System.out.println("Enter income:");
        System.out.println(id);
        int income=scanner.nextInt();
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("UPDATE card set balance=balance+"+income+" where number="+card+" AND pin="+PIN);
                System.out.println("Income was added!");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     void addCardToDatabase(String cardNumber, String pin) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        long PIN=Long.parseLong(pin);
        try (Connection con = dataSource.getConnection()) {
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate("INSERT INTO card VALUES ("+id+","+cardNumber+","+ String.format("%04d", PIN)+","+0+")");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        id++;
    }
     void userMenu() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

//    private void showCards() {
//        SQLiteDataSource dataSource = new SQLiteDataSource();
//        dataSource.setUrl(url);
//
//        try (Connection con = dataSource.getConnection()) {
//            try (Statement statement = con.createStatement()) {
//                try (ResultSet cards = statement.executeQuery("SELECT * FROM card")) {
//                    while (cards.next()) {
//                        int id = cards.getInt("id");
//                        String cardNumber = cards.getString("number");
//                        String cardpin = cards.getString("pin");
//                        int balance =cards.getInt("balance");
//
//                        System.out.printf("Card ID %d%n", id);
//                        System.out.printf("Card Number: %s%n", cardNumber);
//                        System.out.printf("CardPIN: %s%n", cardpin);
//                        System.out.printf("Balance: %d%n", balance);
//                    }
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
}
