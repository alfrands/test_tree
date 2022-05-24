package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class CardValidator {

    private String cardNumber;
    private String expiryDate;
    private String dob;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(cardNumber) + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }

    public String toString2() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(cardNumber) + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                '}';
    }

}
