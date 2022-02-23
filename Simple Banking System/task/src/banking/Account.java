package banking;

import banking.bankcard.Card;

//User's bank account, that keeps card and balance
public class Account {
    private int balance;
    private Card card;

    public Account(Card card) {
        this.card = card;
        this.balance = 0;
    }

    public Account(Card card, int balance) {
        this.card = card;
        this.balance = balance;
    }

    public void addBalance(int income) {
        this.balance += income;
    }

    public Card getCard() {
        return card;
    }

    public int getBalance() {
        return balance;
    }
}
