package banking;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static String url=null;
    public static void main(String[] args) throws SQLException {
        url = "jdbc:sqlite:" + args[1];
        Scanner scanner=new Scanner(System.in);

        DatabaseConnection databaseConnection=new DatabaseConnection(url);
        databaseConnection.createDatabase();
        databaseConnection.createNewTable();

        while (true) {
            printMenu();
            switch (scanner.nextInt()){
                case 1:
                    CreditCard creditCard=new CreditCard();
                    generateAccount(creditCard);
                    break;
                case 2:
                    databaseConnection.logIn();
                    break;
                case 0:
                    System.exit(0);
            }
        }
    }

    private static void generateAccount(CreditCard creditCard) {
        DatabaseConnection databaseConnection=new DatabaseConnection(url);
        System.out.println("Your card has been created");
        System.out.println("Your card number:\n"+creditCard.getCardNumber());
        System.out.println("Your card PIN:\n"+creditCard.getPIN());
        databaseConnection.addCardToDatabase(creditCard.getCardNumber(),creditCard.getPIN());
    }

    private static void printMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }
}