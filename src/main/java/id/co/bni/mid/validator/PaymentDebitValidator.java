package id.co.bni.mid.validator;

import id.co.bni.mid.helpers.StringHelpers;

public class PaymentDebitValidator extends EcollValidator{

    private String debitCardNumber;
    private String ccCardNumber;

    public String getDebitCardNumber() {
        return debitCardNumber;
    }

    public void setDebitCardNumber(String debitCardNumber) {
        this.debitCardNumber = debitCardNumber;
    }

    public String getCcCardNumber() {
        return ccCardNumber;
    }

    public void setCcCardNumber(String ccCardNumber) {
        this.ccCardNumber = ccCardNumber;
    }

    @Override
    public String toString() {
        return "{" +
                "ccCardNumber ='" + StringHelpers.maskCardnumber(ccCardNumber) + '\'' +
                ", debitCardNumber='" + StringHelpers.maskCardnumber(debitCardNumber) + '\'' +
                ", trxId='" + getTrxId() + '\'' +
                ", paymentNtb='" + getPaymentNtb() + '\'' +
                ", amount='" + getAmount() + '\'' +
                ", dateTime='" + getDateTimePayment() + '\'' +
                '}';
    }

}
