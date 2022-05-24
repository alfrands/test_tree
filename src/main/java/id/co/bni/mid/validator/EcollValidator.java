package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class EcollValidator {

    private String cardNumber;
    private String trxId;
    private String paymentNtb;
    private String amount;
    private String dateTimePayment;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getPaymentNtb() {
        return paymentNtb;
    }

    public void setPaymentNtb(String paymentNtb) {
        this.paymentNtb = paymentNtb;
    }

    public String getDateTimePayment() {
        return dateTimePayment;
    }

    public void setDateTimePayment(String dateTimePayment) {
        this.dateTimePayment = dateTimePayment;
    }

    @Override
    public String toString() {
        return "{" +
                "cardNumber='" + StringHelpers.maskCardnumber(cardNumber) + '\'' +
                ", trxId='" + trxId + '\'' +
                ", paymentNtb='" + paymentNtb + '\'' +
                ", amount='" + amount + '\'' +
                ", dateTime='" + dateTimePayment + '\'' +
                '}';
    }

}
