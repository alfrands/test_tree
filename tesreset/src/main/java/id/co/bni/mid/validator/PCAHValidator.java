package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class PCAHValidator {

    private String cardNumber;
    private String cardOrg;
    private String cardType;
    private String customerOrg;
    private String customerNumber;
    private String postingFlag;
    private String cycle;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardOrg() {
        return cardOrg;
    }

    public void setCardOrg(String cardOrg) {
        this.cardOrg = cardOrg;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCustomerOrg() {
        return customerOrg;
    }

    public void setCustomerOrg(String customerOrg) {
        this.customerOrg = customerOrg;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getPostingFlag() {
        return postingFlag;
    }

    public void setPostingFlag(String postingFlag) {
        this.postingFlag = postingFlag;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(cardNumber) + '\'' +
                ", cardOrg='" + cardOrg + '\'' +
                ", cardType='" + cardType + '\'' +
                ", customerOrg='" + customerOrg + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                ", postingFlag='" + postingFlag + '\'' +
                ", cycle='" + cycle + '\'' +
                '}';
    }
}
