package banking;

import banking.bankcard.Card;
import banking.bankcard.CardGenerator;
import banking.Account;
import banking.database.InterfaceDB;
import banking.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BankingSystem {

    private Scanner scanner = new Scanner(System.in);
    private InterfaceDB dataBase;
    private boolean isExit = false;

    public void run(String databaseName) {
        dataBase = new SQLiteDatabase(databaseName);

        while(!isExit) {
            initialUI();
            int input = Integer.parseInt(scanner.nextLine());
            switch (input) {
                case 1:
                    registerCard();
                    break;
                case 2:
                    loggingInBankSystem();
                    break;

                case 0:
                    System.out.println("Bye!");
                    isExit = true;
                    break;
                default:
                    System.out.println("Wrong input! Try again.");
                    break;
            }
        }
    }

    private void loggingInBankSystem() {
        System.out.println("\nEnter your card number:");
        String cardNumberInput = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pinInput = scanner.nextLine();
        Account currentAccount = dataBase.loginAccount(new Card(cardNumberInput, pinInput));

        if (currentAccount != null) {
            System.out.println("\nYou have successfully logged in!\n");
            boolean isLogout = false;
            while (!isLogout) {
                accountUI();
                int input = Integer.parseInt(scanner.nextLine());
                switch (input) {
                    case 1:
                        System.out.println("\nBalance: " + currentAccount.getBalance());
                        break;
                    case 2:
                        System.out.println("\nEnter income: ");
                        int increment;
                        try {
                            increment = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            increment = 0;
                        }
                        dataBase.updateAccount(currentAccount.getCard().getNumber(), increment);
                        currentAccount.addBalance(increment);
                        System.out.println("Income was added!");
                        break;
                    case 3:
                        System.out.println("Transfer\n" + "Enter card number:");
                        String recepientCardNumber = scanner.nextLine();
                        if (recepientCardNumber.equals(currentAccount.getCard().getNumber())) {
                            System.out.println("You can't transfer money to the same account!");
                        } else if (!CardGenerator.isCorrectNumber(recepientCardNumber)) {
                            System.out.println("Probably you made mistake in the card number. Please try again!");
                        } else if (!dataBase.findAccount(recepientCardNumber)) {
                            System.out.println("Such a card does not exist.");
                        } else {
                            System.out.println("Enter how much money you want to transfer:");
                            int moneyTransfer = 0;
                            try {
                               moneyTransfer = Integer.parseInt(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                System.out.println("You should write a number!");
                            }
                            if (moneyTransfer > currentAccount.getBalance()) {
                                System.out.println("Not enough money! " + currentAccount.getBalance());
                            } else if (moneyTransfer < 0) {
                                System.out.println("You can not transfer negative sum");
                            } else {
                                dataBase.updateAccount(currentAccount.getCard().getNumber(), -moneyTransfer);
                                currentAccount.addBalance(-moneyTransfer);
                                dataBase.updateAccount(recepientCardNumber, moneyTransfer);
                                System.out.println("Success!");
                            }
                        }
                        break;
                    case 4:
                        dataBase.deleteAccount(currentAccount);
                        System.out.println("The account has been closed!");
                        isLogout = true;
                        break;
                    case 5:
                        System.out.println("\nYou have successfully logged out!\n");
                        isLogout = true;
                        break;
                    case 0:
                        System.out.println("\nBye!");
                        isExit = true;
                        return;
                }
            }

        } else {
            System.out.println("\nWrong card number or PIN!\n");
        }

    }

    private void registerCard() {
        Account newUser = new Account(CardGenerator.createCard());
        dataBase.createAccount(newUser);
        System.out.println(String.format("\nYour card has been created\n" +
                "Your card number:\n" +
                "%s\n" +
                "Your card PIN:\n" +
                "%s\n",
                newUser.getCard().getNumber(),
                newUser.getCard().getPin() ));
    }



    private void initialUI() {
        System.out.println("\n1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");
    }

    private void accountUI() {
        System.out.println("1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");
    }
}
