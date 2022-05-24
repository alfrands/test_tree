package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class CPCRDValidator {

    private String cardNumber;
    private String org;
    private String type;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + cardNumber + '\'' +
                ", org='" + org + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
