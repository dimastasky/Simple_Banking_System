package banking.bankcard;

//Class that describes Card entity
public class Card {
    String number;
    String pin;

    public Card(String number, String pin) {
        this.number = number;
        this.pin = pin;
    }

    public String getNumber() {
        return number;
    }

    public String getPin() {
        return pin;
    }
}
