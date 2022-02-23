package banking.bankcard;

import java.util.Random;

//Generator for the card. It generates card number, pin and checks if card number is correct.
public class CardGenerator {
    static Random random = new Random();

    /**
     * Public function to generate Card
     * bankCard - stores generated card number and pin
     * @return bankCard
     */
    public static Card createCard() {
        Card bankCard = new Card(generateCardNumber(),generatePin());
        return bankCard;
    }

    /**
     * Generate card number, using CheckSum
     * bin - is leading 6 digits of card number. It helps to identify Bank of the card
     * accountID - is the next 9 numbers of the card.
     * checkSum - is the last digit, that generates by Lunh's algorithm
     * cardNumber - bin + accountID + checkSum
     * @return cardNumber
     */
    private static String generateCardNumber() {
        final int bin = 400000;
        final int accountId = random.nextInt(900_000_000) + 100_000_000;
        final int checkSum = checkSum(bin,accountId);
        String cardNumber = "" + bin + accountId + checkSum;
        return cardNumber;
    }

    /**
     * Generates pin
     * @return pin
     */
    private static String generatePin() {
        String pin = "" + (1000 + random.nextInt(9000));
        return pin;
    }

    /**
     * Function to check sum that uses Luhn's algorithm
     * @param bin Bin of the card
     * @param accountId Account ID of the card
     * @return Check sum
     */
    private static int checkSum(int bin, int accountId) {
        String rawNumbers = "" + bin + accountId;
        int sum = 0;
        for (int i =0; i < rawNumbers.length(); i++) {
            int digit = Integer.parseInt(rawNumbers.substring(i,i+1));
            if (i % 2 == 0) {
                digit *= 2;
            }
            digit = (digit % 10) + (digit / 10);
            sum += digit;
        }
        return (10 - (sum%10)) %10;
    }

    /**Checks if card number is correct
     *
     * @param cardNumber Number of the card
     * @return check sum
     */
    public static boolean isCorrectNumber(String cardNumber) {
        if (cardNumber.length() != 16) {
            return false;
        }
        int bin = 0;
        int accountIdentifier = 0;
        int lastDigit = 0;
        try {
            bin = Integer.parseInt(cardNumber.substring(0, 6));
            accountIdentifier = Integer.parseInt(cardNumber.substring(6, 15));
            lastDigit = Integer.parseInt(cardNumber.substring(15));
        } catch (NumberFormatException e) {
            return false;
        }
        return checkSum(bin, accountIdentifier) == lastDigit;
    }


}
