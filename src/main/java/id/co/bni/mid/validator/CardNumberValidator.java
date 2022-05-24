package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class CardNumberValidator {
    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(cardNumber) + '\'' +
                '}';
    }
}
